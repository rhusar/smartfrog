package org.smartfrog.services.cloudfarmer.test.client.web.webapp;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class StrutsWebappTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/webapp/";

    public StrutsWebappTest(String name) {
        super(name);
    }

    public void testStrutsHappy() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testStrutsHappy");
    }

    public void testTilesHappy() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testTilesHappy");
    }


}