package org.smartfrog.test.system.passwords;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.test.system.workflow.conditional.ConditionalTest;

/**
 * test delays
 */
public class PasswordsTest extends DeployingTestBase {

    protected static final String FILES = "org/smartfrog/test/system/passwords/";


    public PasswordsTest(String s) {
        super(s);
    }

    public void testDeploy() throws Throwable {
        runTestsToCompletion(FILES,"testDeploy");
    }

    public void testInline() throws Throwable {
        expectSuccessfulTestRun(FILES, "testInline");
    }

    public void testFile() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFile");
    }
}
