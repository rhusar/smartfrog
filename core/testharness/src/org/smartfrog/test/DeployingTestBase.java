/* (C) Copyright 2006-2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test;

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.assertions.events.TestEventSink;
import org.smartfrog.services.assertions.events.TestInterruptedEvent;
import org.smartfrog.SFSystem;

import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;

import junit.framework.AssertionFailedError;


/**
 * An extension of SmartFrogTestBase with test awareness created 13-Oct-2006 16:28:33
 */

public abstract class DeployingTestBase extends SmartFrogTestBase implements TestProperties {
    private static final int SPIN_INTERVAL = 10;

    /**
     * The event sink for the deployed test application
     */
    private TestEventSink eventSink;

    protected DeployingTestBase(String name) {
        super(name);
    }


    protected void tearDown() throws Exception {
        if (!stopListening()) {
            getLog().warn("Failed to unsubscribe event sink");
        }
        super.tearDown();
    }

    /**
     * Stop listening to events this call is synchronous, and idempotent.
     *
     * @return true if we are no longer listening -that is we werent listening, or we unsubscribed quietly
     */
    private synchronized boolean stopListening() {
        if (eventSink != null) {
            Thread t = eventSink.asyncUnsubscribe();
            eventSink = null;
            return t != null;
        } else {
            //we are implicitly no longer listening
            return true;
        }
    }

    /**
     * Start listening; unsubscribe the event sink and bind to a new application
     *
     * @param prim the event source
     * @throws RemoteException           for network problems
     * @throws SmartFrogRuntimeException for subscription problems
     */
    private synchronized void startListening(Prim prim) throws RemoteException, SmartFrogRuntimeException {
        stopListening();

        eventSink = new TestEventSink(prim);
    }


    public TestEventSink getEventSink() {
        return eventSink;
    }

    protected TerminationRecord expectSuccessfulTermination(TestBlock testBlock) throws Throwable {
        return expectTermination(testBlock, true);
    }

    protected TerminationRecord expectAbnormalTermination(TestBlock testBlock) throws Throwable {
        return expectTermination(testBlock, false);
    }

    protected TerminationRecord expectTermination(TestBlock testBlock, boolean normal) throws Throwable {
        TerminationRecord status = spinUntilFinished(testBlock);
        assertNotNull("Null termination record", status);
        assertTrue("Expected " + (normal ? "normal" : "abnormal") + " termination, but got " + status,
                normal == status.isNormal());
        return status;
    }

    /**
     * Delay until a test has finished, sleeping (and yielding the CPU) until that point is reached. There is no
     * timeout.
     *
     * @param testBlock component to spin on
     * @param timeout   how long to wait (in millis)
     * @return the termination record of the component
     * @throws Throwable if something went wrong
     */
    protected TerminationRecord spinUntilFinished(TestBlock testBlock, long timeout) throws Throwable {
        try {
            long endtime = System.currentTimeMillis() + timeout;
            while (!testBlock.isFinished() && System.currentTimeMillis() < endtime) {
                Thread.sleep(SPIN_INTERVAL);
            }
            assertTrue("timeout (" + timeout + "ms) waiting for application to finish", testBlock.isFinished());
            return testBlock.getStatus();
        } catch (NoSuchObjectException e) {
            //some kind of remoting problem may happen during termination.
            logThrowable("Object has been deleted", e);
            throw e;
        } catch (RemoteException e) {
            //some kind of remoting problem may happen during termination.
            logThrowable("RMI exceptions during spin-waits may be network race conditions", e);
            throw e;
        }
    }

    /**
     * Delay until a test has finished, sleeping (and yielding the CPU) until that point is reached. There is no
     * timeout.
     *
     * @param testBlock component to spin on
     * @return the termination record of the component
     * @throws Throwable anything that got thrown
     */
    protected TerminationRecord spinUntilFinished(TestBlock testBlock) throws Throwable {
        return spinUntilFinished(testBlock, getTestTimeout());
    }

