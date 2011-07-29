package org.smartfrog.services.hadoop.operations.test.system.operations.mr

import org.smartfrog.services.hadoop.operations.test.system.operations.dfs.DFSTestBase
import org.smartfrog.services.hadoop.operations.test.system.HadoopTestBase

class MrOperationsTest extends HadoopTestBase {

    protected String PACKAGE = "/org/smartfrog/services/hadoop/operations/test/system/operations/mr/"
    public void testDirectoryCreateAndDelete() {
        expectSuccessfulTestRun(PACKAGE, "testDirectoryCreateAndDelete")
    }

    public void testDfsLive() {
        expectSuccessfulTestRun(PACKAGE, "testDfsLive")
    }


}
