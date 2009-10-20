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

    public void testClusterList() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testClusterList");
    }

    public void testClusterAdd() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testClusterAdd");
    }

    public void testClusterAdmin() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testClusterAdmin");
    }

    public void testViewHost() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testViewHost");
    }

    public void testClusterTerminate() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testClusterTerminate");
    }

    public void testChangeManager() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testChangeManager");
    }

}
