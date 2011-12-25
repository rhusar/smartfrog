/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.hadoop.operations.dfs;

import org.apache.hadoop.fs.FileSystem;
import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.operations.utils.DfsUtils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;

/**
 * The Base class for DFS operations.
 * <p/>
 * <p/>
 * <p/>
 * It can contain a worker thread which then invokes {@link #performDfsOperation()} to create a filesystem and invoke
 * {@link #performDfsOperation(FileSystem, ManagedConfiguration)}, which subclasses can use to perform component-specific
 * operations.
 * <p/>
 * <p/>
 * <p/>
 * Subclasses should also consider overriding {@link #sfStart()} to read in extra values.
 * <p/>
 * <p/>
 * <p/>
 * Thread termination is automatic with the terminate operation of the WorkerThreadPrimImpl class that is the parent of
 * this class.
 */
@SuppressWarnings({"AbstractClassExtendsConcreteClass"})
public abstract class DfsOperationImpl extends DfsClusterBoundImpl implements DfsOperation {
    protected boolean closeFilesystem;


    protected DfsOperationImpl() throws RemoteException {
    }


    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        closeFilesystem = sfResolve(ATTR_CLOSE_FILESYSTEM, true, true);
    }


    /**
     * For subclassing: this routine will be called by the default worker thread, if that thread gets started
     *
     * @param fileSystem the filesystem; this is closed afterwards
     * @param conf       the configuration driving this operation
     * @throws Exception on any failure
     */
    protected void performDfsOperation(FileSystem fileSystem, ManagedConfiguration conf)
            throws Exception {

    }

    /**
     * Creating the filesystem, running the {@link #performDfsOperation(FileSystem, ManagedConfiguration)}
     * operation and then optionally closing the filesystem afterwards.
     *
     * @throws Exception on any failure
     */
    protected void performDfsOperation() throws Exception {
        ManagedConfiguration conf = createConfiguration();
        if (closeFilesystem) {
            conf.setBoolean("fs.hdfs.impl.disable.cache", true);
        }
        FileSystem fileSystem = DfsUtils.createFileSystem(conf);
        boolean finished = false;
        try {
            performDfsOperation(fileSystem, conf);
            finished = true;
        } finally {
            if (closeFilesystem) {
                //close the filesystem.
                if (finished) {
                    //end of a successful operation; throw up any problems
                    DfsUtils.closeDfs(fileSystem);
                } else {
                    //if this happened during an exception, it is closed quietly,
                    //so as to not lose the original problem
                    DfsUtils.closeQuietly(fileSystem);
                }
            }
        }
    }

    /**
     * Create a worker thread.
     */
    protected synchronized void startWorkerThread() {
        startWorkerThread(true);
    }

    /**
     * Create a worker thread.
     *
     * @param workflowTermination is workflow termination expected?
     */
    protected synchronized void startWorkerThread(boolean workflowTermination) {
        assert getWorker() == null : "A worker thread has already been created";
        setWorker(new DfsWorkerThread(workflowTermination));
        getWorker().start();
    }


    /**
     * This is a worker thread that optionally can be started by the DfsOperationImpl subclass, in which case it calls
     * back to do useful work.
     */
    protected class DfsWorkerThread extends WorkflowThread {

        /**
         * Create a worker thread. Notification is bound to a local notification object.
         *
         * @param workflowTermination is workflow termination expected?
         */
        protected DfsWorkerThread(boolean workflowTermination) {
            super(DfsOperationImpl.this, workflowTermination);
        }

        /**
         * Call back into the {@link DfsOperationImpl#performDfsOperation()} operation of the owner class; that is where
         * the work should be implemented.
         *
         * @throws Throwable if anything went wrong
         */
        @SuppressWarnings({"RefusedBequest"})
        public void execute() throws Throwable {
            performDfsOperation();
        }
    }
}
