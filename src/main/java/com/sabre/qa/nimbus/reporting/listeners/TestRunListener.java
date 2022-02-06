/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.reporting.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCaseRunner;
import com.eviware.soapui.impl.wsdl.teststeps.*;
import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.AssertionError;
import com.eviware.soapui.model.testsuite.*;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;
import com.eviware.soapui.support.StringUtils;
import com.sabre.qa.nimbus.constants.ApplicationConstants;
import com.sabre.qa.nimbus.datadrivencapability.DataDriver;
import com.sabre.qa.nimbus.reporting.extent.SoapUIExtentReportsIntegrationUtils;
import com.sabre.qa.nimbus.utils.properties.ProcessSystemProperties;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class that provides implementation for TestRunListenerAdapter interace which is required for writing custome soapui listeners
 *
 * @author Kiran Chitivolu
 */
public class TestRunListener extends TestRunListenerAdapter {
    public static final SoapUIExtentReportsIntegrationUtils reportUtil = SoapUIExtentReportsIntegrationUtils.getInstance();

    private ExtentTest getTestCaseLogger(TestCaseRunner testRunner) {
        return reportUtil.getTestCaseLoggerMap()
                .get(testRunner.getTestCase().getTestSuite().getName() + "#" + testRunner.getTestCase().getName());
    }

    private void logTestStepStatusToTestLogger(TestStepResult testStepResult, ExtentTest testCaseLogger,
                                               String logFileLink) {
        String log = ApplicationConstants.TEST_CASE_LOG_PREFIX + testStepResult.getTestStep().getName() + ApplicationConstants.TESTSET_LOG_POSTFIX
                + testStepResult.getStatus().toString() + ApplicationConstants.WITH_MESSAGE + logFileLink;
        if (testStepResult.getStatus().toString().equals(ApplicationConstants.UNKNOWN) || testStepResult.getStatus().toString().equals("OK")) {
            testCaseLogger.log(Status.PASS, log);
        } else {
            StringBuilder testStepMessageBuffer = new StringBuilder();
            for (String message : testStepResult.getMessages()) {
                testStepMessageBuffer.append(ApplicationConstants.HTML_LINE_BREAK).append(message);
            }
            testCaseLogger.log(Status.FAIL, log + ApplicationConstants.HTML_LINE_BREAK + testStepMessageBuffer.toString());
        }
    }

    private List<TestAssertion> getAssertionList(TestStepResult testStepResult) {
        List<TestAssertion> testAssertionList = null;
        if (testStepResult.getTestStep().getClass().equals(WsdlTestRequestStep.class)) {
            testAssertionList = ((WsdlTestRequestStep) testStepResult.getTestStep()).getAssertionList();
        } else if (testStepResult.getTestStep().getClass().equals(HttpTestRequestStep.class)) {
            testAssertionList = ((HttpTestRequestStep) testStepResult.getTestStep()).getAssertionList();
        } else if (testStepResult.getTestStep().getClass().equals(RestTestRequestStep.class)) {
            testAssertionList = ((RestTestRequestStep) testStepResult.getTestStep()).getAssertionList();
        } else if (testStepResult.getTestStep().getClass().equals(JdbcRequestTestStep.class)) {
            testAssertionList = ((JdbcRequestTestStep) testStepResult.getTestStep()).getAssertionList();
        }
        return testAssertionList;
    }

    private void logTestAssertionStatusToTestLogger(ExtentTest testCaseLogger, TestStepResult testStepResult) {
        List<TestAssertion> testAssertionList = getAssertionList(testStepResult);
        if (testAssertionList != null && !testAssertionList.isEmpty()) {
            for (TestAssertion testAssertion : testAssertionList) {
                if (testAssertion.getStatus().toString().equals("VALID")
                        || testAssertion.getStatus().toString().equals(ApplicationConstants.UNKNOWN)) {
                    testCaseLogger.log(Status.PASS, ApplicationConstants.TEST_STEP_ASSERTION + testAssertion.getName()
                            + ApplicationConstants.TESTSET_LOG_POSTFIX + testAssertion.getStatus().toString() + " ]");
                } else {
                    StringBuilder assertionErrorBuffer = new StringBuilder();
                    for (AssertionError error : testAssertion.getErrors()) {
                        assertionErrorBuffer.append(ApplicationConstants.HTML_LINE_BREAK).append(error.getMessage());
                    }
                    testCaseLogger.log(Status.FAIL,
                            ApplicationConstants.TEST_STEP_ASSERTION + testAssertion.getName() + ApplicationConstants.TESTSET_LOG_POSTFIX
                                    + testAssertion.getStatus().toString() + ApplicationConstants.WITH_ERROR_MESSAGE
                                    + assertionErrorBuffer);
                }
            }
        }
    }

