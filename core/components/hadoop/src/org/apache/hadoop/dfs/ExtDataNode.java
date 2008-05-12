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


package org.apache.hadoop.dfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.StringUtils;
import org.smartfrog.services.hadoop.core.HadoopPingable;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.prim.Prim;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.AbstractList;

/**
 * This class is in the hadoop dfs package to get at package scoped operations
 * and internal datastructures that are only visible in package scope.
 * <p/>
 * To use these classes in a secure classloader, both the hadoop-core and
 * sf-hadoop JARs will need to be signed by the same entities.
 */
public class ExtDataNode extends DataNode implements HadoopPingable {

    private boolean stopped;
    private DataNodeThread worker;
    private Prim owner;

    public ExtDataNode(Prim owner,Configuration conf, AbstractList<File> dataDirs)
            throws IOException {
        super(conf, dataDirs);
        this.owner = owner;
    }

    /**
     * Shut down
     */
    public void terminate() {
        shutdown();
    }


    /**
     * Thread safe shutdown
     */
    public synchronized void shutdown() {
        if (!isStopped()) {
            stopped();
            super.shutdown();
            SmartFrogThread.requestThreadTermination(worker);
        }
    }

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
     * No matter what kind of exception we get, keep retrying to offerService().
     * That's the loop that connects to the NameNode and provides basic DataNode
     * functionality.
     * <p/>
     * Only stop when "shouldRun" is turned off (which can only happen at
     * shutdown).
     */
    public void run() {
        try {
            super.run();
        } finally {
            stopped();
        }
    }


    public void start() {
        startWorkerThread();
    }


    /**
     * Ping the node; report an error if we have stopped
     *
     * @throws SmartFrogLivenessException if the node is unhappy
     * @throws RemoteException for network problems
     */
    public void ping()
            throws SmartFrogLivenessException, RemoteException {
        if (isStopped()) {
            throw new SmartFrogLivenessException("DataNode is stopped");
        }
        SmartFrogThread.ping(worker);
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
         * If this thread was constructed using a separate {@link Runnable} run
         * object, then that <code>Runnable</code> object's <code>run</code>
         * method is called; otherwise, this method does nothing and returns.
         * <p> Subclasses of <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            ExtDataNode.this.run();
        }

        /**
         * Add an interrupt to the thread termination
         */
        public synchronized void requestTermination() {
            if (!isTerminationRequested()) {
                super.requestTermination();
                //and interrupt
                interrupt();
            }
        }
    }

}
