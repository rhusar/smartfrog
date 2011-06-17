package org.smartfrog.test.system.workflow.conditional;

import org.smartfrog.test.DeployingTestBase;

/**
 * test delays
 */
public class FailingConditionTest extends DeployingTestBase {

    protected static final String FILES = "org/smartfrog/test/system/workflow/conditional/";

    public FailingConditionTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testFailingWaitFor() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFailingWaitFor");
    }
}
