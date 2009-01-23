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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This represents a JDBC operation that runs in a background thread
 */
public abstract class AsyncJdbcOperation extends AbstractJdbcOperation {


    private Throwable queuedFault;
    private WorkflowThread workerThread;

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


    protected synchronized Throwable queueFault(Throwable e) {
        return queuedFault = e;
    }

    protected synchronized Throwable queueFault(String action, SQLException e) {
        queuedFault = translate(action, e);
        return queuedFault;
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
    @Override
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
     * @param status termination record
     */
    @Override
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
     * @throws SmartFrogDeploymentException if the thread wont start
     */
    protected synchronized Thread startWorkerThread()
            throws SmartFrogDeploymentException {
        if (workerThread != null) {
            throw new SmartFrogDeploymentException(
                    "Cannot start the worker thread, as it is already running");
        }

        workerThread = createWorkerThread();
        workerThread.start();
        return workerThread;
    }

    /**
     * Override this to implement a new worker thread
     * @return the worker thread
     */
    protected DatabaseThread createWorkerThread() {
        return new DatabaseThread();
    }

    /**
     * Stop the worker thread, or at least interrupt it. Does nothing if the
     * thread is not running.
     */
    protected synchronized void stopWorkerThread() {
        SmartFrogThread.requestThreadTermination(workerThread);
        workerThread = null;
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

    private class DatabaseThread extends WorkflowThread {

        /**
         * Create a new workflow thread
         */
        private DatabaseThread() {
            super(AsyncJdbcOperation.this, true, new Object());
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses of
         * <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            Connection connection = null;
            try {
                connection = connect();
                performOperation(connection);
                commitAndClose(connection);
            } catch (SQLException e) {
                throw queueFault("processing transactions", e);
            } catch (SmartFrogException e) {
                throw queueFault(e);
            } catch (RemoteException e) {
                throw queueFault(e);
            } finally {
                closeQuietly(connection);
            }
        }

    }
}
