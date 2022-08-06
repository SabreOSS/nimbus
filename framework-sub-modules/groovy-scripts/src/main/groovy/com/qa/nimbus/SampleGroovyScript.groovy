package com.qa.nimbus

import com.eviware.soapui.model.testsuite.TestCaseRunContext
import com.eviware.soapui.model.testsuite.TestCaseRunner
import com.sabre.qa.nimbus.reporting.commons.NimbusReportUtils
import org.apache.logging.log4j.*


class SampleGroovyScript {

    TestCaseRunner testRunner;
    TestCaseRunContext context;
    Logger log;
    NimbusReportUtils nimbusReportUtils

    SampleGroovyScript(TestCaseRunner testRunner, TestCaseRunContext context, Logger log) {
        this.testRunner = testRunner
        this.context = context
        this.log = log
        nimbusReportUtils = new NimbusReportUtils(testRunner,context)
    }

    public void GroovyMethod(){
        log.info("Groovy Method")
        nimbusReportUtils.writePassLogToTestLogger("Passed")
    }

}