    private String getTestStepOutPutLogFileNameFromCurrentStep(Long count, TestStep currentStep,
                                                               TestStepStatus testStepStatus) {
        String nameBase = "";
        String exportSeparator = System
                .getProperty(com.eviware.soapui.tools.SoapUITestCaseRunner.SOAPUI_EXPORT_SEPARATOR, "-");
        nameBase = StringUtils.createFileName(currentStep.getTestCase().getTestSuite().getName(), '_') + exportSeparator
                + StringUtils.createFileName(currentStep.getTestCase().getName(), '_') + exportSeparator
                + StringUtils.createFileName(currentStep.getName(), '_') + "-" + count.longValue() + "-"
                + testStepStatus;
        return nameBase;
    }

    private Long getStepCount(TestCaseRunContext runContext, String stepName) {
        String countPropertyName = stepName + " run count nimbus";
        Long count = (Long) runContext.getProperty(countPropertyName);
        if (count == null) {
            count = 0L;
        }
        runContext.setProperty(countPropertyName, Long.valueOf (count.longValue() + 1));
        return count;
    }

    private String getTestStepOutPutLogFileName(TestCaseRunContext runContext, TestStepResult testStepResult) {
        TestStep currentStep = runContext.getCurrentStep();
        Long count = getStepCount(runContext, currentStep.getName());
        String nameBase = getTestStepOutPutLogFileNameFromCurrentStep(count, currentStep, testStepResult.getStatus());
        WsdlTestCaseRunner callingTestCaseRunner = (WsdlTestCaseRunner) runContext
                .getProperty("#CallingTestCaseRunner#");
        if (callingTestCaseRunner != null) {
            nameBase = getTestStepOutPutLogFileNameUsingCallingTestCaseRunner(runContext, callingTestCaseRunner, count,
                    testStepResult.getStatus());
        }
        return nameBase;
    }

    private String getTestStepOutPutLogFileNameUsingCallingTestCaseRunner(TestCaseRunContext runContext,
                                                                          WsdlTestCaseRunner callingTestCaseRunner, Long count, TestStepStatus testStepStatus) {
        String nameBase = "";
        String exportSeparator = System
                .getProperty(com.eviware.soapui.tools.SoapUITestCaseRunner.SOAPUI_EXPORT_SEPARATOR, "-");
        TestStep currentStep = runContext.getCurrentStep();
        TestCase tc = currentStep.getTestCase();
        WsdlTestCase ctc = callingTestCaseRunner.getTestCase();
        WsdlRunTestCaseTestStep runTestCaseTestStep = (WsdlRunTestCaseTestStep) runContext
                .getProperty("#CallingRunTestCaseStep#");
        nameBase = StringUtils.createFileName(ctc.getTestSuite().getName(), '_') + exportSeparator
                + StringUtils.createFileName(ctc.getName(), '_') + exportSeparator
                + StringUtils.createFileName(runTestCaseTestStep.getName(), '_') + exportSeparator
                + StringUtils.createFileName(tc.getTestSuite().getName(), '_') + exportSeparator
                + StringUtils.createFileName(tc.getName(), '_') + exportSeparator
                + StringUtils.createFileName(currentStep.getName(), '_') + "-" + count.longValue() + "-"
                + testStepStatus;
        return nameBase;
    }

    private boolean isDataDrivenTest(TestCaseRunner testRunner) {
        return testRunner.getTestCase().getPropertyValue(ApplicationConstants.NIMBUS_DATA_DRIVEN_FLAG) != null && testRunner.getRunContext()
                .expand("${#TestCase#NimbusDataDrivenFlag}").equalsIgnoreCase("true");
    }

    private boolean shouldReportInitiationBeDoneBeforeRunningThisTestCase(TestCaseRunContext runContext) {
        return (runContext.getProperty("isNimbusRunningTestSuite") == null && !reportUtil.isDataDrivenTestStarted()
                && runContext.getProperty("nimbusReportInitiationDone") == null);
    }

    private boolean evaluateExcludePropertyToDetermineIfExecutionIsNeeded(TestCaseRunContext runContext) {
        boolean testCaseExcludeProperty = Boolean.parseBoolean(getSOPUIPropertyAfterEvaluatingTestCaseAndTestSuiteProperties(runContext, ApplicationConstants.NIMBUS_EXCLUDE));
        boolean executeNimbusExclude = Boolean.parseBoolean(System.getProperty(ApplicationConstants.EXECUTE_NIMBUS_EXCLUDE));
        return executeNimbusExclude || !testCaseExcludeProperty;
    }

