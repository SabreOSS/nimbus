/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.utils.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 * Class that holds the logic for reading and writing to an excel work book
 *
 * @author Kiran Chitivolu
 */
public class ExcelUtil {
    private String filePath;
    private String sheetName;
    private int sheetIndex = 0;
    private Workbook workbook;
    private Sheet worksheet;
    private InputStream inputStream;

    public ExcelUtil(String filePath, String sheetName) {
        try {
            this.filePath = filePath;
            this.sheetName = sheetName;
            this.inputStream =  new FileInputStream(filePath);
            setWorkbookUsingFilePath();
            setWorksheet();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ExcelUtil(String filePath, int sheetIndex) {
        try {
            this.filePath = filePath;
            this.sheetIndex = sheetIndex;
            this.inputStream =  new FileInputStream(filePath);
            setWorkbookUsingFilePath();
            setWorksheet();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ExcelUtil(String filePath) {
        try {
            this.filePath = filePath;
            this.inputStream =  new FileInputStream(filePath);
            setWorkbookUsingFilePath();
            setWorksheet();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setWorkbookUsingFilePath() {
        try {
            this.workbook = WorkbookFactory.create(inputStream);
            inputStream.close();
        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }

    }

    private void setWorksheet() {
        if(sheetName != null && sheetName != "") {
            workbook.forEach(sheet -> {
                if(sheet.getSheetName().equalsIgnoreCase(sheetName)) {
                    worksheet= sheet;
                }
            });
        }
        if(worksheet == null) {
            worksheet = workbook.getSheetAt(sheetIndex);
        }
    }

    private List<String> getRowHeader(){
        return getRowData(0);
    }

    private Row getRow(int rowNumber) {
        Row returnRow = null;
        int rowCounter = 0;
        for (Row row: worksheet) {
            if(rowNumber == rowCounter) {
                returnRow= row;
                break;
            }
            rowCounter++;
        }
        return returnRow;
    }

    private int getColumnNumberFromHeaderName(String columnName) {
        int columnNumber = 0;
        List<String> rowHeaderList = getRowHeader();
        for(String rowHeader : rowHeaderList) {
            if(rowHeader.equalsIgnoreCase(columnName)) {
                return columnNumber;
            }
            columnNumber = columnNumber + 1;
        }

        return -1;
    }

    private List<String> getRowData(int rowNumber) {
        List<String> rowData =  new ArrayList<String>();
        DataFormatter dataFormatter = new DataFormatter();
        Row row = getRow(rowNumber);
        for(int i=row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            rowData.add(dataFormatter.formatCellValue(cell,evaluator));
        }
        return rowData;
    }

    public int getRowCount() {
        return worksheet.getPhysicalNumberOfRows();
    }

    public Map<String,String> getRowDataAlongWithHeaders(int rowNumber) {
        List<String> headerData = getRowHeader();
        List<String> rowData = getRowData(rowNumber);
        Map<String, String> rowDataMappedWithHeaders= new HashMap<String, String>();
        for(int i=0; i<headerData.size(); i++) {
            try {
                rowDataMappedWithHeaders.put(headerData.get(i), rowData.get(i));
            } catch(IndexOutOfBoundsException e) {
                rowDataMappedWithHeaders.put(headerData.get(i), "");
            }
        }
        return rowDataMappedWithHeaders;
    }

    public void updateCellValue(int rowNumber, String headerName, String cellValue) {
        int columnNumberAssociatedWithHeaderName = getColumnNumberFromHeaderName(headerName);
        Row row = getRow(rowNumber);
        Cell cell = row.getCell(row.getFirstCellNum() + columnNumberAssociatedWithHeaderName, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellValue(cellValue);
    }

    public void saveExcel() {
        try {
            FileOutputStream  fileOutPutStream = new FileOutputStream(filePath);
            workbook.write(fileOutPutStream);
            fileOutPutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void closeWorkbook() {
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {

        ExcelUtil excelUtil = new ExcelUtil("samples/data/DataDrivenTestCase_Data.xlsx","TaggedDataDrivenTestCase");
        Map<String, String> rowDataMappedWithHeaders1= excelUtil.getRowDataAlongWithHeaders(2);
        for (Map.Entry<String, String> entry : rowDataMappedWithHeaders1.entrySet()) {
            System.out.println(entry.getKey() + "##" + entry.getValue());
        }
        System.out.println(excelUtil.getRowCount());
        excelUtil.closeWorkbook();
    }
}
