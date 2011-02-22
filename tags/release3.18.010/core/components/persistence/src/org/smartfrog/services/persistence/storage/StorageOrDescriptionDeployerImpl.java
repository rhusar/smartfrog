/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.persistence.storage;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.Reference;


/**
 * StorageOrDescriptionDeployerImpl is a deployer that deploys a component from
 * storage if it is already persisted, or from the description if it is not.
 */
public class StorageOrDescriptionDeployerImpl extends PrimProcessDeployerImpl {

    public StorageOrDescriptionDeployerImpl(ComponentDescription descr) {
        super(descr);
        // TODO Auto-generated constructor stub
    }

    /**
     * This deployer looks to see if the description to deploy contains a storage description. If it does
     * it attempts to deploy it from the component stored in the store referred to by the storage 
     * description. If that does not work it goes on to deploy the description as usual. The intention
     * is to recover a component from its storage if it has been previously successfully deployed, but
     * to deploy it as usual if this is the first attempt.
     * 
     * {@inheritDoc}
     */
    public Prim deploy(Reference name, Prim parent, Context params) throws SmartFrogDeploymentException {

        try {
            Object obj = target.sfResolve(RComponent.STORAGE_DATA_ATTR, (Object) null, false);
            if (Storage.isStorageDescription(obj)) {
                System.out.println("Attempting to deploy " + name + " from storage");
                return SFProcess.getProcessCompound().sfDeployComponentDescription(name, parent,
                        (ComponentDescription) obj, null);
            }
        } catch (Exception ex) {
            // TODO should we drop quietly? - better to check why it failed
            // either there is no storage data or there is no storage - both are
            // ok.
            // actually maybe there should be storage data - this deployer
            // should only be used with it.
            // could there be something else that should be a failure?
        }

        System.out.println("Attempting to deploy " + name + " from component description");
        return super.deploy(name, parent, params);
    }

}
