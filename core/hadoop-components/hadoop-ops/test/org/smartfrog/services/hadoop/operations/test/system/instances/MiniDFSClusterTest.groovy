package org.smartfrog.services.hadoop.operations.test.system.instances


class MiniDFSClusterTest extends InstanceTestBase {


    public void testMicroDfsCluster() throws Throwable {
        expectSuccessfulTestRun(PACKAGE, "testMicroDfsCluster")
    }
}
