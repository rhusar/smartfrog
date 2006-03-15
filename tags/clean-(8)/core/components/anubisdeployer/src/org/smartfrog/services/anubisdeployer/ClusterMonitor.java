package org.smartfrog.services.anubisdeployer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Control interface for the host-advertising component of the
 * resource manager which allows the resource manager to set the host
 * description data, and to reserve and release capabilities of the
 * host.
 *
 * A host is described in terms of three reservable aspects, set
 * if units that need reservation for a component to be deployed
 * on the resource. These are
 *
 *    cpu resources : an integer that represents some abstract compution unit
 *    storage resources : an integer that represents some abstract storage unit
 *    io resources : an integer that represents some abtrsact IO units
 *
 * Thes abstract units define the reservable aspects, however
 * arbitrary other attributes may be included for use by the
 * resource management decsisions.
 *
 */

public interface ClusterMonitor extends Remote {


    /*
     * Obtain the current resource information about the cluster
     * @return the current status
     */
    public ComponentDescription clusterStatus()
	throws RemoteException;

    /**
     * Register for notification in changes in cluster resource information
     *
     * @param classback the interface to notify of changes in status
     * @return the current status
     */
    public ComponentDescription registerForClusterStatus(ClusterStatus callback)
	throws RemoteException;

    /**
     * Deregister for notification in changes in cluster resource information
     *
     * @param classback the interface to remove from tbe notification
     */
    public void deregisterForClusterStatus(ClusterStatus callback)
	throws RemoteException;



   
}
