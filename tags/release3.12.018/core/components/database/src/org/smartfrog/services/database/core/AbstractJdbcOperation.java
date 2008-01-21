/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.database.core;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * abstract Jdbc operation; contains common code
 * created 05-Dec-2006 15:10:03
 */

public abstract class AbstractJdbcOperation extends PrimImpl implements JdbcOperation {
    private JdbcBinding database;
    private boolean autocommit = false;
    private Log log;
    private ComponentHelper helper;


    protected AbstractJdbcOperation() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        log = LogFactory.getLog(this);
        helper = new ComponentHelper(this);
        database = (JdbcBinding) sfResolve(ATTR_DATABASE, (Prim)null, true);
        autocommit = sfResolve(ATTR_AUTOCOMMIT, autocommit, true);
    }

    /**
     * Connect to the database; return a simple database connection bound to the
     * jdbc options.
     *
     * @return a new database connection
     * @throws SmartFrogDeploymentException  failure while starting
     * @throws SmartFrogResolutionException  failure to resolve the attributes
     * @throws RemoteException In case of network/rmi error
     */
    protected Connection connect() throws
            SmartFrogDeploymentException,
            SmartFrogResolutionException,
            RemoteException {
        JdbcBinding binding = getDatabase();
        String driverName = binding.getDriver();
        String url = binding.getUrl();
        Properties props = binding.createConnectionProperties();
        String dbinfo = "database " + url + " using " + driverName;
        log.debug("Binding to " + dbinfo);
        Connection connection = null;
        try {
            Driver driver = loadDriver(driverName);
            connection = driver.connect(url, props);

            if (connection == null) {
                // Driver doesn't understand the URL
                throw new SmartFrogDeploymentException("Failed to load " + dbinfo);
            }
        } catch (SQLException e) {
            throw translate("Exception when loading " + dbinfo, e);
        }
        if (isAutocommit()) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                closeQuietly(connection);
                throw translate("setting autocommit flag on "+dbinfo, e);
            }
        }
        return connection;
    }

    /**
     * any logic to convert from a SQL exception to a smartfrog one
     *
     * @param operation what was attempted
     * @param fault what went wrong
     *
     * @return a new exception to throw.
     */

    protected SmartFrogDeploymentException translate(String operation,
                                                     SQLException fault) {
        return new SmartFrogDeploymentException(operation, fault);
    }

    public JdbcBinding getDatabase() {
        return database;
    }

    public Log getLog() {
        return log;
    }



    /**
     * Gets an instance of the required driver.
     * @param driver the driver classname
     * @return the driver instance
     * @throws SmartFrogDeploymentException to wrap failures to create an instance
     * @throws SmartFrogResolutionException if the class would not load
     * @throws RemoteException on network problems
     */
    private Driver loadDriver(String driver) throws
            SmartFrogDeploymentException,
            SmartFrogResolutionException,
            RemoteException {
        Driver instance = null;
        try {
            Class clazz = helper.loadClass(driver);
            instance = (Driver) clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new SmartFrogDeploymentException(
                    "IllegalAccessException when trying to load " + driver,
                    e);
        } catch (InstantiationException e) {
            throw new SmartFrogDeploymentException(
                    "IllegalAccessException when trying to load " + driver,
                    e);
        }
        return instance;
    }

    /**
     * Commit the operation and close the database
     * @param connection  connection to close
     * @throws SmartFrogDeploymentException if something went wrong at this point
     */
    public void commitAndClose(Connection connection)
            throws SmartFrogDeploymentException {
        try {
            if (!autocommit) {
                connection.commit();
            }
            connection.close();
        } catch (SQLException e) {
            throw translate("Exception when closing the connection ", e);
        }

    }

    /**
     * Close a connection, turning any SqlException into a SmartFrog exeception
     * This does not attempt to call commit, even if it is required.
     *
     * @param connection connection to close
     *
     * @throws SmartFrogDeploymentException if something went wrong closing the connection
     *
     */
    protected synchronized void close(Connection connection) throws
            SmartFrogDeploymentException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw translate("Exception when closing the connection ", e);
        }
    }

    /**
     * close a connection without throwing any exception, just log it.
     *
     * @param connection connection to close
     */
    protected void closeQuietly(Connection connection) {
        try {
            close(connection);
        } catch (SmartFrogDeploymentException e) {
            log.error("Exception when closing the connection ", e);
        }
    }

    /**
     * check the connection
     * @throws SmartFrogDeploymentException if the connection won't start
     * @throws SmartFrogResolutionException if there is something wrong with the settings
     * @throws RemoteException on network problems
     */
    protected void checkConnection()
            throws SmartFrogDeploymentException, SmartFrogResolutionException,
            RemoteException {
        //do a quick connect to see that we are ok
        Connection connection=null;
        try {
            connection = connect();
        } finally {
            close(connection);
        }
    }

    /**
     * Get the component helper
     * @return the component helper created in sfDeploy()
     */
    protected ComponentHelper getHelper() {
        return helper;
    }

    /**
     * query the autocommit flag
     * @return the current autocommit value
     */
    public boolean isAutocommit() {
        return autocommit;
    }

    /**
     * set the autocommit flag
     * @param autocommit the new value
     */
    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }

    /**
     * Log warnings
     * @param statement the statement that was executed
     * @param results results (can be null)
     * @return the number of warnings printed
     * @throws SQLException if something goes wrong extracting the warnings.
     */
    protected int logWarnings(Statement statement, ResultSet results) throws SQLException {
        return logWarnings(statement.getWarnings())
                +(results!=null?logWarnings(results.getWarnings()):0);
    }

    /**
     * Recursive log of all warnings returned by an operation
     * @param warning a possibly null warning
     * @return the number of warnings
     */
    protected int logWarnings(SQLWarning warning) {
        if (warning == null) {
            return 0;
        }
        logOneWarning(warning);
        SQLWarning nextWarning = warning.getNextWarning();
        if (warning != nextWarning) {
            return 1+logWarnings(nextWarning);
        } else {
            return 1;
        }
    }

    /**
     * Override point: log one warning
     * @param warning the warning to print
     */
    protected void logOneWarning(SQLWarning warning) {
        Log l = getLog();
        l.warn("Warning:");
        l.warn(warning.getMessage());
        l.warn("SQL State: " + warning.getSQLState());
        l.warn("Error Code: " + warning.getErrorCode());
    }
}
