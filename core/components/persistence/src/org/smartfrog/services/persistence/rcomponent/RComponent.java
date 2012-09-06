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

import java.rmi.RemoteException;
import java.util.Set;

import org.smartfrog.services.persistence.storage.ConcurrentTransactionException;
import org.smartfrog.services.persistence.storage.Transaction;
import org.smartfrog.services.persistence.storage.TransactionException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.ChildMinder;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;


/**
 * This is the interface presented by a recoverable component. It extends the
 * Compound interface with methods transactional versions of the compound methods.
 * An RComponent can invoke its methods as part of a larger transaction.
 */
public interface RComponent extends Compound {

	/**
	 * The default implementation of this interface
	 */
    public static final String DEFAULT_IMPL = RComponentImpl.class.getName();
    
    /**
     * The default name used to locate the interface manager
     */
    public static final String INTERFACE_MGR_REF_ATTR = "InterfaceManager";
    
    /**
     * Unloading is differentiated from regular normal termination by
     * giving this string as the reason for termination. Normal termination
     * results in components being terminated and removed from the 
     * persistence store. Unload results in them being terminated from the
     * runtime, but retained in the persistence store for later resurection.
     */
    public static final String UNLOAD_TERMINATION_DESC = "unload";
    
    /**
     * The liveness delay is reset after recovery according to this attribute value
     */
    public static final String LIVENESS_DELAY_RESET = "sfLivenessDelayReset";
    
    /**
     * The liveness factor is reset after recovery according to this attribute value
     */
    public static final String LIVENESS_FACTOR_RESET = "sfLivenessFactorReset";
    
    /**
     * This attribute defines the storage information for this component
     */
    public static final String STORAGE_DATA_ATTR = "sfStorageData";

    /**
     * If an attribute with this name is present in the component description
     * this component is being recovered.
     */
    public static final String RECOVERY_MARKER_ATTR = "sfRecoveryMarker";

    /**
     * If the recovery marker has this value the component being recovered 
     * will be recovered to the recovered state
     */
    public static final String NORMAL_RECOVERY_MARKER_VALUE = "normal";

    /**
     * If the recovery marker has this value the component being recovered
     * will be terminated after recovery
     */
    public static final String TERMINATE_RECOVERY_MARKER_VALUE = "terminate";

    /**
     * The attribute containing the transaction used for deployment
     */
    public static final String SWEEP_TRANSACTION_ATTR = "sfSweepTransaction";

    /**
     * If this attribute is present and has the boolean value true it will 
     * use lazy loading to recover its children
     */
    public static final String LAZY_LOADING_ATTR = "sfLazyLoading";

    /**
     * TODO not used?
     */
    public static final String CHILD_RECOVERY_PREFIX = "sfChildRecovery_";

    /**
     * An attribute with this tag is not persisted
     */
    public static final String VOLATILE_TAG = "sfVolatile";
    
    /**
     * An attribute with this tag is persisted with its initial value but never updated in the store
     */
    public static final String CACHED_TAG = "sfCached";
    
    /**
     * If this property is present and set to true the component generates timing information and
     * records it in the log
     */
    public static final String DIAG_TIMING_PROP = "org.smartfrog.services.persistence.diagnostic.timing";
    
    /**
     * If this property is present and set to true the component keeps a stack trace of the thread 
     * obtaining its lock and logs it in the event of lock collisions
     */
    public static final String LOCK_TRACING_PROP = "org.smartfrog.services.persistence.rcomponent.traceLocks";
    
    /**
     * This is a list of smartfrog internal attributes that are volatile by definition.
     */
    public static final String[] SF_VOLATILE_ATTRS = {
        SmartFrogCoreKeys.SF_HOST,
        SmartFrogCoreKeys.SF_PROCESS,
        "sfPort",							// do not save - port is derived from sfExportPort and local process compound at runtime
        SmartFrogCoreKeys.SF_APP_LOG_NAME,
        SmartFrogCoreKeys.SF_BOOT_DATE,
        SmartFrogCoreKeys.SF_TIME_PARSE,
        SmartFrogCoreKeys.SF_TIME_DEPLOY,
        SmartFrogCoreKeys.SF_TIME_START,
        SmartFrogCoreKeys.SF_TIME_STARTED_AT,
        "sfTraceDeployLifeCycle",
        "sfTraceStartLifeCycle",
        SmartFrogCoreKeys.SF_LIVENESS_DELAY, // should this be excluded?
        SmartFrogCoreKeys.SF_LIVENESS_FACTOR,// should this be excluded?
        SWEEP_TRANSACTION_ATTR,
        STORAGE_DATA_ATTR // don't save own storage data in storage (picked up from parent or register)
    };
    
