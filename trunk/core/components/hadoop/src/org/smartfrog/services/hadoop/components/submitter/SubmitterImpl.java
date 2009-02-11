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
import org.apache.hadoop.mapreduce.JobID;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * A component to submit jobs to a hadoop cluster Created 16-Apr-2008 14:28:22
 */

public class SubmitterImpl extends FileUsingComponentImpl implements Submitter {
    private RunningJob runningJob;
    private boolean terminateJob;
    private boolean terminateWhenJobFinishes;
    private boolean pingJob;
    private boolean deleteOutputDirOnStartup;
    private boolean dumpOnFailure;
    private TaskCompletionEventLogger events;
    public static final String ERROR_FAILED_TO_START_JOB = "Failed to submit job to ";
    public static final String ERROR_SUBMIT_FAILED = "Failed to submit a job";
    private String jobURL;
    private JobID jobID;
    private ManagedConfiguration jobConf;
    private JobSubmitThread worker;
    private Prim results;
    private long jobTimeout;
    private long endTime;

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
        terminateJob = sfResolve(ATTR_TERMINATEJOB, true, true);
        deleteOutputDirOnStartup = sfResolve(ATTR_DELETE_OUTPUT_DIR_ON_STARTUP, true, true);
        jobTimeout = sfResolve(ATTR_JOB_TIMEOUT, 0L, true);
        if (jobTimeout > 0) {
            endTime = System.currentTimeMillis() + (jobTimeout * 1000);
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("Terminating Job after " + jobTimeout + " seconds");
            }
        } else {
            endTime = 0;
        }
        pingJob = sfResolve(ATTR_PINGJOB, true, true);
        if (pingJob) {
            terminateWhenJobFinishes = sfResolve(ATTR_TERMINATE_WHEN_JOB_FINISHES, true, true);
        }

        results = sfResolve(ATTR_RESULTS, results, false);
        dumpOnFailure = sfResolve(ATTR_DUMP_ON_FAILURE, false, true);

        //create the job configuration. The cluster reference is optional
        jobConf = ManagedConfiguration.createConfiguration(this, true, false, true);

        //look for the file
        boolean fileRequired = sfResolve(ATTR_FILE_REQUIRED, true, true);
        if (fileRequired) {
            String filePath = sfResolve(ATTR_ABSOLUTE_PATH, (String) null, false);
            if (filePath != null) {
                if (sfLog().isDebugEnabled()) sfLog().debug("Job is using JAR " + filePath);
                jobConf.setJar(filePath);
            }
        }

        validateOrResolve(jobConf, ConfigurationAttributes.MAPRED_INPUT_DIR, ATTR_INPUT_DIR);
        String outputDir = validateOrResolve(jobConf, ConfigurationAttributes.MAPRED_OUTPUT_DIR, ATTR_OUTPUT_DIR);
        validateOrResolve(jobConf, ConfigurationAttributes.MAPRED_WORKING_DIR, ATTR_WORKING_DIR);
        validateOrResolve(jobConf, ConfigurationAttributes.MAPRED_LOCAL_DIR, ATTR_LOCAL_DIR);