    private boolean evaluateNimbusTagsToDetermineIfExecutionIsRequired(TestCaseRunContext runContext) {
        boolean returnFlag = true;
        String testCaseTags = getSOPUIPropertyAfterEvaluatingTestCaseAndTestSuiteProperties(runContext, "NimbusTags");
        String executionTags = System.getProperty("NimbusTags");
        if (executionTags != null && !executionTags.equalsIgnoreCase("")) {
            if (testCaseTags != null && !testCaseTags.equalsIgnoreCase("")) {
                List<String> testCaseTagsItems = Arrays.asList(testCaseTags.split(";"));
                List<String> executionTagsItems = Arrays.asList(executionTags.split(";"));
                returnFlag = !Collections.disjoint(testCaseTagsItems, executionTagsItems);
            } else {
                returnFlag = false;
            }
        }
        return returnFlag;
    }

    private String getSOPUIPropertyAfterEvaluatingTestCaseAndTestSuiteProperties(TestCaseRunContext runContext, String property) {
        String returnValue = "";
        String testCaseTags = runContext.expand("${#TestCase#" + property + "}");
        if (testCaseTags != null && !testCaseTags.equals("")) {
            returnValue = testCaseTags;
        } else {
            returnValue = runContext.expand("${#TestSuite#" + property + "}");
        }
        return returnValue;
    }

    @Override
    public void beforeRun(TestCaseRunner testRunner, TestCaseRunContext runContext) {
        if (shouldReportInitiationBeDoneBeforeRunningThisTestCase(runContext)) {
            reportUtil.beforeProjectRun(testRunner.getTestCase().getTestSuite().getProject());
            reportUtil.beforeTestSuiteRun(testRunner.getTestCase().getTestSuite().getName());
            ProcessSystemProperties.processNimbusSystemProperties(testRunner.getTestCase().getProject());
            runContext.setProperty("nimbusReportInitiationDone", "true");
            runContext.setProperty("nimbusReportInitiationTestCase",
                    testRunner.getTestCase().getTestSuite().getName() + "#" + testRunner.getTestCase().getName());
        }
        String testCaseOriginalName = testRunner.getTestCase().getName();
        if (isDataDrivenTest(testRunner) && !reportUtil.isDataDrivenTestStarted()) {
            reportUtil.setDataDrivenTestStarted(true);
            WsdlTestCase currentTestCase = ((WsdlTestCase) testRunner.getTestCase());
            testRunner.cancel("Cancelling primary execution as this is a data driven test");
            String dataSourcePath = runContext.expand("${#TestCase#NimbusDataSourcePath}");
            DataDriver dataDriver = new DataDriver();
            reportUtil.setTestSuiteStatus(dataDriver.performDataDrivenTestAndReturnTestSuiteStatus(currentTestCase,runContext.getProperties(),dataSourcePath));
            currentTestCase.setName(testCaseOriginalName);
            if (reportUtil.getTestSuiteStatus().equals(ApplicationConstants.MISSING_NIMBUS_COLUMNS)) {
                callReportUtilsBeforeTestCaseRunMethodAndSetTestCaseLoggerIntoRunContext(testRunner, runContext);
            }
            reportUtil.setDataDrivenTestStarted(false);
            reportUtil.setDataDrivenTestFinished(true);
        } else {
            if (evaluateExcludePropertyToDetermineIfExecutionIsNeeded(runContext) && evaluateNimbusTagsToDetermineIfExecutionIsRequired(runContext)) {
                callReportUtilsBeforeTestCaseRunMethodAndSetTestCaseLoggerIntoRunContext(testRunner, runContext);
            } else {
                testRunner.cancel("Cancelling execution based on execution tags/exclusion");
            }
        }
    }

    private void callReportUtilsBeforeTestCaseRunMethodAndSetTestCaseLoggerIntoRunContext(TestCaseRunner testRunner,
                                                                                          TestCaseRunContext runContext) {
        reportUtil.beforeTestCaseRun(testRunner.getTestCase().getTestSuite().getName(),
                testRunner.getTestCase().getName());
        runContext.setProperty("nimbusTestCaseLogger#" + testRunner.getTestCase().getTestSuite().getName() + "#"
                + testRunner.getTestCase().getName(), getTestCaseLogger(testRunner));
    }

    private boolean isCurrentTestNimbusInitiatedTestCase(TestCaseRunner testRunner, TestCaseRunContext runContext) {
        boolean returnFlag = false;
        if ((testRunner.getTestCase().getTestSuite().getName() + "#" + testRunner.getTestCase().getName())
                .equalsIgnoreCase((String) runContext.getProperty("nimbusReportInitiationTestCase"))) {
            returnFlag = true;
        }
        return returnFlag;
    }

