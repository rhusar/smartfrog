/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ant;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Executor;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.io.File;

/**
 * Created 31-Oct-2007 14:24:05
 */

public class AntBuildImpl extends PrimImpl implements AntBuild {

    public static final String ERROR_NO_DIRS = "no build directories specified: one of '" + ATTR_BASEDIR + "' or '"
            + ATTR_DIRECTORIES + "' must be set, or " + ATTR_ANTFILE + " must point to a file";
    public static final String ERROR_MISSING_BUILD_FILE = "Missing build file: ";
    public static final String BUILD_SUCCESSFUL = "Build successful";
    public static final String BUILD_FAILED = "Build failed ";

    private ComponentHelper helper;
    private AntHelper antHelper;
    private Prim propertyTarget;
    private File baseDir;
    private String buildfile;
    private File antfile;
    private Vector<File> directories;
    private Vector<String> targets;
    private AntThread workerAnt;
    private Vector propertyTuples;
    private int logLevel;
    private boolean keepGoingInSingleBuild;
    private boolean keepGoingAcrossFiles;
    private int shutdownTimeout;
    private boolean skipUnimplementedTargets;

    private Stack<BuildPlan> buildqueue;
    private List<BuildPlan> results;
    public static final String ERROR_NO_DIRECTORY = "No directory: ";
    public static final String ERROR_NOT_A_DIRECTORY = "Not a directory: ";
    private Properties properties;
    public static final String ERROR_SHUTDOWN_TIMEOUT = "Ant thread did not shut down in the time allocated: ";
    private static final Reference refTargets = new Reference(ATTR_TARGETS);
    private static final Reference refProperties = new Reference(ATTR_PROPERTIES);


    public AntBuildImpl() throws RemoteException {
    }


    /**
     * read in state and start the component
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        helper = new ComponentHelper(this);
        antHelper = new AntHelper(this);
        antHelper.validateAnt();

        antfile = FileSystem.lookupAbsoluteFile(this, ATTR_ANTFILE, null, null, false, null);
        //you need a build file if there is no generic ant file.
        if (antfile == null) {
            buildfile = sfResolve(ATTR_BUILDFILE, buildfile, true);
        } else if (!antfile.exists()) {
            throw new SmartFrogDeploymentException(ERROR_MISSING_BUILD_FILE + antfile);
        }
        baseDir = FileSystem.lookupAbsoluteFile(this, ATTR_BASEDIR, null, null, false, null);
        directories = FileSystem.resolveFileList(this, new Reference(ATTR_DIRECTORIES), baseDir, false);
        if (directories == null || directories.size() == 0) {

            //when there is no basedir, we get it from the parent dir
            if (baseDir == null && antfile != null) {
                //infer it from the antfile
                baseDir = antfile.getParentFile();
            }
            //if it is still null, trouble
            if (baseDir == null) {
                throw new SmartFrogResolutionException(ERROR_NO_DIRS);
            } else {
                //create a directories
                directories = new Vector<File>(1);
                directories.add(baseDir);
            }
        }

        targets = ListUtils.resolveStringList(this, refTargets, true);

        //set up properties
        propertyTuples = ListUtils.resolveStringTupleList(this, refProperties, false);
        //convert the list
        properties = ListUtils.convertToProperties(propertyTuples);
        String level = sfResolve(Ant.ATTR_LOG_LEVEL, Ant.ATTR_LOG_LEVEL_INFO, false);
        //set up log levels
        logLevel = antHelper.extractLogLevel(level, Project.MSG_INFO);

        keepGoingInSingleBuild = sfResolve(ATTR_KEEPGOINGINSINGLEBUILD, keepGoingInSingleBuild, true);
        keepGoingAcrossFiles = sfResolve(ATTR_KEEPGOINGACROSSFILES, keepGoingInSingleBuild, true);
        skipUnimplementedTargets = sfResolve(ATTR_SKIPUNIMPLEMENTEDTARGETS, false, true);
        shutdownTimeout = sfResolve(ATTR_SHUTDOWNTIMEOUT, shutdownTimeout, true);
        propertyTarget = sfResolve(Ant.ATTR_PROPERTY_TARGET, propertyTarget, false);
        //now create the queue of files to build
        buildqueue = new Stack<BuildPlan>();
        results = new ArrayList<BuildPlan>(directories.size());
        for (File dir : directories) {
            //validate the directory
            if (!dir.exists()) {
                throw new SmartFrogDeploymentException(ERROR_NO_DIRECTORY + dir);
            }
            if (!dir.isDirectory()) {
                throw new SmartFrogDeploymentException(ERROR_NOT_A_DIRECTORY + dir);
            }
            //create a build plan for this directory
            BuildPlan plan = new BuildPlan();
            plan.basedir = dir;
            if (antfile != null) {
                plan.buildFile = antfile;
            } else {
                plan.buildFile = new File(dir, buildfile);
            }
            if (!plan.buildFile.exists()) {
                throw new SmartFrogDeploymentException(ERROR_MISSING_BUILD_FILE + plan.buildFile);
            }
            sfLog().info(plan);
            buildqueue.add(plan);
        }

        //to get here. all is well.
        workerAnt = new AntThread();
        workerAnt.start();
    }


    /**
     * Shut down the ant build
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        if (workerAnt != null) {
            if (!workerAnt.halt(shutdownTimeout)) {
                sfLog().error(ERROR_SHUTDOWN_TIMEOUT + shutdownTimeout + "ms");
                workerAnt.interrupt();
                workerAnt.stop();
            }
        }
        super.sfTerminateWith(status);
    }


    /**
     * This is our build plan, what we want to build for a specific target.
     */
    private static class BuildPlan {
        String name;
        long started, duration;
        File buildFile;
        File basedir;
        Throwable exception;
        int exitStatus;

