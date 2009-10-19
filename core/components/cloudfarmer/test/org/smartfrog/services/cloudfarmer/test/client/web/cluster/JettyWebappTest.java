package org.smartfrog.services.cloudfarmer.test.client.web.cluster;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class JettyWebappTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/cluster/";

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

    public void testStrutsHappy() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testStrutsHappy");
    }
}