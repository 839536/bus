/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.office.support.excel.reader;

import org.aoju.bus.core.toolkit.CollKit;
import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.office.support.excel.ExcelKit;
import org.aoju.bus.office.support.excel.RowKit;
import org.aoju.bus.office.support.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象{@link Sheet}数据读取实现
 *
 * @param <T> 读取类型
 * @author Kimi Liu
 * @version 6.2.5
 * @since JDK 1.8+
 */
public abstract class AbstractSheetReader<T> implements SheetReader<T> {

    /**
     * 读取起始行（包含，从0开始计数）
     */
    protected final int startRowIndex;
    /**
     * 读取结束行（包含，从0开始计数）
     */
    protected final int endRowIndex;
    /**
     * 是否忽略空行
     */
    protected boolean ignoreEmptyRow = true;
    /**
     * 单元格值处理接口
     */
    protected CellEditor cellEditor;
    /**
     * 标题别名
     */
    private Map<String, String> headerAlias = new HashMap<>();

    /**
     * 构造
     *
     * @param startRowIndex 起始行（包含，从0开始计数）
     * @param endRowIndex   结束行（包含，从0开始计数）
     */
    public AbstractSheetReader(int startRowIndex, int endRowIndex) {
        this.startRowIndex = startRowIndex;
        this.endRowIndex = endRowIndex;
    }

    /**
     * 设置单元格值处理逻辑
     * 当Excel中的值并不能满足我们的读取要求时，通过传入一个编辑接口，可以对单元格值自定义，例如对数字和日期类型值转换为字符串等
     *
     * @param cellEditor 单元格值处理接口
     */
    public void setCellEditor(CellEditor cellEditor) {
        this.cellEditor = cellEditor;
    }

    /**
     * 设置是否忽略空行
     *
     * @param ignoreEmptyRow 是否忽略空行
     */
    public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
        this.ignoreEmptyRow = ignoreEmptyRow;
    }

    /**
     * 设置标题行的别名Map
     *
     * @param headerAlias 别名Map
     */
    public void setHeaderAlias(Map<String, String> headerAlias) {
        this.headerAlias = headerAlias;
    }

    /**
     * 增加标题别名
     *
     * @param header 标题
     * @param alias  别名
     */
    public void addHeaderAlias(String header, String alias) {
        this.headerAlias.put(header, alias);
    }

    /**
     * 转换标题别名，如果没有别名则使用原标题，当标题为空时，列号对应的字母便是header
     *
     * @param headerList 原标题列表
     * @return 转换别名列表
     */
    protected List<String> aliasHeader(List<Object> headerList) {
        if (CollKit.isEmpty(headerList)) {
            return new ArrayList<>(0);
        }

        final int size = headerList.size();
        final ArrayList<String> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(aliasHeader(headerList.get(i), i));
        }
        return result;
    }

    /**
     * 转换标题别名，如果没有别名则使用原标题，当标题为空时，列号对应的字母便是header
     *
     * @param headerObj 原标题
     * @param index     标题所在列号，当标题为空时，列号对应的字母便是header
     * @return 转换别名列表
     */
    protected String aliasHeader(Object headerObj, int index) {
        if (null == headerObj) {
            return ExcelKit.indexToColName(index);
        }

        final String header = headerObj.toString();
        return ObjectKit.defaultIfNull(this.headerAlias.get(header), header);
    }

    /**
     * 读取某一行数据
     *
     * @param sheet    {@link Sheet}
     * @param rowIndex 行号，从0开始
     * @return 一行数据
     */
    protected List<Object> readRow(Sheet sheet, int rowIndex) {
        return RowKit.readRow(sheet.getRow(rowIndex), this.cellEditor);
    }

}
