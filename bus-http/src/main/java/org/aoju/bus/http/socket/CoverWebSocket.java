package org.aoju.bus.http.socket;

import org.aoju.bus.core.io.ByteString;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.http.*;
import org.aoju.bus.http.bodys.AbstractBody;
import org.aoju.bus.http.magic.RealResult;
import org.aoju.bus.http.metric.Cancelable;
import org.aoju.bus.http.metric.Convertor;
import org.aoju.bus.http.metric.TaskExecutor;
import org.aoju.bus.http.metric.TaskListener;
import org.aoju.bus.http.metric.http.CoverHttp;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CoverWebSocket implements Cancelable {

    private final List<Object> queues = new ArrayList<>();
    private final TaskExecutor taskExecutor;
    private boolean cancelOrClosed;
    private WebSocket webSocket;
    private Charset charset;

    private String msgType;

    public CoverWebSocket(TaskExecutor taskExecutor, String msgType) {
        this.taskExecutor = taskExecutor;
        this.msgType = msgType;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public synchronized boolean cancel() {
        if (null != webSocket) {
            webSocket.cancel();
        }
        cancelOrClosed = true;
        return true;
    }

    public synchronized boolean close(int code, String reason) {
        if (null != webSocket) {
            webSocket.close(code, reason);
        }
        cancelOrClosed = true;
        return true;
    }

    public void msgType(String type) {
        if (type == null || type.equalsIgnoreCase(Builder.FORM)) {
            throw new IllegalArgumentException("msgType 不可为空 或 form");
        }
        this.msgType = type;
    }

    public long queueSize() {
        if (null != webSocket) {
            return webSocket.queueSize();
        }
        return queues.size();
    }

    public boolean send(Object msg) {
        if (msg == null) {
            return false;
        }
        synchronized (queues) {
            if (null != webSocket) {
                return send(webSocket, msg);
            } else {
                queues.add(msg);
            }
        }
        return true;
    }

    void setWebSocket(WebSocket webSocket) {
        synchronized (queues) {
            for (Object msg : queues) {
                send(webSocket, msg);
            }
            this.webSocket = webSocket;
            queues.clear();
        }
    }

    boolean send(WebSocket webSocket, Object msg) {
        if (msg == null) {
            return false;
        }
        if (msg instanceof String) {
            return webSocket.send((String) msg);
        }
        if (msg instanceof ByteString) {
            return webSocket.send((ByteString) msg);
        }
        if (msg instanceof byte[]) {
            return webSocket.send(ByteString.of((byte[]) msg));
        }
        byte[] bytes = taskExecutor.doMsgConvert(msgType, (Convertor c) -> c.serialize(msg, charset)).data;
        return webSocket.send(new String(bytes, charset));
    }

    public interface Register<T> {

        void on(CoverWebSocket ws, T data);

    }

    public static class Close {

        public static int CANCELED = 0;
        public static int EXCEPTION = -1;
        public static int NETWORK_ERROR = -2;
        public static int TIMEOUT = -3;

        private final int code;
        private final String reason;

        public Close(int code, String reason) {
            this.code = code;
            this.reason = reason;
        }

        /**
         * @return 关闭状态码
         */
        public int getCode() {
            return code;
        }

        /**
         * @return 关闭原因
         */
        public String getReason() {
            return reason;
        }

        /**
         * @return 是否因 WebSocket 连接被取消而关闭
         */
        public boolean isCanceled() {
            return code == CANCELED;
        }

        /**
         * @return 是否因 WebSocket 连接发生异常而关闭
         */
        public boolean isException() {
            return code == EXCEPTION;
        }

        /**
         * @return 是否因 网络错误 而关闭
         */
        public boolean isNetworkError() {
            return code == NETWORK_ERROR;
        }

        /**
         * @return 是否因 网络超时 而关闭
         */
        public boolean isTimeout() {
            return code == TIMEOUT;
        }

        @Override
        public String toString() {
            return "Close [code=" + code + ", reason=" + reason + "]";
        }
    }

    public static class Listener extends WebSocketListener {

        private final Client client;
        CoverWebSocket webSocket;

        Charset charset;

        public Listener(Client client, CoverWebSocket webSocket) {
            this.client = client;
            this.webSocket = webSocket;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            this.charset = client.charset(response);
            this.webSocket.setCharset(charset);
            this.webSocket.setWebSocket(webSocket);
            TaskListener<Results> listener = client.httpv.executor().getResponseListener();
            Results result = new RealResult(client, response, client.httpv.executor());
            if (null != listener) {
                if (listener.listen(client, result) && null != client.onOpen) {
                    client.execute(() -> client.onOpen.on(this.webSocket, result), client.openOnIO);
                }
            } else if (null != client.onOpen) {
                client.execute(() -> client.onOpen.on(this.webSocket, result), client.openOnIO);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            if (null != client.onMessage) {
                client.execute(() -> client.onMessage.on(this.webSocket, new Message(text, client.httpv.executor(), charset)), client.messageOnIO);
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            if (null != client.onMessage) {
                client.execute(() -> client.onMessage.on(this.webSocket, new Message(bytes, client.httpv.executor(), charset)), client.messageOnIO);
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            if (null != client.onClosing) {
                client.execute(() -> client.onClosing.on(this.webSocket, new Close(code, reason)), client.closingOnIO);
            }
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            doOnClose(Results.State.RESPONSED, code, reason);
        }

        private void doOnClose(Results.State state, int code, String reason) {
            TaskListener<Results.State> listener = client.httpv.executor().getCompleteListener();
            if (null != listener) {
                if (listener.listen(client, state) && null != client.onClosed) {
                    client.execute(() -> client.onClosed.on(this.webSocket, toClose(state, code, reason)), client.closedOnIO);
                }
            } else if (null != client.onClosed) {
                client.execute(() -> client.onClosed.on(this.webSocket, toClose(state, code, reason)), client.closedOnIO);
            }
        }

        private Close toClose(Results.State state, int code, String reason) {
            if (state == Results.State.CANCELED) {
                return new Close(Close.CANCELED, "Canceled");
            }
            if (state == Results.State.EXCEPTION) {
                return new Close(Close.CANCELED, reason);
            }
            if (state == Results.State.NETWORK_ERROR) {
                return new Close(Close.NETWORK_ERROR, reason);
            }
            if (state == Results.State.TIMEOUT) {
                return new Close(Close.TIMEOUT, reason);
            }
            return new Close(code, reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            IOException e = t instanceof IOException ? (IOException) t : new IOException(t.getMessage(), t);
            doOnClose(client.toState(e), 0, t.getMessage());
            TaskListener<IOException> listener = client.httpv.executor().getExceptionListener();
            if (null != listener) {
                if (listener.listen(client, e) && null != client.onException) {
                    client.execute(() -> client.onException.on(this.webSocket, t), client.exceptionOnIO);
                }
            } else if (null != client.onException) {
                client.execute(() -> client.onException.on(this.webSocket, t), client.exceptionOnIO);
            } else if (!client.nothrow) {
                throw new InstrumentException("WebSocket exception", t);
            }
        }

    }

    /**
     * @author Kimi Liu
     * @version 6.2.1
     * @since JDK 1.8+
     */
    public static class Client extends CoverHttp<Client> {

        private Register<Results> onOpen;
        private Register<Throwable> onException;
        private Register<Message> onMessage;
        private Register<Close> onClosing;
        private Register<Close> onClosed;

        private boolean openOnIO;
        private boolean exceptionOnIO;
        private boolean messageOnIO;
        private boolean closingOnIO;
        private boolean closedOnIO;

        private int pingSeconds = -1;
        private int pongSeconds = -1;

        public Client(Httpv httpClient, String url) {
            super(httpClient, url);
        }

        /**
         * 设置心跳间隔
         * 覆盖原有的心跳模式，主要区别如下：
         * <p>
         * 1、客户端发送的任何消息都具有一次心跳作用
         * 2、服务器发送的任何消息都具有一次心跳作用
         * 3、若服务器超过 3 * pongSeconds 秒没有回复心跳，才判断心跳超时
         * 4、可指定心跳的具体内容（默认为空）
         *
         * @param pingSeconds 客户端心跳间隔秒数（0 表示不需要心跳）
         * @param pongSeconds 服务器心跳间隔秒数（0 表示不需要心跳）
         * @return this
         */
        public Client heatbeat(int pingSeconds, int pongSeconds) {
            if (pingSeconds < 0 || pongSeconds < 0) {
                throw new IllegalArgumentException("pingSeconds and pongSeconds must greater equal than 0!");
            }
            this.pingSeconds = pingSeconds;
            this.pongSeconds = pongSeconds;
            return this;
        }

        /**
         * 启动 WebSocket 监听
         *
         * @return WebSocket
         */
        public CoverWebSocket listen() {
            String bodyType = getBodyType();
            String msgType = Builder.FORM.equalsIgnoreCase(bodyType) ? Builder.JSON : bodyType;
            CoverWebSocket socket = new CoverWebSocket(httpv.executor(), msgType);
            registeTagTask(socket);
            httpv.preprocess(this, () -> {
                synchronized (socket) {
                    if (socket.cancelOrClosed) {
                        removeTagTask();
                    } else {
                        Request request = prepareRequest("GET");
                        httpv.webSocket(request, new Listener(this, socket));
                    }
                }
            }, skipPreproc, skipSerialPreproc);
            return socket;
        }

        private void execute(Runnable command, boolean onIo) {
            httpv.executor().execute(command, onIo);
        }

        /**
         * 连接打开监听
         *
         * @param onOpen 监听器
         * @return WebSocketCover
         */
        public Client setOnOpen(Register<Results> onOpen) {
            this.onOpen = onOpen;
            openOnIO = nextOnIO;
            nextOnIO = false;
            return this;
        }

        /**
         * 连接异常监听
         *
         * @param onException 监听器
         * @return WebSocketCover
         */
        public Client setOnException(Register<Throwable> onException) {
            this.onException = onException;
            exceptionOnIO = nextOnIO;
            nextOnIO = false;
            return this;
        }

        /**
         * 消息监听
         *
         * @param onMessage 监听器
         * @return WebSocketCover
         */
        public Client setOnMessage(Register<Message> onMessage) {
            this.onMessage = onMessage;
            messageOnIO = nextOnIO;
            nextOnIO = false;
            return this;
        }

        /**
         * 正在关闭监听
         *
         * @param onClosing 监听器
         * @return WebSocketCover
         */
        public Client setOnClosing(Register<Close> onClosing) {
            this.onClosing = onClosing;
            closingOnIO = nextOnIO;
            nextOnIO = false;
            return this;
        }

        /**
         * 已关闭监听（当连接被取消或发生异常时，也会走该回调）
         *
         * @param onClosed 监听器
         * @return WebSocketCover
         */
        public Client setOnClosed(Register<Close> onClosed) {
            this.onClosed = onClosed;
            closedOnIO = nextOnIO;
            nextOnIO = false;
            return this;
        }

        public int pingSeconds() {
            return pingSeconds;
        }

        public int pongSeconds() {
            return pongSeconds;
        }

    }

    /**
     * @author Kimi Liu
     * @version 6.2.1
     * @since JDK 1.8+
     */
    public static class Message extends AbstractBody {

        private String text;
        private ByteString bytes;

        public Message(String text, TaskExecutor taskExecutor, Charset charset) {
            super(taskExecutor, charset);
            this.text = text;
        }

        public Message(ByteString bytes, TaskExecutor taskExecutor, Charset charset) {
            super(taskExecutor, charset);
            this.bytes = bytes;
        }

        public boolean isText() {
            return null != text;
        }

        @Override
        public byte[] toBytes() {
            if (null != text) {
                return text.getBytes(org.aoju.bus.core.lang.Charset.UTF_8);
            }
            if (null != bytes) {
                return bytes.toByteArray();
            }
            return null;
        }

        @Override
        public String toString() {
            if (null != text) {
                return text;
            }
            if (null != bytes) {
                return bytes.utf8();
            }
            return null;
        }

        @Override
        public ByteString toByteString() {
            if (null != text) {
                return ByteString.encodeUtf8(text);
            }
            return bytes;
        }

        @Override
        public Reader toCharStream() {
            return new InputStreamReader(toByteStream());
        }

        @Override
        public InputStream toByteStream() {
            if (null != text) {
                return new ByteArrayInputStream(text.getBytes(org.aoju.bus.core.lang.Charset.UTF_8));
            }
            if (null != bytes) {
                ByteBuffer buffer = bytes.asByteBuffer();
                return new InputStream() {

                    @Override
                    public int read() {
                        if (buffer.hasRemaining()) {
                            return buffer.get();
                        }
                        return -1;
                    }
                };
            }
            return null;
        }

    }

}
