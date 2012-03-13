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

package org.smartfrog.services.persistence.rcomponent;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.smartfrog.services.persistence.framework.activator.DisposalWorker;
import org.smartfrog.services.persistence.framework.activator.LoadWorker;
import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceManager;
import org.smartfrog.services.persistence.storage.ConcurrentTransactionException;
import org.smartfrog.services.persistence.storage.LocalTransactionTarget;
import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentAccessException;
import org.smartfrog.services.persistence.storage.StorageDeploymentMissingException;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.StorageTimeoutException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.services.persistence.storage.TransactionException;
import org.smartfrog.services.persistence.storage.TransactionTarget;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.ChildMinder;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.SFProcess;
import org.smartfrog.sfcore.reference.ProcessReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;


/**
 * A recoverable component is a variation of the SmartFrog compound that stores
 * a copy of its attributes in a database for recovery in the event of failure.
 * Every Recoverable component has to extend this class RComponentImpl. This
 * holds the basic properties and functionality of a Recoverable component. The
 * recoverable component is implemented to perform the deployWith phase of a
 * deployment (the construction phase) as single atomic action against the
 * database. This provides the guarantee that in the event of failure during
 * deployment either the entire deployWith phase succeeds and becomes persistent
 * or none of it does.
 * 
 * Handling of the sfDeploy and sfStart phases is more complex as they may or
 * may not be performed atomically depending on the context. These phases occur
 * both on initial deployment and on recovery (components are deployed and
 * started when recovered).
 * 
 * When deploying components the user does have the option to include the
 * sfDeploy and sfStart phases in a single atomic action with the sfDeployWith
 * phase. However, in recovery they are not performed atomically with the
 * sfDeployWith. So generally sfDeploy and sfStart methods should be programmed
 * as though they are not atomic, allowing them to be performed correctly in
 * either context.
 * 
 */
/**
 * @author ptm
 *
 */
public class RComponentImpl extends org.smartfrog.sfcore.compound.CompoundImpl implements Compound, RComponent, TransactionTarget {
    
	private static String CREATION_ATTR = "creationTime";  // REMOVE
	private long creationTime = 0;                  // REMOVE
    
    protected Storage storage = null;
    private LocalTransactionTarget myXactTarget = new LocalTransactionTarget(this);
    
    /**
     * Update methods from the compound interface that do not include a
     * transaction parameter are mapped to ones that do, using  
     * currentTransaction as the transaction. If this is the null transaction
     * they are implemented as single operations, if it is a valid transaction
     * they are implemented in the scope of that transaction. This is primarily used 
     * in the sfDeployWith, sfDeploy and sfStart phases where the currentTransaction
     * may be set using a sweepTransaction.
     */
    private Transaction currentTransaction = Transaction.nullTransaction;
    
    /**
     * Components are locked when modified by a transaction, or when the user
     * explicitly locks them with a transaction, and are unlocked when the
     * transaction is committed.
     */
    private Object lockMonitor = new Object();
    private Transaction lock = null;
    private Transaction previousLock = null;
    private Transaction prepareLock = null;
    private Exception lockTrace = null;
    private Exception previousLockTrace = null;
    private final static boolean isTracingLocks = Boolean.parseBoolean(System.getProperty(LOCK_TRACING_PROP));
    private DisposalWorker disposalWorker = null;

    /**
     * Sweep transactions are used to carry a transaction through
     * sfDeployWith, sfDeploy and sfStart phases. The transaction is
     * initiated in sfDeployWith if one is not already started and
     * it is the responsibility of the initiator to commit the transaction.
     */
    private boolean sweepTransactionInitiator = false;
    
    /**
     * isInRecoveryDeployment is set to true for the duration of
     * a recovery sfDeployWith phase.
     */
    private boolean isInRecoveryDeployment = false;

    /**
     * sfIsRecovery is true if the components were recovered from
     * storage.
     */
    protected boolean sfIsRecovered = false;
    
    /**
     * sfRecoveryType determines the type of recovery.
     * (e.g. normal vs terminate)
     */
    protected Object sfRecoveryType = null;
    
    /**
     * Lazy load children are components to be loaded using the 
     * lazy loading lifecycle instead of the regular lifecycle.
     */
    protected Vector<Object> lazyLoadChildren = new Vector<Object>();
    protected volatile boolean sfIsLazyLoading = false;
    protected Object loadMonitor = new Object();
    protected LoadWorker loadWorker = null;
    
    /**
     * volatileAttributes is the set of attributes that are always. Classes that
     * derive from this one can add to this set, but should not remove anything.
     * Attributes in this set are typically those created by the system during
     * construction and so should not be retained in storage.
     */
    protected static Set volatileAttributes = new HashSet();
    static {
        for( int i = 0; i<RComponent.SF_VOLATILE_ATTRS.length; i++ ) {
            volatileAttributes.add(RComponent.SF_VOLATILE_ATTRS[i]);
        }
        volatileAttributes.add(CREATION_ATTR);    // REMOVE
    }
    
    /**
     * Check the local context for a volatile attributes. An attribute is volatile if it 
     * is in the standard set of volatile attributes or it has the volatile tag. 
     * 
     * @param name - the attribute
     * @return true if volatile, false if not
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    protected boolean isVolatile(Object name) throws RemoteException, SmartFrogRuntimeException {
        return isVolatile(name, sfContext);
    }
    
    /**
     * Check the given context for a volatile attribute. An attribute is volatile if it 
     * is in the standard set of volatile attributes or it has the volatile tag.
     * 
     * @param name - the attribute
     * @return true if volatile, false if not
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    protected boolean isVolatile(Object name, Context context) throws RemoteException, SmartFrogRuntimeException {
        return (volatileAttributes.contains(name) || context.sfContainsTag(name, VOLATILE_TAG) );
    }
    
    /**
     * An attribute that is "volatile by tag" is one that is not valiatle by default. An attribute attr that
     * is volatile by default does not satisfy isVolatileByTag(attr).
     * 
     * @param name - attribute name
     * @return true only if the attribute has the volatile flag but is not volatile by default.
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    protected boolean isVolatileByTagOnly(Object name) throws RemoteException, SmartFrogRuntimeException {
        return ( sfContainsTag(name, VOLATILE_TAG) && !volatileAttributes.contains(name));
    }
    
    /**
     * interface manager is used to help deal with cleanup and remote interface
     * access control during load and unload. 
     */
    protected InterfaceManager interfaceManager;
    
    /**
     * Reference for a component that has been left pending after detach with
     * out termination. pendingReference is the reference this component had
     * before it was detached.
     */
    protected Reference pendingReference;

    public RComponentImpl() throws RemoteException {
        super();
        creationTime = System.currentTimeMillis(); // REMOVE
    }
    
