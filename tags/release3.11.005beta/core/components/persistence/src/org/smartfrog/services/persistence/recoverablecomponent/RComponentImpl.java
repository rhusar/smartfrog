/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.persistence.recoverablecomponent;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.services.persistence.model.CommitPoints;
import org.smartfrog.services.persistence.model.PersistenceModel;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.StorageRef;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

/**
 * <p>RComponentImpl implements the persistent component, with behavior that is
 * parameterized by a defined PersistenceModel, and persistence mechanism that
 * is implemented by a defined Storage. The component inserts control points
 * throughout start up and termination life cycle of CompoundImpl and in its
 * attribute manipulation methods to allow the persistence model to influence
 * when attributes are persisted and which attributes to persist.</p>
 *
 * <p>By default all attributes are serialized to
 * storage on startup and when they are added or replaced. Removed attributes
 * are removed from storage. The persistence model influences this behavior by
 * identifying attributes that should or should not be persisted - i.e. by
 * providing a marking for volatile and non-volatile attributes that is checked
 * before persisting an attribute.</p>
 *
 * <p>"Commit points" are added to the  begining and end of the CompoundImpl
 * life cycle methods (i.e. before and after sfDeploy, before and after sfStart,
 * etc.) Commits to storage are suspended at the begining of the startup life
 * cycle (in sfDeployWith) and only applied at these commit points. Again,
 * the persistence model  provides a marking to determine if each commit point
 * applies, hence commit points may be skipped. The affect is that changes to
 * attributes are committed atomically at given points in the component's life
 * cycle.</p>
 *
 * <p>The last control points are found immediately prior to component
 * initialisation in the sfDeployWith(...) method and in the new sfRecover()
 * life cycle method. At initialisation the persistence model is given the
 * opportunity to modify the context and implement any other initialisation
 * task. In the sfRecover() method (called instead of sfDeploy() and sfStart()
 * on recovery) the model is given to opportunity to determine if sfDeploy()
 * and sfStart() should in fact be called for this recovery.</p>
 *
 * <p>So, component startup contists of constructing the persistence model
 * and storage, giving the persistence model a chance to modify the context,
 * suspending commits to storage and then performing slightly different tasks
 * depending on whether the component is in initial deployment or recovery. On
 * initial deployment non-volatile attributes in the context are written to
 * storage (writes that are committed at the next applicable commit point), but
 * on recovery they are not (the context was just read from storage!) After
 * initialisation the component will be subject to its normal startup life cycle
 * if in its initial deployment, or it will be subject to the recovery life cycle
 * if it is being recovered from storage. In either case, the writes to storage
 * will only be committed at the points specified by the persistence model.</p>
 *
 * <p>Othewise the RComponentImpl class does not change the behavior of the
 * CompoundImpl class.</p>
 */
