/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.reporting.commons;

import com.aventstack.extentreports.ExtentTest;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.aventstack.extentreports.Status;

/**
 * Class that has methods that allow users to write back to extent report
 *
 * @author Kiran Chitivolu
 */

public class NimbusReportUtils {
    TestCaseRunner testRunner;
    TestCaseRunContext context;
    
    public NimbusReportUtils(TestCaseRunner testRunner, TestCaseRunContext context) {
        this.testRunner = testRunner;
        this.context = context;
    }
    
    public Object returnTestCaseLogger() {
        return context.getProperty("nimbusTestCaseLogger#" + testRunner.getTestCase().getTestSuite().getName() + "#" + testRunner.getTestCase().getName());
    }

    public void writeInfoLogToTestLogger(String statement) {
        ExtentTest testLogger = (ExtentTest) returnTestCaseLogger();
         testLogger.log(Status.INFO,statement);
    }

    public void writeWariningLogToTestLogger(String statement) {
        ExtentTest testLogger = (ExtentTest) returnTestCaseLogger();
         testLogger.log(Status.WARNING,statement);
    }

    public void writePassLogToTestLogger(String statement) {
        ExtentTest testLogger = (ExtentTest) returnTestCaseLogger();
         testLogger.log(Status.PASS,statement);
    }

    public void writeFailLogToTestLogger(String statement) {
        ExtentTest testLogger = (ExtentTest) returnTestCaseLogger();
        testLogger.log(Status.FAIL,statement);
    }

}
