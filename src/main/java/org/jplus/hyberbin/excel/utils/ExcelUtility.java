/*
 * Copyright 2015 www.hyberbin.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Email:hyberbin@qq.com
 */
package org.jplus.hyberbin.excel.utils;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hyberbin on 14-1-21.
 */
public class ExcelUtility {
    public static void copyCell(Cell srcCell, Cell distCell) {
        distCell.setCellStyle(srcCell.getCellStyle());
        if (srcCell.getCellComment() != null) {
            distCell.setCellComment(srcCell.getCellComment());
        }
        int srcCellType = srcCell.getCellType();
        distCell.setCellType(srcCellType);

        if (srcCellType == Cell.CELL_TYPE_NUMERIC) {
            if (DateUtil.isCellDateFormatted(srcCell)) {
                distCell.setCellValue(srcCell.getDateCellValue());
            } else {
                distCell.setCellValue(srcCell.getNumericCellValue());
            }
        } else if (srcCellType == Cell.CELL_TYPE_STRING) {
            distCell.setCellValue(srcCell.getRichStringCellValue());
        } else if (srcCellType == Cell.CELL_TYPE_BLANK) {
            //nothing
        } else if (srcCellType == Cell.CELL_TYPE_BOOLEAN) {
            distCell.setCellValue(srcCell.getBooleanCellValue());
        } else if (srcCellType == Cell.CELL_TYPE_ERROR) {
            distCell.setCellErrorValue(srcCell.getErrorCellValue());
        } else if (srcCellType == Cell.CELL_TYPE_FORMULA) {
            distCell.setCellFormula(srcCell.getCellFormula());
        } else {
            //nothing
        }
    }

    //to same sheet
    public static void copyRows(Sheet st, int startRow, int endRow, int pPosition) {
        int pStartRow = startRow;
        int pEndRow = endRow;
        int targetRowFrom;
        int targetRowTo;
        int columnCount;
        CellRangeAddress region = null;
        int i;
        int j;
        for (i = 0; i < st.getNumMergedRegions(); i++) {
            region = st.getMergedRegion(i);
            if ((region.getFirstRow() >= pStartRow) && (region.getLastRow() <= pEndRow)) {
                targetRowFrom = region.getFirstRow() - pStartRow + pPosition;
                targetRowTo = region.getLastRow() - pStartRow + pPosition;

                CellRangeAddress newRegion = region.copy();

                newRegion.setFirstRow(targetRowFrom);
                newRegion.setFirstColumn(region.getFirstColumn());
                newRegion.setLastRow(targetRowTo);
                newRegion.setLastColumn(region.getLastColumn());
                st.addMergedRegion(newRegion);
            }
        }
        //set the column height and value
        for (i = pStartRow; i <= pEndRow; i++) {
            Row sourceRow = st.getRow(i);
            columnCount = sourceRow.getLastCellNum();
            if (sourceRow != null) {
                Row newRow = st.createRow(pPosition + i);
                newRow.setHeight(sourceRow.getHeight());
                for (j = 0; j < columnCount; j++) {
                    Cell templateCell = sourceRow.getCell(j);
                    if (templateCell != null) {
                        Cell newCell = newRow.createCell(j);
                        copyCell(templateCell, newCell);
                    }
                }
            }
        }
    }

    public static String getCellContent(Cell cell) {
        String content = "";
        if (cell == null) {
            //System.out.println("### cell is null");
            return "";
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = null;
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        sdf = new SimpleDateFormat("HH:mm");
                    } else {// 日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    content = sdf.format(date);
                } else {
                    DecimalFormat dft = new DecimalFormat("#0.00#");
                    content = dft.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_STRING:
                content = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                content = cell.getBooleanCellValue() + "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                content = cell.getCellFormula();
                break;
            default:
                break;
        }
        return content.trim();
    }
}