public class RComponentImpl extends CompoundImpl implements RComponent,
        CommitPoints, Serializable {


    private Storage stableLog;
    private PersistenceModel model;
    protected boolean sfIsRecovered = false;


    /**
     *
     * @throws RemoteException
     */
    public RComponentImpl() throws RemoteException {
        super();
    }


    /**
     * This method constructs the storage and persistence model and sets up
     * persistence of the context prior to initializing itself. The persistence
     * model may choose to modify the context.
     *
     * @param parent parent component
     * @param cxt this component's context
     * @throws SmartFrogDeploymentException - the component failed to deploy properly
     * @throws RemoteException - failure in a remote call during deployment
     */
    public void sfDeployWith( Prim parent, Context cxt ) throws
            SmartFrogDeploymentException, RemoteException {

        try {
        	sfIsRecovered = isRecovery( cxt );
            if ( sfIsRecovered ) {

                model = PersistenceModel.constructModel( cxt );
                stableLog = getStorage( cxt );
                sfStartLifecycleCommitPoints();
                model.recoverContext( cxt );
                sfLifecycleCommitPoint( CommitPoints.PRE_DEPLOY_WITH );

                super.sfDeployWith( parent, cxt );

                sfSaveExportReference();
                sfRecoverComponentState();
                sfLifecycleCommitPoint( CommitPoints.POST_DEPLOY_WITH );

            } else {

                model = PersistenceModel.constructModel( cxt );
                stableLog = Storage.createNewStorage( cxt );
                sfStartLifecycleCommitPoints();
                model.initialContext( cxt );
                saveContext( cxt );
                sfLifecycleCommitPoint( CommitPoints.PRE_DEPLOY_WITH );

                super.sfDeployWith( parent, cxt );

                sfSaveExportReference();
                sfSaveComponentState();
                sfLifecycleCommitPoint( CommitPoints.POST_DEPLOY_WITH );

            }
        } catch ( RemoteException ex ) {
            throw ex;
        } catch ( SmartFrogLifecycleException ex ) {
            throw ( SmartFrogDeploymentException ) SmartFrogDeploymentException.
                    forward( ex );
        } catch ( SmartFrogDeploymentException ex ) {
            throw ex;
        } catch ( StorageException ex ) {
            throw ( SmartFrogDeploymentException ) SmartFrogDeploymentException.
                    forward( ex );
        }

    }


    /**
     * This method adds a commit point before and after the super class
     * sfDeploy().
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void sfDeploy() throws RemoteException, SmartFrogException {
        sfLifecycleCommitPoint( CommitPoints.PRE_DEPLOY );

        super.sfDeploy();

        sfLifecycleCommitPoint( CommitPoints.POST_DEPLOY );
    }


    /**
     * this method adds a commit point before and after the super class
     * sfStart(). The end of this method marks the end of the life cycle
     * commit points, from here on all writes to storage will commit
     * immediately unless explicitly suspended.
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void sfStart() throws RemoteException, SmartFrogException {
        sfLifecycleCommitPoint( CommitPoints.PRE_START );

        super.sfStart();

        sfLifecycleCommitPoint( CommitPoints.POST_START );
        sfEndLifecycleCommitPoints();
    }


    /**
     * This is the new recovery life cycle method. This method is called
     * instead of sfDeploy() and sfStart() if the component is being
     * recovered from storage. There is a commit point at the start and
     * end of this method. The method may also call sfDeploy() and sfStart()
     * if deemed appropriate by the persistence model. The end of this method
     * marks the end of the life cycle commit points, from here on all writes
     * to storage will commit immediately unless explicitly suspended.
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void sfRecover() throws RemoteException, SmartFrogException {

        sfLifecycleCommitPoint( CommitPoints.PRE_RECOVER );

        /**
         * Do we need to deploy
         */
        if ( model.redeploy( this ) ) {
            sfDeploy();
        } else {
            sfIsDeployed = true;
        }

        /**
         * Do we need to start
         */
        if ( model.restart( this ) ) {
            sfStart();
        } else {
            sfIsStarted = true;
        }

        sfLifecycleCommitPoint( CommitPoints.POST_RECOVER );
        sfEndLifecycleCommitPoints();
    }
    
    
    /**
     * Determines if this component was reconstructed from its persistent storage.
     * 
     * @return true if recovered from storage, false if initial deployment
     */
    public boolean sfIsRecovered() {
    	return sfIsRecovered;
    }


    /**
     * The storage will be closed on termination. The store will be deleted
     * in the event of normal termination.
     *
     * @param status TerminationRecord
     */
    public synchronized void sfTerminateWith( TerminationRecord status ) {
        try {
            /**
             * Discard any data that has not been committed, then enable
             * commits. close and delete are required to have effect
             * regardless of commit enablement.
             */
            abort();
            enableCommit();

            if ( model.leaveTombStone( this, status ) ) {
                /**
                 * if the storage is to be retained close it
                 */
                stableLog.close();

            } else {
                /**
                 * If the storage is to be removed delete it
                 */
                stableLog.delete();
            }

            stableLog = null;

        } catch ( StorageException ex ) {
            if( sfLog().isErrorEnabled() ) {
                sfLog().error("Failed to write to stable storage in termination", ex);
            }
        }

        super.sfTerminateWith( status );
    }


    /**
     * Adds an attribute with the given name to this component.
     *
     * @param name name of attribute
     * @param value value of attribute
     *
     * @return added attribute if non-existent or null otherwise
     *
     * @throws SmartFrogRuntimeException - when name or value are null
     * @throws RemoteException - In case of Remote/nework error
     */
    public synchronized Object sfAddAttribute( Object name, Object value ) throws
            SmartFrogRuntimeException, RemoteException {

        Object retvalue = super.sfAddAttribute( name, value );

        try {
            /**
             * Do not need the same hack as replace because sfAddAttribute is
             * not called to add a child.
             */

            /**
             * Some attributes may be volatile.
             */
            if ( model.isVolatile( name ) ) {
                return retvalue;
            }

            /**
             * Special case for component descriptions - prim parent is not
             * serializable, so cut it off and replace it after storing.
             * Note that the prim parent should be this component.
             */
            if ( value instanceof ComponentDescription ) {
                try {
                    ( ( ComponentDescription ) value ).setPrimParent( null );
                    stableLog.addEntry( ( String ) name, ( Serializable ) value );
                    ( ( ComponentDescription ) value ).setPrimParent( this );
                } catch ( StorageException ex ) {
                    ( ( ComponentDescription ) value ).setPrimParent( this );
                    throw ex;
                }
            } else {
                stableLog.addEntry( ( String ) name, ( Serializable ) value );
            }
            commit();
        } catch ( StorageException exc ) {
            throw new SmartFrogRuntimeException(
                    "Error while writing attribute on stable storage",
                    exc );
        }

        return retvalue;
    }


    /**
     * Replace named attribute in component context. If attribute is not
     * present it is added to the context.
     *
     * @param name of attribute to replace
     * @param value value to add or replace
     *
     * @return the old value if present, null otherwise
     *
     * @throws SmartFrogRuntimeException when name or value are null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfReplaceAttribute( Object name, Object value ) throws
            SmartFrogRuntimeException, RemoteException {

        Object retvalue = super.sfReplaceAttribute( name, value );

        try {
            /**
             * Hack to deal with sfAddChild not actually adding
             * the attribute, so we have to check for new children here
             * to notify the model. Ideally sfAddChild should notify the
             * model.
             */
            if ( value instanceof Prim && sfChildren.contains( value ) ) {
                sfReplaceAttribute( SFCHILDREN, sfChildren );
                model.childAdded( this, ( Prim ) value, ( String ) name );
            }
            
            /**
             * Some attributes may be volatile.
             */
            if ( model.isVolatile( ( String ) name ) ) {
                return retvalue;
            }

            /**
             * Special case for component descriptions - prim parent is not
             * serializable, so cut it off and replace it after storing.
             * Note that the prim parent should be this component.
             */
            if ( value instanceof ComponentDescription ) {
                try {
                    ( ( ComponentDescription ) value ).setPrimParent( null );
                    stableLog.replaceEntry( ( String ) name, ( Serializable ) value );
                    ( ( ComponentDescription ) value ).setPrimParent( this );
                } catch ( StorageException ex ) {
                    ( ( ComponentDescription ) value ).setPrimParent( this );
                    throw ex;
                }
            } else {
                stableLog.replaceEntry( ( String ) name, ( Serializable ) value );
            }
            commit();
        } catch ( StorageException exc ) {
            throw new SmartFrogRuntimeException(
                    "Error while writing attribute on stable storage",
                    exc );
        }

        return retvalue;
    }


    /**
     * Removes an attribute from this component.
     *
     * @param name of attribute to be removed
     *
     * @return removed attribute value if successfull or null if not
     *
     * @throws SmartFrogRuntimeException when name is null
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized Object sfRemoveAttribute( Object name ) throws
            SmartFrogRuntimeException, RemoteException {

        Object retvalue = super.sfRemoveAttribute( name );

        try {
            /**
             * some attributes may be volatile
             */
            if ( model.isVolatile( name ) ) {
                return retvalue;
            }

            stableLog.removeEntry( ( String ) name );
            commit();
        } catch ( StorageException exc ) {
            throw new SmartFrogRuntimeException(
                    "Error while writing attribute on stable storage",
                    exc );
        }
        return retvalue;
    }


    /**
     * Creates a new child for this component
     *
     * @param target target to heartbeat
     */
    public void sfAddChild( Liveness target ) throws RemoteException {
        super.sfAddChild( target );
    }


    /**
     * Removes a specific child
     *
     * @param target object to remove from heartbeat
     *
     * @return true if child is removed successfully else false
     */
    public boolean sfRemoveChild( Liveness target ) throws
            SmartFrogRuntimeException, RemoteException {

        Object name = sfAttributeKeyFor( target );
        boolean res = super.sfRemoveChild( target );
        if ( res ) {
            sfReplaceAttribute( SFCHILDREN, sfChildren );
            model.childRemoved( this, ( Prim ) target, ( String ) name );
        }
        return res;
    }


    /**
     * Get the storage for this component from the context. This is provided
     * to allow the component's storage to be passed into deployWith as
     * an attribute of the context, when the context will already have been
     * read from that same storage by the recovery agent.
     *
     * @param context Context
     * @return Storage
     * @throws SmartFrogDeploymentException
     */
    protected Storage getStorage( Context context ) throws
            SmartFrogDeploymentException {

        Object obj = context.remove( RComponent.STORAGEATTRIB );
        if ( !( obj instanceof StorageRef ) ) {
            throw new SmartFrogDeploymentException(
                    "Storage refrence missing in attempt to recover from context" );
        }

        Storage storage = null;
        try {
            storage = ( ( StorageRef ) obj ).getStorage();
        } catch ( StorageException ex ) {
            throw new SmartFrogDeploymentException(
                    "Failed to dereference storage ref attempting to recover from context" );
        }
        return storage;
    }


    /**
     * Checks for the presence of the recovery attribute.
     *
     * @param context Context
     * @return boolean
     */
    protected boolean isRecovery( Context context ) {
        return context.sfContainsAttribute( RECOVERY_ATTR );
    }


    /**
     * Save internal component state that will be needed to re-create
     * the component during deployWith.
     *
     * @throws SmartFrogDeploymentException
     */
    protected void sfSaveComponentState() throws SmartFrogDeploymentException {

        try {

            /**
             * Save parentage if remote or recoverable (null if not)
             */
            if ( sfParent == null || sfParent instanceof ProcessCompound ) {
                sfAddAttribute( SFPARENT, SFNull.get() );
            } else if ( sfParent instanceof RComponent ) {
                sfAddAttribute( SFPARENT, sfParent );
            } else if ( sfIsRemote( sfParent ) ) {
                sfAddAttribute( SFPARENT, RemoteObject.toStub( sfParent ) );
            } else {
                sfAddAttribute( SFPARENT, SFNull.get() ); // this is a "kind of" hack!!!
            }

            /**
             * Save children liveness targets
             */
            sfAddAttribute( SFCHILDREN, sfChildren );

        } catch ( RemoteException ex ) {
            SmartFrogDeploymentException.forward( ex );
        } catch ( SmartFrogRuntimeException ex ) {
            SmartFrogDeploymentException.forward( ex );
        }

    }


    /**
     * Recover any internal component state that is not recreated as part of
     * the super class deployWith.
     */
    protected void sfRecoverComponentState() throws
            SmartFrogDeploymentException {

        try {

            /**
             * recover the pargentage
             */
            if ( !model.isVolatile( SFPARENT ) ) {
                Object parentObj = sfResolve( SFPARENT );
                sfParent = ( Prim ) ( SFNull.get().equals( parentObj ) ? null :
                                      parentObj );
            }

            /**
             * reset the sfLog
             */
            try {
                sfSetLog( sfGetApplicationLog() );
            } catch ( RemoteException ex1 ) {
            } catch ( SmartFrogException ex1 ) {
            }

            /**
             * recover the children liveness targets
             */
            if ( !model.isVolatile( SFCHILDREN ) ) {
                sfChildren = ( Vector ) sfResolve( SFCHILDREN );
            }

            /**
             * Indicate parentage changed
             */
            sfParentageChanged();

        } catch ( RemoteException ex ) {
            SmartFrogDeploymentException.forward( ex );
        } catch ( SmartFrogResolutionException ex ) {
            SmartFrogDeploymentException.forward( ex );
        }
    }


    /**
     * Save the export reference for this component (null if not exported)
     */
    protected void sfSaveExportReference() throws SmartFrogDeploymentException {
        try {
            if ( sfExportRef == null ) {
                sfReplaceAttribute( RComponent.DBStubEntry, SFNull.get() );
            } else {
                sfReplaceAttribute( RComponent.DBStubEntry, sfExportRef );
            }
        } catch ( RemoteException ex ) {
            SmartFrogDeploymentException.forward( ex );
        } catch ( SmartFrogRuntimeException ex ) {
            SmartFrogDeploymentException.forward( ex );
        }
    }


    /**
     * Write the non-volatile attributes of the given context to the
     * storage. The context is assumed to be the context of this component,
     * but it is passed in as a parameter as this method is called before
     * the component has been initialized with the context.
     *
     * @param context the context
     * @throws SmartFrogDeploymentException
     */
    protected void saveContext( Context context ) throws
            SmartFrogDeploymentException {

        try {
            context.sfReplaceAttribute( RECOVERY_ATTR, "indicates context was saved" );
            Iterator attributes = context.sfAttributes();
            Iterator values = context.sfValues();
            while ( attributes.hasNext() ) {
                String entryname = ( String ) attributes.next();
                Serializable value = ( Serializable ) values.next();
                if ( !model.isVolatile( entryname ) ) {
                    stableLog.addEntry( entryname, value );
                }
            }
        } catch ( StorageException ex ) {
            SmartFrogDeploymentException.forward( ex );
        } catch ( SmartFrogContextException ex ) {
            SmartFrogDeploymentException.forward( ex );
        }

    }


    /**
     * enable commits - calls to commit will result in a commit
     */
    protected void enableCommit() {
        stableLog.enableCommit();
    }


    /**
     * disable commits - calls to commit will be ignored. This allows
     * muliple sets of updates to become atomic by ignoring intermediary
     * commits.
     */
    protected void disableCommit() {
        stableLog.disableCommit();
    }


    /**
     * commit updates - updates are made persistent in the storage
     * @throws StorageException
     */
    protected void commit() throws StorageException {
        stableLog.commit();
    }


    /**
     * abort updates - updates are disguarded and revert to the last
     * persisted values.
     * @throws StorageException
     */
    protected void abort() throws StorageException {
        stableLog.abort();
    }


    /**
     * Mark the begining of life cycle startup commit points. Updates during
     * the status life cycle are commited only at certain commit points. These
     * commit points are vetoed by the persistence model.
     */
    protected void sfStartLifecycleCommitPoints() {
        disableCommit();
    }


    /**
     * Mark the end of life cycle startup commit points. Updates during
     * the status life cycle are commited only at certain commit points. These
     * commit points are vetoed by the persistence model. Commits are enabled
     * after this method.
     */
    protected void sfEndLifecycleCommitPoints() {
        enableCommit();
    }


    /**
     * Marks a life cycle commit point. The method checks with the persistence
     * model to see if this is one of the commit points it wants to use.
     *
     * @param point String
     */
    protected void sfLifecycleCommitPoint( String point ) throws
            SmartFrogLifecycleException {
        try {
            if ( model.isCommitPoint( this, point ) ) {
                enableCommit();
                commit();
                disableCommit();
            }
        } catch ( StorageException ex ) {
            throw ( SmartFrogLifecycleException ) SmartFrogLifecycleException.
                    forward(
                            "Failed at commit point " + point, ex );
        } catch ( Exception ex ) {
            throw ( SmartFrogLifecycleException ) SmartFrogLifecycleException.
                    forward(
                            "Failed at commit point " + point, ex );
        }
    }


    /**
     * Marks an attribute as volatile, so it is not persisted.
     * Note that if an attribute with this name has previously been
     * persisted it will still be in stable store.
     *
     * @param attr Object
     * @return boolean
     */
    protected boolean declareVolatile( Object attr ) throws StorageException {
        return model.addVolatile( attr );
    }


    /**
     * Marks an attribute as not volatile, so it does get persisted.
     * Note that if an attribute with this name already exists, it will
     * not have been persisted.
     *
     * @param attr Object
     * @return boolean
     */
    protected boolean declareNotVolatile( Object attr ) throws StorageException {
        return model.removeVolatile( attr );
    }


    /**
     * Get the proxy locator.
     *
     * @return RComponentProxyLocator
     * @throws RemoteException
     * @throws StorageException
     */
    public RComponentProxyLocator getProxyLocator() throws RemoteException,
            StorageException {
        return new RComponentProxyLocatorImpl( stableLog.getAgentUrl(),
                                               stableLog.getStorageRef() );
    }


    /**
     * Implemented to provide correct equality checking.
     *
     * If the storage references can not be handled then returns false. This case
     * should only happen when the configuration for the storage is incorrect.
     * !! Not sure if this is good interpretation of inequality - perhaps it
     *    should force an exception ??
     *
     * @param obj object to compare with
     *
     * @return true if equal, false if not
     */
    public boolean equals( Object obj ) {
        if ( !( obj instanceof RComponent ) ) {
            return false;
        }
        if ( obj instanceof RComponentProxyStub ) {
            try {
                return RComponentProxyInvocationHandler.sfGetProxy( this ).equals( obj );
            } catch ( StorageException ex ) {
                return false;
            }
        } else {
            return super.equals( obj );
        }
    }


    /**
     * Replaces the component by its dynamic proxy with recovery properties during
     * serialization.
     *
     * @return A new proxy object linked to this recoverable component
     * @throws ObjectStreamException in case an error occurs
     */
    public Object writeReplace() throws ObjectStreamException, StorageException {
        return RComponentProxyInvocationHandler.sfGetProxy( this );
    }

}
