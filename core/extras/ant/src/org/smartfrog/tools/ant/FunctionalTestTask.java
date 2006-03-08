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

import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.Parallel;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

/**
 * This is an extension of junit that lets us integrate startup,
 * a waitfor condition and a shutdown sequence into the test case.
 * It's based upon Ant's own JUnit task, and credits Cactus as
 * showing what could be done. Apache Cactus is coded around deploying
 * to an application server; this task can deploy to anything for
 * which we have setup and teardown sequences.
 *
 *
 * This is the workflow for testing
 * <ol>
 * <li>The startup sequence is run to completion</li>
 * <li>In parallel, ( a) the application is started</li>
 * <li>and (b) the sequence of waitfor+tests is run.</li>
 * <li>After the tests run, the teardown operation is executed</li>
 * <li>Any build exception thrown by testing is thrown</li>
 * </ol>
 * <p>No matter what goes wrong, once startup has succeeded, teardown
 * gets invoked.
 * </p>
 */
public class FunctionalTestTask extends Task {

    //delegate to hold our settings
    private JUnitTask junitTask;
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
    private TaskHelper helper = new TaskHelper(this);
    public static final String MESSAGE_NO_JUNIT = "No junit element has been defined -no tests will run";
    public static final String EXCEPTION_CAUGHT_ON_CLEANUP = "Exception caught on cleanup:";


    public FunctionalTestTask() throws Exception {
    }


    public Sequential getSetup() {
        return setup;
    }

    /**
     * Define a sequence of operations to run at startup.
     * After running these, the teardown sequence will be called to tear down
     * the system.
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
     * @param teardown
     */
    public void addTeardown(Sequential teardown) {
        this.teardown = teardown;
    }


    public Sequential getApplication() {
        return application;
    }

    /**
     * The sequence of tasks used to define the application. This
     * is for hosting the server in a parallel thread to the test run.
     * @param application
     */
    public void addApplication(Sequential application) {
        this.application = application;
    }

    /**
     * This is the timeout for testing; any timeouts in the probe are not
     * covered under this.
     * @param timeout in seconds.
     */
    public void setTestTimeout(int timeout) {
        this.timeout = timeout;
    }


    /**
     * Add a probe, conditions that have to be met before testing begins.
     * @param probe
     */
    public void addProbe(FaultingWaitForTask probe) {
        if(this.probe !=null) {
            throw new BuildException("Only one probe is supported");
        }
        this.probe =probe;
    }

    /**
     * Declare a list of junit tasks
     * @param junit
     */
    public void addJunit(JUnitTask junit) {
        if (this.junitTask!= null) {
            throw new BuildException("Only one junit is supported");
        }
        this.junitTask = junit;
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
        parallel = new Parallel();
        helper.bindTask(parallel);
        int totalTimeout=timeout;
        if(probe!=null) {
            totalTimeout+=probe.getMaxWait();
        }
        if(totalTimeout>0) {
            parallel.setTimeout(totalTimeout*1000);
        }
        parallel.setFailOnAny(true);

        //add the application and test task in parallel
        if(application!=null) {
            parallel.addTask(application);
        }

        //but the test task runs after the probe
        Sequential testRun=new Sequential();
        helper.bindTask(testRun);
        if(probe !=null) {
            testRun.addTask(probe);
        }
        if(junitTask!=null) {
            junitTask.setTimeout(new Integer(timeout));
            testRun.addTask(junitTask);
        } else {
            log(MESSAGE_NO_JUNIT);
        }
        parallel.addTask(testRun);

        //this is where we encode all the workflow sequence described earlier.
        if(setup !=null) {
            setup.execute();
        }
        BuildException caught=null;
        //here we have started, so from hereon teardown is required to be called
        try {
            parallel.execute();
        } catch (BuildException e) {
            caught=e;
        }
        finally {
            if(teardown!=null) {
                try {
                    teardown.execute();
                } catch (BuildException e) {
                    if(caught!=null) {
                        //dont let the cleanup exception get in the way of any other failure
                        log(EXCEPTION_CAUGHT_ON_CLEANUP +e.toString(), Project.MSG_ERR);
                    } else {
                        caught = e;
                    }
                }
            }
        }
        //rethrow anything we caught during execution or cleanup
        if(caught!=null) {
            throw caught;
        }

    }
}
