package org.smartfrog.test.system.passwords;

import org.smartfrog.test.DeployingTestBase;

/**
 * test delays
 */
public class DeployPasswordsTest extends DeployingTestBase {

    protected static final String FILES = "org/smartfrog/test/system/passwords/";


    public DeployPasswordsTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testDeploy() throws Throwable {
        runTestsToCompletion(FILES,"testDeploy");
    }
}
