package org.smartfrog.services.hadoop.instances

import java.rmi.RemoteException
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption
import org.smartfrog.services.hadoop.operations.dfs.HdfsStartupOptionFactory
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.prim.TerminationRecord

/**
 * This is a groovy class that can bring up a MiniDFS cluster
 */
class MiniDfsClusterImpl extends MiniClusterImpl {

    MiniDFSCluster cluster;

    @Override
    synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart()
        int nameNodePort = sfResolve("namenodePort", 0, true)
        Configuration conf = createConfiguration()
        int numDataNodes = sfResolve("dataNodeCount", 0, true)
        boolean format = sfResolve("format", true, true)
        boolean manageNameDfsDirs = sfResolve("manageNameDfsDirs", true, true)
        boolean manageDataDfsDirs = sfResolve("manageDataDfsDirs", true, true)
        org.smartfrog.sfcore.reference.Reference startupRef = new org.smartfrog.sfcore.reference.Reference("startupOption")
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
        String[] hosts = null
        long[] simulatedCapacities = null

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