    /**
     * Obtain a transaction for use in transactional calls to this interface. Note that the transactional
     * interface only guarantees transactional semantics with regard to updates to the recovery backing store. No 
     * transaction semantics are guaranteed for access to the runtime components. For this interface, transactions
     * can only be used to make updates atomic with respect to crash failures. It is the user's responsibility to
     * commit the transaction to finish it and free the resources in the database. If the commit fails the recovery 
     * store may be left inconsistent with the SmartFrog runtime components. 
     * 
     * @return Transaction - a transaction object
     * @throws SmartFrogRuntimeException - if there is a problem updating the recovery store
     */
    public Transaction sfGetTransaction() throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Locks this component with the given transaction. The component will be unlocked when
     * the transaction is committed - the lock can be over-ridden by the system to deactivate 
     * the component.
     * 
     * @throws SmartFrogRuntimeException - if null transaction
     * @throws TransactionException - if invalid transaction
     * @throws ConcurrentTransactionException - if can not obtain lock
     * @throws RemoteException
     */
    public void sfLock(Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Locks all components in the subtree below and including this component. If it can't lock them
     * all they are left unlocked.
     * 
     * @param xact - the locking transaction
     * @throws SmartFrogRuntimeException - if null transaction
     * @throws TransactionException - if invalid transaction
     * @throws ConcurrentTransactionException - if can not obtain lock
     * @throws RemoteException
     */
    public void sfLockTree(Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Performs the prepare phase of a two phase lock operation across this component and 
     * all its descendants.
     * 
     * @param xact - the locking transaction
     * @throws TransactionException - if invalid transaction
     * @throws ConcurrentTransactionException - if can not obtain lock
     * @throws RemoteException
     */
    public void sfPrepareTreeLock(Transaction xact) throws TransactionException, RemoteException;
    
    /**
     * Performs the lock phase of a two phase lock operation across this component and 
     * all its descendants.
     * 
     * @param xact - the locking transaction
     * @throws TransactionException - if invalid transaction
     * @throws ConcurrentTransactionException - if can not obtain lock
     * @throws RemoteException
     */
    public void sfLockTreeLock(Transaction xact) throws TransactionException, RemoteException;
    
    /**
     * Undoes the prepare phase of a two phase lock operation across this component and 
     * all its descendants.
     * 
     * @param xact - the locking transaction
     * @throws RemoteException
     */
    public void sfRevertTreeLock(Transaction xact) throws RemoteException;
    
    /**
     * Create a child of this component. This operation is performed atomically with respect to storage
     * within the given transaction but can only be performed in isolation from other deployments. 
     * The behaviour of concurrent deployments is undefined.
     * 
     * @param name attribute name for the new child
     * @param cmp component description of the new child
     * @param parms additional parameters
     * @param xact the transaction to create the child in
     * @return Prim - the new child
     * @throws RemoteException
     * @throws SmartFrogDeploymentException
     */
    public Prim sfCreateNewChild(Object name, ComponentDescription cmp, Context parms, Transaction xact)
    throws RemoteException, SmartFrogDeploymentException;
    
    /**
     * Sets this component and all its children to participate in the given transaction.
     * After this call all modifications to this component or any of its children will
     * be applied within this transaction by default. 
     * 
     * @param xact
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    public void sfJoinTransaction(Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * internal method for propagating transaction join
     * @param xact the transaction to join
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void joinTransaction(Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Sets this component and all its children to stop participating in the
     * given transaction. After this call all modifications to this component
     * or any of its children will be applied in separate transactions by default.
     * 
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfLeaveTransaction() throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * This method can be called on any component in a hierarchy and will
     * causes all components in the hierarchy to go through normal termination 
     * without removing the storage of any recoverable components. This is 
     * essentially "paging out" the runtime components. They can be
     * restored to the runtime by recovery from the storage.
     * 
     * @throws RemoteException - in case of remote/network error
     */
    public void sfUnload() throws RemoteException;
    
    /**
     * Adds an attribute with the given name to this component. The attribute
     * must not already exist. As no attributes are defined this will be
     * non-volatile. Done within the given transaction or a new one if null.
     * 
     * @param name name of attribute
     * @param value value of attribute
     * @param xact transaction to use
     * @return added attribute if non-existent or null otherwise
     * @throws SmartFrogRuntimeException when name or value are null
     * @throws RemoteException In case of Remote/nework error
     */
    public Object sfAddAttribute(Object name, Object value, Transaction xact) throws SmartFrogRuntimeException,
            RemoteException;

    /**
     * Replace named attribute in component context (add if not already there).
     * This method is used to add a child; children are always treated as
     * volatile if they are local. If the child is a recoverable component its
     * storage will be retained to initiate recovery.
     * 
     * @param name
     *            of attribute to replace
     * @param value
     *            value to add or replace
     * @param xact
     *            the transaction for grouping this operation
     * 
     * @return the old value if present, null otherwise
     * 
     * @throws SmartFrogRuntimeException
     *             when name or value are null
     * @throws RemoteException
     *             In case of Remote/nework error
     */
    public Object sfReplaceAttribute(Object name, Object value, Transaction xact) throws SmartFrogRuntimeException,
            RemoteException;

    /**
     * Removes an attribute from this component. 
     * 
     * @param name
     *            of attribute to be removed
     * @param xact
     *            the transaction to group this operation
     * 
     * @return removed attribute value if successfull or null if not
     * 
     * @throws SmartFrogRuntimeException
     *             when name is null
     * @throws RemoteException
     *             In case of Remote/nework error
     */
    public Object sfRemoveAttribute(Object name, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Removes a specific child
     * 
     * @param target object to remove 
     * @param xact transaction to use
     * @return true if child is removed successfully else false
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public boolean sfRemoveChild(Liveness target, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Adds a specific child
     * 
     * @param target object to add
     * @param xact transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfAddChild(Liveness target, Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Detach a component and hold it for termination after the current transaction is committed. Components are
     * terminated asynchronously in a separate transaction each. If a component were terminated using sfDetachAndTerminate
     * and a failure occurred before the initiating transaction were committed, it would be possible for the transaction that actually
     * does the termination to complete successfully when the transaction that did the detach aborted. The result on recovery would 
     * be a parent linked to a child that does not exist. This problem is avoided by deferring the termination until after the
     * detaching transaction has completed. If the detach transaction is completed but the termination is not the child will
     * be an orphan and will therefore be terminated on recovery.
     * 
     * @param xact transaction to use for the detach
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfDetachPendingTermination(Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Reattach a component that has been detached with sfDetachPending. A reattached component will not be termianted.
     * A component can be reattched to a different component to the one it was detached from. The change of parentage 
     * will be atomic. This call is only valid from the same transaction that was used in the corresponding
     * sfDetachPendingTermination.
     * 
     * @param name name to use for the component
     * @param parent parent to reattach to
     * @param xact transaction to use for the attach
     * @throws SmartFrogRuntimeException 
     * @throws RemoteException
     */
    public void sfReattachPending(String name, ChildMinder parent, Transaction xact)  throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Add a tag to an attribute of the component in a transaction
     * @param name the name of the attribute
     * @param tag the tag to add
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfAddTag(Object name, String tag, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Add a set of tags to an attribute of the component in a transaction
     * @param name the name of the attribute
     * @param tags the set of tags to add
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfAddTags(Object name, Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Remove a tag from an attribute in a transaction
     * @param name the name of the attribute
     * @param tag the tag to remove
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfRemoveTag(Object name, String tag, Transaction xact) throws SmartFrogRuntimeException,
            RemoteException;

    /**
     * Remove a set of tags from an attribute in a transaction 
     * @param name the name of the attribute
     * @param tags the set of tags to remove
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfRemoveTags(Object name, Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Set the tags of an attribute in a transaction. This will replace any existing tags set for the attribute.
     * @param name the name of the attribute 
     * @param tags the tags to set
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfSetTags(Object name, Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Add a tag to this component in the parent in a transaction
     * @param tag the tag to add
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfAddTag(String tag, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Add a set of tags to this component in the parent in a transaction
     * @param tags the tags to add
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfAddTags(Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Remove a tag from this component in the parent in a transaction.
     * @param tag the tag to remove
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfRemoveTag(String tag, Transaction xact) throws SmartFrogRuntimeException,
            RemoteException;

    /**
     * Remove a set of tags from this component in the parent in a transaction
     * @param tags the set tags to remove
     * @param xact
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfRemoveTags(Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Set the tags for this component in the parent in a transaction. This will replace any existing tags for this component.
     * @param tags the set of tags
     * @param xact the transaction to use
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void sfSetTags(Set tags, Transaction xact) throws SmartFrogRuntimeException, RemoteException;
    
    /**
     * Get a reference for this component in the form <code>HOST localhost:rootProcess:&ltcomponent path&gt</code>
     * where %ltcomponent path&gt</code> is the path to the component.
     * @return the reference
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public Reference sfLocalRef() throws SmartFrogRuntimeException, RemoteException;
    
}
