/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org OSHI and other contributors.                 *
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
 ********************************************************************************/
package org.aoju.bus.health.windows.drivers;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.windows.WmiQueryHandler;

/**
 * Utility to query WMI class {@code Win32_ComputerSystemProduct}
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Win32ComputerSystemProduct {

    private static final String WIN32_COMPUTER_SYSTEM_PRODUCT = "Win32_ComputerSystemProduct";

    private Win32ComputerSystemProduct() {
    }

    /**
     * Queries the Computer System.
     *
     * @return Assigned serial number of the software element.
     */
    public static WmiResult<ComputerSystemProductProperty> queryIdentifyingNumber() {
        WmiQuery<ComputerSystemProductProperty> identifyingNumberQuery = new WmiQuery<>(WIN32_COMPUTER_SYSTEM_PRODUCT,
                ComputerSystemProductProperty.class);
        return WmiQueryHandler.createInstance().queryWMI(identifyingNumberQuery);
    }

    /**
     * Computer System ID number
     */
    public enum ComputerSystemProductProperty {
        IDENTIFYINGNUMBER
    }

}