        /**
         * Returns a string representation of the object.
         *
         * @return a string representation of the object.
         */
        public synchronized String toString() {
            return "Building " + buildFile + " in " + basedir
                    + (exception != null ? ("\nExited with " + exception.getMessage())
                        : "");

        }
    }


    /**
     * This thread knows how to build a queue of projects
     */
    private class AntThread extends SmartFrogThread {


        public volatile boolean halted;

        private InterruptibleExecutor executor = new InterruptibleExecutor(skipUnimplementedTargets);
        private InterruptibleLogger interruptibleLogger;


        private synchronized void setInterruptibleLogger(InterruptibleLogger interruptibleLogger) {
            this.interruptibleLogger = interruptibleLogger;
        }

        /**
         * Halt for a given period of time.
         *
         * @param timeout timeout in milliseconds
         * @return true if the build halted in that time
         */
        public boolean halt(long timeout) {
            //we are halted
            synchronized (this) {
                halted = true;
                //stop the executor too
                executor.halt();
                if (interruptibleLogger != null) {
                    interruptibleLogger.halt();
                }
            }
            try {
                boolean finished = waitForNotification(timeout);
                if (finished) {
                    return true;
                }
            } catch (InterruptedException e) {
                //interrupted!
                return true;
            }
            //not interrupted, not finished. not good.
            return false;
        }


        /**
         * do the work
         */
        public void execute() throws Throwable {
            Throwable result = null;
            int errors = 0;
            while (!buildqueue.empty() && !halted) {
                BuildPlan plan = buildqueue.pop();
                runOneBuildPlan(plan);
                results.add(plan);
                if (plan.exception != null) {
                    result = plan.exception;
                    errors++;
                    if (!keepGoingAcrossFiles) {
                        //exit the loop here
                        break;
                    }
                }
            }
            TerminationRecord tr;
            if (errors==0) {
                tr = TerminationRecord.normal(BUILD_SUCCESSFUL,
                        sfCompleteNameSafe(),
                        result);
            } else {
                tr = TerminationRecord.abnormal(BUILD_FAILED + "error count=" + errors
                        +"; "+result.getMessage(),
                        sfCompleteNameSafe(),
                        result);
            }
            helper.targetForWorkflowTermination(tr);
        }


        /**
         * Run one build plan and put the results back in the plan. Any build that throws an error other than a
         * successful exit is logged as an error.
         *
         * @param plan plan to run
         * @return true if the build worked
         */
        boolean runOneBuildPlan(BuildPlan plan) {
            plan.started = System.currentTimeMillis();
            try {
                innerBuild(plan);
            } catch (RemoteException e) {
                plan.exception = e;
            } catch (SmartFrogAntBuildException e) {
                if (e.hasExitStatus()) {
                    plan.exitStatus = e.getExitStatus();
                    if (e.getExitStatus() != 0) {
                        plan.exception = e;
                    }
                } else {
                    //its an abnormal failure, log it
                    plan.exception = e;
                }
            }
            long finished = System.currentTimeMillis();
            plan.duration = finished - plan.started;
            sfLog().info("Build duration of " + plan.buildFile + " " + plan.duration / 1000.0 + "s");
            return plan.exception == null;
        }

