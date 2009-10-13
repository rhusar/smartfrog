package org.smartfrog.services.cloudfarmer.test.client.web.cluster;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class ClusterWebappTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/cluster/";

    public ClusterWebappTest(String name) {
        super(name);
    }

    public void testJettyDeployed() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testJettyDeployed");
    }
}
