package org.smartfrog.test.system.services.scripting.groovy

import org.smartfrog.services.scripting.groovy.GroovyTestBase

/**
 * Test that the groovy test base works
 */
class TestGroovyTestBase  extends GroovyTestBase {
    def PACKAGE = "/org/smartfrog/test/system/services/scripting/groovy/";


    public void testWorkerInstall() {
        expectSuccessfulTestRun(PACKAGE, "testInlineLifecycle")
    }

    public void testInlineLifecycleTerminating() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testInlineLifecycleTerminating");
    }

    public void testResourceTerminating() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testResourceTerminating");
    }


    public void testConditionTrue() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testConditionTrue");
    }

    public void testConditionFalse() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testConditionFalse");
    }

    public void testFailOnStartup() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testFailOnStartup");
    }

    public void testConditionScriptError() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testConditionScriptError");
    }
}