    /**
     * Load but do not start a component description. Any error raised during loading is passed on
     *
     * @param appName application name
     * @param testURL URL of the application
     * @param startupTimeout time in milliseconds before assuming a load timeout
     * @return the loaded CD, which is not yet deployed or started
     * @throws Throwable in the event of trouble.
     * @throws SmartFrogRuntimeException for a timeout in loading
     */
    private Prim loadApplication(String appName, String testURL, int startupTimeout) throws Throwable {
        ConfigurationDescriptor configurationDescriptor =
                new ConfigurationDescriptor(appName,
                        testURL,
                        ConfigurationDescriptor.Action.LOAD,
                        hostname,
                        null);
        ApplicationLoaderThread loader=new ApplicationLoaderThread(configurationDescriptor, true);
        loader.start();
        if(!loader.waitForNotification(startupTimeout)) {
            loader.interrupt();
            loader.stop();
            throw new SmartFrogRuntimeException("Time out loading the configuration descriptor after " + startupTimeout
                    + " mS: "+ testURL);
        }
        Object loaded = loader.getLoaded();

        //throw any deployment exception
        lookForThrowableInDeployment(loaded);
        //or any exception during startup
        loader.rethrow();
        return (Prim) loaded;
    }

    /**
     * Load a thread in the background
     */
    private static class ApplicationLoaderThread extends SmartFrogThread {

        private Object loaded;
        private ConfigurationDescriptor configuration;
        private boolean throwException;

        /**
         * Create a thread
         * @param configuration config to load
         * @param throwException should exceptions be thrown
         */
        ApplicationLoaderThread(ConfigurationDescriptor configuration, boolean throwException) {
            super(new Object());
            this.configuration = configuration;
            this.throwException = throwException;
            setDaemon(true);
        }

        /**
         * load the configuration
         *
         * @throws Throwable if anything went wrong
         */
        public void execute() throws Throwable {
            loaded=SFSystem.runConfigurationDescriptor(configuration, throwException);
        }

        public Object getLoaded() {
            return loaded;
        }
    }

    /**
     * combine the package and filename to URL. Appends .sf if needed
     * @param packageName package containing the file
     * @param filename the filename
     * @return a concatenated package with .sf at the end
     */
    public String createUrlString(String packageName, String filename) {
        StringBuilder buffer = new StringBuilder(packageName);
        if (!packageName.endsWith("/")) {
            buffer.append('/');
        }
        buffer.append(filename);
        if (!filename.endsWith(".sf")) {
            buffer.append(".sf");
        }
        return buffer.toString();
    }

    /**
     * This runs tests to completion, be it success or failure. The application is saved to the application field; the
     * event sink to eventSink. Both will be cleaned up during teardown.
     *
     * @param packageName    name of the package
     * @param filename       filename excluding .sf
     * @param startupTimeout limit in millis to start up
     * @param executeTimeout limit in millis to execute
     * @return the lifecycle at the end of the run. This is either a {@link org.smartfrog.services.assertions.events.TestCompletedEvent}
     *         or a {@link org.smartfrog.sfcore.workflow.events.TerminatedEvent}
     * @throws Throwable on failure. A {@link  org.smartfrog.services.assertions.TestTimeoutException} indicates
     *                   timeout.
     */
    protected LifecycleEvent runTestDeployment(String packageName, String filename, int startupTimeout,
                                               int executeTimeout) throws Throwable {
        String urlstring = createUrlString(packageName, filename);
        application = loadApplication(filename, urlstring, startupTimeout);
        startListening(application);
        LifecycleEvent event = getEventSink().runTestsToCompletion(startupTimeout, executeTimeout);
        return event;
    }

    /**
     * This runs tests to completion, be it success or failure. The application is saved to the application field; the
     * event sink to eventSink. Both will be cleaned up during teardown.
     *
     * @param packageName name of the package
     * @param filename    filename excluding .sf
     *
     *                    The default test startup and execution timeout will be used, unless overridden by system
     *                    properties.
     * @return the lifecycle at the end of the run. This s either a {@link TestCompletedEvent} or a {@link
     *         org.smartfrog.sfcore.workflow.events.TerminatedEvent}
     * @throws Throwable on failure. A {@link  org.smartfrog.services.assertions.TestTimeoutException} indicates
     *                   timeout.
     */
    protected LifecycleEvent runTestDeployment(String packageName, String filename) throws Throwable {
        return runTestDeployment(packageName, filename, getTestStartupTimeout(),
                getTestTimeout());
    }


