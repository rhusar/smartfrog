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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * abstract Jdbc operation; contains common code
 */
public abstract class JdbcOperationImpl extends PrimImpl implements JdbcOperation, Runnable {


    protected JdbcBinding database;
    private Throwable queuedFault;
    private boolean autocommit = false;
    private Log log;
    private Thread workerThread;
    private ComponentHelper helper;
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
        log = LogFactory.getLog(this);
        helper = new ComponentHelper(this);
        database=(JdbcBinding) sfResolve(ATTR_DATABASE,database,true);
        autocommit = sfResolve(ATTR_AUTOCOMMIT, autocommit, true);
    }


    /**
     * Connect to the database; return a simple database connection
     * bound to the jdbc options.
     * @return a new database connection
     * @throws SmartFrogDeploymentException
     */
    protected Connection connect() throws
            SmartFrogDeploymentException, SmartFrogResolutionException, RemoteException {
        String driverName = database.getDriver();
        String url = database.getUrl();
        Properties props = database.createConnectionProperties();
        String dbinfo= "database " + url + " using " + driverName;
        log.debug("Binding to "+ dbinfo);
        Connection connection=null;
        try {
            Driver driver=loadDriver(driverName);
            connection = driver.connect(url,props);

            if (connection == null) {
                // Driver doesn't understand the URL
                throw new SmartFrogDeploymentException("Failed to load "+dbinfo);
            }
        } catch (SQLException e) {
            throw translate("Exception when load " + dbinfo,e);
        }
        if (autocommit) {
            try {
                connection.setAutoCommit(autocommit);
            } catch (SQLException e) {
                closeQuietly(connection);
                throw translate("setting autocommit flag", e);
            }
        }
        return connection;
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


    public boolean isAutocommit() {
        return autocommit;
    }

    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }

    public JdbcBinding getDatabase() {
        return database;
    }

    public Log getLog() {
        return log;
    }

    public Thread getWorkerThread() {
        return workerThread;
    }

    /**
     * Gets an instance of the required driver. Uses the ant class loader and
     * the optionally the provided classpath.
     *
     * @return the driver instance
     *
     */
    private Driver loadDriver(String driver) throws
            SmartFrogDeploymentException, SmartFrogResolutionException, RemoteException {
        Driver instance = null;
        try {
            Class clazz= helper.loadClass(driver);
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


    public void commitAndClose(Connection connection) throws SmartFrogDeploymentException {
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
     * close a connection without throwing any exception, just log it at debug level
     * @param connection
     */
    protected void closeQuietly(Connection connection)  {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Exception when closing the connection ", e);
        }
    }

    /**
     * check the connection
     */
    protected void checkConnection() throws SmartFrogDeploymentException, SmartFrogResolutionException,
            RemoteException {
        //do a quick connect to see that we are ok
        Connection connection = connect();
        close(connection);
    }

    protected synchronized void queueFault(Throwable e) {
        queuedFault =e;
    }

    protected synchronized void queueFault(String action,SQLException e) {
        queuedFault = translate(action, e);
    }

    protected Throwable getQueuedFault() {
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
        Throwable fault = getQueuedFault();
        if(fault!=null) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(fault);
        }
    }

    /**
     * stop the worker thread if it is running.
     * @param status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        stopWorkerThread();
    }

    protected ComponentHelper getHelper() {
        return helper;
    }

    /**
     * Start the worker thread. this should be called from sfStart if the implementation wants to
     * do work in the {@link #performOperation(java.sql.Connection)} method.
     * @return
     */
    protected synchronized Thread startWorkerThread() throws SmartFrogDeploymentException {
        if(workerThread!=null) {
            throw new SmartFrogDeploymentException("Cannot start the worker thread, as it is already running");
        }
        Thread thread=new Thread(this, sfCompleteNameSafe().toString());
        workerThread=thread;
        thread.start();
        return thread;
    }

    /**
     * Stop the worker thread, or at least interrupt it.
     * Does nothing if the thread is not running.
     */
    protected synchronized void stopWorkerThread() {
        if(workerThread!=null && workerThread.isAlive()) {
            workerThread.interrupt();
            workerThread=null;
        }
    }


    /**
    * run the operation by creating a connection, calling {@link #performOperation(java.sql.Connection)} and
    * then closing the connection afterwards.
    * <p>
    * the component is then terminated after the run, if the sfTerminate or similar attributes are set.
    * @see Thread#run()
    */
    public void run() {
        Throwable caught=null;
        Connection connection = null;
        try {
            connection = connect();
            performOperation(connection);
            commitAndClose(connection);

        } catch (SQLException e) {
            caught=e;
            queueFault("processing transactions", e);
        } catch (SmartFrogException e) {
            caught = e;
            queueFault(e);
        } catch (RemoteException e) {
            caught = e;
            queueFault(e);
        } finally {
            closeQuietly(connection);
        }
        getHelper().sfSelfDetachAndOrTerminate(null,
                null,
                null,
                caught);
    }

    /**
     * Something for components to override. This performs the operation
     * @param connection the open connection.
     * @throws java.sql.SQLException
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     */
    public void performOperation(Connection connection) throws SQLException, SmartFrogException {
        log.debug("performing no useful work");
    }
}
