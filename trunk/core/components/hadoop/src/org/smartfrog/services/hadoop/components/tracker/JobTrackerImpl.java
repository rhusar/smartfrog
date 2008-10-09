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
import org.apache.hadoop.util.Service;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.ClusterManager;
import org.smartfrog.services.hadoop.components.cluster.HadoopServiceImpl;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 19-May-2008 13:55:33
 */

public class JobTrackerImpl extends HadoopServiceImpl implements HadoopCluster, ClusterManager {

    //private TrackerThread worker;
    private static final String NAME = "JobTracker";

    public JobTrackerImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     * @return the name of the Hadoop service deployed
     */
    @Override
    protected String getName() {
        return NAME;
    }


    /**
     * Get the underlying job tracker
     * @return the job tracker or null
     */
    public ExtJobTracker getJobTracker() {
        return (ExtJobTracker) getService();
    }

    /**
     * Start the service deployment in a new thread
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        //to work around a bug, HADOOP-3438, we set the system property "hadoop.log.dir"
        //to an empty string if it is not set
        //see https://issues.apache.org/jira/browse/HADOOP-3438
        if (System.getProperty(ConfigurationAttributes.HADOOP_LOG_DIR) == null) {
            System.setProperty(ConfigurationAttributes.HADOOP_LOG_DIR, ".");
        }

        createAndDeployService();
    }

    /** {@inheritDoc} */
    protected Service createTheService(ManagedConfiguration configuration) throws IOException, SmartFrogException {
        try {
            Service service = new ExtJobTracker(this, configuration);
            return service;
        } catch (InterruptedException e) {
            throw new SmartFrogLifecycleException(ERROR_NO_START + getName() + ": " + e, e, this);
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
/*
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread t = worker;
        worker = null;
        SmartFrogThread.requestThreadTermination(t);
    }*/

    /**
     * Change the shutdown process to include an interrupt
     * @param deployer
     */
    @Override
    protected void terminateDeployerThread(ServiceDeployerThread deployer) {
        deployer.requestTerminationWithInterrupt();
    }

    /**
     * Create the deployer thread
     *
     * @param hadoopService service to bind to and deploy.
     * @param conf          configuration -used for diagnostics
     * @return a new thread that is an instance of ServiceDeployerThread
     */
    @Override
    protected ServiceDeployerThread createDeployerThread(Service hadoopService,
                                                         ManagedConfiguration conf) {
        return new ServiceDeployerThread(hadoopService, conf, true);
    }


    /**
     * Get the count of current workers
     *
     * @return 0 if not live, or the count of active workers
     * @throws RemoteException for network problems
     */
    public int getLiveWorkerCount() throws RemoteException {
        return getJobTracker().getNumResolvedTaskTrackers();
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
/*    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (worker == null || worker.isFinished()) {
            throw new SmartFrogLivenessException("Worker is not running");
        }
    }*/


    /**
     * after deployment, call {@link ExtJobTracker#offerService()} to start the service.
     * This call will not return until the work is finished
     * @throws IOException IO problems
     * @throws SmartFrogException smartfrog problems
     */
    @Override
    protected void onServiceDeploymentComplete() throws IOException, SmartFrogException {
        super.onServiceDeploymentComplete();
        //check that the tracker is now bound to a filesystem
        String dir = getJobTracker().getSystemDir();
        sfLog().info("System dir is " + dir);
        getJobTracker().getFilesystemName();
        try {
            getJobTracker().offerService();
        } catch (InterruptedException e) {
            //this is ok, it is time to terminate
            sfLog().ignore("tracker was interrupted",e);
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
