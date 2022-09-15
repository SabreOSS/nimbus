/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.datadrivencapability;

import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.support.types.StringToObjectMap;
import com.sabre.qa.nimbus.constants.ApplicationConstants;
import com.sabre.qa.nimbus.exceptions.MissingNimbusColumNamesException;
import com.sabre.qa.nimbus.reporting.listeners.TestRunListener;
import com.sabre.qa.nimbus.utils.excel.ExcelUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Class that holds the logic for data driven testing
 *
 * @author Kiran Chitivolu
 */

public class DataDriver {

    public String performDataDrivenTestAndReturnTestSuiteStatus(WsdlTestCase currentTestCase, StringToObjectMap runContextProperties, String dataSource) {
        String testSetStatus = ApplicationConstants.FINISHED;
        ExcelUtil excelUtil = null;
        try {
            List<String> dataSourceItems = Arrays.asList(dataSource.split(ApplicationConstants.NIMBUS_DATASOURCE_PATH_TOKEN));
            String dataSourcePath = dataSourceItems.get(0);
            if (dataSourceItems.size() > 1) {
                List<String> sheetNamesList = Arrays.asList(dataSourceItems.get(1).split("~"));
                for (String sheetName : sheetNamesList) {
                    excelUtil = new ExcelUtil(dataSourcePath, sheetName);
                    testSetStatus = preformDataDrivenTest(currentTestCase, runContextProperties, excelUtil, testSetStatus);
                }
            } else {
                excelUtil = new ExcelUtil(dataSourcePath);
                testSetStatus = preformDataDrivenTest(currentTestCase, runContextProperties, excelUtil, testSetStatus);
            }
        } catch (MissingNimbusColumNamesException mnce) {
            mnce.printStackTrace();
            testSetStatus = ApplicationConstants.MISSING_NIMBUS_COLUMNS;
        }
        return testSetStatus;
    }

    private boolean areNimbusColumnsPresent(Map<String, String> rowHeaders) {
        return rowHeaders.containsKey(ApplicationConstants.NIMBUS_EXECUTION_FLAG) && rowHeaders.containsKey(ApplicationConstants.NIMBUS_TEST_CASE); //&& rowHeaders.containsKey(ApplicationConstants.NIMBUS_RESULT);
    }

    private boolean shouldThisRowBeExecuted(Map<String, String> rowData) {
        return rowData.get(ApplicationConstants.NIMBUS_EXECUTION_FLAG).equalsIgnoreCase("y") || rowData.get(ApplicationConstants.NIMBUS_EXECUTION_FLAG).equalsIgnoreCase("yes")
                || rowData.get(ApplicationConstants.NIMBUS_EXECUTION_FLAG).equalsIgnoreCase("true") || rowData.get(ApplicationConstants.NIMBUS_EXECUTION_FLAG).equalsIgnoreCase("");
    }

    public String preformDataDrivenTest(WsdlTestCase currentTestCase, StringToObjectMap runContextProperties, ExcelUtil excelUtil, String testSetStatus) throws MissingNimbusColumNamesException {
        int rowCount = excelUtil.getRowCount();
        Map<String, String> rowHeaders = excelUtil.getRowDataAlongWithHeaders(0);
        if (areNimbusColumnsPresent(rowHeaders)) {
            addTestCasePropertiesToCurrentTEstCase(rowHeaders,currentTestCase);
            for (int i = 1; i < rowCount; i++) {
                Map<String, String> rowData = excelUtil.getRowDataAlongWithHeaders(i);
                if (shouldThisRowBeExecuted(rowData)) {
                    currentTestCase.setName(rowData.get(ApplicationConstants.NIMBUS_TEST_CASE));
                    addTestCasePropertyValuesToCurrentTestCase(rowData, currentTestCase);
                    WsdlTestCaseRunner wsdlTestCaseRunner = currentTestCase.run(runContextProperties, false);
                    String testCaseExecutionStatus = "";
                    try {
                        testCaseExecutionStatus = TestRunListener.reportUtil.getTestCaseLoggerMap().get(currentTestCase.getTestSuite().getName() + "#" + currentTestCase.getName()).getStatus().toString();
                        if (! testCaseExecutionStatus.equals(ApplicationConstants.PASS)) {
                            testSetStatus = testCaseExecutionStatus;
                        }
                    } catch (Exception e) {
                        testCaseExecutionStatus = ApplicationConstants.CANCELLED;
                    }
                    //excelUtil.updateCellValue(i, ApplicationConstants.NIMBUS_RESULT, testCaseExecutionStatus);
                    //excelUtil.saveExcel();
                }
            }
            removeTestCasePropertiesFromCurrentTestCase(rowHeaders,currentTestCase);
            excelUtil.closeWorkbook();
        } else {
            throw new MissingNimbusColumNamesException(
                    ApplicationConstants.MISSING_COLUMNS_EXCEPTION_STATEMENT);
        }
        return testSetStatus;
    }

    private void addTestCasePropertyValuesToCurrentTestCase(Map<String, String> rowData, WsdlTestCase currentTestCase){
        for (Map.Entry<String, String> entry : rowData.entrySet()) {
            currentTestCase.setPropertyValue(entry.getKey(), entry.getValue());
        }
    }

    private void addTestCasePropertiesToCurrentTEstCase(Map<String, String> rowHeaders, WsdlTestCase currentTestCase){
        for (Map.Entry<String, String> entry : rowHeaders.entrySet()) {
            currentTestCase.addProperty(entry.getKey());
        }
    }

    private void removeTestCasePropertiesFromCurrentTestCase(Map<String, String> rowHeaders, WsdlTestCase currentTestCase){
        for (Map.Entry<String, String> entry : rowHeaders.entrySet()) {
            currentTestCase.removeProperty(entry.getKey());
        }
    }
}
