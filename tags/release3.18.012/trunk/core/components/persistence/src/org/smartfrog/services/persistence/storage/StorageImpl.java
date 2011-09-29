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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.smartfrog.services.persistence.database.SQLDialect;
import org.smartfrog.services.persistence.database.Statements;
import org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool;
import org.smartfrog.services.persistence.framework.connectionpool.SQLPoolAccessException;
import org.smartfrog.services.persistence.framework.connectionpool.SQLPoolClosedException;
import org.smartfrog.services.persistence.framework.connectionpool.SQLPoolTimeoutException;
import org.smartfrog.services.persistence.rcomponent.UniqueNameGenerator;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.processcompound.SFProcess;



/**
 * StorageImpl is an implementation of Storage that adapts to a JDBC interface
 * to a database.
 */
public class StorageImpl extends Storage {

    private LogSF log = LogFactory.getLog(this.getClass().toString());

    /**
     * flag to indicate the storage has been closed
     */
    private boolean isClosed = true;
    
    /**
     * the target for storage exception notifications
     */
    private StorageExceptionNotification storageExceptionReceiver = null;
    
    /**
     * the SQL dialect for this storage
     */
    private SQLDialect sqlDialect = null;
    
    /**
     * the SQL statements to use for this storage
     */
    private Statements statements = null;

    /**
     * the storage description for this storage
     */
    private ComponentDescription config = null;

    /**
     * the unique name for this storage
     */
    private String componentName = null;

    /**
     * the connection pool for this storage
     */
    private ConnectionPool connectionPool = null;

