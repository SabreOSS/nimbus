/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.constants;

/**
 * Class that holds all literals used in the project
 *
 * @author Kiran Chitivolu
 */

public class ApplicationConstants {
    public static final String NIMBUS_KLOV = "nimbus.klov";
    public static final String NIMBUS_KLOV_MONGODB_URI = "nimbus.klov.mongodb.uri";
    public static final String NIMBUS_KLOV_URL = "nimbus.klov.url";
    public static final String NIMBUS_KLOV_DEFAULT_VALUE = "false";
    public static final String NIMBUS_KLOV_MONGODB_URI_DEFAULT_VALUE = "mongodb://localhost:27017";
    public static final String NIMBUS_KLOV_URL_DEFAULT_VALUE = "http://localhost:7777";
    public static final String TESTSUITE_STATUS = "FINISHED";
    public static final String TEST_SET_LOG_PREFIX = "Test Set [ ";
    public static final String TESTSET_LOG_POSTFIX = " ] completed with status [";
    public static final String TEST_CASE_LOG_PREFIX = "Test Case [ ";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String WITH_MESSAGE = " ] with messages : ";
    public static final String NIMBUS_OUTPUT_LOCATION = "nimbus.output.location";
    public static final String NIMBUS_OUTPUT_LOCATION_DEFAULT_VALUE = "req_res_files";
    public static final String NIMBUS_DATA_DRIVEN_FLAG = "NimbusDataDrivenFlag";
    public static final String NIMBUS_EXCLUDE = "NimbusExclude";
    public static final String EXECUTE_NIMBUS_EXCLUDE = "ExecuteNimbusExclude";
    public static final String TEST_STEP_ASSERTION = "Test Step Assertion [ ";
    public static final String WITH_ERROR_MESSAGE = " ] with error message : ";
    public static final String MISSING_COLUMNS_EXCEPTION_STATEMENT = "Specified datasource doesn't have one or more of NIMBUS data driven columns Nimbus_Execution_Flag/Nimbus_TestCase, please add them to the datasource and reexecute";
    public static final String NIMBUS_EXTENT_REPORT_FILENAME = "reports/NimbusExtentReport_SoapUI.html";

    private ApplicationConstants(){

    }
    public static final String NIMBUS_EXECUTION_FLAG = "Nimbus_Execution_Flag";
    public static final String MISSING_NIMBUS_COLUMNS = "MISSING_NIMBUS_COLUMNS";
    public static final String NIMBUS_TEST_CASE = "Nimbus_TestCase";
    public static final String FINISHED = "FINISHED";
    public static final String NIMBUS_DATASOURCE_PATH_TOKEN = "\\|";
    public static final String PASS = "pass";
    public static final String CANCELLED = "cancelled";
    public static final String NIMBUS_EXTENT_REPORT_CONFIG_FILE = System.getProperty("user.dir") + "/config/extent-reports/extent-config.xml";
    public static final String HTML_LINE_BREAK = "<br />";
}
