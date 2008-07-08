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
package org.smartfrog.services.hadoop.components.tracker;

import org.apache.hadoop.mapred.ExtJobTracker;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.HadoopComponentImpl;
import org.smartfrog.services.hadoop.components.cluster.HadoopServiceImpl;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 19-May-2008 13:55:33
 */

public class JobTrackerImpl extends HadoopServiceImpl implements HadoopCluster {

    private TrackerThread worker;

    public JobTrackerImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        try {

            //to work around a bug, HADOOP-3438, we set the system property "hadoop.log.dir"
            //to an empty string if it is not set
            //see https://issues.apache.org/jira/browse/HADOOP-3438
            if (System.getProperty(ConfigurationAttributes.HADOOP_LOG_DIR) == null) {
                System.setProperty(ConfigurationAttributes.HADOOP_LOG_DIR, ".");
            }
            ExtJobTracker tracker = new ExtJobTracker(createConfiguration());
            setService(tracker);
            worker = new TrackerThread(tracker);
            worker.start();
        } catch (IOException e) {
            throw new SmartFrogLifecycleException("When creating the job tracker " + e.getMessage(), e, this);
        } catch (InterruptedException e) {
            throw new SmartFrogLifecycleException("When creating the job tracker " + e.getMessage(), e, this);
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread t = worker;
        worker = null;
        SmartFrogThread.requestThreadTermination(t);
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (worker == null || worker.isFinished()) {
            throw new SmartFrogLivenessException("Worker is not running");
        }
    }

    /**
     * This is a private worker thread that can be interrupted better
     */
    private class TrackerThread extends WorkflowThread {
        private ExtJobTracker tracker;

        /**
         * Creates a new thread
         * @param tracker the tracker
         */
        private TrackerThread(ExtJobTracker tracker) {
            super(JobTrackerImpl.this, true);
            this.tracker = tracker;
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
            tracker.offerService();
        }

        public ExtJobTracker getTracker() {
            return tracker;
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
