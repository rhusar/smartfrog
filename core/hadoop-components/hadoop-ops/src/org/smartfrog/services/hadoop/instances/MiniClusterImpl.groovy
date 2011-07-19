package org.smartfrog.services.hadoop.instances

import org.smartfrog.sfcore.prim.PrimImpl
import org.smartfrog.services.hadoop.operations.core.HadoopComponentImpl
import java.rmi.RemoteException
import org.smartfrog.sfcore.common.SmartFrogException

/**
 * This class is the Groovy base class for the MiniDFSCluster and MiniMRCluster
 */
class MiniClusterImpl extends HadoopComponentImpl {

    MiniClusterImpl() {
    }

    @Override
    synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart()
    }


}
