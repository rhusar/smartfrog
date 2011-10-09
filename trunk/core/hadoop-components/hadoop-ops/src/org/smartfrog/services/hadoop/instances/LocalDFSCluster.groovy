package org.smartfrog.services.hadoop.instances

import org.apache.hadoop.hdfs.MiniDFSCluster
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption

/**
 *
 */
class LocalDFSCluster extends MiniDFSCluster implements LocalCluster {

    private LocalDFSCluster(int nameNodePort, Configuration conf, int numDataNodes, boolean format, boolean manageNameDfsDirs, boolean manageDataDfsDirs, StartupOption operation, String[] racks, String[] hosts, long[] simulatedCapacities) {
        super(nameNodePort, conf, numDataNodes, format, manageNameDfsDirs, manageDataDfsDirs, operation, racks, hosts,
                simulatedCapacities)
    }

    @Override
    void close() {
        shutdown();
    }

    @Override
    String getURI() {
        return "hdfs://localhost:${getNameNode()?getNameNodePort():0}/"
    }

    @Override
    String toString() {
        return getURI();
    }


    static LocalDFSCluster createInstance(int nameNodePort, Configuration conf, int numDataNodes, boolean format, boolean manageNameDfsDirs, boolean manageDataDfsDirs, StartupOption operation, String[] racks, String[] hosts, long[] simulatedCapacities) {

        patchTestDataDir(conf)
        return new LocalDFSCluster(nameNodePort, conf, numDataNodes, format, manageNameDfsDirs, manageDataDfsDirs, operation, racks, hosts,
                simulatedCapacities)
    }

    private static def patchTestDataDir(Configuration conf) {
        System.setProperty(ClusterConstants.TEST_DATA_DIR, conf.get(ClusterConstants.TEST_DATA_DIR))
    }
}