        /**
         * run one build file
         *
         * @param plan the plan
         * @throws SmartFrogAntBuildException if the build fails
         * @throws RemoteException            for network problems
         */
        void innerBuild(BuildPlan plan) throws SmartFrogAntBuildException, RemoteException {

            try {
                File buildFile = plan.buildFile;
                File basedir = plan.basedir;
                Project project = antHelper.createNewProject();

                //Register build listener
                setInterruptibleLogger(antHelper.listenToProject(project, logLevel, sfLog()));
                //set the properties
                antHelper.addUserProperties(project, properties);
                project.setKeepGoingMode(keepGoingInSingleBuild);
                //project.
                project.setUserProperty(MagicNames.ANT_FILE,
                        buildFile.getAbsolutePath());
                project.setExecutor(executor);

                //it's not clear what to do when there is a basedir in the project itself.
                //<subant> and <ant> behave differently here.
                project.setBaseDir(basedir);

                //first build event
                //tell the world the build began
                project.fireBuildStarted();
                //the rest of the work is surrounded by a try/catch purely to ensure that
                //the loggers and listeners are notified at the end
                Throwable thrown = null;
                try {
                    ProjectHelper.configureProject(project, buildFile);
                    plan.name = project.getName();
                    String defaultTarget = project.getDefaultTarget();
                    if (targets.size() == 0) {
                        if (defaultTarget != null) {
                            sfLog().info("default target of " + buildFile + " is " + defaultTarget);
                            targets.addElement(defaultTarget);
                        } else {
                            //no default target. This is not an error, you could just have simple script
                            sfLog().info("no default target in " + buildFile);
                        }
                    }
                    //here the project is set up, so run it
                    project.executeTargets(targets);
                } catch (RuntimeException e) {
                    thrown = e;
                    throw e;
                } catch (Error e) {
                    thrown = e;
                    throw e;
                } finally {
                    project.fireBuildFinished(thrown);
                    propagateProperties(project);
                }
            } catch (ExitStatusException ese) {
                //raised in a <fail> operation.
                int status = ese.getStatus();
                if (status != 0) {
                    throw new SmartFrogAntBuildException(ese);
                } else {
                    sfLog().debug("Build exited successfully");
                }
            } catch (BuildException e) {
                //any other event is wrapped
                throw new SmartFrogAntBuildException(e);
            }
        }

        /**
         * Propagate the ant properties to whatever is in {@link #propertyTarget} Any failure to set these is not
         * treated as an error, we log at warn level and continue. why? So that a failure here does not hide underlying
         * build problems.
         *
         * @param project project to work with
         * @throws RemoteException for network problems
         */
        private void propagateProperties(Project project) throws RemoteException {
            if (propertyTarget != null) {
                try {
                    AntRuntime.propagateAntProperties(propertyTarget, project.getProperties());
                } catch (SmartFrogRuntimeException e) {
                    //we don't throw anything else here, log it and continue
                    sfLog().warn("Failed to set Ant properties on the propertyTarget", e);
                } catch (RemoteException e) {
                    //we don't throw anything else here, log it and continue
                    sfLog().warn("Failed to set Ant properties on the propertyTarget", e);
                }
            }
        }

    }

    /**
     * This is a special executor that can interrupt the build between targets
     */
    private static class InterruptibleExecutor implements Executor {

        private volatile boolean halted;
        private boolean skipMissingTargets;


        /**
         * Create an instance; pass in its policy w.r.t. missing targets
         *
         * @param skipMissingTargets should we skip missing targets
         */
        InterruptibleExecutor(boolean skipMissingTargets) {
            this.skipMissingTargets = skipMissingTargets;
        }

        /**
         * is the build halted?
         *
         * @return true if the build has been halted
         */
        public boolean isHalted() {
            return halted;
        }

        /**
         * Trigger a halt
         */
        public synchronized void halt() {
            halted = true;
        }

        /**
         * {@inheritDoc}.
         */
        public void executeTargets(Project project, String[] targetNames) throws BuildException {
            BuildException thrown = null;
            for (String target : targetNames) {
                if (isHalted()) {
                    throw new BuildInterruptedException("Interrupted in " + project.getName() + "." + target);
                }
                try {
                    if (skipMissingTargets
                            && project.getTargets().get(target) == null) {
                        project.log("Skipping missing target " + target, Project.MSG_WARN);
                    } else {
                        project.executeTarget(target);
                    }
                } catch (BuildException e) {
                    if (project.isKeepGoingMode()) {
                        thrown = thrown == null ? e : thrown;
                    } else {
                        throw e;
                    }
                }
            }
            //throw the first build exception we hit (and not the last)
            if (thrown != null) {
                throw thrown;
            }
        }

        /**
         * {@inheritDoc}.
         */
        public Executor getSubProjectExecutor() {
            return new InterruptableSubProjectExecutor(this);
        }
    }


    /**
     * This is for sub projects; it reads the interrupted state from its parent Note that we currently don't do anything
     * useful with that information, as project is in charge of ordering execution. Note also that the subant task does
     * its own scheduling. <p/> All that means, it's not that easy to interrupt a running build cleanly. We'd need to
     * extend Ant to do it.
     */
    private static class InterruptableSubProjectExecutor implements Executor {
        private InterruptibleExecutor parent;

        InterruptableSubProjectExecutor(InterruptibleExecutor parent) {
            this.parent = parent;
        }

        /**
         * is the build halted?
         *
         * @return true if the build has been halted
         */
        public boolean isHalted() {
            return parent.isHalted();
        }

        /**
         * {@inheritDoc}.
         */
        public void executeTargets(Project project, String[] targetNames)
                throws BuildException {
            project.executeSortedTargets(
                    project.topoSort(targetNames, project.getTargets(), false));
        }

        /**
         * {@inheritDoc}.
         */
        public Executor getSubProjectExecutor() {
            return this;
        }
    }
}
