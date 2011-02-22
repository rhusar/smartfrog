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

import java.io.ObjectStreamException;
import java.io.Serializable;

import org.smartfrog.services.persistence.rcomponent.RComponent;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Copying;




/**
 * Transaction is the object that is used to group operations on the 
 * recoverable components. It is replaced with a singleton
 * null instance if serialized. The null instance identifies itself as a
 * representative of a remote object.
 * 
 * Note: the null instance is a singleton, so all remote transactions are equal
 * wrt equals() and hashCode().
 * 
 * This base class is invalid and must be extended with an implementation.
 */
public class Transaction implements Serializable, Copying {

    public static final Transaction remoteTransaction = new Transaction();
    public static final Transaction nullTransaction = new Transaction();
    
    protected Transaction() {
    }
    
    /**
     * Set this transaction as reusable or not.
     *  
     * @param reuse true for reusable, false for not reusable
     * @throws TransactionException
     */
    public void setReusable(boolean reuse) throws TransactionException {
        if (isRemote()) {
            throw new TransactionException("Attempt to set reuse on a remote transaction object");
        }
    }
    
    /**
     * Add a lock to this transaction.
     * 
     * @param lock the lock
     * @throws TransactionException
     */
    public void addLock(TransactionTarget lock) throws TransactionException {
        if (isRemote()) {
            throw new TransactionException("Attempt to add lock to a remote transaction object");
        }
    }
    
    /**
     * Add a component that is pending termination to this transaction. These are locked by this 
     * transaction. 
     * 
     * @param lock the pending component 
     * @throws StorageException
     */
    public void addPending(TransactionTarget lock) throws StorageException {
        if (isRemote()) {
            throw new StorageException("Attempt to add pending to a remote transaction object");
        }
    }
    
    /**
     * Remove a component that is pending termination from this transaction. These are locked by
     * this transaction.
     * 
     * @param lock the pending component
     * @throws StorageException
     */
    public void removePending(TransactionTarget lock) throws StorageException {
        if (isRemote()) {
            throw new StorageException("Attempt to remove pending from a remote transaction object");
        }
    }

    /**
     * Commit the transaction.
     * 
     * @throws StorageException
     */
    public void commit() throws StorageException {
        if (isRemote()) {
            throw new StorageException("Attempt to commit remote transaction object");
        }
    }

    /**
     * THIS IS FOR INTERNAL USE ONLY - PERSISTENCE TRANSACTIONS WILL LEAVE THE STORE AND 
     * THE RUNTIME INCONSISTENT IF ABORT IS USED.
     * 
     * This method is used to abort the transaction. This is used internally to end operations
     * such as unsuccessful deployment, where the smartfrog framework will clean up the runtime 
     * and the transaction abort cleans up the storage. 
     * 
     * @throws StorageException
     */
    public void abort() throws StorageException {
        if (isRemote()) {
            throw new StorageException("Attempt to abort remote transaction object");
        }
    }

    /**
     * Checks to see if this is a remote transaction.
     * 
     * @return true if this is the remote transaction, false otherwise
     */
    public boolean isRemote() {
        return this.equals(remoteTransaction);
    }

    /**
     * Checks if this is the null transaction.
     * 
     * @return true if this is the null transaction, false otherwise
     */
    public boolean isNull() {
        return this.equals(nullTransaction);
    }

    /**
     * By default transactions can not be passed remotely. This method overrides
     * the serialization behaviour to replace this transaction with the remote
     * transaction if it is serialized. If a transaction that can be passed remotely
     * (such as a distributed transaction) is implemented, it should override this 
     * method appropriately.
     * 
     * @return the remote transaction
     * @throws ObjectStreamException
     */
    protected Object writeReplace() throws ObjectStreamException {
        return remoteTransaction;
    }

    /**
     * By default transactions can not be passed remotely. This method overrides
     * the deserialization behaviour to replace this transaction with the remote
     * transaction if it is deserialized. If a transaction that can be passed remotely
     * (such as a distributed transaction) is implemented, it should override this 
     * method appropriately.
     * 
     * @return the remote transaction
     * @throws ObjectStreamException
     */
    protected Object readResolve() throws ObjectStreamException {
        return remoteTransaction;
    }

    /**
     * {@inheritDoc}
     */
    public Object copy() {
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object clone() {
        return this;
    }
      
    /**
     * Adds this transaction to the given context (parameter set) 
     * and returns the modified context. If the context is null this will 
     * return a new context with just this one attribute.
     * The new attribute has the sweep transaction name.
     * 
     * @param parms the context of parameters or null
     * @return the modified context or a new context if was null
     */
    public Context addToParameters(Context parms) {
        Context context;
        if( parms == null ) {
            context = new ContextImpl();
        } else {
            context = parms;
        }
        context.put(RComponent.SWEEP_TRANSACTION_ATTR, this);
        return context;
    }

    /**
     * Create a context with this transaction in it as an attribute.
     * The attribute name will be the sweep transaction.
     * 
     * @return a new context with this transaction as its only attribute 
     */
    public Context asParamater() {
        Context context = new ContextImpl();
        context.put(RComponent.SWEEP_TRANSACTION_ATTR, this);
        return context;
    }
    
    /**
     * Returns true if the parms contains a sweep transaction, false otherwise.
     * @param parms the parms context
     * @return true if parms contains a sweep transaction, false otherwise
     */
    public static boolean inParameters(Context parms) {
    	return ( parms != null && parms.containsKey(RComponent.SWEEP_TRANSACTION_ATTR) );
    }
    
    /**
     * Get a transaction from the given context or return the null
     * transaction if there was no transaction attribute. The transaction
     * attribute is assumed to be named after the sweep transaction.
     * 
     * @param parms the parms context
     * @return the sweep transaction if it was in parms, the null transaction otherwise
     */
    public static Transaction fromParameters(Context parms) {
    	if( inParameters(parms) ) {
    		return (Transaction)parms.get(RComponent.SWEEP_TRANSACTION_ATTR);
    	} else {
    		return Transaction.nullTransaction;
    	}
    }
}
