/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
package org.smartfrog.services.cloudfarmer.client.web.model.components;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.cloudfarmer.client.web.model.cluster.ClusterController;

import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.ConnectException;
import java.io.IOException;

/**
 * This is a singleton for keeping some of the core model entities which have to be shared across every session.
 * 
 */

public class StrutsModelSingletonsImpl extends PrimImpl implements Remote {

    public StrutsModelSingletonsImpl() throws RemoteException {
    }


    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    @Override
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        super.sfTerminatedWith(status, comp);
        setClusterController(null);
    }

    private static ClusterController clusterController = null;

    /**
     * Get any existing cluster controller
     * @return the cluster controller
     */
    public static synchronized ClusterController getClusterController() {
        return clusterController;
    }

    /**
     * Update the cluster controller
     * @param clusterController the new cluster controller
     */
    public static synchronized void setClusterController(ClusterController clusterController) {
        StrutsModelSingletonsImpl.clusterController = clusterController;
    }

    /**
     * Update the cluster controller in an atomic operation. The controller is only updated if the
     * original value is null. Do not use this if you are switching controllers
     * @param newController the new cluster controller
     * @return the cluster controller you should be using. 
     */
    public static synchronized ClusterController initClusterController(ClusterController newController) {
        if (clusterController == null) {
            clusterController = newController;
        }
        return clusterController;
    }

    /**
     * Atomic bind and init operation. If an exception is thrown, binding failed, and the singleton
     * cluster controller is still null
     * @param newController the new cluster controller
     * @return the cluster controller you should be using. 
     * @throws IOException IO problems
     * @throws SmartFrogException SF problems
     */
    public static synchronized ClusterController initAndBindClusterController(ClusterController newController)
            throws IOException,SmartFrogException  {
        if (clusterController != null) {
            //existing controller is already present
            return clusterController ;
        }
        ClusterController.bind(newController);
        return initClusterController(newController);
    }
}
