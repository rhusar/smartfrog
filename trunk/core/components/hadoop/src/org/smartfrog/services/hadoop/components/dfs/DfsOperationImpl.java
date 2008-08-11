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


package org.smartfrog.services.hadoop.components.dfs;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.rmi.RemoteException;

/**
 * Base class for DFS operations does nothing useful at all other than resolve the cluster settings and fail if they are
 * absent. It also has support for a worker thread (which get terminated during shutdown, if set)
 */
public abstract class DfsOperationImpl extends PrimImpl implements DfsOperation {

    private Prim cluster;
    private WorkflowThread worker;
    /**
     * Error string {@value}
     */
    public static final String FAILED_TO_COPY = "Failed to copy ";


    protected DfsOperationImpl() throws RemoteException {
    }

    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        cluster = sfResolve(ATTR_CLUSTER, cluster, true);
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        //shut down any non-null worker
        terminateWorker();
    }

    /**
     * Shut down any worker if running
     */
    protected synchronized void terminateWorker() {
        WorkflowThread w = worker;
        worker = null;
        SmartFrogThread.requestThreadTermination(w);
    }

    protected synchronized WorkflowThread getWorker() {
        return worker;
    }

    protected synchronized void setWorker(WorkflowThread worker) {
        this.worker = worker;
    }


    /**
     * Resolve an attribute to a DFS path
     * @param attribute name of the attribute
     * @return the path
     * @throws SmartFrogException resolution problems
     * @throws SmartFrogLifecycleException for a failure to create the path
     * @throws RemoteException network problems
     */
    protected Path resolveDfsPath(String attribute) throws SmartFrogException, RemoteException {
        String pathName = sfResolve(attribute, "", true);
        try {
            return new Path(pathName);
        } catch (IllegalArgumentException e) {
            throw new SmartFrogLifecycleException("Failed to create the path defined by attribute "+ attribute
                    +" with value "+pathName
                    +" : "+ e.getMessage(), e, this);
        }
    }

    /**
    * Get the cluster binding
    *
    * @return the cluster
    */
    public Prim getCluster() {
        return cluster;
    }

    /**
     * Create a managed configuration
     *
     * @return a new SF-managed configuration
     */
    public ManagedConfiguration createConfiguration() {
        return new ManagedConfiguration(cluster);
    }

    /**
     * Create a filesystem from our configuration
     *
     * @return a new file system
     * @throws SmartFrogRuntimeException for any problem creating the FS.
     */
    protected DistributedFileSystem createFileSystem() throws SmartFrogRuntimeException {
        ManagedConfiguration conf = createConfiguration();
        DistributedFileSystem fileSystem = DfsUtils.createFileSystem(conf);
        return fileSystem;
    }

    /**
     * For subclassing: this routine will be called by the default worker thread, if that thread gets started
     *
     * @param fileSystem the filesystem; this is closed afterwards
     * @param conf       the configuration driving this operation
     * @throws Exception on any failure
     */
    protected void performDfsOperation(DistributedFileSystem fileSystem, ManagedConfiguration conf)
            throws Exception {

    }

    /**
     * do the work
     *
     * @throws Exception on any failure
     */
    protected void performDfsOperation() throws Exception {
        ManagedConfiguration conf = createConfiguration();
        DistributedFileSystem fileSystem = DfsUtils.createFileSystem(conf);
        try {
            performDfsOperation(fileSystem, conf);
        } catch (Exception e) {
            DfsUtils.closeQuietly(fileSystem);
            throw e;
        }
        DfsUtils.closeDfs(fileSystem);
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
        public DfsWorkerThread(boolean workflowTermination) {
            super(DfsOperationImpl.this, workflowTermination);
        }

        /**
         * Call back into the {@link DfsOperationImpl#performDfsOperation()} operation of the owner class; that is where
         * the work should be implemented.
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            performDfsOperation();
        }
    }
}
