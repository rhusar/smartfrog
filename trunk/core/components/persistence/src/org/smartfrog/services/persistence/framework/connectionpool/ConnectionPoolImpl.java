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

package org.smartfrog.services.persistence.framework.connectionpool;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.SQLNestedException;
import org.smartfrog.services.persistence.database.SQLDialect;
import org.smartfrog.services.persistence.database.SQLDialectPrim;
import org.smartfrog.services.persistence.database.Statements;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 * This class is a SmartFrog component that manages an SQL database connection pool 
 * implementing the ConnectionPool interface. The connection pool is created and ready
 * to use when the SmartFrog sfStart() upcall has been invoked and is closed
 * when the SmartFrog sfTerminateWith() upcall has been invoked. At any other time
 * the pool can be closed and created using the ConnectionPool interface.
 * 
 */
public class ConnectionPoolImpl extends CompoundImpl implements Compound, ConnectionPool, ConnectionPoolSize {

    private static final String MAX_CONNECTIONS_ATTR   = "maxConnections";
    private static final String MAX_WAIT_ATTR          = "maxWait";
    private static final String DATA_SOURCE_ATTR       = "dataSourceURL";
    private static final String PASSWORD_ATTR          = "password";
    private static final String USER_ATTR              = "user";
    private static final String DRIVER_ATTR            = "driver";
    private static final String VENDOR_DIALECT_ATTR    = "sqlDialect";

    private BasicDataSource dataSource;
    private String          driver;
    private String          user;
    private String          password;
    private String          dataSourceURL;
    private int             maxConnections;
    private long            maxWait;
    private SQLDialect      sqlDialect;
    private Statements      statements;

    public ConnectionPoolImpl() throws RemoteException {
    }

    
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
    	super.sfDeploy();

    	driver = sfResolve(DRIVER_ATTR, (String) null, true);
    	user = sfResolve(USER_ATTR, (String) null, true);
    	password = sfResolve(PASSWORD_ATTR, (String) null, true);
    	dataSourceURL = sfResolve(DATA_SOURCE_ATTR, (String) null, true);
    	maxConnections = sfResolve(MAX_CONNECTIONS_ATTR, (int)0, true );
    	maxWait = sfResolve(MAX_WAIT_ATTR, (long)0, false);
    	sqlDialect = ((SQLDialectPrim)sfResolve(VENDOR_DIALECT_ATTR)).resolveDialect();
    	statements = new Statements(sqlDialect);

    	if( sfLog().isDebugEnabled() ) {
    		sfLog().debug("Deployed connection pool.");
    	}
    }

    
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        closeConnectionPool();
        if( sfLog().isDebugEnabled() ) {
            sfLog().debug("Terminated connection pool.");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool#createConnectionPool()
     */
    public void createConnectionPool() {
        
        if( dataSource != null ) {
            return;
        }
        
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setUrl(dataSourceURL);
        dataSource.setInitialSize(maxConnections);
        dataSource.setMaxIdle(maxConnections);
        dataSource.setMaxActive(maxConnections);
        dataSource.setMaxWait(maxWait);
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true); 
        dataSource.setTimeBetweenEvictionRunsMillis(5*60*1000); // 5 minutes
        dataSource.setValidationQuery( sqlDialect.getValidationQuery() ); 
        if( sfLog().isDebugEnabled() ) {
            sfLog().debug("Creating connection pool [" +
                    "testOnBorrow=" + dataSource.getTestOnBorrow() + ", " +
                    "testWhileIdle=" + dataSource.getTestWhileIdle() + ", " +
                    "evitRunTimeout=" + dataSource.getTimeBetweenEvictionRunsMillis() + ", " +
                    "validationQuery=" + dataSource.getValidationQuery() + ", " +
                    "maxIdle=" + dataSource.getMaxIdle() + ", " +
                    "maxActive=" + dataSource.getMaxActive() + ", " +
                    "maxWait=" + dataSource.getMaxWait() + "]" );
        } 
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool#closeConnectionPool()
     */
    public void closeConnectionPool() {
        
        if( dataSource == null ) {
            return;
        }
        
        try {
            dataSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataSource = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool#getConnection()
     */
    public Connection getConnection() throws SQLException {
        
        /**
         * If the data source is closed throw exception
         */
        if( dataSource == null ) {
            throw new SQLPoolClosedException("Connection pool is closed");
        }
        
        /**
         * get the connection
         * Cannot create PoolableConnectionFactory 
         */
        try {
        	
            return dataSource.getConnection();
            
        } catch (SQLNestedException e) {
            
        	// TODO: get all the reasons from dbcp documentation
        	Throwable cause = e.getCause();
        	if( cause instanceof java.util.NoSuchElementException ) {
        		throw new SQLPoolTimeoutException("Timeout trying to obtain a connection from the connection pool", e);
        	} else {
        		throw new SQLPoolAccessException("Failure trying to obtain a connection from the connection pool", e);
        	}
        	
        } catch(SQLException e) {
        	
        	throw new SQLPoolAccessException("Failure trying to obtain a connection from the connection pool", e);
        	
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool#getDialect()
     */
    public SQLDialect getDialect() {
        return sqlDialect;
    }
    
    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPool#getStatements()
     */
    public Statements getStatements() {
    	return statements;
    }

    /*
     * (non-Javadoc)
     * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPoolSize#freeOneConnection()
     */
	public synchronized boolean freeOneConnection() {
		
		/**
		 * if data source is closed just return
		 */
		if( dataSource == null ) {
			return false;
		}
		
		/**
		 * if the data source only has one connection just return
		 */
		int currentMax = dataSource.getMaxActive();
		if( currentMax == 1 ) {
			return false;
		}
		
		/**
		 * get connection, reduce pool size - idle->0, close connection
		 * (should be dropped from the pool), then set idle to new pool size.
		 */
		try {
			Connection con = dataSource.getConnection();
			dataSource.setMaxActive(currentMax - 1);
			dataSource.setMaxIdle(0);
			con.close();
			dataSource.setMaxIdle(currentMax - 1);
		} catch (SQLException e) {
			dataSource.setMaxActive(currentMax);
			dataSource.setMaxIdle(currentMax);
			return false;
		}
		
		/**
		 * success
		 */
		return true;
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPoolSize#resetMaxSize()
	 */
	public synchronized void resetMaxSize() {
		
		/**
		 * if data source is closed just return
		 */
		if( dataSource == null ) {
			return;
		}
		
		/**
		 * reset pool size
		 */
		dataSource.setMaxActive(maxConnections);
		dataSource.setMaxIdle(maxConnections);
	}

	/*
	 * (non-Javadoc)
	 * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPoolSize#getCurrentMaxSize()
	 */
	public synchronized int getCurrentMaxSize() {
		return dataSource.getMaxActive();
	}

	/*
	 * (non-Javadoc)
	 * @see org.smartfrog.services.persistence.framework.connectionpool.ConnectionPoolSize#getDefaultMaxSize()
	 */
	public synchronized int getDefaultMaxSize() {
		return maxConnections;
	}

}
