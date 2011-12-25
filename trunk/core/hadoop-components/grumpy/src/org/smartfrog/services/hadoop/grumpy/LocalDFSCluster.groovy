package org.smartfrog.services.hadoop.grumpy

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption

/**
 * This is a minidfs cluster whose URI is that of the localhost.
 * It extends the Apache Hadoop MiniDFSCluster that has some bugs fixed in 0.23+; this makes things work better in
 * 0.20.20x
 */
class LocalDFSCluster extends MiniDFSCluster implements ClusterURI {

    private LocalDFSCluster(int nameNodePort, 
                            Configuration conf, 
                            int numDataNodes,
                            boolean format, 
                            boolean manageNameDfsDirs,
                            boolean manageDataDfsDirs,
                            StartupOption operation,
                            String[] racks,
                            String[] hosts,
                            long[] simulatedCapacities) {
        super(nameNodePort, conf, numDataNodes, format, manageNameDfsDirs, manageDataDfsDirs, operation, racks, hosts,
                simulatedCapacities)
    }

    @Override
    void close() {
        shutdown();
    }

    @Override
    String getURI() {
        return "hdfs://localhost:${getNameNode() ? getNameNodePort() : 0}/"
    }

    @Override
    String toString() {
        return getURI();
    }


    static LocalDFSCluster createInstance(int nameNodePort, Configuration conf, int numDataNodes, boolean format,
                                          boolean manageNameDfsDirs, boolean manageDataDfsDirs,
                                          StartupOption operation, String[] racks, String[] hosts,
                                          long[] simulatedCapacities) {

        patchTestDataDir(conf)
        return new LocalDFSCluster(nameNodePort, conf, numDataNodes, format, manageNameDfsDirs, manageDataDfsDirs, operation, racks, hosts,
                simulatedCapacities)
    }

    /**
     * Patch in the test data directory to the system properties. The cluster config value
     * takes precedence over the system property (which may be left over from a previous instance)
     * @param conf the configuration
     * @throws IllegalArgumentException if the property is missing from the cluster config and isn't set in a system
     * property.
     */
    private static void patchTestDataDir(Configuration conf) {
        String testDataDir = conf.get(ClusterConstants.TEST_DATA_DIR)
        if (!testDataDir) {
            if (!System.getProperty(ClusterConstants.TEST_DATA_DIR)) {
                throw new IllegalArgumentException("Missing Attribute in configuration: ${ClusterConstants.TEST_DATA_DIR}")
            }
        } else {
            System.setProperty(ClusterConstants.TEST_DATA_DIR, testDataDir)
        }
    }


}
