package org.smartfrog.services.hadoop.operations.test.system.operations.dfs


class BasicDFSOperationsTest extends DFSTestBase {

    public void testDirectoryCreateAndDelete() {
        expectSuccessfulTestRun(PACKAGE, "testDirectoryCreateAndDelete")
    }

    public void NotestDfsLive() {
        expectSuccessfulTestRun(PACKAGE, "testDfsLive")
    }


}
