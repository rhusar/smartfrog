/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.ShouldDetachOrTerminate;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.services.xunit.serial.ThrowableTraceInfo;
import org.smartfrog.services.xunit.log.TestListenerLog;
import org.smartfrog.services.assertions.TestBlock;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * This is the test runner. It runs multiple test suites; It keeps all its
 * public state in a configuration object that can be got/cloned and serialized
 * to suites created.
 *
 * This class implements (incompletely) the {@link TestBlock} interface
 * This lets the class be hosted inside junit test running code that
 * has been written for TestBlock instances. 
 */

public class TestRunnerImpl extends CompoundImpl implements TestRunner,
        Runnable, TestBlock {

    private Log log;
    private ComponentHelper helper;
    private Reference name;

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
     * Should we terminate after running our tests?
     * {@link ShouldDetachOrTerminate#ATTR_SHOULD_TERMINATE}
     */
    private boolean shouldTerminate = true;

    /**
     * if terminating, should we detach?
     * Should we terminate after running our tests?
     * {@link ShouldDetachOrTerminate#ATTR_SHOULD_DETACH}
     */
    private boolean shouldDetach = false;

    /**
     * String to set to the name of a single test component to run
     */
    private String singleTest=null;

    /**
     * thread to run the tests
     */
    private Thread worker = null;

    /**
     * keeper of statistics
     */
    private Statistics statistics = new Statistics();

    /**
     * who listens to the tests? This is potentially remote
     */
    private RunnerConfiguration configuration = new RunnerConfiguration();

    public static final String ERROR_TESTS_IN_PROGRESS = "Component is already running tests";
    public static final String TESTS_FAILED = "Tests Failed";
    private static final String TEST_WAS_INTERRUPTED = "Test was interrupted";

    /**
     * constructor
     *
     * @throws RemoteException for network problems
     */
    public TestRunnerImpl() throws RemoteException {
        helper = new ComponentHelper(this);
    }

    private synchronized Thread getWorker() {
        return worker;
    }

    private synchronized void setWorker(Thread worker) {
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
     * Deploy the compound. Deployment is defined as iterating over the context
     * and deploying any parsed eager components.
     *
     * @throws SmartFrogException     failure deploying compound or
     *                                  sub-component
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        log = helper.getLogger();
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed
     * components in the compound context. Any failure will cause the compound
     * to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException In case of Remote/nework error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        //this will deploy all our children, including the test suites
        super.sfStart();
        name = sfCompleteName();
        Object o = sfResolve(ATTR_LISTENER,
                configuration.getListenerFactory(),
                true);
        if (!(o instanceof TestListenerFactory)) {
            throw new SmartFrogException("The attribute " +
                    ATTR_LISTENER
                    + "must refer to an implementation of TestListenerFactory");
        }
        TestListenerFactory listenerFactory = (TestListenerFactory) o;
        String listenerName = ((Prim) listenerFactory).sfResolve(
                TestListenerFactory.ATTR_NAME,
                "",
                true);
        log.info("Test Listener is of type " + listenerName);
        configuration.setListenerFactory(listenerFactory);
        configuration.setKeepGoing(
                sfResolve(ATTR_KEEPGOING, configuration.getKeepGoing(), false));
        failOnError = sfResolve(ATTR_FAILONERROR, failOnError, false);
        threadPriority = sfResolve(ATTR_THREAD_PRIORITY,
                threadPriority,
                false);
        shouldTerminate = sfResolve(
                ShouldDetachOrTerminate.ATTR_SHOULD_TERMINATE, shouldTerminate, false);
        shouldDetach = sfResolve(
                ShouldDetachOrTerminate.ATTR_SHOULD_DETACH, shouldDetach, false);
        singleTest = sfResolve(ATTR_SINGLE_TEST,singleTest,false);
        TestListenerLog testLog = (TestListenerLog) sfResolve(ATTR_TESTLOG, (Prim) null, false);
        configuration.setTestLog(testLog);

        validate();
        //execute the tests in all the suites attached to this class
        boolean runTests = sfResolve(ATTR_RUN_TESTS_ON_STARTUP, true, true);
        if (runTests) {
            startTests();
        } else {
            log.info("Tests will only start when directly invoked");
        }
    }

    /**
     * Liveness tests first delegates to the parent, then considers itself live
     * unless all of the following conditions are met <ol> <li>We are finished
     * <li>There was an exception <li>failOnError is set </ol> In which case the
     * cached exception gets thrown.
     *
     * @param source source of ping
     * @throws SmartFrogLivenessException liveness failed
     */
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        //check the substuff
        super.sfPing(source);
        //then look to see if we had a failure with our tests
        synchronized(this) {
            if (failOnError && isFinished() && getCachedException() != null) {
                SmartFrogLivenessException.forward(getCachedException());
            }
        }
    }


    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        Thread thread = getWorker();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * run the test
     *
     * @throws java.rmi.RemoteException
     */
    public synchronized boolean startTests() throws RemoteException,
            SmartFrogException {
        if (getWorker() != null) {
            throw new SmartFrogException(ERROR_TESTS_IN_PROGRESS);
        }
        Thread thread = new Thread(this);
        thread.setName("tester");
        thread.setPriority(threadPriority);
        log.info("Starting new tester at priority " + threadPriority);
        setWorker(thread);
        thread.start();
        return true;
    }

    /**
     * this is the thread entry point; runs the tests in a new thread.
     *
     * @see Thread#run()
     */
    public void run() {
        setFinished(false);
        log.info("Beginning tests");
        try {
            if (!executeTests()) {
                throw new SmartFrogException(TESTS_FAILED);
            }
        } catch (InterruptedException e) {
            catchException(e);
        } catch (RemoteException e) {
            catchException(e);
        } catch (SmartFrogException e) {
            catchException(e);
        } finally {
            boolean testFailed = getCachedException() != null;
            log.info("Completed tests "
                +(testFailed?"with errors ":"successfully"));
            //declare ourselves finished
            setFinished(true);
            //unset the worker field
            setWorker(null);

            //now look at our termination actions
            if (shouldTerminate) {
                TerminationRecord record;
                if (!testFailed || !failOnError) {
                    record = TerminationRecord.normal(name);
                } else {
                    record = TerminationRecord.abnormal("Test failure", name, getCachedException());
                }
                log.info("terminating test component"+record.toString());
                helper.targetForTermination(record, shouldDetach, false);
            }
        }
    }


    /**
     * run all the tests; this is the routine run in the worker thread. Break
     * out (between suites) if we are interrupted. Sets the {@link
     * TestResultAttributes#ATTR_FINISHED} attribute to true on completion.
     *
     * @return true if the tests worked
     * @throws SmartFrogException for problems
     * @throws RemoteException for network problems
     * @throws InterruptedException if the tests get blocked
     */
    public boolean executeTests() throws SmartFrogException, RemoteException, InterruptedException {

        try {
            if(singleTest==null || singleTest.length()==0) {
                return executeBatchTests();
            } else {
                return executeSingleTest();
            }
        } finally {
            //this is here as it can throw an exception
            sfReplaceAttribute(TestRunner.ATTR_FINISHED, Boolean.TRUE);
        }
    }


    private boolean executeBatchTests() throws RemoteException, SmartFrogException, InterruptedException {
        boolean successful = true;
        Enumeration e = sfChildren();
        while (e.hasMoreElements()) {
            Object child = e.nextElement();
            if (child instanceof TestSuite) {
                TestSuite suiteComponent = (TestSuite) child;
                successful &= executeTestSuite(suiteComponent);
            }
            //break out if the thread is interrupted
            Thread thisThread = Thread.currentThread();
            synchronized(thisThread) {
                if (thisThread.isInterrupted()) {
                    thisThread.interrupt();
                    log.info(TEST_WAS_INTERRUPTED);
                    throw new InterruptedException(TEST_WAS_INTERRUPTED);
                }
            }
            if(!successful && !configuration.getKeepGoing()) {
                //we have failed and asked to stop in this situation
                log.info("Stopping tests after a failure");
                return false;
            }
        }
        return successful;
    }

    /**
     * if a single test was asked for, run it.
     * @return true iff it worked
     * @throws RemoteException network trouble
     * @throws SmartFrogException other problems
     * @throws InterruptedException if the test run is interrupted
     */
    private boolean executeSingleTest()
            throws SmartFrogException, RemoteException, InterruptedException {
        Prim child=null;
        child=sfResolve(singleTest,child,false);
        if(child==null) {
            log.info("No test suite called "+singleTest);
            return false;
        }
        TestSuite suiteComponent = (TestSuite) child;
        return executeTestSuite(suiteComponent);
    }

    /**
     * Execute a single test
     * @param suiteComponent the suite to run
     * @return true if the tests were successful
     * @throws RemoteException network trouble
     * @throws SmartFrogException other problems
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
     * @throws RemoteException network trouble
     * @throws SmartFrogException other problems
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
     * @return stats
     * @throws java.rmi.RemoteException
     */
    public Statistics getStatistics() throws RemoteException {
        return statistics;
    }


    /**
     * @return true only if the test has finished and failed
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on other problems
     */
    public boolean isFailed() throws RemoteException, SmartFrogException {
        return !isSucceeded();
    }

    /**
     * @return true iff the test succeeded
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on other problems
     */

    public boolean isSucceeded() throws RemoteException, SmartFrogException {
        return statistics.isSuccessful();
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on other problems
     */
    public TerminationRecord getStatus() throws RemoteException, SmartFrogException {
        return null;
    }

    /**
     * return the current action
     *
     * @return the child component. this will be null after termination.
     * @throws RemoteException    on network trouble
     * @throws SmartFrogException on other problems
     */
    public Prim getAction() throws RemoteException, SmartFrogException {
        return null;
    }

    /**
     * turn true if a test is skipped; if some condition caused it not to run
     *
     * @return whether or not the test block skipped deployment of children.
     */
    public boolean isSkipped() throws RemoteException, SmartFrogException {
        return false;
    }
}
