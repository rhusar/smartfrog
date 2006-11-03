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

import java.rmi.Remote;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

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

public interface ClusterNode extends Remote {


    /**
     * Set the data for the host to advertise, and that describes the host.
     *
     * @param data the additional attributes which describes the node
     *
     */
    public void setHostNodeDescription(ComponentDescription data)
	throws SmartFrogException, RemoteException;

    /**
     * Reserve some units of each of the reserrvable aspects
     * @param id an identifier to be used to indicate the set of reservations
     * @param info data information regarding the reservation
     * @param forComponent the component for which the resources are being reserved
     */
    public void reserveResources(String id, ComponentDescription info, Prim forComponent)
	throws SmartFrogException, RemoteException;

    /**
     *Release all reservations associated with an indetifier
     *
     *@param id the identifier
     */
    public void releaseResources(String id)
	throws SmartFrogException, RemoteException;



    /**
     * Reboot the host
     */
    public void reboot()
	throws RemoteException;

    /**
     * Shutdown the host
     */
    public void shutdown()
	throws RemoteException;

}
