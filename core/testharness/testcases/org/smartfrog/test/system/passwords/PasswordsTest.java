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

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testDeploy() throws Throwable {
        runTestsToCompletion(FILES,"testDeploy");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testInline() throws Throwable {
        expectSuccessfulTestRun(FILES, "testInline");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testFile() throws Throwable {
        expectSuccessfulTestRun(FILES, "testFile");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testPropertyPassword() throws Throwable {
        expectSuccessfulTestRun(FILES, "testPropertyPassword");
    }


    /**
     * test case
     * @throws Throwable on failure
     */
    public void testPropertyPasswordUnset() throws Throwable {
        expectSuccessfulTestRun(FILES, "testPropertyPasswordUnset");
    }
}
