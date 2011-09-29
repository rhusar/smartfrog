package org.smartfrog.services.hadoop.operations.test.system.instances


class MiniClusterTest extends InstanceTestBase {

    public void testMiniCluster() {
        expectSuccessfulTestRun(PACKAGE, "testMiniCluster")
    }
}
