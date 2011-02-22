package org.smartfrog.services.cloudfarmer.test.client.web.webapp;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class ConfServletTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/webapp/";

    public ConfServletTest(String name) {
        super(name);
    }

    public void testClusterServletHasNoMaster() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testClusterServletHasNoMaster");
    }

}