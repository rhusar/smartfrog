package org.smartfrog.services.hadoop.instances

import java.rmi.RemoteException
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hdfs.MiniDFSCluster
import org.apache.hadoop.hdfs.server.common.HdfsConstants.StartupOption
import org.smartfrog.services.hadoop.operations.dfs.HdfsStartupOptionFactory
import org.smartfrog.sfcore.common.SmartFrogException
import org.smartfrog.sfcore.common.SmartFrogResolutionException
import org.smartfrog.sfcore.prim.TerminationRecord
import org.smartfrog.services.scripting.groovy.GRef

/**
 * This is a groovy class that can bring up a MiniDFS cluster
 */
class MiniMrClusterImpl extends MiniClusterImpl {


    MiniDFSCluster cluster;

    @Override
    synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart()

  /*      String startupOption = sfResolve(startupRef, "", true)
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
                simulatedCapacities)*/
    }

    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status)
        cluster?.shutdown()
        cluster = null
    }


}