    /**
     * Run tests until they are completed, then analyse the results. The application is saved to the application field;
     * the event sink to eventSink. Both will be cleaned up during teardown.
     *
     * The method will fail if the application terminated abnormally, or returned a test failure. Skipped and successful
     * tests are both viewed as successes.
     *
     * @param packageName    name of the package
     * @param filename       test file name, excluding .sf
     * @param startupTimeout limit in millis to start up
     * @param executeTimeout limit in millis to execute
     * @return the test results
     * @throws Throwable on any problem. A {@link  org.smartfrog.services.assertions.TestTimeoutException} indicates
     *                   timeout waiting for results A {@link junit.framework.AssertionFailedError} is raised if the
     *                   tests were not successful
     */
    protected TestCompletedEvent completeTestDeployment(String packageName, String filename, int startupTimeout,
                                                        int executeTimeout)
            throws Throwable {
        LifecycleEvent event = runTestDeployment(packageName, filename,
                startupTimeout,
                executeTimeout);
        conditionalFail(event instanceof TestInterruptedEvent,
                "Test run interrupted without completing the tests", event);
        conditionalFail(event instanceof TerminatedEvent,
                "Test run terminated without completing the tests", event);
        //if not a terminated event, its test results
        TestCompletedEvent results = (TestCompletedEvent) event;
        conditionalFail(results.isForcedTimeout(),
                "Forced timeout", event);
        if (results.isFailed() && !results.isSkipped()) {
            String message = "Test failed" + '\n' + results;
            throw new TerminationRecordException(message,results.getStatus());
        }
        return results;
    }


    /**
     * Run tests until they are completed, then analyse the results. The application is saved to the application field;
     * the event sink to eventSink. Both will be cleaned up during teardown.
     *
     * The default test startup and execution timeout will be used, unless overridden by system properties.
     *
     * The method will fail if the application terminated abnormally, or returned a test failure. Skipped and successful
     * tests are both viewed as successes.
     *
     * @param packageName name of the package
     * @param filename    test file name, excluding .sf
     * @return the test results
     * @throws Throwable on any problem. A {@link  org.smartfrog.services.assertions.TestTimeoutException} indicates
     *                   timeout waiting for results A {@link junit.framework.AssertionFailedError} is raised if the
     *                   tests were not successful
     */
    protected TestCompletedEvent runTestsToCompletion(String packageName, String filename) throws Throwable {
        return completeTestDeployment(packageName, filename,
                getTestStartupTimeout(),
                getTestTimeout());
    }

    /**
     * Get the timeout for test startup -either the default or an overridden property
     *
     * @return time in MS for tests to start up
     */
    protected int getTestStartupTimeout() {
        return TestHelper.getTestPropertyInt(TEST_TIMEOUT_STARTUP, STARTUP_TIMEOUT);
    }

    /**
     * Fail if a condition is not met; the message raised includes the message and the string value of the event.
     *
     * @param test    condition to evaluate
     * @param message message to print
     * @param event   related event
     * @throws junit.framework.AssertionFailedError
     *          if the condition is true
     */
    private void conditionalFail(boolean test, String message, LifecycleEvent event) {
        if (test) {
            AssertionFailedError afe=new AssertionFailedError(message + '\n' + event);
            TerminationRecord tr = event.getStatus();
            if(tr != null ) {
                afe.initCause(tr.getCause());
            }
            throw afe;
        }
    }

    /**
     * return the max time in milliseconds for tests
     *
     * @return the value set by a system property {@link #TEST_TIMEOUT_EXECUTE} or the default {@link
     *         #getDefaultTestExecutionTimeout()}
     */
    protected int getTestTimeout() {
        return TestHelper.getTestPropertyInt(TEST_TIMEOUT_EXECUTE, getDefaultTestExecutionTimeout());
    }

