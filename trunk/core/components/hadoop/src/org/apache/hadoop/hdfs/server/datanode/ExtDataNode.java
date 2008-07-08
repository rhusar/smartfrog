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


package org.apache.hadoop.hdfs.server.datanode;

import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.File;
import java.io.IOException;
import java.util.AbstractList;

/**
 * This class is in the hadoop dfs package to get at package scoped operations and internal datastructures that are only
 * visible in package scope. <p/> To use these classes in a secure classloader, both the hadoop-core and sf-hadoop JARs
 * will need to be signed by the same entities.
 */
public class ExtDataNode extends DataNode {

    private volatile boolean stopped;
    private DataNodeThread worker;
    private Prim owner;

    public ExtDataNode(Prim owner, ManagedConfiguration conf, AbstractList<File> dataDirs)
            throws IOException {
        super(conf, dataDirs);
        this.owner = owner;
    }


    /**
     * Start our parent and the worker thread
     *
     * @throws IOException if necessary
     */
    public void innerStart() throws IOException {
        super.innerStart();
        startWorkerThread();
    }

    /**
     * Shut down this instance of the datanode. Returns only after shutdown is complete.
     */
    @Override
    public synchronized void innerTerminate() throws IOException {
        super.innerTerminate();
        if (!isStopped()) {
            stopped();
            SmartFrogThread.requestThreadTermination(worker);
            worker = null;
        }
    }

  /**
   * Method called whenever there is a state change. The base class logs the
   * event at debug level
   *
   * @param oldState existing state
   * @param newState new state.
   */
  @Override
  protected void onStateChange(ServiceState oldState,
                               ServiceState newState) {
    super.onStateChange(oldState, newState);

  }

  /**
     * Set our stopped flag
     */
    private synchronized void stopped() {
        stopped = true;
    }

    /**
     * Get the stopped exception
     *
     * @return true if we have stopped
     */
    public synchronized boolean isStopped() {
        return stopped;
    }

    /**
     * Override the normal run and note that we got stopped
     */
    @Override
    public void run() {
        try {
            super.run();
        } finally {
            stopped();
        }
    }

    /**
     * Ping the node; report an error if we have stopped
     *
     * @throws IOException for any liveness problem
     */
    @Override
    public void ping()
            throws IOException {
        super.ping();
        if(getServiceState() == ServiceState.RUNNING) {
            if (isStopped()) {
                throw new ServiceStateException("DataNode is stopped",
                        getServiceState());
            }
            try {
                SmartFrogThread.ping(worker);
            } catch (SmartFrogLivenessException e) {
                throw new ServiceStateException("", e,
                        getServiceState());
            }
        }
    }

    /**
     * Start the worker thread
     */
    public synchronized void startWorkerThread() {
        if (worker == null) {
            worker = new DataNodeThread();
            worker.start();
        }
    }


    /**
     * This is a private worker thread that can be interrupted better
     */
    private class DataNodeThread extends WorkflowThread {

        /**
         * Creates a new thread
         */
        private DataNodeThread() {
            super(ExtDataNode.this.owner, true);
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses
         * of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        @Override
        public void execute() throws Throwable {
            ExtDataNode.this.run();
        }

        /**
         * Add an interrupt to the thread termination
         */
        @Override
        public synchronized void requestTermination() {
            if (!isTerminationRequested()) {
                super.requestTermination();
                //and interrupt
                interrupt();
            }
        }
    }

}
