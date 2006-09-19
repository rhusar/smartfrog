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
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * abstract Jdbc operation; contains common code
 */
public abstract class JdbcOperationImpl extends PrimImpl implements JdbcOperation {


    protected JdbcBinding database;
    private SmartFrogException queuedFault;

    /**
     * Protected constructor as this class is abstract
     * @throws RemoteException
     */
    protected JdbcOperationImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        database=(JdbcBinding) sfResolve(ATTR_DATABASE,database,true);
    }


    /**
     * Connect to the database; return a simple database connection
     * bound to the jdbc options.
     * @return a new database connection
     * @throws SmartFrogDeploymentException
     */
    protected Connection connect() throws
            SmartFrogDeploymentException{
        String driverName = database.getDriver();
        String url = database.getUrl();
        Properties props = database.createConnectionProperties();
        Log log = LogFactory.getOwnerLog(this);
        String dbinfo= "database " + url + " using " + driverName;
        log.debug("Binding to "+ dbinfo);

        try {
            Driver driver=loadDriver(driverName);
            Connection connection = driver.connect(url,props);

            if (connection == null) {
                // Driver doesn't understand the URL
                throw new SmartFrogDeploymentException("Failed to load "+dbinfo);
            }

            return connection;
        } catch (SQLException e) {
            throw translate("Exception when load " + dbinfo,e);
        }

    }

    /**
     * any logic to convert from a SQL exception to a smartfrog one
     * @param operation
     * @param fault
     * @return a new exception to throw.
     */

    protected SmartFrogDeploymentException translate(String operation,SQLException fault) {
        return new SmartFrogDeploymentException(operation,fault);
    }

    private Class loadDriverClass(String driver) throws
            SmartFrogDeploymentException {
        try {
            Class aClass = SFClassLoader.forName(driver);
            return aClass;
        } catch (ClassNotFoundException e) {
            throw new SmartFrogDeploymentException("Could not load " + driver, e);
        }
    }

    /**
     * Gets an instance of the required driver. Uses the ant class loader and
     * the optionally the provided classpath.
     *
     * @return the driver instance
     *
     */
    private Driver loadDriver(String driver) throws
            SmartFrogDeploymentException {
        Driver instance = null;
        try {
            Class clazz=loadDriverClass(driver);
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
     * Close a connection, turning any SqlException into a SmartFrog exeception
     * @param connection
     * @throws org.smartfrog.sfcore.common.SmartFrogDeploymentException
     */
    protected void close(Connection connection) throws
            SmartFrogDeploymentException {
        try {
            if(connection!=null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw translate("Exception when closing the connection ", e);
        }
    }

    /**
     * check the connection
     */
    protected void checkConnection() throws SmartFrogDeploymentException {
        //do a quick connect to see that we are ok
        Connection connection = connect();
        close(connection);
    }

    protected synchronized void queueFault(SmartFrogException e) {
        queuedFault =e;
    }

    protected synchronized void queueFault(String action,SQLException e) {
        queuedFault = translate(action, e);
    }

    protected SmartFrogException getQueuedFault() {
        return queuedFault;
    }

    /**
     * Relay any queued fault to the caller
     *
     * @param source source of call
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogLivenessException
     *                                  component is terminated
     * @throws java.rmi.RemoteException for consistency with the {@link
     *                                  org.smartfrog.sfcore.prim.Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        SmartFrogException fault = getQueuedFault();
        if(fault!=null) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(fault);
        }
    }
}
