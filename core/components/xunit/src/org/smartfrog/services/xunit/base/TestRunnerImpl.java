/** (C) Copyright 1998-2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xunit.base;

import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.TestCompoundImpl;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.assertions.events.TestStartedEvent;
import org.smartfrog.services.xunit.log.TestListenerLog;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.Executable;
import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.utils.WorkflowThread;
import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.workflow.events.DeployedEvent;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;
import org.smartfrog.sfcore.workflow.events.StartedEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;

import java.rmi.RemoteException;

/**
 * This is the test runner. It runs multiple test suites; It keeps all its public state in a configuration object that
 * can be got/cloned and serialized to suites created.
 *
 * This class implements (incompletely) the {@link TestBlock} interface This lets the class be hosted inside junit test
 * running code that has been written for TestBlock instances.
 */
@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
public class TestRunnerImpl extends ConditionCompound implements TestRunner, Executable {

    private Log log;
    private ComponentHelper helper;
    private Reference self;

    /**
     * a cached exception that is thrown on a liveness failure
     */
    private volatile Throwable cachedException = null;
    /**
     * flag set when the tests are finished
     */
    private volatile boolean finished = false;

    /**
     * should we fail messily if a test failed
     */
    private boolean failOnError = true;

    /**
     * thread priority
     */
    private int threadPriority = Thread.NORM_PRIORITY;

    /**
     * Should we terminate after running our tests? {@link ShouldDetachOrTerminate#ATTR_SHOULD_TERMINATE}
     */
    private boolean shouldTerminate = true;

    /**
     * if terminating, should we detach? Should we terminate after running our tests? {@link
     * ShouldDetachOrTerminate#ATTR_SHOULD_DETACH}
     */
    private boolean shouldDetach;

    /**
     *  Description attitude
     */
    private String description;

    /**
     * String to set to the name of a single test component to run
     */
    private String singleTest;

    /**
     * thread to run the tests
     */

    private TestRunnerThread worker;

    /**
     * keeper of statistics
     */
    private Statistics statistics = new Statistics(sfLog());

    /**
     * who listens to the tests? This is potentially remote
     */
    private RunnerConfiguration configuration = new RunnerConfiguration();

    /**
     * Error text {@value}
     */
    public static final String ERROR_TESTS_IN_PROGRESS = "Component is already running tests";
    private static final String TEST_FAILURE_IN = "Test failure reported in ";
    /**
     * Error text {@value}
     */
    public static final String TESTS_FAILED = TEST_FAILURE_IN;
    /**
     * Error text {@value}
     */
    public static final String TEST_WAS_INTERRUPTED = "Test was interrupted";

    private Prim listenerPrim;
    private boolean skipped = false;
    private boolean failed;
    private boolean succeeded;
    protected TestListenerFactory listenerFactory;

    /**
     * constructor
     *
     * @throws RemoteException for network problems
     */
    public TestRunnerImpl() throws RemoteException {
        helper = new ComponentHelper(this);
    }


    /**
     * {@inheritDoc}
     *
     * @return false, always
     */
    @SuppressWarnings({"RefusedBequest"})
    @Override
    protected boolean isOldNotationSupported() {
        return false;
    }


    /**
     * Override point: where the condition is deployed at startup. The default action is to call {@link
     * #deployCondition()}
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    protected void deployConditionAtStartup() throws SmartFrogException, RemoteException {
        //do nothing
    }

    /**
     * Override point: is the condition required. IF not, there is no attempt to deploy it at startup
     *
     * @return false, always
     */
    @Override
    protected boolean isConditionRequired() {
        return false;
    }

    private synchronized TestRunnerThread getWorker() {
        return worker;
    }

    private synchronized void setWorker(TestRunnerThread worker) {
        this.worker = worker;
    }