    /**
     * Opens access to the storage from this component via a connection pool.
     * If no connection pool is specified it looks for it in the default location.
     *
     * {@inheritDoc}
     */
    public void openStorage0(ComponentDescription config) throws StorageException {
        try {
            this.config = config;
            componentName = UniqueNameGenerator.getUniqueName(config);
            if( config.sfContainsAttribute(CONNECTION_POOL_ATTR) ) {
                connectionPool = (ConnectionPool) config.sfResolve(CONNECTION_POOL_ATTR);
            } else {
                connectionPool = (ConnectionPool)SFProcess.getProcessCompound().sfResolve(CONNECTION_POOL_ATTR);
            }
            isClosed = false;
            sqlDialect = connectionPool.getDialect();
            statements = connectionPool.getStatements();
        } catch (SmartFrogRuntimeException e) {
            throw new StorageException("Failed to resolve configuration: config=" + config, e);
        } catch (RemoteException e) {
            throw (StorageException)StorageException.forward("Failed to obtain a connection pool", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to close inactive storage");
        }

        componentName = null;
        connectionPool = null;
        isClosed = true;
    }

    /**
     * {@inheritDoc}
     * 
     * A transaction corresponds to a JDBC connection obtained from
     * the connection pool.
     */
    public Transaction getTransaction() throws StorageException {
        try {
            return new TransactionImpl(connectionPool.getConnection(), this);
        } catch (SQLException e) {
            throw wrapSQLException("Failed to construct transaction", e);
        } 
    }

    /**
     * {@inheritDoc}
     */
    public void initialiseRegister(Transaction xact) throws StorageException {
        
        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        Statement createRegStmt = null;
        ResultSet rs = null;

        try {
        	
        	conn = getConnection(xact);
        	createRegStmt = conn.createStatement();
        	createRegStmt.execute(statements.createRegistrationTableSQL);

        } catch (SQLException e) {
        	
        	if( !sqlDialect.isErrorType(SQLDialect.ErrorType.TABLE_ALREADY_EXISTS, e.getErrorCode()) ) {
        		throw wrapSQLException("Failed initializing registration table", e);
        	}
        	
        } finally {
        	if( rs != null ) {
        		try {
					rs.close();
				} catch (SQLException e) {
				}
        	}
            if (createRegStmt != null) {
                try {
                    createRegStmt.close();
                } catch (Exception e) {
                }
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void initialiseAttributes(Transaction xact) throws StorageException {
        
        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        Statement createRegStmt = null;
        ResultSet rs = null;

        try {
        	
        	conn = getConnection(xact);
        	createRegStmt = conn.createStatement();
        	createRegStmt.execute(statements.createAttributesTableSQL);

        } catch (SQLException e) {

        	if( !sqlDialect.isErrorType(SQLDialect.ErrorType.TABLE_ALREADY_EXISTS, e.getErrorCode()) ) {
        		throw wrapSQLException("Failed initializing attributes table", e);
        	}
        	
        } finally {
        	if( rs != null ) {
        		try {
					rs.close();
				} catch (SQLException e) {
				}
        	}
            if (createRegStmt != null) {
                try {
                    createRegStmt.close();
                } catch (Exception e) {
                }
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Set getOrphanRoots(Transaction xact) throws StorageException {
        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Set result = new HashSet();

        try {
            conn = getConnection(xact);
            stmt = conn.createStatement();
            rs = stmt.executeQuery( statements.getOrphanRegistrationsSQL );
            
            while( rs.next() ) {
                result.add( fromInputStream(rs.getBinaryStream(1)) );
            }
            
            if (log.isTraceEnabled()) {
                log.trace("Got orphan registrations");
            }
            
            return result;

        } catch (SQLException e) {
            throw wrapSQLException("Trying to get orphan components", e);
        } catch (IOException e) {
            throw new StorageException("Trying to get orphan component storage description", e);
        } catch (ClassNotFoundException e) {
            throw new StorageException("Trying to get orphan component storage description", e);
        } catch (SmartFrogException e) {
            throw new StorageException("Trying to get orphan component storage description", e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
        
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set getRecoveryRoots(Transaction xact) throws StorageException {
        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        Set result = new HashSet();

        try {
            conn = getConnection(xact);
            stmt = conn.createStatement();
            rs = stmt.executeQuery( statements.getRootRegistrationsSQL );
            
            while( rs.next() ) {
                result.add( fromInputStream(rs.getBinaryStream(1)) );
            }
            
            if (log.isTraceEnabled()) {
                log.trace("Got recovery root registrations");
            }
            
            return result;

        } catch (SQLException e) {
            throw wrapSQLException("Trying to recovery root components", e);
        } catch (IOException e) {
            throw new StorageException("Trying to get recovery root component storage description", e);
        } catch (ClassNotFoundException e) {
            throw new StorageException("Trying to get recovery root component storage description", e);
        } catch (SmartFrogException e) {
            throw new StorageException("Trying to get recovery root component storage description", e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean exists(Transaction xact) throws StorageException {
        return getStorageDescription(componentName, xact) != null;
    }
    
    /**
     * Gets the storage description for the named component. This is obtained from
     * the registration table.
     * 
     * @param name the unique name of the component
     * @param xact the transaction to use
     * @return the storage description
     * @throws StorageException 
     */
    public ComponentDescription getStorageDescription(String name, Transaction xact) throws StorageException {
        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ComponentDescription result = null;

        try {
            conn = getConnection(xact);
            stmt = conn.prepareStatement(statements.getRegistrationSQL);
            stmt.setString(1, name);
            rs = stmt.executeQuery();
            
            if( rs.next() ) {
                result = (ComponentDescription)fromInputStream(rs.getBinaryStream(1));
                if (log.isTraceEnabled()) {
                    log.trace("Got registered storage description for " + name);
                }
            } else {
                result = null;
                if (log.isTraceEnabled()) {
                    log.trace("No registration found for " + name);
                }
            }

            return result;

        } catch (SQLException e) {
            throw wrapSQLException("Trying to get storage description for registered component", e);
        } catch (IOException e) {
            throw new StorageException("Trying to get storage description for registered component", e);
        } catch (ClassNotFoundException e) {
            throw new StorageException("Trying to get storage description for registered component", e);
        } catch (SmartFrogException e) {
            throw new StorageException("Trying to get storage description for registered component", e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
        
    }
    


    /**
     * {@inheritDoc}
     */
    public void createComponent(String localParent, Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        PreparedStatement regStmt = null;

        try {
            /**
             * these operations are atomic
             */
            conn = getAtomicConnection(xact);
            regStmt = conn.prepareStatement( statements.addRegistrationSQL );
            regStmt.setString(1, componentName);
            regStmt.setString(2, localParent);
            byte[] storageBytes = toBytes((Serializable)config);
            regStmt.setBinaryStream(3, new ByteArrayInputStream(storageBytes), storageBytes.length);
            regStmt.executeUpdate();
            

            if (log.isTraceEnabled()) {
                log.trace(componentName + " entered in registration table with parent " + localParent );
            }

        } catch (IOException e) {
            throw new StorageException("Failed to create component " + componentName + " in storage with parent " + localParent, e);
        } catch (SQLException e) {
            throw wrapSQLException("Failed to create component " + componentName + " in storage with parent " + localParent, e);
        } catch (SmartFrogResolutionException e) {
            throw new StorageException("Failed to create component " + componentName + " in storage with parent " + localParent, e);
        } finally {
            try {
                regStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteComponent(Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to delete inactive storage");
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement regStmt = null;

        try {
            /**
             * these operations have to be atomic
             */
            conn = getAtomicConnection(xact);
            
            /**
             * remove the component's registration.
             * Attributes cascade the delete through a foreign key reference.
             */
            regStmt = conn.prepareStatement( statements.removeRegistrationSQL );
            regStmt.setString(1, componentName);
            regStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Component registration for " + componentName + " removed ok.");
            }

        } catch (SQLException e) {
            throw wrapSQLException("Failed trying to delete " + componentName, e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
            }
            try {
                regStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }
    
    

 

    /**
     * {@inheritDoc}
     */
    @Override
    public void reparentComponent(String localParent, Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to reparent inactive storage");
        }

        Connection conn = null;
        PreparedStatement regStmt = null;

        try {

            conn = getConnection(xact);
            regStmt = conn.prepareStatement( statements.reparentRegistrationSQL );
            regStmt.setString(1, localParent);
            regStmt.setString(2, componentName);
            regStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Component " + componentName + " reparented to parent " + localParent);
            }

        } catch (SQLException e) {
            throw wrapSQLException("Failed trying to reparent component " + componentName + " to parent " + localParent, e);
        } finally {
            try {
                regStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Context getContext(Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        PreparedStatement getAllAttributesStmt = null;
        Connection conn = null;
        ResultSet rs = null;
        Context retvalue = null;

        try {
            Context context = new ContextImpl();

            conn = getConnection(xact);
            getAllAttributesStmt = conn.prepareStatement( statements.getAllAttributesSQL );
            getAllAttributesStmt.setString(1, componentName);
            rs = getAllAttributesStmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                Object tags = TagsString.fromString(rs.getString(2));
                Object value = fromInputStream(rs.getBinaryStream(3));
                context.put(name, value);
                context.sfAddTags(name, (Set) tags);
            }
            
            if( context.isEmpty() && !exists(xact) ) {
                throw new StorageNoSuchTableException("Cannot get component " + componentName + " does not exist in the database");
            }

            retvalue = context;

        } catch (SQLException e) {
            throw wrapSQLException("Trying to get all rows for " + componentName, e);
        } catch (IOException e) {
            throw new StorageException("Trying to deserialize a value retrieved for " + componentName, e);
        } catch (ClassNotFoundException e) {
            throw new StorageException("Trying to deserialize a value retrieved for " + componentName, e);
        } catch (StorageNoSuchTableException e) {
            throw e;
        } catch (SmartFrogException e) {
            throw new StorageException("Trying to populate context with data retrieved for " + componentName, e);
        } finally {
            try {
                rs.close();
            } catch (Exception e) {
            }
            try {
                getAllAttributesStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }

        return retvalue;
    }

    /**
     * {@inheritDoc}
     */
    public void addAttribute(String name, Serializable tags, Serializable value, Transaction xact)
            throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        Connection conn = null;
        PreparedStatement addAttributeStmt = null;

        try {

            conn = getConnection(xact);
            addAttributeStmt = conn.prepareStatement( statements.addAttributeSQL );
            byte[] valueBytes = toBytes(value);
            byte[] tagBytes = toBytes(tags);
            addAttributeStmt.setString(1, componentName);
            addAttributeStmt.setString(2, name);
            addAttributeStmt.setString(3, TagsString.toString((Set)tags) );
            addAttributeStmt.setBinaryStream(4, new ByteArrayInputStream(valueBytes), valueBytes.length);
            addAttributeStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Added row: " + componentName + " - " + tags + name + " = " + value);
            }

        } catch (IOException e) {
            throw new StorageException("Trying to add row: " + componentName + " - " + tags + name + " = " + value, e);
        } catch (SQLException e) {
            throw wrapSQLException("Trying to add row: " + componentName + " - " + tags + name + " = " + value, e);
        } catch (SmartFrogResolutionException e) {
            throw new StorageException("Trying to add row: " + componentName + " - " + tags + name + " = " + value, e);
        } finally {
            try {
                addAttributeStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeAttribute(String name, Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        PreparedStatement removeAttributeStmt = null;
        Connection conn = null;

        try {

            conn = getConnection(xact);
            removeAttributeStmt = conn.prepareStatement( statements.removeAttributeSQL );
            removeAttributeStmt.setString(1, componentName);
            removeAttributeStmt.setString(2, name);
            removeAttributeStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Deleted row: " + componentName + " - " + name);
            }

        } catch (SQLException e) {
            throw wrapSQLException("Trying to delete a row: " + componentName + " - " + name, e);
        } finally {
            try {
                removeAttributeStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeAllAttributes(Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        PreparedStatement removeAttributeStmt = null;
        Connection conn = null;

        try {

            conn = getConnection(xact);
            removeAttributeStmt = conn.prepareStatement( statements.removeAllAttributesSQL );
            removeAttributeStmt.setString(1, componentName);
            removeAttributeStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Deleted all rows: " + componentName);
            }

        } catch (SQLException e) {
            throw wrapSQLException("Trying to delete all rows: " + componentName, e);
        } finally {
            try {
                removeAttributeStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void replaceAttribute(String name, Serializable value, Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        PreparedStatement replaceAttributeStmt = null;
        Connection conn = null;

        try {

            conn = getConnection(xact);
            replaceAttributeStmt = conn.prepareStatement( statements.replaceAttributeSQL );
            byte[] valueBytes = toBytes(value);
            replaceAttributeStmt.setBinaryStream(1, new ByteArrayInputStream(valueBytes), valueBytes.length);
            replaceAttributeStmt.setString(2, componentName);
            replaceAttributeStmt.setString(3, name);
            replaceAttributeStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Replaced attribute value: " + componentName + " - " + name + " = " + value);
            }

        } catch (IOException e) {
            throw new StorageException("Trying to replace attribute value: " + componentName + " - " + name
                    + " = " + value, e);
        } catch (SQLException e) {
            throw wrapSQLException("Trying to replace attribute value: " + componentName + " - " + name
                    + " = " + value, e);
        } catch (SmartFrogResolutionException e) {
            throw new StorageException("Trying to replace attribute value: " + componentName + " - " + name
                    + " = " + value, e);
        } finally {
            try {
                replaceAttributeStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public void setTags(String name, Serializable tags, Transaction xact) throws StorageException {

        if (isClosed) {
            throw new StorageException("Attempt to invoke inactive storage");
        }

        if (xact == null) {
            throw new StorageException("Attempt to invoke storage with xact=null");
        }

        PreparedStatement replaceAttributeStmt = null;
        Connection conn = null;

        try {

            conn = getConnection(xact);
            replaceAttributeStmt = conn.prepareStatement( statements.replaceTagsSQL );
            byte[] valueBytes = toBytes(tags);
            replaceAttributeStmt.setString(1, TagsString.toString((Set)tags) );
            replaceAttributeStmt.setString(2, componentName);
            replaceAttributeStmt.setString(3, name);
            replaceAttributeStmt.executeUpdate();

            if (log.isTraceEnabled()) {
                log.trace("Replaced tags: " + componentName + " - " + name + " = " + tags);
            }

        } catch (IOException e) {
            throw new StorageException("Trying to replace tags: " + componentName + " - " + name
                    + " = " + tags, e);
        } catch (SQLException e) {
            throw wrapSQLException("Trying to replace tags: " + componentName + " - " + name
                    + " = " + tags, e);
        } catch (SmartFrogResolutionException e) {
            throw new StorageException("Trying to replace tags: " + componentName + " - " + name
                    + " = " + tags, e);
        } finally {
            try {
                replaceAttributeStmt.close();
            } catch (Exception e) {
            }
            try {
                closeConnection(conn, xact);
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * Obtain the transaction's connection. If null transaction generate a new
     * connection with autocommit.
     * 
     * @param xact the transaction
     * @return the connection
     * @throws StorageException
     */
    protected Connection getConnection(Transaction xact) throws StorageException {
        return getConnection(xact, true);
    }
    
    /**
     * Obtain the transaction's connection. If null transaction then generate a new
     * connection without autocommit.
     * 
     * @param xact the transaction
     * @return the atomic connection (no auto commit)
     * @throws StorageException
     */
    protected Connection getAtomicConnection(Transaction xact) throws StorageException {
        return getConnection(xact, false);
    }
    

    /**
     * Obtain the transaction's connection or get a new connection.
     * If there is no transaction the auto commit status of the connection
     * will be set as specified by the autocommit parameter.
     * 
     * @param xact the transaction
     * @param autoCommit setting for auto commit flag
     * @return the connection
     * @throws StorageException
     */
    protected Connection getConnection(Transaction xact, boolean autoCommit) throws StorageException {

        /**
         * get a new connection if there is no transaction.
         * set autocommit as required.
         */
        if (xact.isNull()) {
        	
            Connection connection = null;
            /**
             * get the connection
             */
            try {
				connection = connectionPool.getConnection();
            } catch (SQLException e) {
                throw wrapSQLException("Trying to get a connection", e);
            }
            
            /**
             * set autocommit as required
             */
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException e) {
                throw wrapSQLException("Trying to set auto commit on connection", e);
            }
            
            return connection;
        }

        /**
         * complain about invalid transaction types
         */
        if (!(xact instanceof TransactionImpl)) {
            throw new StorageException("Attempt to use an unknown transaction type " + xact);
        }

        /**
         * This must be one of our transactions so use it
         */
        return ((TransactionImpl) xact).getConnection();
    }

    /**
     * release the connection unless it belongs to the transaction. 
     * 
     * @param conn the connection
     * @param xact the transaction
     * @throws SQLException
     */
    protected void closeConnection(Connection conn, Transaction xact) throws SQLException {
        /**
         * Set the transaction to auto commit - this has no effect if autocommit is alredy 
         * set, but otherwise commits an uncommited transaction. 
         */
        if (xact.isNull()) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    /**
     * Serialize an object to byte array. This is used to convert generic objects to a uniform
     * format for storage.
     * 
     * @param value the object
     * @return the byte array containing the serialized object
     * @throws IOException
     * @throws SmartFrogResolutionException
     */
    private byte[] toBytes(Serializable value) throws IOException, SmartFrogResolutionException {
        Object obj = (value instanceof ComponentDescription) ? CDString.toString((ComponentDescription) value) : value;

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = new ObjectOutputStream(byteArrayOS);
        objectOS.writeObject(obj);
        objectOS.flush();
        objectOS.reset();
        byte[] bytes = byteArrayOS.toByteArray();
        return bytes;
    }

    /**
     * Deserialize an object from an input stream and return the object. This is used to recreate
     * an object read from a generic format used for storage.
     * 
     * @param binStream the input stream containing the object
     * @return the object
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SmartFrogParseException
     * @throws SmartFrogException
     */
    private Object fromInputStream(InputStream binStream) throws IOException, ClassNotFoundException,
            SmartFrogParseException, SmartFrogException {
        ObjectInputStream oIS = new ObjectInputStream(binStream);
        Object obj = oIS.readObject();
        if (CDString.isCDString(obj)) {
            obj = CDString.fromString((String) obj);
        }
        return obj;
    }
    

    /**
     * Helper method - wraps a storage exception as a StorageAccessException or
     * a StorageException depending on if the cause was a broken connection.
     * 
     * @param message
     * @param cause
     * @return the storage exception wrapping the SQLException
     */
    public StorageException wrapSQLException(String message, SQLException cause) {
        
        StorageException se = null; 
        
        if( cause instanceof SQLPoolTimeoutException ) {
        	return new StorageTimeoutException(message, cause);
        } else if( cause instanceof SQLPoolClosedException ) {
        	se = new StorageAccessException(message, cause);
        } else if( cause instanceof SQLPoolAccessException ) {
        	se = new StorageAccessException(message, cause);
        } else if( sqlDialect.isErrorType(SQLDialect.ErrorType.BROKEN_CONNECTION, cause.getErrorCode()) ) {
            se = new StorageAccessException(message, cause);
        } else if( sqlDialect.isErrorType(SQLDialect.ErrorType.READ_ONLY, cause.getErrorCode()) ){
            se = new StorageReadOnlyException(message, cause);
        } else if( sqlDialect.isErrorType(SQLDialect.ErrorType.NO_SUCH_TABLE, cause.getErrorCode()) ){
            se = new StorageNoSuchTableException(message, cause);
        } else {
            se = new StorageException(message, cause);
        }
        notifyStorageException(se);
        return se;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionNotifications(StorageExceptionNotification notify) {
        storageExceptionReceiver = notify;
    }
    
    /**
     * Forward the storage exception to the storage exception notification
     * target if one is set. 
     * @param se the storage exception.
     */
    private void notifyStorageException(StorageException se) {
        if( storageExceptionReceiver != null ) {
            storageExceptionReceiver.storageExceptionNotification(se);
        }        
    }

}