    /**
     * This gets a reference to the interface manager, but is done like this because we
     * don't know which initialising method may need to call it first. 
     * 
     * @param context the context
     * @throws SmartFrogDeploymentException
     * @throws RemoteException
     */
    protected void setInterfaceManager(Context context) throws SmartFrogDeploymentException, RemoteException {
        
        /**
         * Only do this once.
         */
        if( interfaceManager != null ) {
            return;
        }
        
        try {
            /**
             * get the interface manager - if not specified use the default. 
             */
            Reference mgrRef = (Reference)context.get(INTERFACE_MGR_REF_ATTR);
            if( mgrRef == null ) {
                interfaceManager = (InterfaceManager)SFProcess.getProcessCompound().sfResolve(INTERFACE_MGR_REF_ATTR);
            } else {
                interfaceManager = (InterfaceManager)SFProcess.getProcessCompound().sfResolve(mgrRef);
            }
        } catch (SmartFrogResolutionException e) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        } catch( NullPointerException e) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(e);
        }        
    }

    /**
     * Override sfDeployWith to allow two different construction cases: 1)
     * inital deploy 2) recovery
     * {@inheritDoc}
     */
    public void sfDeployWith(Prim parent, Context context) throws SmartFrogDeploymentException, RemoteException {

        setInterfaceManager(context);
        
        try {
            determineRecoveryType(context);
            
            if (sfIsRecovered) {

                openStorage(context);
                joinSweepTransaction(context);
                processRecoveredContext(context);
                isInRecoveryDeployment = true;
                super.sfDeployWith(parent, context);
                isInRecoveryDeployment = false;
                recoverComponentState();
                leaveSweepTransaction();

            } else {

                openStorage(context);
                joinSweepTransaction(context);
                processInitialContext(context);
                storage.createComponent(localParentStorageName(parent), currentTransaction);
                saveContext(context);
                super.sfDeployWith(parent, context);
                saveComponentState();
                leaveSweepTransaction();

            }
            
            super.sfContext.put(CREATION_ATTR, creationTime);  // REMOVE
            
        } catch (StorageException ex) {
            if( ex instanceof StorageAccessException ) {
                throw new StorageDeploymentAccessException(ex);
            } else {
                throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(ex);
            }
        } catch (RemoteException ex) {
            throw ex;
        } catch (SmartFrogDeploymentException ex) {
            throw ex;
        } catch (SmartFrogException ex) {
            throw (SmartFrogDeploymentException) SmartFrogDeploymentException.forward(ex);
        } finally {
            try {
                dropSweepTransaction();
            } catch (Exception e) {
            }
        }
    }
    
    
    /**
     * Determine the type of recovery. This method sets the values of sfIsREcovered, sfRecoveryType
     * and sfIsLazyLoading.
     * 
     * @param context
     * @throws SmartFrogContextException
     * @throws SmartFrogResolutionException
     */
    protected void determineRecoveryType(Context context) throws SmartFrogContextException, SmartFrogResolutionException {
        sfRecoveryType = context.remove(RECOVERY_MARKER_ATTR);
        sfIsRecovered = (sfRecoveryType != null);
        if( RComponent.NORMAL_RECOVERY_MARKER_VALUE.equals(sfRecoveryType) ) { 
            ComponentDescription storageData = (ComponentDescription) context.sfResolveAttribute(STORAGE_DATA_ATTR);
            sfIsLazyLoading = Boolean.TRUE.equals(context.get(LAZY_LOADING_ATTR));
        }   
    }
    
    
    /**
     * sfDeployWithChildren - this version will allow children to be missing if 
     * the deployment is a "terminate recovery".
     *
     * {@inheritDoc}
     */
    protected void sfDeployWithChildren() throws SmartFrogDeploymentException {
        
        /**
         * This code section does not really need to be synchronized on the 
         * load monitor as we are in construction (deployWith) so there should 
         * not be any concurrent attempt to access to the lazyLoadChildren. 
         * But we synchronize it anyway for completeness.
         */
        synchronized(loadMonitor) {

            for (Enumeration e = sfContext().keys(); e.hasMoreElements(); ) {

                Object key = e.nextElement();
                Object elem = sfContext.get(key);
                try { 

                    if ( elem instanceof ComponentDescription ) {

                        ComponentDescription cd = (ComponentDescription)elem;
                        /**
                         * If its not to be deployed move on to next
                         */
                        if( !cd.getEager() ) {
                            continue;
                        }
                        /**
                         * If this is storage and we are lazy loading then add
                         * it to the lazy load lifecycle.
                         */
                        if( sfIsLazyLoading && Storage.isStorageDescription(cd) ) {
                            lazyLoadChildren.add(key);
                            continue;
                        }
                        /**
                         * If this is storage or contains storage then add the sweep transaction
                         * and recovery marker
                         */
                        if( Storage.isStorageDescription(cd) ||
                                Storage.isStorageDescription(cd.sfResolve(RComponent.STORAGE_DATA_ATTR, (Object)null, false)) ) {
                            cd.sfReplaceAttribute(SWEEP_TRANSACTION_ATTR, currentTransaction);
                            if( sfIsRecovered ) {
                                cd.sfReplaceAttribute(RECOVERY_MARKER_ATTR, sfRecoveryType);
                            }
                        }
                        /**
                         * Deploy the component description using normal lifecycle
                         */
                        lifecycleChildren.add( sfDeployComponentDescription(key, this, cd, null) );
                    }

                } catch( StorageDeploymentMissingException ex ) {

                    /**
                     * Its ok for a child to be missing if we are in terminate recovery
                     */
                    if( TERMINATE_RECOVERY_MARKER_VALUE.equals(sfRecoveryType) ) {
                        continue;
                    } else {
                        new TerminatorThread(this, ex, null).quietly().start();
                        throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(ex);
                    }

                } catch (Exception sfex) {

                    new TerminatorThread(this, sfex, null).quietly().start();
                    throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward(sfex);

                }
            }

            /**
             * If we have children to load lazily add them to the
             * load worker. Otherwise mark that we have no lazy
             * loading to do.
             */
            if( sfIsLazyLoading && !lazyLoadChildren.isEmpty() ) {
                loadWorker.add(this);
            } else {
                sfIsLazyLoading = false;
            }

        }
    }
    
    
    /**
     * Set the load worker for this component
     * 
     * @param worker the load worker
     */
    public void setLoadWorker(LoadWorker worker) {
        loadWorker = worker;
    }
    
    /**
     * Trigger load of all lazy load child components
     * 
     * @throws StorageDeploymentAccessException if can not access the storage
     */
    public void loadLazy() throws StorageDeploymentAccessException {
        
        if( !sfIsLazyLoading ) {
            return;
        }
        
        /**
         * If this component is locked, wait until the 
         * lock is released or closed.
         */
        waitOnLock();

        synchronized(loadMonitor) {
            
            /**
             * Do nothing if we are terminating
             */
            if( sfIsTerminating || sfIsTerminated ) {
                return;
            }

            if( lazyLoadChildren.isEmpty() ) {
                loadWorker.remove(this);
                sfIsLazyLoading = false;
                return;
            }
            
            /**
             * Get the reserved transaction from the load
             * worker.
             */
            Transaction xact = Transaction.nullTransaction;
            
            try {

                xact = loadWorker.getTransaction();
                
                for( Object key : lazyLoadChildren ) {

                    try {
                        Object obj = sfResolveHere(key);
                        if( Storage.isStorageDescription(obj) ) {
                            ComponentDescription cd = (ComponentDescription)obj;
                            cd.sfAddAttribute(RECOVERY_MARKER_ATTR, sfRecoveryType);
                            sfCreateNewChild(key, cd, null, xact);                    
                        } else {
                            if( sfLog().isErrorEnabled() ) {
                                sfLog().error("Failed to find storage description for " + key + " instead found " + obj);
                            }
                        }

                    } catch(StorageDeploymentAccessException e) {

                        throw e;

                    } catch (Exception e) {

                        if( sfLog().isErrorEnabled() ) {
                            sfLog().error("Failed to lazy load component " + key);
                        }

                    }
                }
            } catch (TransactionException e) {
                
                if( sfLog().isDebugEnabled() ) {
                    sfLog().debug("Failed to get a transaction for lazy loading");
                }
                
            } finally {
                if( !xact.isNull() ) {
                    try { xact.commit(); }
                    catch(Exception e) {}
                    loadWorker.releaseTransaction(xact);
                }
                lazyLoadChildren.clear();
                loadWorker.remove(this);
                sfIsLazyLoading = false;
            }
        }

    }
    
    /**
     * Add this component to the lazy load queue
     */
    public void queueLazy() {
        
        if( sfIsLazyLoading ) {
            
            synchronized(loadMonitor) {
                
                if(lazyLoadChildren.isEmpty() ) {
                    return;
                }

                loadWorker.add(this);
            }
        }
    }
    
    /**
     * RComponents may perform lazy loading. If the the target of this
     * sfResolveHere is an RComponent, this method will cause it to
     * load its children.
     *
     * {@inheritDoc}
     */
    public Object sfResolveHere(Object name) throws SmartFrogResolutionException {
        Object obj = super.sfResolveHere(name);
        if( obj instanceof RComponentImpl ) {
            try {
                ((RComponentImpl)obj).loadLazy();
            } catch (StorageDeploymentAccessException e) {
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(e);
            }
        }
        return obj;
    }

    /**
     * get the storage name from the local parent. If there is no local parent, or there is no storage name
     * return null.
     * 
     * @param parent the parent
     * @return storage name or null
     * @throws RemoteException
     * @throws SmartFrogResolutionException
     */
    private String localParentStorageName(Prim parent) throws RemoteException, SmartFrogResolutionException {
        
        if( (parent == null) || sfIsRemote(parent) || SFProcess.getProcessCompound().equals(parent) ) {
            return null;
        }
        
        Object obj = parent.sfContext().get(STORAGE_DATA_ATTR);
        
        if( obj == null ) {
            return null;
        }
        
        if( !Storage.isStorageDescription(obj) ) {
            throw new SmartFrogResolutionException("Parent has " + STORAGE_DATA_ATTR + " attribute that is not a storage description: " + obj);
        }
        
        return ((ComponentDescription)obj).sfResolve(Storage.COMPONENT_NAME_ATTR, (String)null, true);
    }

    /**
     * {@inheritDoc}
     */
    public synchronized boolean sfUpdateWith(Context newContext) throws RemoteException, SmartFrogException {
        
        boolean isSameStorage;
        boolean isSameStorageName;
        boolean okToUpdate;
        
        /**
         * Join and propogate the transaction and do sfUpdateWith
         */
        try {
            joinSweepTransaction(newContext);
            isSameStorage = sameStorage(sfContext, newContext);
            isSameStorageName = sameStorageName(sfContext, newContext);
            
            propogateSweepTransaction(newContext);
            okToUpdate = super.sfUpdateWith(newContext);
            
            if( !okToUpdate && isSameStorageName ) {
                throw new StorageException("Storage name must change for the component to redeploy");
            }
            
            if( okToUpdate && !isSameStorage ) {
                throw new StorageException("Storage must stay the same if the component is to remain the same");
            }
            
            return okToUpdate;
        } 
        
        /**
         * if we fail for any reason drop the transaction and forward the exception as
         * a SmartFrogException
         */
        catch (Exception e) {
            try {
                dropSweepTransaction();
            } catch( StorageException e1 ) {
                // do nothing
            }
            throw SmartFrogException.forward(e);
        }
    }
    
    /**
     * Checks if two contexts contain the same storage descriptions. Two storage descriptions are the same
     * if they contain the same attributes and values.
     * 
     * @param context1 first storage description
     * @param context2 second storage description
     * @return true if they are the same, false if not
     * @throws SmartFrogContextException
     */
    private boolean sameStorage(Context context1, Context context2) throws SmartFrogContextException {
        
        Context config1 = ((ComponentDescription)context1.get(STORAGE_DATA_ATTR)).sfContext();
        Context config2 = ((ComponentDescription)context2.get(STORAGE_DATA_ATTR)).sfContext();
        
        if( config1.size() != config2.size() ) {
            return false;
        }
        
        Iterator attributes = config1.sfAttributes();
        while (attributes.hasNext()) {

            Object name = attributes.next();
            Object value1 = config1.sfResolveAttribute(name);
            Object value2 = config2.sfResolveAttribute(name);
            if( !value1.equals(value2) ) {
                return false;
            }
            
        }
        return true;
    }
    
    /**
     * Checks the names in the storage descriptions are the same
     * 
     * @param context1 first context
     * @param context2 second context
     * @return true if the storage names are the same
     * @throws SmartFrogResolutionException
     */
    private boolean sameStorageName(Context context1, Context context2) throws SmartFrogResolutionException {
        Object name1 = ((ComponentDescription)context1.get(STORAGE_DATA_ATTR)).sfResolveHere(Storage.COMPONENT_NAME_ATTR);
        Object name2 = ((ComponentDescription)context2.get(STORAGE_DATA_ATTR)).sfResolveHere(Storage.COMPONENT_NAME_ATTR); 
        return ( (name1 != null) && name1.equals(name2) );
    }
    
    
    /**
     * perform the update
     * 
     * {@inheritDoc}
     */
    public synchronized void sfUpdate() throws RemoteException, SmartFrogException {

        /**
         * Do the update
         */
        try {

            /**
             * remove old attributes from storage
             */
            storage.removeAllAttributes(currentTransaction);
            
            /**
             * do the update
             */
            super.sfUpdate();

            /**
             * construct new attributes in storage
             */
            processInitialContext(sfContext);
            saveContext(sfContext);

            /**
             * leave the transaction
             */
            leaveSweepTransaction();
        } catch (Exception e) {

            try {
                dropSweepTransaction();
            } catch (StorageException e1) {
                // do nothing
            }

            throw SmartFrogException.forward(e);
        }   
    }
    
    /**
     * Abort the update
     * 
     * {@inheritDoc}
     */
    public synchronized void sfAbandonUpdate() throws RemoteException {
        
        /**
         * do the abandon
         */
        try {

            super.sfAbandonUpdate();
            
        } 
        
        /**
         * drop the sweep transaction
         */
        finally {
            try {
                
                dropSweepTransaction();
                
            } catch ( Exception e ) {
                if( sfLog().isErrorEnabled() ) {
                    sfLog().error("Storage failed while updating component", e);
                }
            }
        }
    }
    
    /**
     * Terminate the component with a normal termination record that indicates 
     * unload on termination. This will propogate throughout the component's 
     * hierarchy causing all components to terminate without removing their 
     * storage.
     */
    public void sfUnload() {
        sfTerminate(TerminationRecord.normal(UNLOAD_TERMINATION_DESC, null));
    }
    
    /**
     * Determines if the termination record indicates RComponent unload as 
     * the reason for termination.
     * 
     * @param tr the termination record
     * @return true if this is an unload, false otherwise
     */
    public boolean sfIsUnloadTermination(TerminationRecord tr) {
        return tr.isNormal() && UNLOAD_TERMINATION_DESC.equals(tr.description);
    }
    
    /**
     * Assign the disposal worker for this component.
     * @param worker the disposal worker
     */
    public void setDisposalWorker(DisposalWorker worker) {
        disposalWorker = worker;
    }
    
    
    /**
     * Dispose of the storage for this component. This is called by the 
     * disposal worker when it is ready.
     * 
     * @param xact the transaction to use
     */
    public void disposeStorage(Transaction xact) {
        synchronized(lockMonitor) {
            
            if( lock == null || lock.equals(Transaction.nullTransaction) ) {
                /**
                 * Not locked by a transaction so lock it with null to 
                 * prevent future locks and then delete the storage
                 */
                lock = Transaction.nullTransaction;
                try {
                	
                    storage.deleteComponent(xact);
                    
                } catch (StorageTimeoutException e) {
                	
                	disposalWorker.add(this);
                	
                } catch (StorageException e) {
                	
                    if( sfLog().isErrorEnabled() ) {
                        sfLog().error("Failed to delete storage - giving up on component", e);
                    }
                }
                
            } else {
                /**
                 * locked so give back to disposal worker
                 */
                disposalWorker.add(this);
            }
        }
    }

    
    /**
     * Terminate and clean up the storage as appropriate. If the termination is to
     * unload the RComponent then just terminate the SmartFrog runtime 
     * component and leave the storage. Otherwise this will add this component
     * to the disposal workers queue of things to do.
     * 
     * {@inheritDoc}
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {

        super.sfTerminateWith(status);

        if (sfIsUnloadTermination(status)) {
            /**
             * retain component in storage (i.e. do nothing)
             */ 
        } else {
            /**
             * dispose the resources
             */
            terminateLazyChildren();
            disposalWorker.add(this);
        }
    }
    
    /**
     * If there are any children to terminate that would have been loaded lazily
     * they will be added to the orphan queue of the disposal worker.
     */
    private void terminateLazyChildren() {
        
        if( !sfIsLazyLoading ) {
            return;
        }
        
        synchronized(loadMonitor) {
            for(Object key : lazyLoadChildren) {
                try {

                    Object obj = sfResolveHere(key);
                    if( Storage.isStorageDescription(obj) ) {
                        disposalWorker.addOrphan((ComponentDescription)obj);
                    } else {
                        if( sfLog().isErrorEnabled() ) {
                            sfLog().error("Failed to find storage description for " + key + " found " + obj);
                        }
                    }

                } catch (SmartFrogResolutionException e) {
                    if( sfLog().isErrorEnabled() ) {
                        sfLog().error("Failed to find storage description for " + key );
                    }
                }
            }
            lazyLoadChildren.clear();
            sfIsLazyLoading = false;
            loadWorker.remove(this);            
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void sfJoinTransaction(Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        if (xact == null) {
            throw new SmartFrogRuntimeException("transaction is null");
        }
        lockTree(xact);
        joinTransaction(xact);
    }
    
    /**
     * {@inheritDoc}
     */
    public void joinTransaction(Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        currentTransaction = xact;
        for( Object child : sfChildren ) {
            if( child instanceof RComponent ) {
                ((RComponent)child).joinTransaction(xact);
            }
        }
    }

   
    /**
     * {@inheritDoc}
     */
    public void sfLeaveTransaction() throws SmartFrogRuntimeException, RemoteException {
        
        currentTransaction = Transaction.nullTransaction;
        for( Object child : sfChildren ) {
            if( child instanceof RComponent ) {
                ((RComponent)child).sfLeaveTransaction();
            }
        }
    }

    
    /**
     * Join the sweep transaction. This will start a new transaction if there
     * isn't one already or the existing transaction is remote.
     * 
     * @param context
     * @throws StorageException
     * @throws TransactionException
     */
    private void joinSweepTransaction(Context context) throws StorageException, TransactionException {
        Object obj = context.remove(SWEEP_TRANSACTION_ATTR);
        if (obj == null || ((Transaction) obj).isNull() ) {
            Transaction xact = storage.getTransaction();
            try {
                lock(xact);
            } catch(TransactionException e) {
                /**
                 * We got the transaction, but failed to use it
                 * - dispose quitetly and return the lock failure.
                 */
                commitQuietly(xact);
                throw e;
            } 
            sweepTransactionInitiator = true;
            currentTransaction = xact;
        } else {
            Transaction xact = (Transaction) obj;
            lock(xact);
            sweepTransactionInitiator = false;
            currentTransaction = xact;
        }
    }

    /**
     * Leave the sweep transaction. If this component initiated the
     * transaction it will commit it.
     * 
     * @param deployWithSuccess
     * @throws StorageException
     */
    private void leaveSweepTransaction() throws StorageException {
        try {
            if (sweepTransactionInitiator) {
                currentTransaction.commit();
            }
        } finally {
            sweepTransactionInitiator = false;
            currentTransaction = Transaction.nullTransaction;
        }
    }

    /**
     * drop the sweep transaction. If this component initiated the
     * transaction it will abort it.
     * 
     * @throws StorageException
     */
    private void dropSweepTransaction() throws StorageException {
        try {
            if (currentTransaction.isNull()) {
                return;
            }
            if (sweepTransactionInitiator) {
                currentTransaction.abort();
            }
        } finally {
            currentTransaction = Transaction.nullTransaction;
        }
    }
    
    /**
     * Pass the sweep transaction to all children in the context.
     * 
     * @param context
     * @throws SmartFrogException
     */
    private void propogateSweepTransaction(Context context) throws SmartFrogException {
        Enumeration e = context.keys();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            Object value = context.sfResolveAttribute(name);
            if (value instanceof ComponentDescription && ((ComponentDescription) value).getEager()) {
                ((ComponentDescription) value).sfReplaceAttribute(SWEEP_TRANSACTION_ATTR, currentTransaction);
            }
        }        
    }

    /**
     * Process the initial context prior to component construction. This
     * includes creating attributes used by the component to keep track of
     * initial state that must be restored in the event of recovery.
     * 
     * @param context
     * @throws SmartFrogException
     */
    private void processInitialContext(Context context) throws SmartFrogException {
        /**
         * Reset the liveness delay and factor
         */
        resetLiveness(context);
        /**
         * Set the default termination behaviour
         */
        setDefaultSyncBehaviour(context);
    }

    /**
     * Process the recovered context prior to component construction. This
     * includes reconstructing attributes that must be recreated in the initial
     * state.
     * 
     * @param context
     * @throws SmartFrogException
     * @throws SmartFrogContextException
     */
    private void processRecoveredContext(Context context) throws SmartFrogContextException, SmartFrogException {
        /**
         * Reset the liveness delay and factor
         */
        resetLiveness(context);
        /**
         * Set the default termination behaviour
         */
        setDefaultSyncBehaviour(context);
    }
    
    /**
     * By default the liveness delay is set to 0 and factor is set to 2.
     * These can be over-ridden by the attributes sfLivenessDalayReset and
     * sfLivenessFactorReset.
     *  
     * @param context
     */
    private void resetLiveness(Context context) {
        Object delay = context.get(LIVENESS_DELAY_RESET);
        context.put(SmartFrogCoreKeys.SF_LIVENESS_DELAY, (delay == null ? new Long(0) : delay) );
        Object factor = context.get(LIVENESS_FACTOR_RESET);
        context.put(SmartFrogCoreKeys.SF_LIVENESS_FACTOR, (delay == null ? new Integer(2) : delay) );        
    }
    
    /**
     * Set the default termination behaviour. SmartFrog normally uses asynchrnous 
     * component termination by default - we want to use synchronous as the default
     * for RComponentImpl.
     * 
     * @param context
     */
    private void setDefaultSyncBehaviour(Context context) throws SmartFrogContextException {
        if( !context.containsKey(SmartFrogCoreKeys.SF_SYNC_TERMINATE) ) {
            context.sfAddAttribute(SmartFrogCoreKeys.SF_SYNC_TERMINATE, Boolean.TRUE);
        }        
    }

    /**
     * childRecoveryData is called to obtain information that should be saved about
     * a child when it registers (called from sfReplaceAttribute).
     * It checks to see if the attribute is one of our children and it is recoverable. 
     * If it is return its recovery data, if it is not our child or it is not 
     * recoverable it marks the attribute as volatile and returns the original value.
     * 
     * @param name
     * @param value
     * @return
     * @throws RemoteException
     * @throws SmartFrogException
     */
    private Object childRecoveryData(String name, Object value) throws RemoteException, SmartFrogRuntimeException {

        if (sfIsRemote(value)) {
            return value;
        }

        if (!sfChildren.contains(value)) {
            sfAddTag(name, VOLATILE_TAG);
            return value;
        }

        /**
         * child is not recoverble if it has no storage data
         */
        ComponentDescription childConfig = (ComponentDescription) ((Prim) value).sfResolve(STORAGE_DATA_ATTR, false);
        if (!Storage.isStorageDescription(childConfig)) {
            sfAddTag(name, VOLATILE_TAG);
            return value;
        }
        
        /**
         * this is our child and it is recoverable, so store the recovery data
         */
        ComponentDescription recoveryData = (ComponentDescription) childConfig.copy();
        recoveryData.setEager(true);
        return recoveryData;
    }

    /**
     * Write the non-volatile attributes of the given context to the storage.
     * The context is assumed to be the context of this component, but it is
     * passed in as a parameter as this method is called before the component
     * has been initialized with the context.
     * 
     * @param context
     *            the context
     * @throws SmartFrogException
     * @throws StorageException
     * @throws RemoteException 
     */
    protected void saveContext(Context context) throws SmartFrogException, StorageException, RemoteException {

        Iterator attributes = context.sfAttributes();
        while (attributes.hasNext()) {

            Object name = attributes.next();
            Object value = context.sfResolveAttribute(name);
            
            /**
             * Do not save the basic SmartFrog attributes if they are default values
             */
            if( SmartFrogCoreKeys.SF_CLASS.equals(name) && RComponent.DEFAULT_IMPL.equals(value) ) {
                continue;
            } else if( SmartFrogCoreKeys.SF_CODE_BASE.equals(name) && "default".equals(value) ) {
                continue;
            } else if( SmartFrogCoreKeys.SF_EXPORT.equals(name) && value.equals(false) ) {
                continue;
            } else if( SmartFrogCoreKeys.SF_SYNC_TERMINATE.equals(name) && value.equals(true) ) {
                continue;
            }
            
            /**
             * if its a child to be deployed don't save the description - it will be replaced
             * by the child itself within this transaction.
             */
            if( value instanceof ComponentDescription && ((ComponentDescription)value).getEager() ) {
                value = name;
            }
            
            /**
             * if its a Prim do the recovery data (can happen in update)
             */
            if( value instanceof Prim ) {
                value = childRecoveryData((String) name, value);
            }

            /**
             * if its volatile skip it 
             */
            if ( isVolatile(name, context) ) {
                continue;
            }

            storage.addAttribute((String) name, (Serializable) context.sfGetTags(name), (Serializable) value, currentTransaction);
        }
    }

    /**
     * Open the storage object.
     * 
     * @param context
     * @throws StorageException
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws StorageException
     * @throws SmartFrogContextException
     */
    private void openStorage(Context context) throws StorageException, SmartFrogContextException {
        ComponentDescription storageData = (ComponentDescription) context.sfResolveAttribute(STORAGE_DATA_ATTR);
        storage = Storage.openStorage(storageData);
        storage.exceptionNotifications(interfaceManager);
    }
    
    /**
     * Save component state data as attributes so they can be recovered. In the
     * general case it would also deal with parentage and child liveness. This
     * method is here as a place holder for that code.
     * 
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    private void saveComponentState() throws SmartFrogRuntimeException, RemoteException {
        // do nothing
    }

    /**
     * Recover component state data from attributes. In the general case it
     * would also deal with parentage and child liveness. This method is here as
     * a place holder for that code.
     * 
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    private void recoverComponentState() throws SmartFrogRuntimeException, RemoteException {
        // do nothing
    }

    /**
     * Create a new application. There is no support for grouping sfCreateNewApp
     * within a larger transaction.
     * 
     * 
     * {@inheritDoc}
     */
    public Prim sfCreateNewApp(String name, ComponentDescription cmp, Context parms) 
    throws RemoteException, SmartFrogDeploymentException {
        if( Transaction.fromParameters(parms).isNull() ) {
            return super.sfCreateNewChild(name, null, cmp, parms);
        } else {
            throw new SmartFrogDeploymentException("Transaction supplied in parms (no support for external transaction)");
        }
    }

    /**
     * Create a child of this component. If the parms contain a sweep transaction attribute the
     * operation will be performed atomically with that transaction. Otherwise it will be performed 
     * atomically with in a new transaction. The behaviour of concurrent deployments is undefined.
     * 
     * {@inheritDoc}
     * 
     * @param name name of attribute which the deployed component should adopt
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successful
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     *
     * 
     */
    public Prim sfCreateNewChild(Object name, ComponentDescription cmp, Context parms)
    throws RemoteException, SmartFrogDeploymentException {
    	if( Transaction.inParameters(parms) ) {
    		return sfCreateNewChild(name, this, cmp, parms, Transaction.fromParameters(parms));
    	} else {
    		return sfCreateNewChild(name, this, cmp, parms, currentTransaction);
    	}
    }
    
    /**
     * create a child for the given parent component. If the parms contain a sweep transaction attribute the
     * operation will be performed atomically with that transaction. Otherwise it will be performed 
     * atomically with in a new transaction. The behaviour of concurrent deployments is undefined.
     * 
     * @param name name of attribute which the deployed component should adopt
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     *
     * @return deployed component if successfull
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewChild(Object name, Prim parent, ComponentDescription cmp, Context parms) 
    throws RemoteException, SmartFrogDeploymentException {
    	if( parent == this && !Transaction.inParameters(parms) ) {
    		return sfCreateNewChild(name, this, cmp, parms, currentTransaction);
    	} else {
    		return sfCreateNewChild(name, parent, cmp, parms, Transaction.fromParameters(parms));
    	}
    }
    
    
    /**
     * {@inheritDoc}
     */
    public Prim sfCreateNewChild(Object name, ComponentDescription cmp, Context parms, Transaction xact) 
    throws RemoteException, SmartFrogDeploymentException {
        return sfCreateNewChild(name, this, cmp, parms, xact);
    }

    /**
     * Create a child under the specified component. This operation is performed atomically with respect to storage
     * within the given transaction but can only be performed in isolation from other deployments. 
     * The behaviour of concurrent deployments is undefined.
     * 
     * @param name name of attribute which the deployed component should adopt
     * @param parent the component that will be the parent of the new child
     * @param cmp compiled component to deploy and start
     * @param parms parameters for description
     * @param xact the transaction to perform this deployment in
     *
     * @return deployed component if successfull
     *
     * @exception SmartFrogDeploymentException failed to deploy compiled
     * component
     * @exception RemoteException In case of Remote/nework error
     */
    public Prim sfCreateNewChild(Object name, Prim parent, ComponentDescription cmp, Context parms, Transaction xact)
    throws RemoteException, SmartFrogDeploymentException {
                
        /**
         * This component can not be involved if it is terminating
         */
    	if( sfIsTerminated || sfIsTerminating ) {
    		StringBuffer message = new StringBuffer();
    		if( sfIsTerminated ) {
    			message.append("This component has termianted, cannot create child");
    		} else {
    			message.append("This component has started terminating, cannot create child");
    		}
    		if( parent != this ) {
    			message.append(" with parent " + parent.sfCompleteName().toString());
    		}
    		message.append(". This components creationTime=" + creationTime);
    		throw new SmartFrogDeploymentException(this.sfCompleteName(), this.sfCompleteName(), name, cmp, parms, message.toString(), null, null);
        }
        
        /**
         * If the parent is null deploy as a new app under the
         * process compound and, if named, add the name to 
         * the parameters.
         */
        if( parent == null ) {
            parent = SFProcess.getProcessCompound();
            if( name != null ) {
               cmp.sfContext().put(SmartFrogCoreKeys.SF_PROCESS_COMPONENT_NAME, name);
            }
        }
        
        /**
         * If the parent is another RComponent pass
         * the deployment on to that component so it can handle its own 
         * locking correctly.
         */
        if( parent instanceof RComponent && parent != this ) {
            return ((RComponent)parent).sfCreateNewChild(name, cmp, parms, xact);
        }
       
        /**
         * We can not handle remote transactions.
         */
        if( xact.isRemote() ) {
            throw new SmartFrogDeploymentException("sfCreateNewChild can not use a remote transaction");
        }
        
        Prim prim = null;
        boolean noTransactionOnEntry = xact.isNull();
        boolean noCurrentTransactionOnEntry = currentTransaction.isNull();
      
        try {
            /**
             * This block must be atomic, if there is no user transaction 
             * then create one for this block.
             */
            if (noTransactionOnEntry) {
                try {
                    xact = storage.getTransaction();
                } catch(StorageException e) {
                    throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward("Create child failed to obtain a transaction", e);
                }
            }
                       
            /**
             * if this is the parent then lock this component for the transaction
             * and set the current transaction to perform core operations
             * within that transaction. Note that the lock will collide if the current
             * transaction is already set to another transaction.
             */
            try {
                if( parent == this ) {
                    lock(xact);
                    currentTransaction = xact;
                }
            } catch(TransactionException e) {
                throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward("Create child failed to aquire lock", e);
            }
    
            /**
             * construct the child component. The transaction is added to the parameters
             * as this is the only way we can pass it into a deployment.
             */
            prim = sfDeployComponentDescription(name, parent, (ComponentDescription)cmp.copy(), xact.addToParameters(parms) );
            
            
            /**
             * Perform deploy and start 
             */
            if( prim instanceof RComponent ) {
                /**
                 * join the child to our transaction, then deploy and start the child, then 
                 * leave the child from the transaction. This causes attribute modifcations etc 
                 * in the sfDeploy or sfStart to happen in our transaction.
                 * In exception terminate the child using sfDetachPendingTermination
                 */                
                RComponent rcomp = (RComponent)prim;
                try {
                    rcomp.sfJoinTransaction(xact);
                    rcomp.sfDeploy();
                    rcomp.sfStart();
                    rcomp.sfLeaveTransaction();
                } catch(Exception e) {
                    try {
                        rcomp.sfDetachPendingTermination(xact);
                    } catch (SmartFrogRuntimeException e1) {
                        if( sfLog().isDebugEnabled() ) {
                            sfLog().debug("Failed to initiate pending termination", e1);
                            sfLog().debug(" - reason for termination", e);
                        }
                    }
                    throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward("Failed attempting to create child", e);
                }
                
            } else {
                /**
                 * deploy and start the child - in exception terminate the child
                 * using sfTermiante
                 */
                try {
                    prim.sfDeploy();
                    prim.sfStart();
                } catch(SmartFrogException e) {
                    prim.sfTerminate(TerminationRecord.abnormal("Failed in deploy or start", sfCompleteNameSafe()));
                    throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward("Failed attempting to create child", e);
                }
            }
            
            /**
             * commit if locally generated transaction
             */
            if( noTransactionOnEntry ) {
                try {
                    xact.commit();
                } catch(StorageException e) {
                    throw (SmartFrogDeploymentException)SmartFrogDeploymentException.forward("Failed attempting to create child", e);
                }
            }
            
        } finally {
            if( noTransactionOnEntry ) {
                commitQuietly(xact);
            }
            if( parent == this && noCurrentTransactionOnEntry ) {
                currentTransaction = Transaction.nullTransaction;
            }
        }
        
        return prim;
    }
    
    /**
     * commit a transaction and discard storage exceptions
     * @param xact the transaction to commit
     */
    protected void commitQuietly(Transaction xact) {
        try { 
            xact.commit();
        } catch(StorageException se) { 
            if(sfLog().isIgnoreEnabled()) 
                sfLog().ignore("transaction commit failed during quiet commit", se); 
        }
    }

    /**
     * Adds an attribute with the given name to this component. The attribute
     * must not already exist. As no attributes are defined this will be
     * non-volatile.
     * 
     * {@inheritDoc}
     * 
     * @param name
     *            name of attribute
     * @param value
     *            value of attribute
     * 
     * @return added attribute if non-existent or null otherwise
     * 
     * @throws SmartFrogRuntimeException -
     *             when name or value are null
     * @throws RemoteException -
     *             In case of Remote/nework error
     */
    public synchronized Object sfAddAttribute(Object name, Object value) throws SmartFrogRuntimeException,
            RemoteException {
        return sfAddAttribute(name, value, currentTransaction);
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized Object sfAddAttribute(Object name, Object value, Transaction xact)
            throws SmartFrogRuntimeException, RemoteException {

        boolean noTransactionOnEntry = xact.isNull();
        Object retvalue = null;
        try {
            /**
             * This block must be atomic, if there is no user transaction 
             * then create one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock this component for the transaction
             */
            lock(xact);

            /**
             * do the smartfrog operation
             */
            retvalue = super.sfAddAttribute(name, value);

            /**
             * don't save if volatile (e.g. a default volatile attr)
             */
            if( isVolatile(name) ) {
                return retvalue;
            }
            
            /**
             * Add it to storage
             */
            storage.addAttribute((String) name, (Serializable) sfGetTags(name), (Serializable) value, xact);

        } finally {
            if (noTransactionOnEntry) {
                // commitQuietly(xact);
                xact.commit();
            }
        }

        return retvalue;
    }

 
    /**
     * Replace named attribute in component context (add if not already there).
     * This method is used to add a child; children are always treated as
     * volatile if they are local. If the child is a recoverable component its
     * storage will be retained to initiate recovery.
     * 
     * {@inheritDoc}
     * 
     * @param name
     *            of attribute to replace
     * @param value
     *            value to add or replace
     * 
     * @return the old value if present, null otherwise
     * 
     * @throws SmartFrogRuntimeException
     *             when name or value are null
     * @throws RemoteException
     *             In case of Remote/nework error
     */
    public synchronized Object sfReplaceAttribute(Object name, Object value) throws SmartFrogRuntimeException,
            RemoteException {
        return sfReplaceAttribute(name, value, currentTransaction);
    }

    
    /**
     * {@inheritDoc}
     */
    public synchronized Object sfReplaceAttribute(Object name, Object value, Transaction xact) throws SmartFrogRuntimeException,
            RemoteException {

        boolean noTransactionOnEntry = xact.isNull();
        Object retvalue = null;
        try {
            /**
             * This block must be atomic, if there is no user transaction 
             * then create one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);
            
            /**
             * do the smartfrog operations
             */
            boolean isNewAttribute = !super.sfContainsAttribute(name);
            retvalue = super.sfReplaceAttribute(name, value);
            
            /**
             * don't save child linkup during recovery if we are expecting the child
             * we already know it - this is performance opt.
             */
            if( isInRecoveryDeployment && sfChildren.contains(value) && value instanceof RComponent ) {
                return retvalue;
            }

            /**
             * Hack to deal with sfAddChild not actually adding the attribute,
             * so we have to check for new children here to notify the model.
             * Ideally sfAddChild should notify the model.
             */
            if (value instanceof Prim) {
                value = childRecoveryData((String) name, value);
            }

            /**
             * Some attributes may be volatile.
             */
            if ( isVolatile(name) ) {
                return retvalue;
            }
            
            /**
             * storage.replaceAttribute needs the attribute to exist in order to
             * replace it, sfReplaceAttribute does not - deal with both cases
             */
            if (isNewAttribute) {
                
                storage.addAttribute((String) name, (Serializable) sfGetTags(name), (Serializable) value,
                        xact);
                
            } else {

                /**
                 * Some attributes may be cached
                 */
                if( sfContainsTag(name, CACHED_TAG) ) {
                    return retvalue;
                }

                storage.replaceAttribute((String) name, (Serializable) value, xact);
            }

        } finally {
            if (noTransactionOnEntry) {
                // commitQuietly(xact);
                xact.commit();
            }
        }

        return retvalue;
    }

    /**
     * Removes an attribute from this component.
     * 
     * {@inheritDoc}
     * 
     * @param name
     *            of attribute to be removed
     * 
     * @return removed attribute value if successful or null if not
     * 
     * @throws SmartFrogRuntimeException
     *             when name is null
     * @throws RemoteException
     *             In case of Remote/network error
     */
    public synchronized Object sfRemoveAttribute(Object name) throws SmartFrogRuntimeException, RemoteException {
        return sfRemoveAttribute(name, currentTransaction);
    }

    
    /**
     * {@inheritDoc}
     */
    public synchronized Object sfRemoveAttribute(Object name, Transaction xact) throws SmartFrogRuntimeException,
    RemoteException {

        Object retvalue;
        boolean noTransactionOnEntry = xact.isNull();

        try {
            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);
            
            /**
             * check fo volatile before removing anything
             * - would lose the tags otherwise
             */
            boolean vol = isVolatile(name);
            
            /**
             * do the smartfrog operation - this is tricky hack.
             * If the attribute is an instance of Liveness then 
             * CompoundImpl.sfRemoveAttribute will call
             * sfRemoveChild without passing a transaction, so we will
             * get a new transaction and a lock failure. By removing
             * the element from sfChildren and checking for that in sfRemoveChild
             * before starting a new transaction we break this loop
             * and avoid the unwanted transaction.
             */
            Object value = sfContext.get(name);
            if( value != null ) {
                sfChildren.removeElement(value);
            }      
            retvalue = super.sfRemoveAttribute(name);

            /**
             * Some attributes may be volatile. 
             */
            if ( vol ) {
                return retvalue;
            }

            /**
             * Remove from storage
             */
            storage.removeAttribute((String) name, xact);

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

        return retvalue;

    }

    /**
     * Creates a new child for this component in a transaction. Note that sfAddChild does
     * not touch attributes, so this has no affect on storage, but it locks the component
     * ahead of the update - can't add the child if can't lock the component.
     * 
     * @param target
     *            target to heartbeat
     */
    public void sfAddChild(Liveness target) throws RemoteException {

        /**
         * sfAddChild has no affect on attributes - see comments in
         * sfReplaceAttribute. Included here for completeness.
         */
        super.sfAddChild(target);
    }
    
    /**
     * {@inheritDoc}
     * 
     * Creates a new child for this component in a transaction. Note that sfAddChild does
     * not touch attributes, so this has no affect on storage, but it locks the component
     * ahead of the update - can't add the child if can't lock the component.
     */
    public void sfAddChild(Liveness target, Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        
        lock(xact);
        
        super.sfAddChild(target);
    }


    /**
     * Removes a specific child
     * 
     * {@inheritDoc}
     * 
     * @param target
     *            object to remove from heartbeat
     * 
     * @return true if child is removed successfully else false
     */
    public boolean sfRemoveChild(Liveness target) throws SmartFrogRuntimeException, RemoteException {
        if( sfChildren.contains(target) ) {
            return sfRemoveChild(target, currentTransaction);
        } else {
            return false;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean sfRemoveChild(Liveness target, Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        boolean retvalue;
        boolean noTransactionOnEntry = xact.isNull();

        try {
            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }
            
            /**
             * lock the component
             */
            lock(xact);

            Object name = sfAttributeKeyFor(target);

            /**
             * sfRemoveChild calls sfRemoveAttribute. The child should be
             * volatile, so we shouldn't need to include this in the transaction -
             * but if for some reason it has been set to non-volatile we want to
             * group its removal with removal of the recovery data.
             */
            retvalue = sfChildren.removeElement(target);
            try {
              sfRemoveAttribute(sfAttributeKeyFor(target), xact);
            } catch (SmartFrogRuntimeException ex) {
              //Ignore: it happens when attribute does not exist
            }
            

            // not used any more (since long time ago)
            //if (sfContainsAttribute(CHILD_RECOVERY_PREFIX + name)) {
            //    sfRemoveAttribute(CHILD_RECOVERY_PREFIX + name, xact);
            //}

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

        return retvalue;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void sfDetachPendingTermination(Transaction xact) throws SmartFrogRuntimeException,
            RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {
            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }
            
            pendingReference = sfCompleteName();
            
            /**
             * lock the component and all components in its subtree for the transaction
             */
            lockTree(xact);

            /**
             * detach the component, leaving it with a dangling parent reference. This
             * will be cleaned up in recovery if this component does not get terminated
             * before failure. Add it to the pending termination list to make sure
             * it gets unloaded as it will not be found under the process compound.
             */
            if( sfParent == null ) {
                // nothing to detach from
            } else if( sfParent instanceof RComponent ) {
                //System.out.println("+++++++++++++ detatching from RComponent parent in " + sfCompleteNameSafe() + " using transaction " + xact);
                ((RComponent)sfParent).sfRemoveChild(this, xact);
            } else {
                //System.out.println("+++++++++++++ detatching from ChildMinder parent in " + sfCompleteNameSafe() + " not using transaction ");
                ((ChildMinder)sfParent).sfRemoveChild(this);
            }
            sfParent = null;
            storage.reparentComponent(Storage.NO_PARENT_PENDING_TERMINATION, xact);
            interfaceManager.getPendingTermination().add(this);
            xact.addPending(myXactTarget);
            sfStartLivenessSender();
            sfParentageChanged();

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

    }
    
    /**
     * {@inheritDoc}
     */
    public void sfReattachPending(String name, ChildMinder parent, Transaction xact)  throws SmartFrogRuntimeException, RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {

            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }
            
            /**
             * lock the component for the transaction
             */
            lock(xact);
            
            sfStopLivenessSender();
            
            if( parent instanceof RComponent ) {
                RComponent rparent = (RComponent)parent;
                /**
                 * Get the parents name
                 */
                ComponentDescription parentStorage = (ComponentDescription)rparent.sfResolve(RComponent.STORAGE_DATA_ATTR);
                String parentName = (String)parentStorage.sfResolve(Storage.COMPONENT_NAME_ATTR);

                /**
                 * Reparent the component and remove it from the pending termination
                 * list. 
                 */
                rparent.sfAddChild(this, xact);
                rparent.sfReplaceAttribute(name, this, xact);
                storage.reparentComponent(parentName, xact);
            } else {
                Compound cparent = (Compound)parent;
                /**
                 * Non transactional changes to parent if not an RComponent (e.g. processc compound)
                 */
                parent.sfAddChild(this);
                ((Prim)parent).sfAddAttribute(name, this);                
            }
            
            sfParent = (Prim)parent;
            interfaceManager.getPendingTermination().remove(this);
            xact.removePending(myXactTarget);
            sfParentageChanged();
            sfStartLivenessSender();
            pendingReference = null;

        } finally {
            if (noTransactionOnEntry) {
                // commitQuietly(xact);
                xact.commit();
            }
        }

    }
    
    
    /**
     * {@inheritDoc}
     */
    public void terminatePending() {
        interfaceManager.getPendingTermination().remove(this);
        sfTerminate(TerminationRecord.normal("Completing pending termination", pendingReference));
    }


    /**
     * {@inheritDoc}
     */
    public void sfAddTag(Object name, String tag) throws RemoteException, SmartFrogRuntimeException {
        sfAddTag(name, tag, currentTransaction);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void sfAddTag(Object name, String tag, Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {

            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);

            /**
             * do the smartfrog operation
             */
            boolean wasNotVolatileButNowIs = (VOLATILE_TAG.equals(tag) && !isVolatile(name));
            super.sfAddTag(name, tag);

            /**
             * if was not volatile but now is remove it from storage
             */
            if (wasNotVolatileButNowIs) {
                storage.removeAttribute((String) name, xact);
                return;
            }

            /**
             * Some attributes may be volatile.
             */
            if ( isVolatile(name) ) {
                return;
            }

            /**
             * update the tags
             */
            storage.setTags((String) name, (Serializable) sfGetTags(name), xact);

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

    }


    /**
     * {@inheritDoc}
     */
    public void sfAddTags(Object name, Set tags) throws RemoteException, SmartFrogRuntimeException {
        sfAddTags(name, tags, currentTransaction);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void sfAddTags(Object name, Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {

            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);

            /**
             * do the smartfrog operation
             */
            boolean wasNotVolatileButNowIs = (tags.contains(VOLATILE_TAG) && !isVolatile(name));
            super.sfAddTags(name, tags);

            /**
             * if was not volatile but now is remove it from storage
             */
            if (wasNotVolatileButNowIs) {
                storage.removeAttribute((String) name, xact);
                return;
            }

            /**
             * Some attributes may be volatile.
             */
            if ( isVolatile(name) ) {
                return;
            }

            /**
             * update the tags
             */
            storage.setTags((String) name, (Serializable) sfGetTags(name), xact);

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfRemoveTag(Object name, String tag) throws RemoteException, SmartFrogRuntimeException {
        sfRemoveTag(name, tag, currentTransaction);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void sfRemoveTag(Object name, String tag, Transaction xact) throws SmartFrogRuntimeException,
            RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {

            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);
            
            /**
             * Do smartfrog operation
             * Can not stop being volatile if it is volatile by default. So it only changes if the tag
             * is the volatile tag, and it was previously tagged volatile, and it is not volatile by default.
             */
            boolean wasVolatileButNowIsnt = (VOLATILE_TAG.equals(tag) && isVolatileByTagOnly(name));
            boolean wasCachedButNowIsnt = (CACHED_TAG.equals(tag) && sfContainsTag(name, CACHED_TAG));
            super.sfRemoveTag(name, tag);

            /**
             * was volatile but now is not so add to storage
             */
            if (wasVolatileButNowIsnt) {
                storage.addAttribute((String) name, (Serializable) sfGetTags(name), (Serializable) sfContext().get(name),
                        xact);
                return;
            }

            /**
             * Some attributes may be volatile.
             */
            if ( isVolatile(name) ) {
                return;
            }
            
            /**
             * Was cached but now isn't so flush current value
             * - assumes not volatile 
             */
            if (wasCachedButNowIsnt) {
                storage.replaceAttribute((String)name, (Serializable) sfContext().get(name), xact);
            }

            /**
             * update the tags
             */
            storage.setTags((String) name, (Serializable) sfGetTags(name), xact);

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void sfRemoveTags(Object name, Set tags) throws RemoteException, SmartFrogRuntimeException {
        sfRemoveTags(name, tags, currentTransaction);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void sfRemoveTags(Object name, Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {

            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);

            /**
             * do the smartfrog operation
             * Can not stop being volatile if it is volatile by default. So it only changes if the tag
             * is the volatile tag, and it was previously tagged volatile, and it is not volatile by default.
             */
            boolean wasVolatileButNowIsnt = (tags.contains(VOLATILE_TAG) && isVolatileByTagOnly(name));
            boolean wasCachedButNowIsnt = (tags.contains(CACHED_TAG) && sfContainsTag(name, CACHED_TAG));
            super.sfRemoveTags(name, tags);

            /**
             * was volatile but now is not so add to storage
             */
            if (wasVolatileButNowIsnt) {
                storage.addAttribute((String) name, (Serializable) sfGetTags(name), (Serializable) sfContext().get(name),
                        xact);
                return;
            }

            /**
             * Some attributes may be volatile.
             */
            if ( isVolatile(name) ) {
                return;
            }
            
            /**
             * Was cached an now isn't so flush current value
             * - assumes not volatile 
             */
            if (wasCachedButNowIsnt) {
                storage.replaceAttribute((String)name, (Serializable) sfContext().get(name), xact);
            }

            /**
             * update the tags
             */
            storage.setTags((String) name, (Serializable) sfGetTags(name), xact);

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void sfSetTags(Object name, Set tags) throws RemoteException, SmartFrogRuntimeException {
        sfSetTags(name, tags, currentTransaction);
    }
    
    /**
     * {@inheritDoc}
     */
    public void sfSetTags(Object name, Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        boolean noTransactionOnEntry = xact.isNull();

        try {

            /**
             * This block must be atomic, if there is no current transaction
             * then start one for this block.
             */
            if (noTransactionOnEntry) {
                xact = storage.getTransaction();
            }

            /**
             * lock the component for the transaction
             */
            lock(xact);

            /**
             * do the smartfrog operation
             * Can not stop being volatile if it is volatile by default. So it only stops being volatile if the tag
             * is the volatile tag, and it was previously tagged volatile, and it is not volatile by default.
             * 
             * It can start being volatile if it we set the volatile tag and it was not previously volatile.
             */
            boolean wasVolatileButNowIsnt = (!tags.contains(VOLATILE_TAG) && isVolatileByTagOnly(name));
            boolean wasNotVolatileButNowIs = (tags.contains(VOLATILE_TAG) && !isVolatile(name));
            boolean wasCachedButNowIsnt = (!tags.contains(CACHED_TAG) && sfContainsTag(name, CACHED_TAG));

            super.sfSetTags(name, tags);

            /**
             * was not volatile but now is so remove from storage
             */
            if (wasNotVolatileButNowIs) {
                storage.removeAttribute((String) name, xact);
                return;
            }

            /**
             * was volatile but now is not so add to storage
             */
            if (wasVolatileButNowIsnt) {
                storage.addAttribute((String) name, (Serializable) sfGetTags(name), (Serializable) sfContext().get(name),
                        xact);
                return;
            }

            /**
             * Some attributes may be volatile.
             */
            if ( isVolatile(name) ) {
                return;
            }
            
            /**
             * Was cached but now isn't so flush current value
             * - assumes not volatile 
             */
            if (wasCachedButNowIsnt) {
                storage.replaceAttribute((String)name, (Serializable) sfContext().get(name), xact);
            }

            /**
             * update the tags
             */
            storage.setTags((String) name, (Serializable) sfGetTags(name), xact);

        } finally {
            if (noTransactionOnEntry) {
                xact.commit();
            }
        }

    }

    
    /**
     * {@inheritDoc}
     */
    public Transaction sfGetTransaction() throws SmartFrogRuntimeException {
        if (storage == null) {
            throw new SmartFrogRuntimeException("Attempt to getTransaction with uninitialized or terminated storage");
        }

        return storage.getTransaction();
    }

    
    /**
     * {@inheritDoc}
     */
    public Reference sfLocalRef() throws SmartFrogRuntimeException {
        Reference ref;        
        ReferencePart hrp; 
        
        try {
            ref = (Reference)sfCompleteName().copy();
            hrp = new ProcessReferencePart();
            ref.setElementAt(hrp, 0);
            return ref;
        } catch (RemoteException e) {
            throw new SmartFrogRuntimeException("Failed to get component reference");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfAddTag(String tag, Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        if( sfParent == null ) {
            return;
        }
        String name = (String)sfParent.sfAttributeKeyFor(this);
        if( sfParent instanceof RComponent) {
            ((RComponent)sfParent).sfAddTag(name, tag, xact);
        } else {
            sfParent.sfAddTag(name, tag);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfAddTags(Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        if( sfParent == null ) {
            return;
        }
        String name = (String)sfParent.sfAttributeKeyFor(this);
        if( sfParent instanceof RComponent) {
            ((RComponent)sfParent).sfAddTags(name, tags, xact);
        } else {
            sfParent.sfAddTags(name, tags);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfRemoveTag(String tag, Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        if( sfParent == null ) {
            return;
        }
        String name = (String)sfParent.sfAttributeKeyFor(this);
        if( sfParent instanceof RComponent) {
            ((RComponent)sfParent).sfRemoveTag(name, tag, xact);
        } else {
            sfParent.sfRemoveTag(name, tag);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfRemoveTags(Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        if( sfParent == null ) {
            return;
        }
        String name = (String)sfParent.sfAttributeKeyFor(this);
        if( sfParent instanceof RComponent) {
            ((RComponent)sfParent).sfRemoveTags(name, tags, xact);
        } else {
            sfParent.sfRemoveTags(name, tags);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfSetTags(Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        if( sfParent == null ) {
            return;
        }
        String name = (String)sfParent.sfAttributeKeyFor(this);
        if( sfParent instanceof RComponent) {
            ((RComponent)sfParent).sfSetTags(name, tags, xact);
        } else {
            sfParent.sfSetTags(name, tags);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void sfLock(Transaction xact) throws SmartFrogRuntimeException, RemoteException {

        if( xact == null ) {
            throw new SmartFrogRuntimeException("null value");
        }

        if( xact.isRemote() ) {
            throw new TransactionException("Attempted to lock using a remote transaction in " + sfCompleteNameSafe());
        }
        
        lock(xact);
    }
    
    /**
     * {@inheritDoc}
     */
    public void sfLockTree(Transaction xact) throws SmartFrogRuntimeException, RemoteException {
        
        if( xact == null ) {
            throw new SmartFrogRuntimeException("null value");
        }
        
        if( xact.isRemote() ) {
            throw new TransactionException("Attempted to lock using a remote transaction in " + sfCompleteNameSafe());
        }
        
        lockTree(xact);
    }    

    /**
     * {@inheritDoc}
     */
    public void lock(Transaction xact) throws TransactionException {
        synchronized(lockMonitor) {
            if( lock == null ) {
                /**
                 * lock the component
                 */
                lock = xact;
                xact.addLock(myXactTarget);
                if( isTracingLocks ) {
                    lockTrace = new Exception("Thread holding lock");
                }

            } else if( !xact.equals(lock) ) {
                /**
                 * Fail to lock the component - held by another transaction
                 */
                ConcurrentTransactionException e = new ConcurrentTransactionException("Concurrent lock exception attempting to lock " + sfCompleteNameSafe() );
                if( isTracingLocks && sfLog().isErrorEnabled() ) {
                    sfLog().error("Lock collision - traces follow...");
                    sfLog().error("Thread blocked by lock is ", e);
                    sfLog().error("Thread holding lock aquired it at ", lockTrace);
                }
                throw e;
            } else {
                /**
                 * lock already held by this transaction
                 * do nothing
                 */
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unlock(Transaction xact) {
        synchronized(lockMonitor) {
            
            if( !xact.equals(lock) ) {
                /**
                 * This case should not happen - it suggests lock corruption 
                 * between obtaining a lock and releasing it. Leave lock alone.
                 * This method does not throw an exception, but it does log the error.
                 * Maybe it should throw an exception?
                 */
                ConcurrentTransactionException e = new ConcurrentTransactionException("Lost lock prior to commit in " + sfCompleteNameSafe());
                if( sfLog().isErrorEnabled() ) {
                    sfLog().error("Lost lock prior to commit in " + sfCompleteNameSafe());
                    if( lock != null && isTracingLocks ) {
                        sfLog().error("Lost lock collision - traces follow...");
                        sfLog().error("Thread that lost the lock is ", e);
                        sfLog().error("Thread holding lock aquired it at ", lockTrace);
                    }
                }
                return;
            }
            
            /**
             * Unlock this component 
             * and notify any threads waiting on it
             */
            if( xact.equals(lock) ) {  // @TODO this conditional is redundant
                lock = null;
                if( isTracingLocks ) {
                    lockTrace = null;
                }
                lockMonitor.notifyAll();
            }
        }
    }
    
    /**
     * Block until the lock is free or it has been closed (held by
     * the null transaction). If the lock is free this will return 
     * true, if it has been closed it will return false. 
     * Note that if the lock is free there is no guarantee that the 
     * lock is not obtained by another transaction by the time this 
     * method returns.
     * 
     * @return true if the lock is free, false if it has been closed.
     */
    protected boolean waitOnLock() {
        synchronized(lockMonitor) {
            
            if( Transaction.nullTransaction.equals(lock) ) {
                return false;
            }
            
            while( lock != null ) {
                try {
                    lockMonitor.wait();
                } catch (InterruptedException e) {
                    // ignore  
                }
            }
            
            return true;
        }
    }
    
    /**
     * An internal method to lock the tree from this component down using the given transaction. 
     * Locking a tree is a two phase operation.
     * first attempt to lock,
     * if successful, complete the locks
     * if not revert the locks
     * @param xact the transaction to use
     * @throws TransactionException if there is a lock collision
     * @throws RemoteException
     */
    public void lockTree(Transaction xact) throws TransactionException, RemoteException {
        
        try {
            /**
             * Attempt to lock
             */
            sfPrepareTreeLock(xact);
            
        } catch( TransactionException e ) {
            /**
             * failed to lock, so revert the locks
             */
            sfRevertTreeLock(xact);
            throw e;
            
        } catch( RemoteException e ) {
            /**
             * shouldn't happen if all local - revert locks
             */
            sfRevertTreeLock(xact);
            throw e;
        }
        
        /**
         * sucess so complete locks
         */
        sfLockTreeLock(xact);
        
    }

    /**
     * {@inheritDoc}
     */
    public void sfLockTreeLock(Transaction xact) throws TransactionException, RemoteException {
        
        synchronized(lockMonitor) {
            if( !xact.equals(prepareLock) ) {
                /**
                 * This case should not happen - it suggests lock corruption during the two phase tree lock.
                 * It means the lock was lost between the prepare phase and the lock phase.
                 */
                ConcurrentTransactionException e = new ConcurrentTransactionException("Lost prepare lock during two phase tree lock in " + sfCompleteNameSafe());
                if( sfLog().isErrorEnabled() ) {
                    sfLog().error("Lost prepare lock during two phase tree lock in " + sfCompleteNameSafe());
                    if( lock != null && isTracingLocks ) {
                        sfLog().error("Lost lock collision - traces follow...");
                        sfLog().error("Thread that lost the lock is ", e);
                        sfLog().error("Thread holding lock aquired it at ", lockTrace);
                    }
                }
                throw e;
                
            } else {
                /**
                 * Completing lock for this component
                 */
                previousLock = null;
                prepareLock = null;
                xact.addLock(myXactTarget);
                if( isTracingLocks ) {
                    previousLockTrace = null;
                }
            }
        }
        
        /**
         * Complete lock for children
         */
        Iterator iter = sfChildren.iterator();
        while( iter.hasNext()) {
            Object obj = iter.next();
            if( obj instanceof RComponent ) {
                RComponent rc = (RComponent)obj;
                rc.sfLockTreeLock(xact);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sfPrepareTreeLock(Transaction xact) throws TransactionException, RemoteException {
        
        if( xact.isRemote() ) {
            throw new TransactionException("Attempted to lock using a remote transaction in " + sfCompleteNameSafe());
        }
        
        synchronized(lockMonitor) {
            if( lock == null ) {
                /**
                 * Not locked - aquire lock for this transaction
                 */
                previousLock = null;
                lock = xact;
                prepareLock = xact;
                if( isTracingLocks ) {
                    previousLockTrace = null;
                    lockTrace = new Exception("Thread holding lock");
                }
                
            } else if( xact.equals(lock) ) {
                /**
                 * Already held by this transaction
                 */
                previousLock = lock;
                prepareLock = xact;
                if( isTracingLocks ) {
                    previousLockTrace = lockTrace;
                }
                
            } else {
                /**
                 * Collision - held by another transaction
                 */
                ConcurrentTransactionException e = new ConcurrentTransactionException("Concurrent lock exception attempting to lock " + sfCompleteNameSafe());
                if( isTracingLocks && sfLog().isErrorEnabled() ) {
                    sfLog().error("Lock collision - traces follow...");
                    sfLog().error("Thread blocked by lock is ", e);
                    sfLog().error("Thread holding lock aquired it at ", lockTrace);
                }             
                throw e;
            }
        }
        
        /**
         * prepare lock for children
         */
        Iterator iter = sfChildren.iterator();
        while( iter.hasNext()) {
            Object obj = iter.next();
            if( obj instanceof RComponent ) {
                RComponent rc = (RComponent)obj;
                rc.sfPrepareTreeLock(xact);
            }
        }
        
    }

    
    /**
     * {@inheritDoc}
     */
    public void sfRevertTreeLock(Transaction xact) throws RemoteException {
        
        synchronized(lockMonitor) {
            if( !xact.equals(prepareLock) ) { 
                /**
                 * did not obtain lock - leave as is and 
                 * return without going over children
                 */
                return;

            } else {

                /**
                 * Revert lock to previous state
                 */
                lock = previousLock;
                previousLock = null;
                prepareLock = null;
                if( isTracingLocks ) {
                    lockTrace = previousLockTrace;
                    previousLockTrace = null;
                }
            }
        }

        /**
         * Revert children
         */
        Iterator iter = sfChildren.iterator();
        while( iter.hasNext()) {
            Object obj = iter.next();
            if( obj instanceof RComponent ) {
                RComponent rc = (RComponent)obj;
                rc.sfRevertTreeLock(xact);
            }
        }
    }

}
