package org.smartfrog.services.hadoop.operations.test.system.instances


class MiniDFSClusterTest extends InstanceTestBase {

    public void testMicroDfsCluster() {
        expectSuccessfulTestRun(PACKAGE, "testMicroDfsCluster")
    }
}