    /**
     * validate our settings, bail out if they are invalid
     *
     * @throws SmartFrogInitException if the configuration is invalid
     */
    private void validate() throws SmartFrogInitException {
        if (threadPriority < Thread.MIN_PRIORITY ||
                threadPriority > Thread.MAX_PRIORITY) {
            throw new SmartFrogInitException(ATTR_THREAD_PRIORITY +
                    " is out of range -must be within "
                    + Thread.MIN_PRIORITY + " and " + Thread.MAX_PRIORITY);
        }
    }

    /**
     * Deploy the compound. Deployment is defined as iterating over the context and deploying any parsed eager
     * components.
     *
     * @throws SmartFrogException failure deploying compound or sub-component
     * @throws RemoteException    In case of Remote/nework error
     */
    @Override
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
        sendEvent(new DeployedEvent(this));
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed components in the compound context. Any
     * failure will cause the compound to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException    In case of Remote/nework error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        //
        super.sfStart();
        sendEvent(new StartedEvent(this));
        //create and deploy all the children
        synchCreateChildren();
        //now start working with them
        self = sfCompleteName();
        description = sfResolve(ATTR_DESCRIPTION, "", true);
        failOnError = sfResolve(ATTR_FAILONERROR, failOnError, false);
        threadPriority = sfResolve(ATTR_THREAD_PRIORITY,
                threadPriority,
                false);
        shouldTerminate = sfResolve(ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE,
                shouldTerminate,
                false);
        listenerPrim = sfResolve(ATTR_LISTENER,
                (Prim) null,
                true);
        if (listenerPrim == null || !(listenerPrim instanceof TestListenerFactory)) {
            throw new SmartFrogException("The attribute " +
                    ATTR_LISTENER
                    + " must refer to an implementation of TestListenerFactory");
        }

        listenerFactory = (TestListenerFactory) listenerPrim;
        String listenerName = ((Prim) listenerFactory).sfResolve(
                TestListenerFactory.ATTR_NAME,
                "",
                true);
        log.info("Test Listener is of type " + listenerName);
        configuration.setListenerFactory(listenerFactory);
        configuration.setKeepGoing(
                sfResolve(ATTR_KEEPGOING, configuration.getKeepGoing(), false));
        shouldDetach = sfResolve(
                ShouldDetachOrTerminate.ATTR_SHOULD_DETACH, shouldDetach, false);
        singleTest = sfResolve(ATTR_SINGLE_TEST, singleTest, false);
        TestListenerLog testLog = (TestListenerLog) sfResolve(ATTR_TESTLOG, (Prim) null, false);
        configuration.setTestLog(testLog);

        validate();
        //execute the tests in all the suites attached to this class
        boolean runTests = sfResolve(ATTR_RUN_TESTS_ON_STARTUP, true, true);
        //now deploy the condition. Failures are caught, noted and then passed up
        try {
            deployCondition();
        } catch (SmartFrogResolutionException e) {
            noteStartupFailure(TestCompoundImpl.FAILED_TO_START_CONDITION, e);
            throw e;
        } catch (RemoteException e) {
            noteStartupFailure(TestCompoundImpl.FAILED_TO_START_CONDITION, e);
            throw e;
        } catch (SmartFrogDeploymentException e) {
            noteStartupFailure(TestCompoundImpl.FAILED_TO_START_CONDITION, e);
            throw e;
        }
        //evaluate the condition.
        //then decide whether to run or not.

        if (getCondition() != null && !evaluate()) {

            //this means the test gets skipped
            sendEvent(new TestStartedEvent(this));
            skipped = true;
            updateFlags(false);
            String message = getName() + " skipping test run " + description;
            sfLog().info(message);
            //send a test started event
            //followed by a the closing results
            endTestRun(TerminationRecord.normal(message, getName()));
            //initiate cleanup
            sfLog().debug("maybe scheduling termination");
            finish();
            //end: do not deploy anything else
            return;
        } else {
            sendEvent(new TestStartedEvent(this));
            if (runTests) {
                log.info("TestRunner is set to run tests on startup");
                runTests();
            } else {
                log.info("TestRunner tests will only start when directly invoked");
            }
        }
    }

    /**
     * Liveness tests first delegates to the parent, then considers itself live unless all of the following conditions
     * are met <ol> <li>We are finished <li>There was an exception <li>failOnError is set </ol> In which case the cached
     * exception gets thrown.
     *
     * @param source source of ping
     * @throws SmartFrogLivenessException liveness failed
     */
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        //check the substuff
        super.sfPing(source);
        //then look to see if we had a failure with our tests
