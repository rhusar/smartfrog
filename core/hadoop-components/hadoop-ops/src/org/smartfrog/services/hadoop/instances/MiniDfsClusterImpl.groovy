package org.smartfrog.services.hadoop.instances

import java.rmi.RemoteException
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption
import org.smartfrog.services.hadoop.operations.dfs.HdfsStartupOptionFactory
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.prim.TerminationRecord
import org.smartfrog.sfcore.utils.ListUtils
import org.smartfrog.services.scripting.groovy.GRef

/**
 * This is a groovy class that can bring up a MiniDFS cluster
 */
class MiniDfsClusterImpl extends MiniClusterImpl {

    public static final String ATTR_NAMENODE_PORT = "namenodePort"
    public static final String ATTR_DATA_NODE_COUNT = "dataNodeCount"
    public static final String ATTR_FORMAT = "format"
    public static final String ATTR_MANAGE_NAME_DFS_DIRS = "manageNameDfsDirs"
    public static final String ATTR_MANAGE_DATA_DFS_DIRS = "manageDataDfsDirs"
    public static final String ATTR_STARTUP_OPTION = "startupOption"
    public static final String ATTR_HOSTS = "hosts"
    public static final String ATTR_RACKS = "racks"
    public static final String ATTR_SIMULATED_CAPACITIES = "simulatedCapacities"
    MiniDFSCluster cluster;

    @Override
    synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart()
        int nameNodePort = sfResolve(ATTR_NAMENODE_PORT, 0, true)
        Configuration conf = createConfiguration()
        int numDataNodes = sfResolve(ATTR_DATA_NODE_COUNT, 0, true)
        boolean format = sfResolve(ATTR_FORMAT, true, true)
        boolean manageNameDfsDirs = sfResolve(ATTR_MANAGE_NAME_DFS_DIRS, true, true)
        boolean manageDataDfsDirs = sfResolve(ATTR_MANAGE_DATA_DFS_DIRS, true, true)
        GRef startupRef = new GRef(ATTR_STARTUP_OPTION)
        String startupOption = sfResolve(startupRef, "", true)
        StartupOption operation = null
        if (startupOption) {
            operation = HdfsStartupOptionFactory.createStartupOption(startupOption)
            if (operation == null) {
                throw SmartFrogResolutionException.generic(startupRef,
                        this.sfCompleteNameSafe(),
                        "Unsupported operation \"${startupOption}\"")
            }
        }
        String[] racks = null
        List<String> list = ListUtils.resolveStringList(this, new GRef(ATTR_RACKS), true);
        if (!list.empty) {
            racks = list.stringify();
        }
        String[] hosts = null
        list = ListUtils.resolveStringList(this, new GRef(ATTR_HOSTS), true);
        if (!list.empty) {
            hosts = list.stringify();
        }
        long[] simulatedCapacities = null
        Vector<?> vector = null;
        vector = sfResolve(new GRef(ATTR_SIMULATED_CAPACITIES), new Vector<Long>(), true);
        if (!vector.empty) {
            simulatedCapacities = new long[vector.size()];
            int counter = 0;
            vector.each { elt ->
                simulatedCapacities[counter++] = (Long)elt;
            }
        }

        cluster = new MiniDFSCluster(
                nameNodePort,
                conf,
                numDataNodes,
                format,
                manageNameDfsDirs,
                manageDataDfsDirs,
                operation,
                racks,
                hosts,
                simulatedCapacities)
    }



    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status)
        cluster?.shutdown()
        cluster = null
    }


}
