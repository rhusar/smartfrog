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
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.PrimProcessDeployerImpl;
import org.smartfrog.sfcore.reference.Reference;

/**
 * StorageRecoveryDeployerImpl is a deployer that reads the persisted
 * state of a component from its storage and recreates it. The persisted 
 * state is converted into a component description that is marked as 
 * a recovered component and then deployed. The deployer propagates transactions
 * through the description to make deployment an atomic action.
 */
public class StorageRecoveryDeployerImpl extends PrimProcessDeployerImpl {

    public StorageRecoveryDeployerImpl(ComponentDescription descr) {
        super(descr);
    }

    /**
     * This deployer assumes the description is a storage description and deploys the
     * component description found in the store referred to by the storage description.
     * 
     * {@inheritDoc}
     */
    public Prim deploy(Reference name, Prim parent, Context params) throws SmartFrogDeploymentException {
        target = preprocess(target);
        return super.deploy(name, parent, params);
    }

    private ComponentDescription preprocess(ComponentDescription desc) throws SmartFrogDeploymentException {

        try {

            /**
             * Obtain the context from the storage using the deployWith
             * transaction if there is one, and put the transaction in the
             * context. Also add a recovery marker to indicate this deployment
             * is a recovery from the stored context. If there is no sfClass
             * specified declare it the default RComponent implementation default.
             */
            Object recoveryMarker = RComponent.NORMAL_RECOVERY_MARKER_VALUE;
            if( desc.sfContainsAttribute(RComponent.RECOVERY_MARKER_ATTR) ) {
                recoveryMarker = desc.sfResolveHere(RComponent.RECOVERY_MARKER_ATTR);
            }            
            Transaction xact = (Transaction) desc.sfResolve(RComponent.SWEEP_TRANSACTION_ATTR, false);
            if (xact == null) {
                xact = Transaction.nullTransaction;
            }
            Storage storage = Storage.openStorage(desc);
            Context context = storage.getContext(xact);
            storage.close();
            context.sfReplaceAttribute(RComponent.SWEEP_TRANSACTION_ATTR, xact);
            context.sfReplaceAttribute(RComponent.RECOVERY_MARKER_ATTR, recoveryMarker);
            if( !context.containsKey(SmartFrogCoreKeys.SF_CLASS) ) {
                context.sfAddAttribute(SmartFrogCoreKeys.SF_CLASS, RComponent.DEFAULT_IMPL);
            }

            /**
             * Add a clone of the storage description to the component
             * without the sweep transaction or recovery marker. 
             */
            Context cc = (Context)desc.sfContext().clone();
            cc.remove(RComponent.SWEEP_TRANSACTION_ATTR);
            cc.remove(RComponent.RECOVERY_MARKER_ATTR);
            context.sfReplaceAttribute(RComponent.STORAGE_DATA_ATTR, new ComponentDescriptionImpl(null, cc, false));

            /**
             * Construct the component description from the context and parent
             * it to the orginal descriptions parent.
             */
            return new ComponentDescriptionImpl(desc.sfParent(), context, true);

        } catch (StorageNoSuchTableException ex) {
            throw new StorageDeploymentMissingException(ex);
        } catch (SmartFrogContextException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(
                    "Failed to deploy recoverable component from storage", ex);
        } catch (StorageException ex) {
            if (ex instanceof StorageAccessException) {
                throw new StorageDeploymentAccessException("Failed to deploy recoverable component from storage", ex);
            } else {
                throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(
                        "Failed to deploy recoverable component from storage", ex);
            }
        } catch (SmartFrogResolutionException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(
                    "Failed to deploy recoverable component from storage", ex);
        }
    }
}
