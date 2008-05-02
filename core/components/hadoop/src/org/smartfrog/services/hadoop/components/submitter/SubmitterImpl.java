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
package org.smartfrog.services.hadoop.components.submitter;

import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TaskCompletionEvent;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * A component to submit jobs to a hadoop cluster Created 16-Apr-2008 14:28:22
 */

public class SubmitterImpl extends EventCompoundImpl implements Submitter {
    private RunningJob runningJob;
    private boolean terminateJob;
    private boolean terminateWhenJobFinishes;
    private boolean pingJob;
    private Job job;
    private TaskCompletionEventLogger events;
    public static final String ERROR_FAILED_TO_START_JOB = "Failed to submit job to ";

    public SubmitterImpl() throws RemoteException {
    }

    /**
     * Can be called to start components.
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Prim jobPrim = sfResolve(ATTR_JOB, (Prim) null, true);
        job = (Job) jobPrim;
        terminateJob = sfResolve(ATTR_TERMINATEJOB, true, true);
        pingJob = sfResolve(ATTR_PINGJOB, true, true);
        if (pingJob) {
            terminateWhenJobFinishes = sfResolve(ATTR_TERMINATEWHENJOBFINISHES, true, true);
        }
        ManagedConfiguration conf = new ManagedConfiguration(jobPrim);
        String filePath = jobPrim.sfResolve(Job.ATTR_ABSOLUTE_PATH, (String) null, false);
        if (filePath != null) {
            if (sfLog().isDebugEnabled()) sfLog().debug("Job is using JAR " + filePath);
            conf.setJar(filePath);
        }

        validateOrResolve("mapred.input.dir", "input.dir");
        validateOrResolve("mapred.output.dir", "output.dir");
        validateOrResolve("mapred.working.dir", "working.dir");
        validateOrResolve("mapred.local.dir", "local.dir");
        String jobTracker = jobPrim.sfResolve(MAPRED_JOB_TRACKER, "", true);
        try {
            sfLog().info("Submitting to " + jobTracker);
            runningJob = JobClient.runJob(conf);
        } catch (IOException e) {
            throw SmartFrogLifecycleException.forward(ERROR_FAILED_TO_START_JOB + jobTracker, e, this);
        }
        sfReplaceAttribute(ATTR_JOBID, runningJob.getJobID());
        //set up to log events
        events = new TaskCompletionEventLogger(runningJob, sfLog());
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL, "Submitted job to "+jobTracker,null, null);
    }


    String validateOrResolve(String hadoopAttr, String sourceAttr) throws SmartFrogRuntimeException, RemoteException {
        String directory = sfResolve(hadoopAttr, "", false);
        if (directory == null) {
            //resolve the directory attribute instead
            directory = FileSystem.lookupAbsolutePath(this, sourceAttr, null, null, true, null);
            //set it
            sfReplaceAttribute(hadoopAttr, directory);
        }
        //now validate the directory?
        return directory;
    }

    /**
     * Handle notifications of termination
     *
     * @param status termination status of sender
     * @param comp   sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        super.sfTerminatedWith(status, comp);
        if (terminateJob) {
            try {
                terminateJob();
            } catch (IOException e) {
                sfLog().ignore(e);
            }
        }
    }

    public synchronized void terminateJob() throws IOException {
        if (runningJob != null) {
            RunningJob rjob;
            rjob = runningJob;
            runningJob = null;
            rjob.killJob();
        }
    }

    /**
     * Pings by polling for (and logging) remote events, triggering termination if the job has finished
     * Failure to pull for a running job is an error; failure for liveness events is treated less seriously
     * @param source source of ping
     * @throws SmartFrogLivenessException liveness failed
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (runningJob == null) {
            return;
        } else {
            try {
                if (pingJob && runningJob.isComplete()) {
                    boolean succeeded = runningJob.isSuccessful();
                    String message = "Job " + runningJob.getJobID() + " has " + (succeeded ? " succeeded" : "failed");
                    sfLog().info(message);
                    if (terminateWhenJobFinishes) {
                        TerminationRecord record = succeeded ? TerminationRecord.normal(message, name) :
                                TerminationRecord.abnormal(message, name);
                        new ComponentHelper(this).targetForTermination(record, false, false);
                    }
                }
                TaskCompletionEvent[] taskEvents = events.pollForNewEvents();
                if (taskEvents.length > 0) {
                    for (TaskCompletionEvent event : taskEvents) {
                        sfLog().info(event.toString());
                    }
                }
            } catch (IOException e) {
                throw (SmartFrogLivenessException) SmartFrogLifecycleException.forward(e);
            }
        }
    }
}