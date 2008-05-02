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
package org.smartfrog.services.sfunit;

import org.smartfrog.services.xunit.base.RunnerConfiguration;
import org.smartfrog.services.xunit.base.AbstractTestSuite;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.TestTimeoutException;
import org.smartfrog.services.assertions.events.TestEventSink;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

/** created 08-Jan-2007 14:57:40 */

public class SFUnitTestSuiteImpl extends AbstractTestSuite
        implements SFUnitTestSuite {
    public static final String ERROR_NOT_CONFIGURED = "TestSuite has not been configured yet";
    private volatile boolean finished = false;
    private volatile boolean failed = false;
    private volatile boolean succeeded = false;
    private volatile boolean forcedTimeout = false;
    private volatile boolean skipped = false;
    private volatile TerminationRecord status;
    private RunnerConfiguration configuration;
    private ComponentHelper helper;
    SFUnitChildTester testThread;
    private long startupTimeout;
    private long testTimeout;
    private ArrayList<TestSuiteRun> testSuites;

    public SFUnitTestSuiteImpl() throws RemoteException {
    }

    /**
     * Return true iff the component is finished. Spin on this, with a (delay) between calls
     *
     * @return true if we have finished
     */
    public boolean isFinished() {
        return finished;
    }

    /** @return true only if the test has finished and failed */
    public boolean isFailed() {
        return failed;
    }

    /** @return true iff the test succeeded */

    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * {@inheritDoc}
     *
     * @return the skipped state
     */
    public boolean isSkipped() {
        return skipped;
    }

    /**
     * Get the exit record
     *
     * @return the exit record, will be null for an unfinished child
     */


    public TerminationRecord getStatus() {
        return status;
    }

    /**
     * return the tests prim
     *
     * @return null, always
     */
    public Prim getAction() {
        return null;
    }

    /**
     * Stub implementation
     *
     * @return null always
     */
    public TerminationRecord getActionTerminationRecord()  {
        return null;
    }

    /**
     * Stub implementation
     *
     * @return null always
     */
    public TerminationRecord getTestsTerminationRecord() throws RemoteException {
        return null;
    }

    /**
     * Starts the compound.
     * This sends a synchronous sfStart to all managed components in the compound context. 
     * Any failure will cause the compound to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException    In case of Remote/network error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        helper = new ComponentHelper(this);

        //deploy all children. but do not (yet) start them
        Context children = getActions();
        Iterator<Object> iterator = children.sfAttributes();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            ComponentDescription act = (ComponentDescription) children.get(key);
            sfDeployComponentDescription(key, this, act, null);
            if (sfLog().isDebugEnabled()) sfLog().debug("Creating " + key);
        }

        //now start anything that is not a test
        for (Prim elem:sfChildList()) {
            if (!(elem instanceof TestBlock)) {
                sfLog().info("Starting "+elem.sfCompleteName().toString());
                elem.sfStart();
            }
        }

        startupTimeout = sfResolve(ATTR_STARTUP_TIMEOUT,0L,true);
        testTimeout = sfResolve(ATTR_TEST_TIMEOUT, 0L, true);
        //so here everything is started; the tests are ready to run.
    }

    /**
     * Deregisters from all current registrations.
     *
     * @param status Termination  Record
     */
    @Override
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        SmartFrogThread.requestThreadTermination(testThread);
    }

    /**
     * Run the tests.
     *
     * This is done by running through every child test in turn, and deploying it.
     *
     * When it terminates, it is evaluated. No, that doesn't work. We need notification?
     *
     * @return true if they worked
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for other problems
     * @throws InterruptedException if the test run is interrupted
     */
    public boolean runTests() throws RemoteException, SmartFrogException, InterruptedException {
        InetAddress host = sfDeployedHost();
        String hostname = host.getHostName();
        //use our short name
        String suitename = helper.shortName();
        //then look for an override, which is mandatory if we do not know who
        //we are right now.
        suitename = sfResolve(ATTR_NAME, suitename, suitename == null);
        sfLog().info("Running SFUnit test suite " + suitename + " on host " + hostname);

        if (getConfiguration() == null) {
            throw new SmartFrogException(
                    ERROR_NOT_CONFIGURED);
        }
        if (maybeSkipTestSuite()) {
            skipped = true;
            return true;
        }

        boolean successful = true;

        List<Prim> children = sfChildList();
        testSuites = new ArrayList<TestSuiteRun>(children.size());
        for (Prim child : children) {
            if (child instanceof TestBlock) {
                TestBlock testBlock = (TestBlock) child;
                testSuites.add(new TestSuiteRun(testBlock));
            }
        }
        testThread=new SFUnitChildTester(testSuites);
        testThread.start();

        return successful;
    }



    /**
     * Handle child termination.
     * Sequence behaviour for a normal child termination is
     * <ol>
     *  <li> to start the next component.</li>
     *  <li> if it is the last - terminate normally. </li>
     *  <li> If starting the next component raised an
     * error, terminate abnormally</li>
     * </ol>
     *
     * Abnormal child terminations are relayed up.
     *
     * @param record exit record of the component
     * @param comp   child component that is terminating
     * @return true whenever a child component is not started
     */
    protected boolean onChildTerminated(TerminationRecord record, Prim comp) {
        if(comp instanceof TestBlock) {
            //its a test block. so let the test block handling handle it

            //we just remove it from liveness
            removeChildQuietly(comp);
            //and notify the caller we want to keep going
            return false;
        } else {
            //something else terminated
            //whatever it was, it signals the end of this run
            removeChildQuietly(comp);
            return false;
            //return true;
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
            sfLog().error(e);
        } catch (RemoteException e) {
            sfLog().error(e);
        }
    }

    /**
     * This is our class to store test run details
     */
    private static class TestSuiteRun {
        TestBlock target;
        TestEventSink events;
        Exception caught;
        TerminationRecord status;
        SFUnitChildTester testThread;

        private TestSuiteRun() {
        }

        public TestSuiteRun(TestBlock testBlock) {
            target=testBlock;
        }
    }

    /**
     * This is the worker thread that runs tests and waits for responses. It is initially single threaded.
     */
    private class SFUnitChildTester extends SmartFrogThread {

        private List<TestSuiteRun> testRuns;
        private boolean parallel = false;
        private TestSuiteRun currentTest;

        /**
         * Create a tester run
         * @param testRuns the list of tests to run
         * @see Thread#Thread(ThreadGroup,Runnable,String)
         */
        private SFUnitChildTester(List<TestSuiteRun> testRuns) {
            this.testRuns = testRuns;
        }

        /**
         * If this thread was constructed using a separate {@link Runnable} run object, then that <code>Runnable</code>
         * object's <code>run</code> method is called; otherwise, this method does nothing and returns. <p> Subclasses of
         * <code>Thread</code> should override this method.
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            for (TestSuiteRun testRun : testRuns) {
                if (isTerminationRequested()) {
                    break;
                }
                currentTest = testRun;
                testOneChild(currentTest);
            }
        }

        /**
         * Request termination by triggering the superclass termination logic, and forcing an
         * interruption into the test queue
         */
        public synchronized void requestTermination() {
            super.requestTermination();
            if (currentTest != null && currentTest.events != null) {
                try {
                    currentTest.events.interrupt();
                } catch (RemoteException e) {
                    sfLog().ignore("failed to interrupt", e);
                }
            }
        }

        /**
         * Test one testblock.
         *
         * TestC <ol> <li> Subscribing to lifecycle events</li> <li> Starting it.</li> <li> Waiting for it to finish
         * </li> <li> Logging whether it failed or not</li> </ol>
         *
         * @param testRun the test run to execute
         * @return true if the test worked
         * @throws SmartFrogException smartfrog problems
         * @throws RemoteException    network problems
         */
        private synchronized boolean testOneChild(TestSuiteRun testRun) throws SmartFrogException, RemoteException {
            Prim testPrim;
            synchronized (this) {
                TestBlock testBlock=testRun.target;
                testRun.events=new TestEventSink(testBlock);
                currentTest = testRun;
                testPrim = (Prim) testBlock;
            }
            boolean success = false;
            boolean isSkipped = false;
            boolean isFailed = false;
            ComponentHelper ch=new ComponentHelper(testPrim);
            String testName = ch.completeNameSafe().toString();
            sfLog().info("Starting " + testName);
            try {
                LifecycleEvent event = testRun.events.runTestsToCompletion(startupTimeout, testTimeout);
                if (event instanceof TestCompletedEvent) {
                    TestCompletedEvent test = (TestCompletedEvent) event;
                    success = test.isSucceeded();
                    isSkipped = test.isSkipped();
                    isFailed = test.isFailed();
                } else {
                    //got a termination before the tests completed.
                    sfLog().error("terminated "+ testName+" -assuming failure");
                    success = false;
                    isFailed = true;
                }
                //record the output status
                testRun.status=event.getStatus();
            } catch (TestTimeoutException e) {
                //test timed out
                success = false;
                isFailed = true;

            } catch (InterruptedException e) {
                //test was interrupted
                success = false;
                isFailed = true;
            } finally {
                ch.targetForTermination();
            }

            succeeded &= success;
            failed |= isFailed;
            //if we failed, we didn't succeed. Just to make sure :)
            succeeded &= !isFailed;
            skipped |= isSkipped;
            return succeeded;
        }
    }
}
