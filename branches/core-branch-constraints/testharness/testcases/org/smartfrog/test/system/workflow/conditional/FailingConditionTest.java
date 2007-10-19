package org.smartfrog.test.system.workflow.conditional;

import org.smartfrog.test.DeployingTestBase;

/**
 * test delays
 */
public class FailingConditionTest extends DeployingTestBase {

    protected static final String FILES = "org/smartfrog/test/system/workflow/conditional/";
    private static final String WAITFOR_FAILED = "waitfor failed";

    public FailingConditionTest(String s) {
        super(s);
    }

    public void testFailingWaitFor() throws Throwable {
        expectAbnormalTestRun(FILES, "testFailingWaitFor", false, WAITFOR_FAILED);
    }
}
