package org.smartfrog.services.anubisdeployer;

import java.rmi.RemoteException;


import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public class ClusterCompoundImpl extends CompoundImpl implements Compound {

    private ClusterNode resourceManager = null;
    private String id = "";

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

        System.out.println("deployed context");
        System.out.println(comp);
        ComponentDescription reservationInfo = null;

        try {
            reservationInfo = (ComponentDescription)((ComponentDescription) ((ComponentDescription) sfResolve("sfClusterNode", true))).copy();
            resourceManager = (ClusterNode) sfResolve("clusterNodeManager", true);
            id = (String) sfResolve("reservationId", true);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new SmartFrogDeploymentException("Error obtaining cluster node manager or required reservation", e);
        }

        /*
       * The resource reservation assumes that the resource manager is already running.
       * Hence it assumes that reserving the resources is OK as part of the deployment phase.
       * In this way the resources may be used in the start phase.
       */
        try {
            resourceManager.reserveResources(id, reservationInfo, this);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                resourceManager.releaseResources(id);
            } catch (Exception ex) {
            }
            throw new SmartFrogDeploymentException("unable to reserve required resources", e);
        }
    }


    public synchronized void sfTerminateWith(TerminationRecord tr) {
        super.sfTerminateWith(tr);
        try {
            resourceManager.releaseResources(id);
        } catch (Exception e) {
        }
    }


}
