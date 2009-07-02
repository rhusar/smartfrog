package org.smartfrog.services.hadoop.test.system.local.clusterconf;

import org.smartfrog.services.hadoop.test.system.local.namenode.HadoopTestBase;

/**
 *
 */
public class ClusterDrivenFilesystemTest extends HadoopTestBase {
    public static final String PACKAGE = "/org/smartfrog/services/hadoop/test/system/local/clusterconf/";

    public ClusterDrivenFilesystemTest(String name) {
        super(name);
    }

   public void testClusteredJobTracker() throws Throwable {
        checkMapRedCluster();
        expectSuccessfulTestRunOrSkip(PACKAGE, "testClusteredJobTracker");
        enableFailOnPortCheck();
    }

}