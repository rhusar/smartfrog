/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.tools.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.taskdefs.Sequential;

/**
 * 
 * This is an extension of &lt;junit&gt; that lets us integrate startup, a waitfor
 * condition and a shutdown sequence into the test case. 
 * 
 * This task is used for SmartFrog's internal testing purposes, and for 
 * testing components. There are no guarantees of stability over time. 
 * 
 * <p/>
 * It's based upon Ant's
 * own JUnit task, and credits Cactus as showing what could be done. Apache
 * Cactus is coded around deploying to an application server; this task can
 * deploy to anything for which we have setup and teardown sequences.
 * <p/>
 * <p/>
 * This is the workflow for testing 
 * <ol> 
 * <li>The startup sequence is run to
 * completion</li> 
 * <li>In parallel, ( a) the application is started</li> 
 * <li>and (b) the sequence of waitfor+tests is run.</li> 
 * <li>After the tests run, the teardown operation is executed</li>
 * <li>Any build exception thrown by testingis thrown</li>
 * </ol> 
 * 
 * <p>No matter what goes wrong, once startup has
 * succeeded, teardown gets invoked. </p>
 * @ant.task category="SmartFrog" name="sf-functionaltest"
 */
public class FunctionalTestTask extends Task {

    //delegate to hold our settings
    private Sequential test;
    //the parallel workflow
    private Parallel parallel;
    //startup sequence
    private Sequential setup;
    //nested application (if not null)
    private Sequential application;
    //teardown operation
    private Sequential teardown;
    //waitfor operation
    private FaultingWaitForTask probe;
    private int timeout;
    private int shutdownTime=10;
    private TaskHelper helper = new TaskHelper(this);
    public static final String MESSAGE_NO_JUNIT = "No tests defined";
    public static final String EXCEPTION_CAUGHT_ON_CLEANUP = "Exception caught on cleanup:";
    public static final String MESSAGE_FORCED_SHUTDOWN_OF_APPLICATION = "Forced shutdown of application";


    public FunctionalTestTask() {
    }


    public Sequential getSetup() {
        return setup;
    }

    /**
     * Define a sequence of operations to run at startup. After running these,
     * the teardown sequence will be called to tear down the system.
     *
     * @param setup
     */
    public void addSetup(Sequential setup) {
        this.setup = setup;
    }

    public Sequential getTeardown() {
        return teardown;
    }

    /**
     * A sequence of operations that are used to tear down the system.
     *
     * @param teardown teardown sequence
     */
    public void addTeardown(Sequential teardown) {
        if (this.teardown != null) {
            log("Overriding previous definition of <teardown>");
        }
        this.teardown = teardown;
    }


    public Sequential getApplication() {
        return application;
    }

    /**
     * The sequence of tasks used to define the application. This is for hosting
     * the server in a parallel thread to the test run.
     *
     * @param application application sequence
     */
    public void addApplication(Sequential application) {
        if(this.application!=null) {
            log("Overriding previous definition of <test>");
        }
        this.application = application;
    }

    /**
     * This is the timeout for testing; any timeouts in the probe are not
     * covered under this.
     *
     * @param timeout in seconds.
     */
    public void setTestTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Set the timeout allowed for a clean shutdown of the application.
     * After this timeout, a more brutal operation is used.
     * @param shutdownTime timeout in seconds
     */
    public void setShutdownTime(int shutdownTime) {
        this.shutdownTime = shutdownTime;
    }

    /**
     * Add a probe, conditions that have to be met before testing begins.
     *
     * @param probe test to probe
     */
    public void addProbe(FaultingWaitForTask probe) {
        if (this.probe != null) {
            log("Overriding previous definition of <probe>");
        }
        this.probe = probe;
    }

    /**
     * Declare a list of junit tasks
     *
     * @param sequence a test sequence
     */
    public void addTest(Sequential sequence) {
        if (test != null) {
            log("Overriding previous definition of <test>");
        }
        test = sequence;
    }