//        validateOrResolve(ConfigurationAttributes.MAPRED_JOB_SPLIT_FILE, ConfigurationAttributes.MAPRED_JOB_SPLIT_FILE);


        if (deleteOutputDirOnStartup) {
            DfsUtils.deleteDFSDirectory(jobConf, outputDir, true);
        }

        worker = new JobSubmitThread();
        worker.start();
    }

    /**
     * look up the hadoopAttr first, if it is not set, look up the sfDirAttr  as an absolute path
     * and set the hadoopAttr to that value. The directory is validated -it must not be an empty string,
     * but there are no checks on the dir existing (it may be  
     * @param conf job configuration to work off
     * @param hadoopAttr hadoop attribute to look for in the jobconf
     * @param sfDirAttr SF directory attribute
     * @return the directory
     * @throws SmartFrogRuntimeException attribute setting problems
     * @throws RemoteException network trouble
     */
    protected String validateOrResolve(ManagedConfiguration conf, String hadoopAttr, String sfDirAttr) throws SmartFrogRuntimeException, RemoteException {
        String directory = conf.get(hadoopAttr,null);
        if (directory == null) {
            //resolve the directory attribute instead
            directory = FileSystem.lookupAbsolutePath(this, sfDirAttr, null, null, true, null);
            //set it
            sfReplaceAttribute(hadoopAttr, directory);
        }
        //now validate the directory
        if (directory.length() == 0) {
            throw new SmartFrogResolutionException("Empty directory attribute: " + hadoopAttr, this);
        }
        return directory;
    }


    /**
     * Handle notifications of termination by (maybe) terminating the running job
     *
     * @param status termination status of sender
     * @param comp   sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        super.sfTerminatedWith(status, comp);
        terminateWorker();
        if (terminateJob) {
            try {
                terminateJob();
            } catch (IOException e) {
                sfLog().info(e);
            }
        }
    }

    private void terminateWorker() {
        try {
            SmartFrogThread.requestThreadTermination(worker);
        } finally {
            worker = null;
        }
    }

    /**
     * Terminate any running job; leaves {@link #runningJob} null.
     * @throws IOException for any problems terminating the job
     */
    public synchronized void terminateJob() throws IOException {
        if (runningJob != null) {
            sfLog().info("Terminating job " + jobID);
            try {
                runningJob.killJob();
            } finally {
                runningJob = null;
            }
        }
    }

    private class JobSubmitThread extends WorkflowThread {

        private String jobTracker;


        /**
         * Create a basic thread. Notification is bound to a local notification object.
         *
         */
        private JobSubmitThread( ) {
            super(SubmitterImpl.this, true);
        }

        /**
         * submit the job
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            jobTracker = resolveJobTracker(SubmitterImpl.this, new Reference(MAPRED_JOB_TRACKER));
            try {
                sfLog().info("Submitting to " + jobTracker);
                JobClient jc = new JobClient(jobConf);
                runningJob = jc.submitJob(jobConf);

                jobID = runningJob.getID();
                jobURL = runningJob.getTrackingURL();
                sfReplaceAttribute(ATTR_JOBID, jobID);
                sfReplaceAttribute(ATTR_JOBURL, jobURL);
                if(results!=null) {
                    results.sfReplaceAttribute(ATTR_JOBID, jobID);
                    results.sfReplaceAttribute(ATTR_JOBURL, jobURL);
                }
                sfLog().info("Job ID: " + jobID + " URL: " + jobURL);
                //set up to log events
                events = new TaskCompletionEventLogger(runningJob, sfLog());
            } catch (IOException e) {
                SFHadoopException fault = new SFHadoopException(ERROR_FAILED_TO_START_JOB + jobTracker
                        + ": " + e,
                        e,
                        SubmitterImpl.this);
                fault.addConfiguration(jobConf);
                throw fault;
            }
        }

        /**
         * Override point: the termination message
         *
         * @return Job information
         */
        protected String getTerminationMessage() {
            if (jobID != null) {
                return "Submitted job " + jobID + " and URL " + jobURL;
            } else {
                return ERROR_FAILED_TO_START_JOB + jobTracker;
            }
        }
    }

    /**
     * Pings by polling for (and logging) remote events, triggering termination
     * if the job has finished or timed out
     *
     * @param source source of ping
     * @throws SmartFrogLivenessException the job failed or timed out
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        if (runningJob != null) {
            try {
                //look for events
                pollAndLogTaskEvents();
                //look for end of job events
                if (pingJob) {
                    if (runningJob.isComplete()) {
                        processEndOfJob();
                    } else {
                        checkForJobTimeout();
                    }
                }

                //check for timeouts; handle by killing and failing
            } catch (IOException e) {
                throw (SmartFrogLivenessException) SmartFrogLifecycleException.forward(e);
            }
        }
    }

    /**
     * Look for and process task events
     * @throws IOException IO problems
     */
    private void pollAndLogTaskEvents() throws IOException {
        TaskCompletionEvent[] taskEvents = events.pollForNewEvents();
        if (taskEvents.length > 0) {
            for (TaskCompletionEvent event : taskEvents) {
                processTaskCompletionEvent(event);
            }
        }
    }


    /**
     * Process task completions. The base class just logs it
     * @param event event that has just finished
     */
    protected void processTaskCompletionEvent(TaskCompletionEvent event) {
        sfLog().info(event.toString());
    }

    private void checkForJobTimeout() throws SmartFrogLivenessException, IOException {
        long now = System.currentTimeMillis();
        if (endTime > 0 && now > endTime) {
            double elapsedTime = (now - endTime)/1000.0;
            sfLog().warn("Timeout, killing job after "+elapsedTime+" seconds");
            terminateJob();
            throw new SmartFrogLivenessException("Timeout before job completed after "
                    + elapsedTime + " seconds"
                    +" requested Timeout = "+jobTimeout);
        }
    }


    /**
     * Handl the end of the job
     * @throws IOException on any failure
     */
    private void processEndOfJob() throws IOException {
        boolean succeeded = runningJob.isSuccessful();
        int taskCount = 0;
        int failures = 0;
        String message = "Job " + runningJob.getJobName()
                + " ID=" + runningJob.getID().toString()
                + " has " + (succeeded ? " succeeded" : "failed");
        StringBuilder builder = new StringBuilder();

        TaskCompletionEvent[] history = runningJob.getTaskCompletionEvents(0);
        for (TaskCompletionEvent event : history) {
            taskCount++;
            builder.append(event.isMapTask() ? "\nMap: " : "\nReduce: ");
            builder.append(event.toString());
            if (event.getTaskStatus() != TaskCompletionEvent.Status.SUCCEEDED) {
                failures++;
                String[] diagnostics = runningJob.getTaskDiagnostics(event.getTaskAttemptId());
                for (String line : diagnostics) {
                    builder.append("\n ");
                    builder.append(line);
                }
            }
            builder.append("\n Tasks run :").append(taskCount).append(" failed: ").append(failures);
            if(!succeeded && dumpOnFailure) {
                builder.append("Job configuration used");
                builder.append(jobConf.dump());
            }
            message = message + builder.toString();

        }
        sfLog().info(message);
        if (terminateWhenJobFinishes) {
            TerminationRecord record = succeeded ?
                    TerminationRecord.normal(message, sfCompleteNameSafe()) :
                    TerminationRecord.abnormal(message, sfCompleteNameSafe());
            new ComponentHelper(this).targetForTermination(record, false, false);
        }
    }

    /**
     * Resolve a job tracker reference. This resolves the reference then
     * looks for {@link #MAPRED_JOB_TRACKER} value underneath. Works with
     * both Prim and ComponentDescription references
     * @param prim component to work with
     * @param ref reference to resolve
     * @return the job tracker URL
     * @throws SmartFrogResolutionException resolution problems
     * @throws RemoteException network problems
     */
    public static String resolveJobTracker(Prim prim, Reference ref)
            throws SmartFrogResolutionException, RemoteException {
        Object target = prim.sfResolve(ref, true);
        if (target instanceof Prim) {
            Prim jobTracker = (Prim) target;
            return jobTracker.sfResolve(MAPRED_JOB_TRACKER, "", true);
        }
        if (target instanceof ComponentDescription) {
            ComponentDescription jobTracker = (ComponentDescription) target;
            return jobTracker.sfResolve(MAPRED_JOB_TRACKER, "", true);
        }
        //neither of those? resolve to a string and let the runtime handle errors
        return prim.sfResolve(ref, "", true);
    }
}