    @Override
    public void afterRun(TestCaseRunner testCaseRunner, TestCaseRunContext runContext) {
        if (isDataDrivenTest(testCaseRunner)) {
            afterRunProcessIfItIsDataDrivenTest(testCaseRunner,runContext);
        } else {
            afterRunProcessIfItIsNotDataDrivenTest(testCaseRunner, runContext);
        }
        if (runContext.getProperty("isNimbusRunningTestSuite") == null) {
            if (isDataDrivenTest(testCaseRunner)) {
                afterRunProcessWhenNotRunningViaTestSuiteIfItIsDataDrivenTest(testCaseRunner, runContext);
            } else {
                afterRunProcessWhenNotRunningViaTestSuiteIfItIsNotDataDrivenTest(testCaseRunner, runContext);
            }
        }
    }

    private void afterRunProcessWhenNotRunningViaTestSuiteIfItIsNotDataDrivenTest(TestCaseRunner testCaseRunner, TestCaseRunContext runContext){
        if (isCurrentTestNimbusInitiatedTestCase(testCaseRunner, runContext)) {
            reportUtil.afterTestSuiteRun(testCaseRunner.getTestCase().getTestSuite().getName());
            reportUtil.afterProjectRun();
        }
    }

    private void afterRunProcessWhenNotRunningViaTestSuiteIfItIsDataDrivenTest(TestCaseRunner testCaseRunner, TestCaseRunContext runContext){
        if (reportUtil.isDataDrivenTestFinished()) {
            reportUtil.setDataDrivenTestFinished(false);
            if (isCurrentTestNimbusInitiatedTestCase(testCaseRunner, runContext)) {
                reportUtil.afterTestSuiteRun(testCaseRunner.getTestCase().getTestSuite().getName());
                reportUtil.afterProjectRun();
            }
        }
    }

    private void afterRunProcessIfItIsNotDataDrivenTest(TestCaseRunner testCaseRunner, TestCaseRunContext runContext){
        if (evaluateExcludePropertyToDetermineIfExecutionIsNeeded(runContext) && evaluateNimbusTagsToDetermineIfExecutionIsRequired(runContext)) {
            reportUtil.afterTestCaseRun(testCaseRunner.getTestCase().getTestSuite().getName(),
                    testCaseRunner.getTestCase().getName());
        }
    }

    private void afterRunProcessIfItIsDataDrivenTest(TestCaseRunner testCaseRunner, TestCaseRunContext runContext){
        if (reportUtil.isDataDrivenTestStarted()) {
            if (evaluateExcludePropertyToDetermineIfExecutionIsNeeded(runContext) && evaluateNimbusTagsToDetermineIfExecutionIsRequired(runContext)) {
                reportUtil.afterTestCaseRun(testCaseRunner.getTestCase().getTestSuite().getName(),
                        testCaseRunner.getTestCase().getName());
            }
        } else if (reportUtil.getTestSuiteStatus().equals(ApplicationConstants.MISSING_NIMBUS_COLUMNS)) {
            reportUtil.afterTestCaseRun(testCaseRunner.getTestCase().getTestSuite().getName(),
                    testCaseRunner.getTestCase().getName());
        }
    }

    @Override
    public void afterStep(TestCaseRunner testRunner, TestCaseRunContext runContext, TestStepResult testStepResult) {
        ExtentTest testCaseLogger = getTestCaseLogger(testRunner);
        if (testStepResult.getTestStep().getClass().equals(JdbcRequestTestStep.class)) {
            testCaseLogger.log(Status.INFO,
                    "JDBC Request Test Step [ " + testStepResult.getTestStep().getName() + " ] ResponseAsXML <br> <br>"
                            + StringEscapeUtils.escapeXml(
                            ((JdbcRequestTestStep) testStepResult.getTestStep()).getAssertableContentAsXml()));
        }
        logTestAssertionStatusToTestLogger(testCaseLogger, testStepResult);
        String nameBase = getTestStepOutPutLogFileName(runContext, testStepResult);
        String relativeOutputFolder = System.getProperty(ApplicationConstants.NIMBUS_OUTPUT_LOCATION,ApplicationConstants.NIMBUS_OUTPUT_LOCATION_DEFAULT_VALUE);;
        String fileName = relativeOutputFolder + File.separator + nameBase + ".txt";
        String logFileLink = "<a href=\"" + fileName + "\" target=\"_blank\">" + nameBase + ".txt</a>";
        logTestStepStatusToTestLogger(testStepResult, testCaseLogger, logFileLink);
    }
    
}
