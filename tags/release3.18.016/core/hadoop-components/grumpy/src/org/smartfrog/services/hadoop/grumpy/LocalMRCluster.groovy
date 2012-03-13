package org.smartfrog.services.hadoop.grumpy

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapred.MiniMRCluster

/**
 * This is a groovy class that represents a local MR cluster. It does the workarounds
 * needed to fix some assumptions about system properties in the Hadoop MR Cluster. 
 * (that's easier than forking) though it does mean that changes in the different
 * Hadoop versions can surface. 
 *
 * Changes.
 * <ol>
 *     <li>The conf must have the property LOG_DIR = "hadoop.log.dir"</li>
 * </ol>
 */
class LocalMRCluster extends MiniMRCluster implements ClusterURI {

    public static final String LOG_DIR = "hadoop.log.dir"

    Log LOG = LogFactory.getLog(LocalMRCluster.class)

    private LocalMRCluster(int numTaskTrackers, String fsURI, int numDir, String[] hosts, JobConf conf) {
        super(numTaskTrackers, fsURI, numDir, null, hosts, conf)
    }

    private LocalMRCluster(int numTaskTrackers, String fsURI, int numDir) {
        super(numTaskTrackers, fsURI, numDir)
    }

    private LocalMRCluster(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers, String fsURI, int numDir) {
        super(jobTrackerPort, taskTrackerPort, numTaskTrackers, fsURI, numDir)
    }


    private LocalMRCluster(int jobTrackerPort,
                           int taskTrackerPort,
                           int numTaskTrackers,
                           String namenode,
                           int numDir,
                           String[] racks,
                           String[] hosts,
                           JobConf conf,
                           int numTrackerToExclude) {
        super(jobTrackerPort,
              taskTrackerPort,
              numTaskTrackers,
              namenode,
              numDir,
              racks,
              hosts,
              null,
              conf,
              numTrackerToExclude)
    }

    @Override
    void close() {
        shutdown();
    }

    @Override
    String getURI() {
        return "http://localhost:" + jobTrackerPort + "/";
    }

    @Override
    String toString() {
        return URI;
    }

    static LocalMRCluster createInstance(int numTaskTrackers,
                                         String fsURI,
                                         int numDir,
                                         String[] hosts,
                                         JobConf conf) {
        patchLogDir(conf);
        return new LocalMRCluster(numTaskTrackers, fsURI, numDir, hosts, conf)
    }

    static LocalMRCluster createInstance(int jobTrackerPort, int taskTrackerPort, int numTaskTrackers,
                                         String namenode,
                                         int numDir,
                                         String[] racks,
                                         String[] hosts,
                                         JobConf conf,
                                         int numTrackerToExclude) {
        patchLogDir(conf)
        return new LocalMRCluster(jobTrackerPort, taskTrackerPort,
                                  numTaskTrackers,
                                  namenode,
                                  numDir,
                                  racks,
                                  hosts,
                                  conf,
                                  numTrackerToExclude);
    }

    /**
     * Deal with an issue in the JT by fixing the log dir.
     * <a href="https://issues.apache.org/jira/browse/MAPREDUCE-2785">
     *     MAPREDUCE-2785: MiniMR cluster thread crashes if no hadoop log dir set</a>
     * @param conf
     */
    private static void patchLogDir(JobConf conf) {
        System.setProperty(ClusterConstants.HADOOP_LOG_DIR,
                           conf.get(ClusterConstants.HADOOP_LOG_DIR,
                                    System.getProperty("java.io.tmpdir")))
    }

}
