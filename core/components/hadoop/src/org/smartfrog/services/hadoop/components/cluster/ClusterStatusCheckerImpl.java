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
package org.smartfrog.services.hadoop.components.cluster;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobTracker;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.HadoopConfiguration;
import org.smartfrog.services.hadoop.components.submitter.SubmitterImpl;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 30-Apr-2008 14:20:50
 */


public class ClusterStatusCheckerImpl extends PrimImpl
        implements HadoopConfiguration, HadoopCluster, ClusterStatusChecker, Condition {
    private JobClient client;
    private boolean checkOnLiveness;
    private boolean jobTrackerLive;

    //declares that we can handle the filesystem
    private boolean supportedFileSystem;

    /**
     * For all the min/max values, <0 means 'dont check'
     */
    private int minActiveMapTasks;
    private int maxActiveMapTasks;
    private int minActiveReduceTasks;
    private int maxActiveReduceTasks;
    private int maxSupportedMapTasks;
    private int maxSupportedReduceTasks;
    private String jobTracker;
    public static final String STATUS_CHECKED = "Hadoop Cluster status checked against ";
    public static final String ERROR_CANNOT_CONNECT = "Cannot connect to ";


    public ClusterStatusCheckerImpl() throws RemoteException {
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
        jobTracker = SubmitterImpl.resolveJobTracker(this, new Reference(MAPRED_JOB_TRACKER));
        supportedFileSystem = sfResolve(ATTR_SUPPORTEDFILESYSTEM, false, true);
        jobTrackerLive = sfResolve(ATTR_JOBTRACKERLIVE, false, true);
        minActiveMapTasks = sfResolve(ATTR_MIN_ACTIVE_MAP_TASKS, 0, true);
        maxActiveMapTasks = sfResolve(ATTR_MAX_ACTIVE_MAP_TASKS, 0, true);
        maxSupportedMapTasks = sfResolve(ATTR_MAX_SUPPORTED_MAP_TASKS, 0, true);
        minActiveReduceTasks = sfResolve(ATTR_MIN_ACTIVE_REDUCE_TASKS, 0, true);
        maxActiveReduceTasks = sfResolve(ATTR_MAX_ACTIVE_REDUCE_TASKS, 0, true);
        maxSupportedReduceTasks = sfResolve(ATTR_MAX_SUPPORTED_REDUCE_TASKS, 0, true);
        checkOnLiveness = sfResolve(ATTR_CHECK_ON_LIVENESS, false, true);
        boolean checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, false, true);

        if (checkOnStartup) {
            checkClusterStatus();
            new ComponentHelper(this)
                    .sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                            STATUS_CHECKED + jobTracker, null, null);
        }
    }

    /**
     * Demand create the cluster proxy. This can take time and fail.
     *
     * @return a client which may exist already
     * @throws SFHadoopException if the connection cannot be made
     */

    private synchronized JobClient createClientOnDemand() throws SFHadoopException {
        if (client != null) {
            return client;
        }
        try {
            ManagedConfiguration conf = new ManagedConfiguration(this);
            sfLog().info("Connecting to " + jobTracker);
            client = new JobClient(conf);
            return client;
        } catch (IOException e) {
            throw new SFHadoopException(ERROR_CANNOT_CONNECT + jobTracker, e, this);
        }
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
        if (checkOnLiveness) {
            try {
                checkClusterStatus();
            } catch (SFHadoopException e) {
                throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
            }
        }
    }

    /**
     * Check the cluster status
     *
     * @throws SFHadoopException on any problem with the checks
     */
    private void checkClusterStatus() throws SFHadoopException {

        try {
            JobClient cluster = createClientOnDemand();
            ClusterStatus status = cluster.getClusterStatus();

            if (supportedFileSystem) {
                try {
                    FileSystem fs = cluster.getFs();
                } catch (IOException e) {
                    throw new SFHadoopException("File system will not load "
                            + e.getMessage(),
                            e,
                            this);
                } catch (IllegalArgumentException e) {
                    throw new SFHadoopException("Bad File system URI"
                            + e.getMessage(),
                            e,
                            this);
                }
            }
            if (jobTrackerLive) {
                JobTracker.State state = status.getJobTrackerState();
                if (!state.equals(JobTracker.State.RUNNING)) {
                    throw new SFHadoopException("Job Tracker at " + jobTracker + " is not running", this);
                }
            }
            checkRange(minActiveMapTasks, maxActiveMapTasks, status.getMapTasks(), "map task");
            checkRange(minActiveReduceTasks, maxActiveReduceTasks, status.getReduceTasks(), "reduce task");
            checkMax(maxSupportedMapTasks, status.getMaxMapTasks(), "supported max map task");
            checkMax(maxSupportedReduceTasks, status.getMaxReduceTasks(), "supported max reduce task");
        } catch (IOException e) {
            throw new SFHadoopException("Cannot connect to" + jobTracker, e, this);
        }
    }


    /**
     * Check that the range is valid
     *
     * @param min    minimum value
     * @param max    maxiumum value
     * @param actual current value
     * @param field  field name
     * @throws SFHadoopException if we are out of range
     */
    private void checkRange(int min, int max, int actual, String field) throws SFHadoopException {
        checkMin(min, actual, field);
        checkMax(max, actual, field);
    }

    private void checkMin(int min, int actual, String field) throws SFHadoopException {
        if (min >= 0 && actual < min) {
            throw new SFHadoopException(field + " count too low - minimum " + min + " actual " + actual);
        }
    }

    private void checkMax(int max, int actual, String field) throws SFHadoopException {
        if (max >= 0 && actual > max) {
            throw new SFHadoopException(field + " count too high - maximum " + max + " actual " + actual);
        }
    }

    /**
     * Check that the cluster is reachable.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            checkClusterStatus();
            return true;
        } catch (SFHadoopException e) {
            sfLog().debug(e);
            return false;
        }
    }
}
