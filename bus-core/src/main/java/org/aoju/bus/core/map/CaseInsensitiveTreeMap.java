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
package org.aoju.bus.core.map;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 忽略大小写的{@link TreeMap}
 * 对KEY忽略大小写，get("Value")和get("value")获得的值相同，put进入的值也会被覆盖
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Kimi Liu
 * @version 6.5.0
 * @since Java 17+
 */
public class CaseInsensitiveTreeMap<K, V> extends CaseInsensitiveMap<K, V> {

    private static final long serialVersionUID = 1L;

    /**
     * 构造
     */
    public CaseInsensitiveTreeMap() {
        this((Comparator<? super K>) null);
    }

    /**
     * 构造
     *
     * @param map 初始Map
     */
    public CaseInsensitiveTreeMap(Map<? extends K, ? extends V> map) {
        this();
        this.putAll(map);
    }

    /**
     * 构造
     *
     * @param map 初始Map，键值对会被复制到新的TreeMap中
     */
    public CaseInsensitiveTreeMap(SortedMap<? extends K, ? extends V> map) {
        super(new TreeMap<K, V>(map));
    }

    /**
     * 构造
     *
     * @param comparator 比较器，{@code null}表示使用默认比较器
     */
    public CaseInsensitiveTreeMap(Comparator<? super K> comparator) {
        super(new TreeMap<>(comparator));
    }

}