    /**
     * An override point for those tests that take a long time to run
     *
     * @return the time to execute tests
     */
    protected int getDefaultTestExecutionTimeout() {
        return EXECUTE_TIMEOUT;
    }

    /**
     * Assert that a test failed for a specific reason
     *
     * @param event          event to analyse
     * @param abnormalStatus is an abnormal status expected
     * @param errorText      optional error text
     */
    protected void assertTestRunFailed(LifecycleEvent event, boolean abnormalStatus, String errorText) {
        assertTrue("not a TestCompletedEvent: " + event, event instanceof TestCompletedEvent);
        TestCompletedEvent result = (TestCompletedEvent) event;
        assertTrue("test did not fail:\n" + event, result.isFailed());
        assertFalse("test succeeded:\n" + event, result.isSucceeded());
        TerminationRecord status = result.getStatus();
        assertNotNull("No termination record in " + result, status);
        if (abnormalStatus) {
            assertFalse("Status is normal when it should be abnormal:" + status, status.isNormal());
        } else {
            assertTrue("Status is abnormal when it should be normal:" + status, status.isNormal());
        }
        if (errorText != null) {
            assertContains(status.description, errorText);
        }
    }

    /**
     * Do a test run, assert that it passed and did not skip. The application and eventSink are both saved in member
     * variables, ready for cleanup in teardown
     *
     * @param packageName package containing the deployment
     * @param filename    filename (with no .sf extension)
     * @return the test completion event
     * @throws Throwable if things go wrong
     */
    protected TestCompletedEvent expectSuccessfulTestRun(String packageName, String filename) throws Throwable {
        TestCompletedEvent results = runTestsToCompletion(packageName, filename);
        conditionalFail(results.isFailed(), "Test failed", results);
        return results;
    }

    /**
     * Do a test run, assert that it passed or that it skipped. Skipped tests are warned about; there's no way to do
     * anything else with them in JUnit3
     *
     * @param packageName package containing the deployment
     * @param filename    filename (with no .sf extension)
     * @return the test completion event
     * @throws Throwable if things go wrong
     */
    protected TestCompletedEvent expectSuccessfulTestRunOrSkip(String packageName, String filename) throws Throwable {
        TestCompletedEvent results = runTestsToCompletion(packageName, filename);
        if (results.isSkipped()) {
            getLog().warn("skipped " + results);
        }
        return results;
    }

    /**
     * Do a test run, assert that it failed. The application and eventSink are both saved in member variables, ready for
     * cleanup in teardown
     *
     * @param packageName package containing the deployment
     * @param filename    filename (with no .sf extension)
     * @return the test completion event
     * @throws Throwable if things go wrong
     */
    protected TestCompletedEvent expectTestTimeout(String packageName, String filename) throws Throwable {
        LifecycleEvent event = runTestDeployment(packageName, filename);
        conditionalFail(event instanceof TerminatedEvent,
                "Test run terminated without completing the tests", event);
        //if not a terminated event, its test results
        TestCompletedEvent results = (TestCompletedEvent) event;
        conditionalFail(!results.isForcedTimeout(),
                "Tests failed to time out", event);
        return results;
    }

    /**
     * Do a test run, assert that it failed. The application and eventSink are both saved in member variables, ready for
     * cleanup in teardown
     *
     * @param packageName package containing the deployment
     * @param filename    filename (with no .sf extension)
     * @param abnormal    flag to indicate an abnormal failure record is expected
     * @param errorText   optional error text to look for
     * @return the test completion event
     * @throws Throwable if things go wrong
     */
    protected TestCompletedEvent expectAbnormalTestRun(String packageName, String filename, boolean abnormal,
                                                       String errorText) throws Throwable {
        LifecycleEvent event = runTestDeployment(packageName, filename);
        assertTestRunFailed(event, abnormal, errorText);
        return (TestCompletedEvent) event;
    }

}
