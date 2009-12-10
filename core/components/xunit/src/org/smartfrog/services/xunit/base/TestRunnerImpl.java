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
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.*;
import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.workflow.events.DeployedEvent;
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
public class TestRunnerImpl extends ConditionCompound implements TestRunner,
        TestBlock, Executable {

    private Log log;
    private ComponentHelper helper;
    private Reference self;

    /**
     * a cached exception that is thrown on a liveness failure
     */
    private Throwable cachedException = null;
    /**
     * flag set when the tests are finished
     */
    private boolean finished = false;

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
    private boolean shouldDetach = false;

    /**
     *  Description attitude
     */
    private String description;

    /**
     * String to set to the name of a single test component to run
     */
    private String singleTest = null;

    /**
     * thread to run the tests
     */

    private TestRunnerThread worker = null;

    /**
     * keeper of statistics
     */
    private Statistics statistics = new Statistics();

    /**
     * who listens to the tests? This is potentially remote
     */
    private RunnerConfiguration configuration = new RunnerConfiguration();

    /**
     * Error text {@value}
     */
    public static final String ERROR_TESTS_IN_PROGRESS = "Component is already running tests";
    private static final String TEST_FAILURE_IN = "Test failure in ";
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
        try {
            super.sfStart();
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
                    (Prim) configuration.getListenerFactory(),
                    true);
            if (!(listenerPrim instanceof TestListenerFactory)) {
                throw new SmartFrogException("The attribute " +
                        ATTR_LISTENER
                        + " must refer to an implementation of TestListenerFactory");
            }
            TestListenerFactory listenerFactory = (TestListenerFactory) listenerPrim;
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
                sendEvent(new TestStartedEvent(this));
                skipped = true;
                updateFlags(false);
                String message = "Skipping test run " + getName();
                sfLog().info(message);
                //send a test started event
                //followed by a the closing results
                endTestRun(TerminationRecord.normal(message, getName()));
                //initiate cleanup
                finish();
                //end: do not deploy anything else
                return;
            }

            if (runTests) {
                startTests();
            } else {
                log.info("Tests will only start when directly invoked");
            }
        } finally {
            sendEvent(new TestStartedEvent(this));
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
        synchronized (this) {
            if (failOnError && isFinished() && getCachedException() != null) {
                SmartFrogLivenessException.forward(getCachedException());
            }
        }
    }


    /**
     * send out a notification and kill the worker
     *
     * @param status termination status
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord status) {
        sendEvent(new TerminatedEvent(this, status));
        super.sfTerminateWith(status);
        WorkflowThread thread = getWorker();
        thread.requestTerminationWithInterrupt();
        WorkflowThread.requestThreadTerminationWithInterrupt(thread);
    }

    /**
     * run the test
     *
     * @throws RemoteException
     */
    @Override
    public synchronized boolean startTests() throws RemoteException,
            SmartFrogException {
        if (getWorker() != null) {
            throw new SmartFrogException(ERROR_TESTS_IN_PROGRESS);
        }
        TestRunnerThread thread = new TestRunnerThread();
        thread.setName("tester");
        thread.setPriority(threadPriority);
        if(log.isDebugEnabled()) {
            log.info("Starting new tester at priority " + threadPriority);
        }
        setWorker(thread);
        thread.start();
        return true;
    }

    /**
     * Run the tests in a new thread
     * @throws Throwable
     */
    @Override
    @SuppressWarnings({"ProhibitedExceptionDeclared"})
    public void execute() throws Throwable {
        setFinished(false);
        log.info("Beginning tests");
        try {
            if (!executeTests()) {
                throw new TestsFailedException(TESTS_FAILED + description);
            }
        } catch (Throwable e) {
            catchException(e);
        } finally {
            boolean testFailed = getCachedException() != null;
            log.info("Completed tests "
                    + (testFailed ? "with errors " : "successfully"));
            //declare ourselves finished
            setFinished(true);
            //unset the worker field
            setWorker(null);
            TerminationRecord record = createTerminationRecord();
            sendEvent(createTestCompletedEvent(record));


            //now look at our termination actions
/*            if (shouldTerminate) {
                if (!testFailed || !failOnError) {
                    record = TerminationRecord.normal(self);
                } else {
                    record = TerminationRecord.abnormal("Test failure", self, getCachedException());
                }
                log.info("terminating test component" + record);
                helper.targetForTermination(record, shouldDetach, false);
            }*/
        }
    }

  
    /**
     * Thread that runs the tests
     */
    private class TestRunnerThread extends WorkflowThread {
        private TestRunnerThread() {
            super(TestRunnerImpl.this, TestRunnerImpl.this, true);
        }


        /**
         * The termination record is created slighlty differently
         * @return
         */
        @Override
        protected TerminationRecord createTerminationRecord() {
            return TestRunnerImpl.this.createTerminationRecord();
        }
    }

    /**
     * send out a completion event
     */
    private void sendTestCompleteEvent() {
        TerminationRecord record = createTerminationRecord();
        sendEvent(createTestCompletedEvent(record));
    }

    protected TerminationRecord createTerminationRecord() {
        TerminationRecord record;
        if (getCachedException() == null) {
            record = TerminationRecord.normal(description, self);
        } else {
            record = TerminationRecord.abnormal(TEST_FAILURE_IN + description,
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
     * send out a skipping event
     */
    private void sendTestSkippedEvent() {
        TerminationRecord record;
        record = TerminationRecord.normal(description, self);
        sendEvent(new TestCompletedEvent(this, false, false, true, record, description));
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
        if (!success) {
            sfLog().warn(record.toString());
            if (record.getCause() != null) {
                sfLog().warn(record.getCause());
            }
        }
/*        setStatus(record);
        actionTerminationRecord = record;
        updateFlags(success);*/
        sendEvent(new TestCompletedEvent(this, success, false , testSkipped, record, description));
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
        sendEvent(new TestCompletedEvent(this, isSucceeded(), forcedTimeout, isSkipped(), record, description));
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
        sfLog().debug("Terminated Test with status " + record + " timeout=" + timeout);
        sfReplaceAttribute(ATTR_STATUS, record);
        sfReplaceAttribute(ATTR_SUCCEEDED, Boolean.valueOf(success));
        sfReplaceAttribute(ATTR_FAILED, Boolean.valueOf(!success));
        sfReplaceAttribute(ATTR_FORCEDTIMEOUT, Boolean.valueOf(timeout));
    }
    
    /**
     * run all the tests; this is the routine run in the worker thread. Break out (between suites) if we are
     * interrupted. Sets the {@link TestResultAttributes#ATTR_FINISHED} attribute to true on completion.
     *
     * @return true if the tests worked
     * @throws SmartFrogException   for problems
     * @throws RemoteException      for network problems
     * @throws InterruptedException if the tests get blocked
     */
    public boolean executeTests() throws SmartFrogException, RemoteException, InterruptedException {

        boolean succeeded;
        try {
            if (singleTest == null || singleTest.length() == 0) {
                succeeded = executeBatchTests();
            } else {
                succeeded = executeSingleTest();
            }
            return succeeded;
        } finally {
            //this is here as it can throw an exception
            sfReplaceAttribute(TestRunner.ATTR_FINISHED, Boolean.TRUE);
        }
    }


    private boolean executeBatchTests() throws RemoteException, SmartFrogException, InterruptedException {
        boolean successful = true;
        for (Prim child : sfChildList()) {
            if (child instanceof TestSuite) {
                TestSuite suiteComponent = (TestSuite) child;
                successful &= executeTestSuite(suiteComponent);
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
     * @throws SmartFrogException   other problems
     * @throws InterruptedException if the test run is interrupted
     */
    private boolean executeSingleTest()
            throws SmartFrogException, RemoteException, InterruptedException {
        Prim child = null;
        child = sfResolve(singleTest, child, false);
        if (child == null) {
            log.info("No test suite called " + singleTest);
            return false;
        }
        TestSuite suiteComponent = (TestSuite) child;
        return executeTestSuite(suiteComponent);
    }

    /**
     * Execute a single test
     *
     * @param suiteComponent the suite to run
     * @return true if the tests were successful
     * @throws RemoteException      network trouble
     * @throws SmartFrogException   other problems
     * @throws InterruptedException if the test run is interrupted
     */
    private boolean executeTestSuite(TestSuite suiteComponent)
            throws RemoteException, SmartFrogException, InterruptedException {
        //bind to the configuration. This will set the static properties.
        suiteComponent.bind(getConfiguration());
        boolean result;
        try {
            result = suiteComponent.runTests();
        } finally {
            //unbind from this test
            suiteComponent.bind(null);
            updateResultAttributes((Prim) suiteComponent);
        }
        return result;
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


    public TestListenerFactory getListenerFactory() throws RemoteException {
        return configuration.getListenerFactory();
    }

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

    public synchronized boolean isFinished() throws RemoteException {
        return finished;
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Get test execution statistics
     *
     * @return statistics
     * @throws RemoteException
     */
    public Statistics getStatistics() throws RemoteException {
        return statistics;
    }


    /**
     * @return true only if the test has finished and failed
     */
    public boolean isFailed() {
        return !isSucceeded();
    }

    /**
     * @return true iff the test succeeded
     */

    public boolean isSucceeded() {
        return statistics.isSuccessful();
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     */
    public TerminationRecord getStatus() {
        return null;
    }

    /**
     * return the current action
     *
     * @return the child component. this will be null after termination.
     */
    public Prim getAction() {
        return null;
    }

    /**
     * turn true if a test is skipped; if some condition caused it not to run
     *
     * @return whether or not the test block skipped deployment of children.
     */
    public boolean isSkipped()  {
        return false;
    }
}
