/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP
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


package org.smartfrog.services.persistence.deployer;

import org.smartfrog.services.persistence.recoverablecomponent.RComponent;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.reference.Reference;

public class StorageRecoveryDeployerImpl extends PrimProcessDeployerImpl {

    public StorageRecoveryDeployerImpl( ComponentDescription descr ) {
        super( descr );
    }

    public Prim deploy( Reference name, Prim parent, Context params ) throws
            SmartFrogDeploymentException {
        target = preprocess( target );
        return super.deploy( name, parent, params );
    }


    private ComponentDescription preprocess( ComponentDescription desc ) throws
            SmartFrogDeploymentException {

        try {

            /**
             * Construct a new component description from the storage
             */
            Storage storage = Storage.createExistingStorage( desc );
            Object[] v = storage.getEntries();
            ContextImpl cntxt = new ContextImpl();
            for ( int i = 0; i < v.length; i++ ) {
                String entryname = ( String ) v[i];
                cntxt.sfAddAttribute( entryname, storage.getEntry( entryname ) );
                storage.commit();
            }

            /**
             * the recovered component will want its storage, but storage can
             * not be serialized, so pass a reference instead. It will have to
             * re-create the storage itself.
             */
            cntxt.sfAddAttribute( RComponent.STORAGEATTRIB, storage.getStorageRef() );
            storage.close();

            /**
             * Construct the component description from the context and
             * parent it to the orginal descriptions parent.
             */
            return new ComponentDescriptionImpl( desc.sfParent(), cntxt, true );

        } catch ( SmartFrogContextException ex ) {
            throw ( SmartFrogDeploymentException ) SmartFrogDeploymentException.forward(
                    "Failed to deploy recoverable component from storage", ex );
        } catch ( StorageException ex ) {
            throw ( SmartFrogDeploymentException ) SmartFrogDeploymentException.forward(
                    "Failed to deploy recoverable component from storage", ex );
        }
    }
}
