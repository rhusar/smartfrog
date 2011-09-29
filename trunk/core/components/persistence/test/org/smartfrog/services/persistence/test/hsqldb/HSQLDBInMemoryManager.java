package org.smartfrog.services.persistence.test.hsqldb;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.smartfrog.services.persistence.framework.activator.Activator;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.StorageExceptionNotification;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class HSQLDBInMemoryManager extends CompoundImpl implements Compound, StorageExceptionNotification {

	private static final String PERSISTENCE_FRAMEWORK_ATTR = "persistence";
    private static final String PASSWORD_ATTR              = "password";
    private static final String USER_ATTR                  = "user";
    private static final String DATA_SOURCE_URL_ATTR       = "dataSourceURL";
    
	private Activator persistenceFramework = null; 
	private String password = null;
	private String user     = null;
	private String dataSource = null;
	
	public HSQLDBInMemoryManager() throws RemoteException {
	}

	public synchronized void sfStart() throws SmartFrogException,
			RemoteException {
		super.sfStart();
		if( !openDatabase() ) {
			throw new SmartFrogDeploymentException("Database not opened");
		}
		persistenceFramework.activate();
	}

	public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
		super.sfDeploy();
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			throw new SmartFrogDeploymentException("Failed to load hsqldb driver", e);
		}
		persistenceFramework = (Activator)sfResolve(PERSISTENCE_FRAMEWORK_ATTR);
		password = (String)sfResolve(PASSWORD_ATTR);
		user = (String)sfResolve(USER_ATTR);
		dataSource = (String)sfResolve(DATA_SOURCE_URL_ATTR);
	}

	public synchronized void sfTerminateWith(TerminationRecord status) {
		persistenceFramework.deactivate();
		closeDatabase();
		super.sfTerminateWith(status);
	}

	public void storageExceptionNotification(StorageException s) {
		persistenceFramework.deactivate();
		closeDatabase();
	}
	
	public boolean openDatabase() {
		
		if( sfLog().isDebugEnabled() ) {
			sfLog().debug("opening hsqldb in memory database");
		}

		Connection con = null;
		try {
			con = DriverManager.getConnection(dataSource, user, password);
			return true;
		} catch (SQLException e) {

			if( sfLog().isErrorEnabled() ) {
				sfLog().error("Failed to open hsqldb in memory database", e);
			}
			return false;
			
		} finally {
			if( con != null ) {
				try { con.close(); }
				catch(SQLException ex) {}
			}
		}

	}
	
	public void closeDatabase() {
		
		if( sfLog().isDebugEnabled() ) {
			sfLog().debug("closing hsqldb in memory database");
		}
		
		Connection con = null;
		Statement stmt = null;
		try {
			con = DriverManager.getConnection(dataSource, user, password);
			stmt = con.createStatement();
			stmt.execute("shutdown");
		} catch (SQLException e) {

			if( sfLog().isErrorEnabled() ) {
				sfLog().error("Failed to close hsqldb in memory database", e);
			}
			
		} finally {
			if( stmt != null ) {
				try { stmt.close(); }
				catch(SQLException ex) {}
			}
			if( con != null ) {
				try { con.close(); }	
				catch(SQLException ex) {}
			}
		}
		
	}

}