/*
        synchronized (this) {
            if (failOnError && isFinished() && getCachedException() != null) {
                SmartFrogLivenessException.forward(getCachedException());
            }
        }
*/
    }


    /**
     * send out a notification and kill the worker
     *
     * @param status termination status
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord status) {
        sendEvent(new TerminatedEvent(this, status));
        WorkflowThread.requestThreadTerminationWithInterrupt(getWorker());
        super.sfTerminateWith(status);
    }

    @Override
    protected boolean onChildTerminated(final TerminationRecord record, final Prim comp)
            throws SmartFrogRuntimeException, RemoteException {
        //a child has terminated.
        //Two actions: report it upstream or continue.
        //Plan: trigger a component helper to do this, and return false. Why this? so that the
        //various termination option flags can be used

        if (comp instanceof TestBlock) {
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("child TestBlock " + comp.sfCompleteName() + " terminated with TR = " + record);
            }
            //its a test block. so let the test block handling handle it
            //we just remove it from liveness
            removeChildQuietly(comp);
            //and notify the caller we want to keep going

            return false;
        } else {
            //something else terminated
            if (sfLog().isDebugEnabled()) {
                sfLog().debug("child component " + comp.sfCompleteName() + " terminated -ending test run; TR = " + record);
            }
            //whatever it was, it signals the end of this run
            sfRemoveChild(comp);
            //return false;
            return true;
        }

    }


    /**
     * Remove a child quietly; ignore problems
     * @param comp component to remove
     */
    private void removeChildQuietly(Prim comp) {
        try {
            sfRemoveChild(comp);
        } catch (SmartFrogRuntimeException e) {
            sfLog().error(e, e);
        } catch (RemoteException e) {
            sfLog().error(e, e);
        }
    }

    /**
     * run the test
     *
     * @throws RemoteException
     */
    @Override
    public synchronized boolean runTests() throws RemoteException,
            SmartFrogException {
        if (getWorker() != null) {
            throw new SmartFrogException(ERROR_TESTS_IN_PROGRESS);
        }
        TestRunnerThread thread = new TestRunnerThread();
        thread.setName("TestRunner");
        thread.setPriority(threadPriority);
        if (log.isDebugEnabled()) {
            log.debug("Starting "+ thread.getName() +" at priority " + threadPriority);
        }
        setWorker(thread);
        thread.start();
        return true;
    }

    /**
     * Run the tests in the test runner thread.
     * @throws Throwable if it doesn't work
     */
    @Override
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public void execute() throws Throwable {
        setFinished(false);
        log.debug("Beginning test run");
        try {
            if (!executeTestSuite()) {
                sfLog().info("Test execution failed");
                catchException(new TestsFailedException(TESTS_FAILED + description));
            }
        } catch (Throwable e) {
            catchException(e);
        } finally {
            //notify any listener of the event
            sendTestCompleteEvent();

            //declare ourselves finished
            setFinished(true);
            //unset the worker field
            setWorker(null);

        }

        boolean testFailed = getCachedException() != null;
        String outcome = testFailed ? "with errors " : "successfully";
        log.info("Completed tests " + outcome);


        if (shouldTerminate) {
            log.debug("Test runner will now terminate; outcome =" + outcome);
        } else {
            log.debug("Test runner is not terminating; outcome = " + outcome);
        }
        //at this point the thread is finished. It notifies its parent implicitly; the error is stroed in the caught field,
        //while the wait/join logic will catch termination signals.

    }


    /**
     * send out a completion event. This should be called by the child runners in their threads
     */
    protected void sendTestCompleteEvent() {
        TerminationRecord record = createTerminationRecord();
        TestCompletedEvent event = createTestCompletedEvent(record);
        send(event);
    }

    private void send(final LifecycleEvent event) {
        sendEvent(event);
    }

    /**
     * Create a termination record based on whether or not there is a cached exception.
     * If there is an exception: failure. otherwise, success. The record text also includes
     * the test run statistics
     * @return a termination record to use in events and termination
     */
    protected TerminationRecord createTerminationRecord() {
        TerminationRecord record;
        if (getCachedException() == null) {
            record = TerminationRecord.normal(description
                    + ": " + statistics,
                    self);
        } else {
            record = TerminationRecord.abnormal(TEST_FAILURE_IN + sfCompleteNameSafe()
                    + " -- " + description
                    + ": " + getCachedException()
                    + ": " + statistics,
                    self,
                    getCachedException());
        }
        return record;
    }

    /**
     * Create a test completion event from a term record
     * @param record the record; the tests are assumed to have succeeded if the record is normal
     * @return a new event
     */
    private TestCompletedEvent createTestCompletedEvent(TerminationRecord record) {
        return new TestCompletedEvent(this, record.isNormal(), false, false, record, description);
    }

    /**
     * Create an abnormal TerminationRecord from the exception and the text, record the end of the test run in status
     * attributes and workflow notifications
     *
     * @param text   message for the TR
     * @param thrown the exception tht was thrown
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    private void noteStartupFailure(String text, Throwable thrown) throws SmartFrogRuntimeException, RemoteException {
        TerminationRecord record;
        record = TerminationRecord.abnormal(text, getName(), thrown);
        noteEndOfTestRun(record, false, false);
    }

    /**
     * Record the end of the test run in status attributes and workflow notifications. If the test failed, the TR is
     * also logged at error level.
     *
     * @param record  the TR of this test
     * @param success flag set to true to indicate success
     * @param testSkipped skipped flag
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    private void noteEndOfTestRun(TerminationRecord record, boolean success, boolean testSkipped)
            throws SmartFrogRuntimeException, RemoteException {
        String message = "End of test run:" + record.toString();
        if (!success) {
            sfLog().warn(message);
        } else {
            sfLog().debug(message);
        }
/*        setStatus(record);
        actionTerminationRecord = record;
        updateFlags(success);*/
        send(new TestCompletedEvent(this, success, false, testSkipped, record, description));
    }

    /**
     * update finished and succeeded/failed flags when we finish
     *
     * @param success flag to indicated whether we considered the run a success
     */
    protected synchronized void updateFlags(boolean success) {
        succeeded = success;
        failed = !success;
        finished = true;
    }

    /**
     * Use results + internal state to decide if the test passed or not
     *
     * @param record termination record
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    protected void endTestRun(TerminationRecord record) throws SmartFrogRuntimeException, RemoteException {
        //work out the forced timeout info from the terminators
        //its only a forced timeout if the action was expected to terminate itself anyway
/*        boolean actionForcedTimeout = expectTerminate && actionTerminator != null && actionTerminator
                .isForcedShutdown();
        //any timeout of the tests is an error
        boolean testForcedTimeout = testsTerminator != null && testsTerminator.isForcedShutdown();
        //a forced timeout of action or tests => forced timeout of components
        boolean forcedTimeout = actionForcedTimeout || testForcedTimeout;
        */
        boolean forcedTimeout = false;
        //send out a completion event
        send(new TestCompletedEvent(this, isSucceeded(), forcedTimeout, isSkipped(), record, description));
        setTestBlockAttributes(record, forcedTimeout);
    }

    /**
     * Set the various attributes of the component based on whether the test record was success or not
     *
     * @param record  termination record
     * @param timeout did we time out?
     * @throws SmartFrogRuntimeException SmartFrog errors
     * @throws RemoteException           network errors
     */
    public void setTestBlockAttributes(
            TerminationRecord record,
            boolean timeout)
            throws SmartFrogRuntimeException, RemoteException {
        boolean success = record.isNormal();
        sfLog().debug("Finished Test with status " + record + " timeout=" + timeout);
        sfReplaceAttribute(ATTR_STATUS, record);
        sfReplaceAttribute(ATTR_SUCCEEDED, success);
        sfReplaceAttribute(ATTR_FAILED, !success);
        sfReplaceAttribute(ATTR_FORCEDTIMEOUT, timeout);
    }

    /**
     * run all the tests; this is the routine run in the worker thread. Break out (between suites) if we are
     * interrupted. Sets the {@link TestResultAttributes#ATTR_FINISHED} attribute to true on completion, then issue
     * the test completion event.
     *
     * @return true if the tests worked
     * @throws SmartFrogException   for problems
     * @throws RemoteException      for network problems
     * @throws InterruptedException if the tests get blocked
     */
    public boolean executeTestSuite() throws SmartFrogException, RemoteException, InterruptedException {
        boolean testsSucceeded = false;
        try {
            if (singleTest == null || singleTest.length() == 0) {
                testsSucceeded = executeBatchTests();
            } else {
                testsSucceeded = executeSingleTest();
            }
        } finally {
            //this is here as it can throw an exception
            sfReplaceAttribute(TestBlock.ATTR_FINISHED, Boolean.TRUE);
            noteEndOfTestRun(createTerminationRecord(), testsSucceeded, false);
        }
        return testsSucceeded;

    }


    private boolean executeBatchTests() throws RemoteException, SmartFrogException, InterruptedException {
        boolean successful = true;
        for (Prim child : sfChildList()) {
            String childName = child.sfCompleteName().toString();
            if (child instanceof TestSuite) {
                TestSuite suiteComponent = (TestSuite) child;
                successful &= executeTestSuite(suiteComponent, childName);
            }
            //break out if the thread is interrupted
            Thread thisThread = Thread.currentThread();
            synchronized (thisThread) {
                if (thisThread.isInterrupted()) {
                    thisThread.interrupt();
                    log.info(TEST_WAS_INTERRUPTED);
                    throw new InterruptedException(TEST_WAS_INTERRUPTED);
                }
            }
            if (!successful && !configuration.getKeepGoing()) {
                //we have failed and asked to stop in this situation
                log.info("Stopping tests after a failure");
                return false;
            }
        }
        return successful;
    }

    /**
     * if a single test was asked for, run it.
     *
     * @return true iff it worked
     * @throws RemoteException      network trouble
     * @throws TestsFailedException if the test failed to start up
     * @throws SmartFrogException   other problems
     * @throws InterruptedException if the test run is interrupted
     */
    private boolean executeSingleTest()
            throws SmartFrogException, RemoteException, InterruptedException {
        Prim child = null;
        child = sfResolve(singleTest, child, false);
        if (child == null) {
            String message = "No test suite called " + singleTest;
            log.info(message);
            throw new TestsFailedException(message);
        }
        TestSuite suiteComponent = (TestSuite) child;
        return executeTestSuite(suiteComponent, child.sfCompleteName().toString());
    }

    /**
     * Execute a single test
     *
     *
     * @param suiteComponent the suite to run
     * @param suiteName the name of the test suite
     * @return true if the tests were successful
     * @throws RemoteException      network trouble
     * @throws SmartFrogException   other problems
     * @throws InterruptedException if the test run is interrupted
     */
    private boolean executeTestSuite(TestSuite suiteComponent, final String suiteName)
            throws RemoteException, SmartFrogException, InterruptedException {
        //bind to the configuration. This will set the static properties.
        suiteComponent.bind(getConfiguration());
        boolean result;
        try {
            result = suiteComponent.runTestSuite();
            if (!result && log.isDebugEnabled()) {
                log.debug("runTestSuite() of "+ suiteName
                        + " (" + suiteComponent.sfRemoteToString() + ") returned false");
            }
            return result;
        } catch (SmartFrogException e) {
            logExceptionEvent("running the test suite "+ name, e);
            throw e;
        } catch (RemoteException e) {
            logExceptionEvent("running a test suite " + name, e);
            throw e;
        } catch (InterruptedException e) {
            logExceptionEvent("running a test suite " + name, e);
            throw e;
        } finally {
            //unbind from this test
            suiteComponent.bind(null);
            updateResultAttributes((Prim) suiteComponent);
        }
    }

    private void logExceptionEvent(final String activity, final Throwable t) {
        sfLog().info("During " + activity + ": " + t, t);
    }

    /**
     * Log at whatever level is chosen for the default logging
     * @param message message to log
     */
    protected void log(String message) {
        sfLog().info(message);
    }


    /**
     * fetch the test results from the Test suite, then update our own values
     *
     * @param testSuite test suite to patch
     * @throws RemoteException           network trouble
     * @throws SmartFrogRuntimeException other problems
     */
    private synchronized void updateResultAttributes(Prim testSuite)
            throws SmartFrogRuntimeException, RemoteException {
        statistics.retrieveAndAdd(testSuite);
        statistics.updateResultAttributes(this, false);
    }


    @Override
    public TestListenerFactory getListenerFactory() {
        return configuration.getListenerFactory();
    }

    @Override
    public void setListenerFactory(TestListenerFactory listener) {
        configuration.setListenerFactory(listener);
    }


    public boolean getKeepGoing() {
        return configuration.getKeepGoing();
    }

    public void setKeepGoing(boolean keepGoing) {
        configuration.setKeepGoing(keepGoing);
    }

    public TestListenerLog getTestLog() {
        return configuration.getTestLog();
    }

    public void setTestLog(TestListenerLog testLog) {
        configuration.setTestLog(testLog);
    }

    public RunnerConfiguration getConfiguration() {
        return configuration;
    }


    public synchronized Throwable getCachedException() {
        return cachedException;
    }

    public synchronized void catchException(Throwable caught) {
        if (caught != null) {
            ThrowableTraceInfo tti = new ThrowableTraceInfo(caught);
            log.info("Caught exception in tests " + tti, caught);
        }
        cachedException = caught;
    }

    @Override
    public synchronized boolean isFinished()  {
        return finished;
    }

    public synchronized void setFinished(boolean finished) {
        if(sfLog().isDebugEnabled()) sfLog().debug("Setting finished flag to " + finished);
        this.finished = finished;
    }

    /**
     * Get test execution statistics
     *
     * @return statistics the statistics
     */
    @Override
    public Statistics getStatistics() {
        return statistics;
    }


    /**
     * @return true only if the test has finished and failed
     */
    @Override
    public boolean isFailed() {
        return !isSucceeded();
    }

    /**
     * @return true iff the test succeeded
     */

    @Override
    public boolean isSucceeded() {
        return statistics.isSuccessful();
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     */
    @Override
    public TerminationRecord getStatus() {
        return null;
    }

    /**
     * return the current action
     *
     * @return the child component. this will be null after termination.
     */
    @Override
    public Prim getAction() {
        return null;
    }

    /**
     * turn true if a test is skipped; if some condition caused it not to run
     *
     * @return whether or not the test block skipped deployment of children.
     */
    @Override
    public boolean isSkipped() {
        return false;
    }


    /**
     * Thread that runs the tests and whose notify object can be waited for
     */
    private class TestRunnerThread extends SmartFrogThread {

        private TestRunnerThread() {
            super(new Object());
/*
            super(TestRunnerImpl.this, false, new Object());
*/

        }

        /**
         * The termination record is created slightly differently
         * @return
         */
/*
        @Override
        protected TerminationRecord createTerminationRecord() {
            return TestRunnerImpl.this.createTerminationRecord();
        }
*/

        @SuppressWarnings({"ProhibitedExceptionDeclared"})
        @Override
        public void execute() throws Throwable {
            TestRunnerImpl.this.execute();
        }
    }

}
