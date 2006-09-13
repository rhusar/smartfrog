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

import java.io.*;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Date;

import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisProvider;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

public class ClusterNodeManager extends PrimImpl implements Prim, ClusterNode {

    private Reference reservationsRef =
            new Reference(ReferencePart.here("reservations"));
    private Reference attributesRef =
            new Reference(ReferencePart.here("attributes"));
    private Reference resourcesRef =
            new Reference(ReferencePart.here("resources"));
    private Reference dataRef =
            new Reference(ReferencePart.here("data"));
    private Reference descriptionRef =
            new Reference(ReferencePart.here("description"));

    private String shutdownCommandLine, rebootCommandLine;

    private AnubisProvider idProvider = new AnubisProvider("Cluster");
    private AnubisProvider currentServiceProvider = null;
    private AnubisLocator anubis = null;

    private ComponentDescription nodeDescription = null;
    private String hostname = null;
    private String upSince = new Date().toString();

    // /////////////////////////////////////////////////////
    //
    // Constructor method
    //
    // /////////////////////////////////////////////////////

    public ClusterNodeManager() throws RemoteException {
    }

    // /////////////////////////////////////////////////////
    //
    // Template methods
    //
    // /////////////////////////////////////////////////////

    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        shutdownCommandLine = sfResolve("shutdownCommandLine", "", true);
        rebootCommandLine = sfResolve("rebootCommandLine", "", true);
        anubis = (AnubisLocator) sfResolve("anubisLocator");
    }

    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        publishNodeDescription();
        anubis.registerProvider(idProvider);
    }

    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            if (currentServiceProvider != null)
                anubis.deregisterProvider(currentServiceProvider);
        } catch (Exception e) {
        }
        try {
            anubis.deregisterProvider(idProvider);
        } catch (Exception e) {
        }
        super.sfTerminateWith(tr);
    }

    // /////////////////////////////////////////////////////
    //
    // Helper methods
    //
    // /////////////////////////////////////////////////////

    private void publishNodeDescription() throws SmartFrogException, RemoteException {
        if (nodeDescription == null)
            setHostNodeDescription(new ComponentDescriptionImpl(null, new ContextImpl(), true));
        else {
            idProvider.setValue(nodeDescription);
        }
    }

    // /////////////////////////////////////////////////////
    //
    // HostResource interface
    //
    // /////////////////////////////////////////////////////

    public synchronized void setHostNodeDescription(ComponentDescription nodeDescription)
            throws SmartFrogException, RemoteException {
        this.nodeDescription = nodeDescription;
        nodeDescription.sfAddAttribute(
                "hostname",
                (hostname = ((InetAddress) sfResolve("sfHost")).getCanonicalHostName()));
        nodeDescription.sfAddAttribute("upSince", upSince);
        nodeDescription.sfAddAttribute(
                "reservations",
                new ComponentDescriptionImpl(null, new ContextImpl(), true));
        // nodeDescriptionContext.put("interface", RemoteObject.toStub(this));

        publishNodeDescription();
    }

    public synchronized void reserveResources(
            String id,
            ComponentDescription reservationRequirement,
            Prim forComponent)
            throws SmartFrogException, RemoteException {

        ComponentDescription resources = (ComponentDescription) reservationRequirement.sfResolve(resourcesRef);
        ComponentDescription data = (ComponentDescription) reservationRequirement.sfResolve(dataRef);
        ComponentDescription description = (ComponentDescription) reservationRequirement.sfResolve(descriptionRef);

        boolean reservationOK = true;
        ComponentDescription nodeDescriptionCopy = (ComponentDescription) nodeDescription.copy();

        // if id already exists - fail
        if (((ComponentDescription) (nodeDescription.sfResolve(reservationsRef)))
                .sfContext()
                .contains("id")) {
            throw new SmartFrogDeploymentException(
                    "id "
                            + id
                            + " already exists in cluster node manager "
                            + sfCompleteNameSafe());
        }

        // if (check all resources in the resources part of the nodeDescription)
        //     commit resource changes
        //     add reservation to list
        //     publish the new nodeDescription
        //     return true
        // else
        //     return false
        ComponentDescription availableResources =
                (ComponentDescription) nodeDescriptionCopy.sfResolve(resourcesRef);
        ComponentDescription availableData =
                (ComponentDescription) nodeDescriptionCopy.sfResolve(dataRef);

        ClusterResourceMapper.reserveResouces(id, resources, availableResources, this, forComponent);
        ClusterResourceMapper.reserveData(id, data, availableData, this, forComponent);

        Context reservationInfo = new ContextImpl();
        reservationInfo.put("resources", resources);
        reservationInfo.put("data", data);
        reservationInfo.put("description", description);

        ((ComponentDescription) nodeDescriptionCopy.sfResolve(reservationsRef))
                .sfReplaceAttribute(id, new ComponentDescriptionImpl(null, reservationInfo, true));

        nodeDescription = nodeDescriptionCopy;
        publishNodeDescription();
    }


    public synchronized void releaseResources(String id)
            throws SmartFrogException, RemoteException {

        ComponentDescription reservations = null;
        ComponentDescription resources = null;
        ComponentDescription data = null;
        ComponentDescription nodeDescriptionCopy = null;

        try {
            reservations =
                    (ComponentDescription) ((ComponentDescription) nodeDescription
                            .sfResolve(reservationsRef))
                            .sfResolveHere(id, false);
            resources = (ComponentDescription) reservations.sfResolve(resourcesRef);
            data = (ComponentDescription) reservations.sfResolve(dataRef);

        } catch (Exception e) {
            resources = null;
            data = null;
        } finally {
            // no such reservation (perhaps should throw an exception...
            if (resources == null || data == null)
                return;
        }
        // remove the reservation
        ((ComponentDescription) (nodeDescription
                .sfResolve(reservationsRef)))
                .sfRemoveAttribute(id);

        nodeDescriptionCopy = (ComponentDescription) nodeDescription.copy();

        //free up the resources
        ComponentDescription availableResources =
                (ComponentDescription) nodeDescriptionCopy.sfResolve(resourcesRef);
        ComponentDescription availableData =
                (ComponentDescription) nodeDescriptionCopy.sfResolve(dataRef);

        ClusterResourceMapper.releaseResouces(resources, availableResources);
        ClusterResourceMapper.releaseData(data, availableData);

        nodeDescription = nodeDescriptionCopy;
        publishNodeDescription();

    }


    public void reboot() {
        new Thread() {
            public void run() {
                try {
                    Runtime.getRuntime().exec(rebootCommandLine);
                } catch (Exception e) {
                }
            }
        }
                .start();
    }

    public void shutdown() {
        new Thread() {
            public void run() {
                try {
                    Runtime.getRuntime().exec(shutdownCommandLine);
                } catch (Exception e) {
                }
            }
        }
                .start();
    }

}
