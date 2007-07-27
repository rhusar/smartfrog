/** (C) Copyright Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.anubisdeployer;

import java.rmi.RemoteException;


import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public class ClusterCompoundImpl extends CompoundImpl implements Compound {

    private ClusterNode resourceManager = null;
    private String id = "";
    public static final String ATTR_CLUSTER_NODE_MANAGER = "clusterNodeManager";
    public static final String ATTR_SF_RESERVATION_ID = "sfReservationId";
    public static final String ATTR_SF_CLUSTER_NODE = "sfClusterNode";

    // /////////////////////////////////////////////////////
    //
    // Constructor method
    //
    // /////////////////////////////////////////////////////


    public ClusterCompoundImpl() throws RemoteException {
    }

    // /////////////////////////////////////////////////////
    //
    // Template methods
    //
    // /////////////////////////////////////////////////////


    public synchronized void sfDeployWith(Prim parent, Context comp) throws SmartFrogDeploymentException, RemoteException {
        super.sfDeployWith(parent, comp);

        ComponentDescription reservationInfo = null;

        try {
            reservationInfo = (ComponentDescription)(( sfResolve(ATTR_SF_CLUSTER_NODE, (ComponentDescription)null,true))).copy();
            resourceManager = (ClusterNode) sfResolve(ATTR_CLUSTER_NODE_MANAGER, true);
            id = (String) sfResolve(ATTR_SF_RESERVATION_ID, true);
        } catch (Throwable e) {
            throw (SmartFrogDeploymentException)
                    SmartFrogDeploymentException.forward("Error obtaining cluster node manager or required reservation", e);
        }

        /*
       * The resource reservation assumes that the resource manager is already running.
       * Hence it assumes that reserving the resources is OK as part of the deployment phase.
       * In this way the resources may be used in the start phase.
       */
        try {
            resourceManager.reserveResources(id, reservationInfo, this);
        } catch (Exception e) {
            try {
                resourceManager.releaseResources(id);
            } catch (Exception ex) {
                sfLog().ignore(ex);
            }
            throw (SmartFrogDeploymentException)
                    SmartFrogDeploymentException.forward("unable to reserve required resources", e);
        }
    }


    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
        try {
            resourceManager.releaseResources(id);
        } catch (Exception e) {
            sfLog().ignore(e);
        }
    }


}
