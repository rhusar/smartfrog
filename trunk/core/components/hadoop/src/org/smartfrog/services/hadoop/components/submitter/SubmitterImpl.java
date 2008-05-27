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
import org.apache.hadoop.mapred.JobID;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.common.DfsUtils;
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
    private boolean deleteOutputDirOnStartup;
    private Job job;
    private TaskCompletionEventLogger events;
    public static final String ERROR_FAILED_TO_START_JOB = "Failed to submit job to ";
    private Prim jobPrim;
    private String jobURL;
    private JobID jobID;

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
        jobPrim = sfResolve(ATTR_JOB, (Prim) null, true);
        job = (Job) jobPrim;
        terminateJob = sfResolve(ATTR_TERMINATEJOB, true, true);
        deleteOutputDirOnStartup = sfResolve(ATTR_DELETE_OUTPUT_DIR_ON_STARTUP, true, true);
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

        validateOrResolve(ConfigurationAttributes.MAPRED_INPUT_DIR, Job.ATTR_INPUT_DIR);
        String outputDir = validateOrResolve(ConfigurationAttributes.MAPRED_OUTPUT_DIR, Job.ATTR_OUTPUT_DIR);
        validateOrResolve(ConfigurationAttributes.MAPRED_WORKING_DIR, Job.ATTR_WORKING_DIR);
        validateOrResolve(ConfigurationAttributes.MAPRED_LOCAL_DIR, Job.ATTR_LOCAL_DIR);

        if (deleteOutputDirOnStartup) {
            DfsUtils.deleteDFSDirectory(conf, outputDir, true);
        }

        String jobTracker = jobPrim.sfResolve(MAPRED_JOB_TRACKER, "", true);
        try {
            //TODO: move this to a separate thread
            sfLog().info("Submitting to " + jobTracker);
            JobClient jc = new JobClient(conf);
            runningJob = jc.submitJob(conf);

            //JobClient.runJob(conf);
            jobID = runningJob.getID();
            sfReplaceAttribute(ATTR_JOBID, jobID);
            jobURL = runningJob.getTrackingURL();
            sfReplaceAttribute(ATTR_JOBURL, jobURL);
            sfLog().info("Job ID: "+ jobID +" URL: "+jobURL);
        } catch (IOException e) {
            throw SmartFrogLifecycleException.forward(ERROR_FAILED_TO_START_JOB + jobTracker, e, this);
        }
        //set up to log events
        events = new TaskCompletionEventLogger(runningJob, sfLog());
        new ComponentHelper(this)
                .sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL, "Submitted job "+jobID+" and URL "+jobURL , null, null);
    }


    String validateOrResolve(String hadoopAttr, String sourceAttr) throws SmartFrogRuntimeException, RemoteException {
        String directory = jobPrim.sfResolve(hadoopAttr, "", false);
        if (directory == null) {
            //resolve the directory attribute instead
            directory = FileSystem.lookupAbsolutePath(jobPrim, sourceAttr, null, null, true, null);
            //set it
            sfReplaceAttribute(hadoopAttr, directory);
        }
        //now validate the directory
        if (directory.length() == 0) {
            throw new SmartFrogRuntimeException("Empty directory attribute: " + hadoopAttr);
        }
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
     * Pings by polling for (and logging) remote events, triggering termination if the job has finished Failure to pull
     * for a running job is an error; failure for liveness events is treated less seriously
     *
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