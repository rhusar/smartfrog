package org.smartfrog.services.hadoop.instances

import java.rmi.RemoteException
import org.apache.hadoop.conf.Configuration

import org.smartfrog.sfcore.common.SmartFrogException

import org.smartfrog.sfcore.prim.TerminationRecord

import org.apache.hadoop.mapred.MiniMRCluster
import org.smartfrog.sfcore.common.SmartFrogDeploymentException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.services.scripting.groovy.GRef

/**
 * This is a groovy class that can bring up a MiniDFS cluster.
 *
 * MiniMR will go away in Hadoop 0.23.
 */
class MiniMrClusterImpl extends MiniClusterImpl {

    /** {@value } */

    public static final String ATTR_JOB_TRACKER_PORT = "jobTrackerPort"
    /** {@value } */
    public static final String ATTR_TASK_TRACKER_PORT = "taskTrackerPort"
    /** {@value } */
    public static final String ATTR_DIRECTORY_COUNT = "directoryCount"

    /** {@value } */
    public static final String ATTR_NUM_TRACKER_TO_EXCLUDE = "numTrackerToExclude"

    protected LocalMRCluster cluster;

    @Override
    synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart()

        int jobTrackerPort = sfResolve(ATTR_JOB_TRACKER_PORT, 0, true);
        int taskTrackerPort = sfResolve(ATTR_TASK_TRACKER_PORT, 0, true);
        int nodeCount = sfResolve(ATTR_NODE_COUNT, 0, true);
        int numDir = sfResolve(ATTR_DIRECTORY_COUNT, 0, true);
        int numTrackerToExclude = sfResolve(ATTR_NUM_TRACKER_TO_EXCLUDE, 0, true);
        String fsuri = sfResolve(ATTR_FILESYSTEM_URI, "", true);
        if (!fsuri) {
            throw new SmartFrogResolutionException(sfCompleteNameSafe(),
                                                   new GRef(ATTR_FILESYSTEM_URI),
                                                   "Empty attribute " + ATTR_FILESYSTEM_URI);
        }
        String[] racks = resolveListToArray(ATTR_RACKS)
        String[] hosts = resolveListToArray(ATTR_HOSTS)
        Configuration conf = createAndCacheConfig()

        cluster = LocalMRCluster.createInstance(jobTrackerPort,
                                    taskTrackerPort,
                                    nodeCount,
                                    fsuri,
                                    numDir,
                                    racks,
                                    hosts,
                                    conf,
                                    numTrackerToExclude)
    }

    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        cluster?.shutdown()
        cluster = null
        super.sfTerminateWith(status)
    }


}