    /**
     * Called by the project to let the task do its work. This method may be
     * called more than once, if the task is invoked more than once. For
     * example, if target1 and target2 both depend on target3, then running "ant
     * target1 target2" will run all tasks in target3 twice.
     *
     * @throws org.apache.tools.ant.BuildException
     *          if something goes wrong with the build.
     */
    public void execute() throws BuildException {

        //set up the test and application in parallel; if
        //the application is null, then only the tests run
        int totalTimeout = timeout;
        if (probe != null) {
            totalTimeout += probe.getMaxWait();
        }
        parallel = new Parallel();
        helper.bindTask(parallel);
        parallel.setFailOnAny(true);

        Parallel applicationParallel = new Parallel();
        helper.bindTask(applicationParallel);
        BackgroundTask backgroundApp = null;
        backgroundApp = new BackgroundTask(applicationParallel);

        //add the application and test task in parallel
        if (application != null) {
            applicationParallel.addTask(application);
        }
        int timeoutMillis = totalTimeout * 1000;
        if (totalTimeout > 0) {
            parallel.setTimeout(timeoutMillis);
        }

        //but the test task runs after the probe
        Sequential testRun = new Sequential();
        helper.bindTask(testRun);
        if (probe != null) {
            testRun.addTask(probe);
        }
        if (test != null) {
            testRun.addTask(test);
        } else {
            log(MESSAGE_NO_JUNIT);
        }
        parallel.addTask(testRun);

        //this is where we encode all the workflow sequence described earlier.
        if (setup != null) {
            setup.execute();
        }
        BuildException testFault = null;
        BuildException teardownFault = null;
        //here we have started, so from hereon teardown is required to be called
        try {
            //start the app
            backgroundApp.start();
            //start the probe+test sequence
            parallel.execute();
        } catch (BuildException e) {
            //something went wrong, probably in execution
            testFault = e;
        }
        finally {
            //teardown
            if (teardown != null) {
                try {
                    teardown.execute();
                } catch (BuildException e) {
                    teardownFault=e;
                }
            }
        }
        //now, teardown has executed, so we wait for the application to finish
        try {
            //first a moment of time for a clean shutdown, equal to the total time
            if (backgroundApp.isRunning()) {
                backgroundApp.waitForTermination(shutdownTime);
            }
            if (backgroundApp.isRunning()) {
                //then, if its still running, interrupt it.
                log(MESSAGE_FORCED_SHUTDOWN_OF_APPLICATION);
                backgroundApp.interrupt();
                backgroundApp.waitForTermination(shutdownTime);
            }
        } catch (InterruptedException e) {
            //this is good.
        }

        //now some fault cleanup.
        //test faults get priority
        //then application faults
        //and finally teardown ones


        //look for an application fault
        BuildException applicationFault = backgroundApp.getException();
        if (applicationFault != null) {
            if (testFault == null ) {
                //copy any
                testFault = applicationFault;
            } else {
                log(EXCEPTION_CAUGHT_ON_CLEANUP + applicationFault.toString(),
                        Project.MSG_ERR);
            }
        }

        //now look for teardown faults
        if(teardownFault!=null) {
            if (testFault == null) {
                testFault = teardownFault;
            } else {
                //dont let the cleanup exception get in the way of any other failure
                log(EXCEPTION_CAUGHT_ON_CLEANUP + teardownFault.toString(),
                        Project.MSG_ERR);
            }
        }

        //rethrow anything we caught during execution or cleanup
        if (testFault != null) {
            throw testFault;
        }

    }

    /**
     * Run a task in the background. when finished, it will signal.
     */
    private static class BackgroundTask implements Runnable {
        private Task task;

        private BuildException exception;
        private Thread thread;

        BackgroundTask(Task task) {
            thread = new Thread(this);
            this.task = task;
        }

        public BuildException getException() {
            return exception;
        }

        public synchronized void start() {
            thread.start();
        }

        public synchronized void waitForTermination(int seconds)
                throws InterruptedException {
            wait(seconds*1000);
        }

        /**
         * Interrupt execution of the system.
         */
        public synchronized void interrupt() {
            if (isRunning()) {
                thread.interrupt();
            }
        }

        /**
         * Test for a thread running
         *
         * @return true if the thread is alove
         */
        private boolean isRunning() {
            return thread.isAlive();
        }

        /**
         * @see Thread#run()
         */
        public void run() {
            try {
                task.execute();
            } catch (BuildException e) {
                exception = e;
            } finally {
                //wake up our owner, if it is waiting
                synchronized(this) {
                    notifyAll();
                }
            }

        }
    }
}
