/*
   	MIT License

  	Copyright 2022 Sabre GLBL Inc.

	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package com.sabre.qa.nimbus.reporting.extent;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentKlovReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.eviware.soapui.SoapUI;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.settings.HttpSettings;
import com.mongodb.MongoClientURI;
import com.sabre.qa.nimbus.constants.ApplicationConstants;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds critical logic integrating soapui executions with extent reports
 *
 * @author Kiran Chitivolu
 */

public class SoapUIExtentReportsIntegrationUtils {
    private static SoapUIExtentReportsIntegrationUtils reportsUtilSingletonInstance = null;
    private ExtentReports extent;
    private Map<String, ExtentTest> testSetLoggerMap = new HashMap<>();
    private Map<String, ExtentTest> testCaseLoggerMap = new HashMap<>();
    private String testSuiteStatus = ApplicationConstants.TESTSUITE_STATUS;
    private boolean dataDrivenTestStarted = false;
    private boolean dataDrivenTestFinished = false;

    public String getTestSuiteStatus() {
        return testSuiteStatus;
    }

    public void setTestSuiteStatus(String testSuiteStatus) {
        this.testSuiteStatus = testSuiteStatus;
    }

    public ExtentReports getExtent() {
        return extent;
    }

    public void setExtent(ExtentReports extent) {
        this.extent = extent;
    }


    public Map<String, ExtentTest> getTestSetLoggerMap() {
        return testSetLoggerMap;
    }

    public void setTestSetLoggerMap(Map<String, ExtentTest> testSetLoggerMap) {
        this.testSetLoggerMap = testSetLoggerMap;
    }

    public Map<String, ExtentTest> getTestCaseLoggerMap() {
        return testCaseLoggerMap;
    }

    public void setTestCaseLoggerMap(Map<String, ExtentTest> testCaseLoggerMap) {
        this.testCaseLoggerMap = testCaseLoggerMap;
    }

    public boolean isDataDrivenTestStarted() {
        return dataDrivenTestStarted;
    }

    public void setDataDrivenTestStarted(boolean dataDrivenTestStarted) {
        this.dataDrivenTestStarted = dataDrivenTestStarted;
    }

    public boolean isDataDrivenTestFinished() {
        return dataDrivenTestFinished;
    }

    public void setDataDrivenTestFinished(boolean dataDrivenTestFinished) {
        this.dataDrivenTestFinished = dataDrivenTestFinished;
    }

    private SoapUIExtentReportsIntegrationUtils() {
        initializeVariables();
    }

    public static SoapUIExtentReportsIntegrationUtils getInstance() {
        if (reportsUtilSingletonInstance == null) {
            reportsUtilSingletonInstance = new SoapUIExtentReportsIntegrationUtils();
        }
        return reportsUtilSingletonInstance;
    }

    public static boolean isInstanceAvailable() {
        return reportsUtilSingletonInstance == null;
    }


    private void initializeVariables() {
    	dataDrivenTestStarted = false;
        setTestSuiteStatus(ApplicationConstants.TESTSUITE_STATUS);
    }

    public void beforeProjectRun(Project project) {
        SoapUI.getSettings().setBoolean(HttpSettings.RESPONSE_COMPRESSION, false);
        String nimbusReport = ApplicationConstants.NIMBUS_EXTENT_REPORT_FILENAME;
        new File(Paths.get(nimbusReport).getParent().toString()).mkdirs();
        ExtentSparkReporter nimbusHtmlReporter = new ExtentSparkReporter(nimbusReport);
        try {
            nimbusHtmlReporter.loadXMLConfig(ApplicationConstants.NIMBUS_EXTENT_REPORT_CONFIG_FILE);
        } catch(Exception e){
            e.printStackTrace();
        }
        nimbusHtmlReporter.config().setReportName(project.getProject().getName());
        extent = new ExtentReports();
        if (Boolean.parseBoolean(System.getProperty(ApplicationConstants.NIMBUS_KLOV, ApplicationConstants.NIMBUS_KLOV_DEFAULT_VALUE))) {
            String mongoDBURI = System.getProperty(ApplicationConstants.NIMBUS_KLOV_MONGODB_URI, ApplicationConstants.NIMBUS_KLOV_MONGODB_URI_DEFAULT_VALUE);
            String klovURL = System.getProperty(ApplicationConstants.NIMBUS_KLOV_URL,ApplicationConstants.NIMBUS_KLOV_URL_DEFAULT_VALUE);
            String projectName = project.getProject().getName();
            ExtentKlovReporter nimbusKlovReporter = new ExtentKlovReporter();
            MongoClientURI mongoClientURI = new MongoClientURI(mongoDBURI);
            nimbusKlovReporter.initMongoDbConnection(mongoClientURI);
            nimbusKlovReporter.setProjectName(projectName);
            nimbusKlovReporter.setReportName(projectName);
            nimbusKlovReporter.initKlovServerConnection(klovURL);
            extent.attachReporter(nimbusHtmlReporter, nimbusKlovReporter);
        } else {
            extent.attachReporter(nimbusHtmlReporter);
        }

        extent.setAnalysisStrategy(AnalysisStrategy.SUITE);
    }


    public void afterProjectRun() {
        extent.flush();
    }

    public String getTestStepMessages(TestStepResult testStepResult) {
        StringBuilder testStepMessageBuffer = new StringBuilder();
        for (String message : testStepResult.getMessages()) {
            testStepMessageBuffer.append(ApplicationConstants.HTML_LINE_BREAK).append(message);
        }
        return testStepMessageBuffer.toString();
    }

    public void beforeTestSuiteRun(String testSuiteName) {
        ExtentTest testSetLogger = extent.createTest(testSuiteName);
        testSetLoggerMap.put(testSuiteName, testSetLogger);
        testSetLogger.assignCategory("TestSets");
    }

    public void afterTestSuiteRun(String testSuiteName) {
        ExtentTest testSuite = testSetLoggerMap.get(testSuiteName);
        if(testSuite.getModel().hasChildren()) {
            if (testSuite.getStatus() == Status.PASS) {
                testSuite.log(Status.PASS, ApplicationConstants.TEST_SET_LOG_PREFIX + testSuiteName + ApplicationConstants.TESTSET_LOG_POSTFIX + testSuite.getStatus().toString() + " ]");
            } else {
                testSuite.log(Status.FAIL, ApplicationConstants.TEST_SET_LOG_PREFIX + testSuiteName + ApplicationConstants.TESTSET_LOG_POSTFIX + testSuite.getStatus().toString() + " ]");
            }
        } else {
            extent.removeTest(testSuite);
        }
    }

    public void beforeTestCaseRun(String testSuiteName, String testCaseName) {
        ExtentTest testCaseLogger = testSetLoggerMap.get(testSuiteName).createNode(testCaseName);
        testCaseLoggerMap.put(testSuiteName + "#" + testCaseName, testCaseLogger);
        testCaseLogger.assignCategory("TestCases");
    }

    public void afterTestCaseRun(String testSuiteName, String testCaseName) {
        ExtentTest testCase = testCaseLoggerMap.get(testSuiteName + "#" + testCaseName);
        testCase.log(testCase.getStatus() == Status.PASS ? Status.PASS : Status.FAIL, ApplicationConstants.TEST_SET_LOG_PREFIX + testSuiteName + ApplicationConstants.TESTSET_LOG_POSTFIX + testCase.getStatus().toString() + " ]");
    }

}
