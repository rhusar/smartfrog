package org.smartfrog.services.cloudfarmer.test.client.web.webapp;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class JettyWebappTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/webapp/";

    public JettyWebappTest(String name) {
        super(name);
    }

    public void testJettyDeployed() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testJettyDeployed");
    }

    public void testJettySupportsJSP() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testJettySupportsJSP");
    }

    public void testJettyExecutesJSP() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testJettyExecutesJSP");
    }

}
