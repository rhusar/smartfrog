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
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This represents a JDBC operation that runs in a background thread
 */
public abstract class AsyncJdbcOperation extends AbstractJdbcOperation
        implements Runnable {


    private Throwable queuedFault;
    private Thread workerThread;

    /**
     * Protected constructor as this class is abstract
     *
     * @throws RemoteException
     */
    protected AsyncJdbcOperation() throws RemoteException {
    }


    public Thread getWorkerThread() {
        return workerThread;
    }


    protected synchronized void queueFault(Throwable e) {
        queuedFault = e;
    }

    protected synchronized void queueFault(String action, SQLException e) {
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
     * @throws SmartFrogLivenessException
     *                                  component is terminated
     * @throws RemoteException for consistency with the {@link Liveness}
     *                                  interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        Throwable fault = getQueuedFault();
        if (fault != null) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(
                    fault);
        }
    }

    /**
     * stop the worker thread if it is running.
     *
     * @param status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        stopWorkerThread();
    }

    /**
     * Start the worker thread. this should be called from sfStart if the
     * implementation wants to do work in the {@link #performOperation(java.sql.Connection)}
     * method.
     *
     * @return the worker thread
     */
    protected synchronized Thread startWorkerThread()
            throws SmartFrogDeploymentException {
        if (workerThread != null) {
            throw new SmartFrogDeploymentException(
                    "Cannot start the worker thread, as it is already running");
        }
        Thread thread = new Thread(this, sfCompleteNameSafe().toString());
        workerThread = thread;
        thread.start();
        return thread;
    }

    /**
     * Stop the worker thread, or at least interrupt it. Does nothing if the
     * thread is not running.
     */
    protected synchronized void stopWorkerThread() {
        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
            workerThread = null;
        }
    }


    /**
     * run the operation by creating a connection, calling {@link
     * #performOperation(java.sql.Connection)} and then closing the connection
     * afterwards.
     * <p/>
     * the component is then terminated after the run, if the sfTerminate or
     * similar attributes are set.
     *
     * @see Thread#run()
     */
    public void run() {
        Throwable caught = null;
        Connection connection = null;
        try {
            connection = connect();
            performOperation(connection);
            commitAndClose(connection);

        } catch (SQLException e) {
            caught = e;
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
     *
     * @param connection the open connection.
     *
     * @throws SQLException SQL errors
     * @throws SmartFrogException smartfrog errors
     *
     */
    public void performOperation(Connection connection)
            throws SQLException, SmartFrogException {
        getLog().info("performing no useful work");
    }
}
