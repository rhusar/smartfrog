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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * TransactionImpl is an implementation of a Transaction base class that 
 * wraps JDBC connections. It does not implement distributed transactions, 
 * so it inherits the Transaction behaviour of repalcing itself with the invalid
 * remote transaction object when serialized. 
 */
public class TransactionImpl extends Transaction {

    private StorageImpl storage;
    private Connection connection;
    private boolean reuse = false;
    protected Set<TransactionTarget> pending = new HashSet<TransactionTarget>();
    protected Set<TransactionTarget> locks = new HashSet<TransactionTarget>();

    /**
     * Construct a transaction with the given JDBC connection and storage
     * 
     * @param connection the JDBC connection
     * @param s the storage
     * @throws SQLException
     */
    public TransactionImpl(Connection connection, StorageImpl s) throws SQLException {
        super();
        this.connection = connection;
        this.connection.setAutoCommit(false);
        storage = s;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setReusable(boolean reuse) {
        this.reuse = reuse;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void addLock(TransactionTarget lock) {
        locks.add(lock);
   }
    
    /**
     * unlocks the list of locks
     */
    protected void unlockLockedComponents() {
        for( TransactionTarget lock : locks ) {
            lock.unlock(this);
        }
        locks.clear();
    }
    
    /**
     * terminates the list of components pending termination
     */
    protected void terminatePending() {
        for( TransactionTarget target : pending ) {
            target.terminatePending();
        }
        pending.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized void addPending(TransactionTarget lock) {
        pending.add(lock);
    }
    
    /**
     * {@inheritDoc}
     */
    public synchronized void removePending(TransactionTarget lock) {
        pending.remove(lock);
    }

    /**
     * Get the connection for this transaction 
     * @return the connection
     */
    public synchronized Connection getConnection() {
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void commit() throws StorageException {
        
        if( connection == null ) {
            throw new StorageException("Attempt to commit a closed transaction");
        }
        
        try {
            connection.commit();
        } catch (SQLException e) {
            throw storage.wrapSQLException("Attempting to commit a transaction", e);
        } finally {
            close();
            unlockLockedComponents();
            terminatePending();
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void abort() throws StorageException {
        
        if( connection == null ) {
            throw new StorageException("Attempt to abort a closed transaction");
        }
        
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw storage.wrapSQLException("Attempting to commit a transaction", e);
        } finally {
            close();
            unlockLockedComponents();
            terminatePending();
        }
    }

    /**
     * If this transaction cannot be reused this will close the connection.
     * If this transaction can be reused this will do nothing. Transactions
     * must be committed before closing.
     */
    protected void close() {
        
        if( connection == null ) {
            return;
        }
        
        if(reuse) {
           return; 
        }
        
        try {
            connection.setAutoCommit(true);
        } catch (Exception e) {
        }
        try {
            connection.close();
        } catch (Exception e) {
        }
        connection = null;
    }

}
