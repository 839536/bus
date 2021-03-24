/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class for executing on the command line and returning the result of
 * execution.
 *
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Executor {

    private static final String[] DEFAULT_ENV = getDefaultEnv();

    private Executor() {

    }

    private static String[] getDefaultEnv() {
        Platform.OS platform = Platform.getCurrentPlatform();
        if (platform == Platform.OS.WINDOWS) {
            return new String[]{"LANGUAGE=C"};
        } else if (platform != Platform.OS.UNKNOWN) {
            return new String[]{"LC_ALL=C"};
        } else {
            return null;
        }
    }

    /**
     * Executes a command on the native command line and returns the result. This is
     * a convenience method to call {@link java.lang.Runtime#exec(String)} and
     * capture the resulting output in a list of Strings. On Windows, built-in
     * commands not associated with an executable program may require
     * {@code cmd.exe /c} to be prepended to the command.
     *
     * @param cmdToRun Command to run
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    public static List<String> runNative(String cmdToRun) {
        String[] cmd = cmdToRun.split(Symbol.SPACE);
        return runNative(cmd);
    }

    /**
     * Executes a command on the native command line and returns the result line by
     * line. This is a convenience method to call
     * {@link java.lang.Runtime#exec(String[])} and capture the resulting output in
     * a list of Strings. On Windows, built-in commands not associated with an
     * executable program may require the strings {@code cmd.exe} and {@code /c} to
     * be prepended to the array.
     *
     * @param cmdToRunWithArgs Command to run and args, in an array
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    public static List<String> runNative(String[] cmdToRunWithArgs) {
        return runNative(cmdToRunWithArgs, DEFAULT_ENV);
    }

    /**
     * Executes a command on the native command line and returns the result line by
     * line. This is a convenience method to call
     * {@link java.lang.Runtime#exec(String[])} and capture the resulting output in
     * a list of Strings. On Windows, built-in commands not associated with an
     * executable program may require the strings {@code cmd.exe} and {@code /c} to
     * be prepended to the array.
     *
     * @param cmdToRunWithArgs Command to run and args, in an array
     * @param envp             array of strings, each element of which has environment variable
     *                         settings in the format name=value, or null if the subprocess
     *                         should inherit the environment of the current process.
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    public static List<String> runNative(String[] cmdToRunWithArgs, String[] envp) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs, envp);
        } catch (SecurityException | IOException e) {
            Logger.trace("Couldn't run command {}: {}", Arrays.toString(cmdToRunWithArgs), e.getMessage());
            return new ArrayList<>(0);
        }

        ArrayList<String> sa = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {
            String line;
            while (null != (line = reader.readLine())) {
                sa.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            Logger.trace("Problem reading output from {}: {}", Arrays.toString(cmdToRunWithArgs), e.getMessage());
            return new ArrayList<>(0);
        } catch (InterruptedException ie) {
            Logger.trace("Interrupted while reading output from {}: {}", Arrays.toString(cmdToRunWithArgs),
                    ie.getMessage());
            Thread.currentThread().interrupt();
        }
        return sa;
    }

    /**
     * Return first line of response for selected command.
     *
     * @param cmd2launch String command to be launched
     * @return String or empty string if command failed
     */
    public static String getFirstAnswer(String cmd2launch) {
        return getAnswerAt(cmd2launch, 0);
    }

    /**
     * Return response on selected line index (0-based) after running selected
     * command.
     *
     * @param cmd2launch String command to be launched
     * @param answerIdx  int index of line in response of the command
     * @return String whole line in response or empty string if invalid index or
     * running of command fails
     */
    public static String getAnswerAt(String cmd2launch, int answerIdx) {
        List<String> sa = Executor.runNative(cmd2launch);

        if (answerIdx >= 0 && answerIdx < sa.size()) {
            return sa.get(answerIdx);
        }
        return Normal.EMPTY;
    }

}
