/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.reporting.listeners;

import com.eviware.soapui.model.support.TestSuiteRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestSuiteRunContext;
import com.eviware.soapui.model.testsuite.TestSuiteRunner;
import com.sabre.qa.nimbus.reporting.extent.SoapUIExtentReportsIntegrationUtils;
import com.sabre.qa.nimbus.utils.properties.ProcessSystemProperties;

/**
 * Class that provides implementation for TestSuiteRunListenerAdapter interace which is required for writing custome soapui listeners
 *
 * @author Kiran Chitivolu
 */
public class TestSuiteRunListener extends TestSuiteRunListenerAdapter {
    SoapUIExtentReportsIntegrationUtils reportUtil = SoapUIExtentReportsIntegrationUtils.getInstance();

    @Override
    public void beforeRun(TestSuiteRunner testRunner, TestSuiteRunContext runContext) {
        if(runContext.getProperty("isNimbusRunningProject") == null){
            reportUtil.beforeProjectRun(testRunner.getTestSuite().getProject());
            ProcessSystemProperties.processNimbusSystemProperties(testRunner.getTestSuite().getProject());
        }
        reportUtil.beforeTestSuiteRun(testRunner.getTestSuite().getName());
        runContext.setProperty("isNimbusRunningTestSuite", true);
    }

    @Override
    public void afterRun(TestSuiteRunner testRunner, TestSuiteRunContext runContext) {
        reportUtil.afterTestSuiteRun(testRunner.getTestSuite().getName());
        if(runContext.getProperty("isNimbusRunningProject") == null){
            reportUtil.afterProjectRun();
        }
    }
}
