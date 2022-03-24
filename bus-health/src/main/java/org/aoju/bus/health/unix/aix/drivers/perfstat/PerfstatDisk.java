/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.aix.drivers.perfstat;

import com.sun.jna.platform.unix.aix.Perfstat;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_disk_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_id_t;
import org.aoju.bus.core.annotation.ThreadSafe;

/**
 * Utility to query performance stats for disk_stats
 *
 * @author Kimi Liu
 * @version 6.5.0
 * @since Java 17+
 */
@ThreadSafe
public final class PerfstatDisk {

    private static final Perfstat PERF = Perfstat.INSTANCE;

    /**
     * Queries perfstat_disk for per-disk usage statistics
     *
     * @return an array of usage statistics
     */
    public static perfstat_disk_t[] queryDiskStats() {
        perfstat_disk_t diskStats = new perfstat_disk_t();
        // With null, null, ..., 0, returns total # of elements
        int total = PERF.perfstat_disk(null, null, diskStats.size(), 0);
        if (total > 0) {
            perfstat_disk_t[] statp = (perfstat_disk_t[]) diskStats.toArray(total);
            perfstat_id_t firstdiskStats = new perfstat_id_t(); // name is ""
            int ret = PERF.perfstat_disk(firstdiskStats, statp, diskStats.size(), total);
            if (ret > 0) {
                return statp;
            }
        }
        return new perfstat_disk_t[0];
    }

}
