/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.core.codec.provider;

import org.aoju.bus.core.codec.Decoder;
import org.aoju.bus.core.codec.Encoder;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.StringKit;

/**
 * Base16（Hex）编码解码器
 * 十六进制（简写为hex或下标16）在数学中是一种逢16进1的进位制，一般用数字0到9和字母A到F表示（其中:A~F即10~15）
 * 例如十进制数57，在二进制写作111001，在16进制写作39
 *
 * @author Kimi Liu
 * @version 6.5.0
 * @since Java 17+
 */
public class Base16Provider implements Encoder<byte[], char[]>, Decoder<CharSequence, byte[]> {

    /**
     * 字符信息
     */
    private final char[] alphabets;

    /**
     * 构造
     *
     * @param lowerCase 是否小写
     */
    public Base16Provider(boolean lowerCase) {
        this.alphabets = (lowerCase ? "0123456789abcdef" : "0123456789ABCDEF").toCharArray();
    }

    /**
     * 将十六进制字符转换成一个整数
     *
     * @param ch    十六进制char
     * @param index 十六进制字符在字符数组中的位置
     * @return 一个整数
     * @throws InstrumentException 当ch不是一个合法的十六进制字符时，抛出运行时异常
     */
    private static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit < 0) {
            throw new InstrumentException("Illegal hexadecimal character {} at index {}", ch, index);
        }
        return digit;
    }

    @Override
    public char[] encode(byte[] data) {
        final int len = data.length;
        final char[] out = new char[len << 1];// len*2
        // two characters from the hex value.
        for (int i = 0, j = 0; i < len; i++) {
            out[j++] = alphabets[(0xF0 & data[i]) >>> 4];// 高位
            out[j++] = alphabets[0x0F & data[i]];// 低位
        }
        return out;
    }

    @Override
    public byte[] decode(CharSequence encoded) {
        if (StringKit.isEmpty(encoded)) {
            return null;
        }

        encoded = StringKit.cleanBlank(encoded);
        int len = encoded.length();

        if ((len & 0x01) != 0) {
            // 如果提供的数据是奇数长度，则前面补0凑偶数
            encoded = "0" + encoded;
            len = encoded.length();
        }

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(encoded.charAt(j), j) << 4;
            j++;
            f = f | toDigit(encoded.charAt(j), j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * 将指定char值转换为Unicode字符串形式，常用于特殊字符（例如汉字）转Unicode形式
     * 转换的字符串如果u后不足4位，则前面用0填充，例如：
     *
     * <pre>
     * '你' =》'\u4f60'
     * </pre>
     *
     * @param ch char值
     * @return Unicode表现形式
     */
    public String toUnicodeHex(char ch) {
        return "\\u" +
                alphabets[(ch >> 12) & 15] +
                alphabets[(ch >> 8) & 15] +
                alphabets[(ch >> 4) & 15] +
                alphabets[(ch) & 15];
    }

    /**
     * 将byte值转为16进制并添加到{@link StringBuilder}中
     *
     * @param builder {@link StringBuilder}
     * @param b       byte
     */
    public void appendHex(StringBuilder builder, byte b) {
        int high = (b & 0xf0) >>> 4;// 高位
        int low = b & 0x0f;// 低位
        builder.append(alphabets[high]);
        builder.append(alphabets[low]);
    }

}
