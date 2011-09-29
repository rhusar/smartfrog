package org.smartfrog.services.hadoop.test.system.local.clusterconf;

import org.smartfrog.services.hadoop.test.system.local.namenode.HadoopTestBase;

/**
 *
 */
public class ClusterDrivenJobSubmitTest extends HadoopTestBase {
    public static final String PACKAGE = "/org/smartfrog/services/hadoop/test/system/local/clusterconf/";

    public ClusterDrivenJobSubmitTest(String name) {
        super(name);
    }


    public void testClusteredJobSubmit() throws Throwable {
        checkMapRedCluster();
        expectSuccessfulTestRunOrSkip(PACKAGE, "testClusteredJobSubmit");
        enableFailOnPortCheck();
    }
}
