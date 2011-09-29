/** (C) Copyright 2011 Hewlett-Packard Development Company, LP

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

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.apache.commons.logging.Log;
import org.smartfrog.SFSystem;
import org.smartfrog.services.assertions.TerminationRecordException;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.assertions.events.TestEventSink;
import org.smartfrog.services.assertions.events.TestInterruptedEvent;
import org.smartfrog.sfcore.common.ConfigurationDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SmartFrogSecurityException;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;

import java.net.UnknownHostException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;

/**
 * This class manages lifecycle tests; it is a refactoring of the SmartFrogTestBase and DeployingTestBase
 * classes so that much of the work can also be done in Groovy.
 *
 * It extends the {@link Assert} class s
 */
public class SmartFrogTestManager extends Assert implements TestProperties {

    private Log log;
    private String hostname = "localhost";

    /**
     * The event sink for the deployed test application
     */
    private TestEventSink eventSink;
    private String name;
    private Prim application;
    private int defaultExecutionTimeout = EXECUTE_TIMEOUT;
    private int testStartupTimeout = STARTUP_TIMEOUT;

    public SmartFrogTestManager(final Log log, final String name) {
        this.log = log;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public void setLog(final Log log) {
        this.log = log;
    }

    public Log getLog() {
        return log;
    }

    public String getName() {
        return name;
    }


    public TestEventSink getEventSink() {
        return eventSink;
    }


    /**
     * Get the current application or null
     *
     * @return the current application
     */
    public synchronized Prim getApplication() {
        return application;
    }

    /**
     * set the application
     *
     * @param application new application
     */
    public synchronized void setApplication(Prim application) {
        this.application = application;
    }


    public void setup() {
    }

    public void teardown() throws RemoteException {
        terminateApplication();
    }

    public synchronized void terminateApplication() throws RemoteException {
        terminateApplication(application);
        application = null;
    }


    /**
     * log a throwable
     *
     * @param message the text to log
     * @param thrown  what was thrown
     */
    public void logThrowable(String message, Throwable thrown) {
        log.error(message, thrown);
    }

    /**
     * Look through what we got back from deployment; if it is a CD containing an exception then it is
     * checked for an exception, which is then thrown if not null
     *
     * @param deployedApp the application
     *
     * @throws Throwable any exception raised during deployment
     */
    @SuppressWarnings({"ProhibitedExceptionThrown"})
    protected void lookForThrowableInDeployment(Object deployedApp) throws Throwable {
        if (deployedApp instanceof ConfigurationDescriptor) {
            ConfigurationDescriptor cd = (ConfigurationDescriptor) deployedApp;
            Throwable resultException = cd.getResultException();
            if (resultException != null) {
                log.warn("During deployment: " + resultException, resultException);
                throw resultException;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Test success in description: \n      "
                            + cd.toString("\n        "));
                }

            }
        }
    }

    /**
     * Deploy an application
     *
     * @param appName application name
     * @param testURL URL of the application
     *
     * @return whatever was deployed
     *
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong
     */
    public Object deployApplication(String appName, String testURL)
            throws SmartFrogException, RemoteException,
            SFGeneralSecurityException, UnknownHostException {
        ConfigurationDescriptor cfgDesc =
                new ConfigurationDescriptor(appName,
                        testURL,
                        ConfigurationDescriptor.Action.DEPLOY,
                        hostname,
                        null);
        log.info("Deploying of: " + cfgDesc.toString("\n    "));

        Object deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc, true);
        return deployedApp;
    }


    /**
     * terminate a named application. If the application parameter is null or refers to a nonexistent node, nothing happens.
     *
     * @param target application; can be null
     *
     * @throws RemoteException on network trouble other than an already terminated app
     */
    public void terminateApplication(Prim target) throws RemoteException {
        if (target == null) {
            return;
        }
        Reference name;
        try {
            name = target.sfCompleteName();
        } catch (RemoteException ignore) {
            name = null;
        }
        try {
            target.sfDetachAndTerminate(TerminationRecord.normal(name));
        } catch (NoSuchObjectException ignore) {
            //app is already terminated, do not fail a test.
        }
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
     * @throws SmartFrogSecurityException security problems
     */
    private synchronized void startListening(Prim prim)
            throws RemoteException, SmartFrogRuntimeException, SmartFrogSecurityException {
        stopListening();
        try {
            eventSink = new TestEventSink(prim);
            eventSink.setName(getClass() + "." + getName());
        } catch (NoSuchObjectException e) {
            //some kind of remoting problem may happen during termination.
            logThrowable(
                    "Remote application has been deleted before its lifecycle events could be subscribed to",
                    e);
            throw e;
        }
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
    public Prim loadApplication(String appName, String testURL, int startupTimeout) throws Throwable {
        ConfigurationDescriptor configurationDescriptor =
                new ConfigurationDescriptor(appName,
                        testURL,
                        ConfigurationDescriptor.Action.LOAD,
                        hostname,
                        null);
        ApplicationLoaderThread loader = new ApplicationLoaderThread(configurationDescriptor, true);
        loader.start();
        if (!loader.waitForNotification(startupTimeout)) {
            loader.interrupt();
            loader.stop();
            throw new SmartFrogRuntimeException(
                    "Time out loading the configuration descriptor after " + startupTimeout
                            + " mS: " + testURL);
        }
        Object loaded = loader.getLoaded();

        //throw any deployment exception
        lookForThrowableInDeployment(loaded);
        //or any exception during startup
        loader.rethrow();
        return (Prim) loaded;
    }

    /**
     * Deploys an application and returns the reference to deployed application.
     *
     * @param testURL URL to test
     * @param appName Application name
     *
     * @return Reference to deployed application
     *
     * @throws RemoteException in the event of remote trouble.
     */
    @SuppressWarnings({"ProhibitedExceptionThrown"})
    protected Prim deployExpectingSuccess(String testURL, String appName)
            throws Throwable {
        try {
            Object deployedApp = deployApplication(appName, testURL);

            if (deployedApp instanceof Prim) {
                return ((Prim) deployedApp);
            } else {
                lookForThrowableInDeployment(deployedApp);
                throw new AssertionFailedError("Deployed something of type "
                        + deployedApp.getClass()
                        + ": " + deployedApp);
            }
        } catch (Throwable throwable) {
            logThrowable("thrown during deployment", throwable);
            throw throwable;
        }
    }

    /**
     * get any test property; these are (currently) extracted from the JVM props
     * @param property system property
     * @param defaultValue default value
     * @return the system property value or the default, if that is undefined
     */
    public static String getTestProperty(String property, String defaultValue) {
        return System.getProperty(property, defaultValue);
    }

    /**
     * combine the package and filename to URL. Appends .sf if needed
     * @param packageName package containing the file
     * @param filename the filename
     * @return a concatenated package with .sf at the end
     */
    public static String createUrlString(String packageName, String filename) {
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
     * @throws Throwable any  failure.
     * @throws org.smartfrog.services.assertions.TestTimeoutException on a timeout.
     */
    public LifecycleEvent runTestDeployment(String packageName, String filename, int startupTimeout,
                                            int executeTimeout) throws Throwable {
        String urlstring = createUrlString(packageName, filename);
        setApplication(loadApplication(filename, urlstring, startupTimeout));
        startListening(getApplication());
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
    public LifecycleEvent runTestDeployment(String packageName, String filename) throws Throwable {
        return runTestDeployment(packageName, filename, getTestStartupTimeout(),
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
     * assert that a string contains a substring
     *
     * @param source    source to scan
     * @param substring string to look for
     */
    public void assertContains(String source, String substring) {
        assertContains(source, substring, "", (String) null);
    }


    /**
     * assert that a string contains a substring
     *
     * @param source     source to scan
     * @param substring  string to look for
     * @param resultMessage configuration description
     * @param extraText  any extra text, can be null
     */
    public void assertContains(String source, String substring, String resultMessage, String extraText) {
        assertNotNull("No string to look for [" + substring + "]", source);
        assertNotNull("No substring ", substring);
        final boolean contained = source.contains(substring);

        if (!contained) {
            String message = "- Did not find \n[" + substring + "]\nin \n[" + source + "]" +
                    (resultMessage != null ? ("\n, Result:\n" + resultMessage) : "");
            getLog().error(message + (extraText != null ? ('\n' + extraText) : ""));
            fail(message);
        }
    }


    /**
     * assert that a string contains a substring
     *
     * @param source     source to scan
     * @param substring  string to look for
     * @param resultMessage configuration description
     * @param exception  any exception to look at can be null
     */
    public void assertContains(String source, String substring, String resultMessage, Throwable exception) {
        assertNotNull("No string to look for [" + substring + "]", source);
        assertNotNull("No substring ", substring);
        final boolean contained = source.contains(substring);

        if (!contained) {
            String message = "- Did not find \n[" + substring + "] \nin \n[" + source + ']' +
                    (resultMessage != null ? ("\n, Result:\n" + resultMessage) : "");
            if (exception != null) {
                log.error(message, exception);
            } else {
                log.error(message);
            }
            fail(message);
        }
    }


    /**
     * A deployment failed, and a CD containing exceptions was returned instead of an application.
     * This method scans through looking for the expected exceptions
     * and failing if the type or messages do not match
     *
     * @param deployedApp            what was deployed
     * @param cfgDesc                the original configuration descriptor.
     * @param exceptionName          optional substring to find in the outermost exception
     * @param searchString           optional text to find in the outermost exception
     * @param containedExceptionName optional substring to find in any nested exception
     * @param containedExceptionText optional text to find in any nested  exception
     */
    public void searchForExpectedExceptions(ConfigurationDescriptor deployedApp,
                                            ConfigurationDescriptor cfgDesc,
                                            String exceptionName,
                                            String searchString,
                                            String containedExceptionName,
                                            String containedExceptionText) {
        //we got an exception. let's take a look.
        Throwable returnedFault;
        returnedFault = deployedApp.getResultException();
        assertFaultCauseAndCDContains(returnedFault, exceptionName, searchString, cfgDesc);
        //get any underlying cause
        Throwable cause = returnedFault.getCause();
        assertFaultCauseAndCDContains(cause, containedExceptionName, containedExceptionText, cfgDesc);
    }

    /**
     * assert that something we deployed contained the name and text we wanted.
     *
     * @param cause     root cause. Can be null, if faultName and faultText are also null. 
     *  It is an error if they are defined and the cause is null
     * @param faultName substring that must be in the classname of the fault (can be null)
     * @param faultText substring that must be in the text of the fault (can be null)
     * @param cfgDesc   what we were deploying; the status string is extracted for reporting purposes
     */
    public void assertFaultCauseAndCDContains(Throwable cause, String faultName,
                                              String faultText, ConfigurationDescriptor cfgDesc) {
        String details = cfgDesc.statusString();
        assertFaultCauseAndTextContains(cause, faultName, faultText, details);
    }

    /**
     * /** assert that something we deployed contained the name and text we wanted.
     *
     * @param cause     root cause. Can be null, if faultName and faultText are also null.
     *  It is an error if they are defined and the cause is null
     * @param faultName substring that must be in the classname of the fault (can be null)
     * @param faultText substring that must be in the text of the fault  (can be null)
     * @param details   status string for reporting purposes
     */
    public void assertFaultCauseAndTextContains(Throwable cause, String faultName,
                                                String faultText, String details) {
        //if we wanted the name of a fault
        if (faultName != null) {
            //then look for the name of contained exception and see it matches what was
            // asked for
            assertNotNull("expected throwable of type "
                    + faultName,
                    cause);
            //verify the name
            assertThrowableNamed(cause,
                    faultName,
                    details);
        }
        //look for the exception text
        if (faultText != null) {
            assertNotNull("expected throwable containing text "
                    + faultText,
                    cause);

            assertContains(cause.toString(),
                    faultText,
                    details,
                    extractDiagnosticsInfo(cause));
        }
    }

    /**
     * assert that a throwable's classname is of a given type/substring
     *
     * @param thrown     what was thrown
     * @param name       the name of the class
     * @param message description (can be null)
     */
    public void assertThrowableNamed(Throwable thrown, String name, String message) {
        assertContains(thrown.getClass().getName(),
                name, message, thrown);
    }

    /**
     * Fail if a condition is not met; the message raised includes the message and the string value of the event.
     *
     * @param test    condition to evaluate
     * @param message message to print
     * @param event   related event
     * @throws AssertionFailedError if the condition is true
     */
    public void conditionalFail(boolean test, String message, LifecycleEvent event) {
        conditionalFail(test, message, null, event);
    }

    /**
     * Fail if a condition is not met; the message raised includes the message and the string value of the event.
     *
     * @param test    condition to evaluate
     * @param message message to print
     * @param event   related event
     * @throws AssertionFailedError if the condition is true
     */
    public void conditionalFail(boolean test, String message, String eventHistory, LifecycleEvent event) {
        if (test) {
            AssertionFailedError afe = new AssertionFailedError(message
                    + "\nEvent is "
                    + event
                    + (eventHistory != null ?
                    ("\nHistory:\n" + eventHistory)
                    : "")
            );
            TerminationRecord tr = event.getStatus();
            if (tr != null) {
                afe.initCause(tr.getCause());
            }
            throw afe;
        }
    }

    /**
     * Assert that a termination record contains the expected values. If either the throwableClass or
     * throwableText attributes are non-null, then the record must contain a fault
     *
     * @param record          termination record
     * @param descriptionText text to look for in the description (optional; can be null)
     * @param throwableClass  fragment of the class name/package of the exception. (optional; can be null)
     * @param throwableText   text to look for in the fault text. (optional; can be null)
     */
    public void assertTerminationRecordContains(TerminationRecord record,
                                                String descriptionText,
                                                String throwableClass,
                                                String throwableText) {
        assertNotNull("Null termination record", record);
        if (descriptionText != null) {
            assertContains(record.description, descriptionText);
        }
        if (throwableClass != null || throwableText != null) {
            if (record.getCause() != null) {
                assertFaultCauseAndTextContains(record.getCause(),
                        throwableClass, throwableText, null);
            } else {
                fail("Expected Termination record " + record + " to contain "
                        + " a throwable " + (throwableClass != null ? throwableClass : "")
                        + (throwableText != null ? (" with text" + throwableText) : ""));
            }
        }
    }

    /**
     * return the max time in milliseconds for tests
     *
     * @return the value set by a system property {@link #TEST_TIMEOUT_EXECUTE} or the default {@link
     *         #getDefaultTestExecutionTimeout()}
     */
    public int getTestTimeout() {
        return TestHelper.getTestPropertyInt(TEST_TIMEOUT_EXECUTE, getDefaultTestExecutionTimeout());
    }

    /**
     * An override point for those tests that take a long time to run
     *
     * @return the time to execute tests
     */
    public int getDefaultTestExecutionTimeout() {
        return defaultExecutionTimeout;
    }

    public void setDefaultExecutionTimeout(final int defaultExecutionTimeout) {
        this.defaultExecutionTimeout = defaultExecutionTimeout;
    }


    /**
     * Assert that a test failed for a specific reason
     *
     * @param event          event to analyse
     * @param abnormalStatus is an abnormal status expected
     * @param errorText      optional error text
     */
    public void assertTestRunFailed(LifecycleEvent event, boolean abnormalStatus, String errorText) {
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
    public TestCompletedEvent expectSuccessfulTestRun(String packageName, String filename)
            throws Throwable {
        TestCompletedEvent results = runTestsToCompletion(packageName, filename);
        conditionalFail(results.isFailed(), "Test failed", results);
        if (results.isFailed() || results.isSkipped()) {
            String message = "Test failed or was skipped: " + '\n' + results;
            throw new TerminationRecordException(message, results.getStatus());
        }
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
    public TestCompletedEvent expectSuccessfulTestRunOrSkip(String packageName, String filename)
            throws Throwable {
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
    public TestCompletedEvent expectTestTimeout(String packageName, String filename) throws Throwable {
        LifecycleEvent event = runTestDeployment(packageName, filename);
        conditionalFail(event instanceof TerminatedEvent,
                "Test run terminated when a timeout was expected", event);
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
    public TestCompletedEvent expectAbnormalTestRun(String packageName, String filename, boolean abnormal,
                                                    String errorText) throws Throwable {
        LifecycleEvent event = runTestDeployment(packageName, filename);
        assertTestRunFailed(event, abnormal, errorText);
        return (TestCompletedEvent) event;
    }

    /**
     * extract as much info as we can from a throwable.
     *
     * @param thrown what was thrown
     *
     * @return a string describing the throwable; includes a stack trace
     */
    public static String extractDiagnosticsInfo(Throwable thrown) {
        StringBuilder buffer = new StringBuilder();
        thrown.getStackTrace();
        buffer.append("Message:  ");
        buffer.append(thrown.toString());
        buffer.append('\n');
        buffer.append("Class:    ");
        buffer.append(thrown.getClass().getName());
        buffer.append('\n');
        buffer.append("Stack:    ");
        StackTraceElement[] stackTrace = thrown.getStackTrace();
        for (StackTraceElement frame : stackTrace) {
            buffer.append(frame.toString());
            buffer.append('\n');
        }
        return buffer.toString();
    }

    public ConfigurationDescriptor createDeploymentConfigurationDescriptor(String appName, String testURL)
            throws SmartFrogInitException {
        return new ConfigurationDescriptor(appName,
                testURL,
                ConfigurationDescriptor.Action.DEPLOY,
                hostname,
                null);
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
     *                   timeout waiting for results A {@link  AssertionFailedError} is raised if the
     *                   tests were not successful
     */
    protected TestCompletedEvent runTestsToCompletion(String packageName, String filename) throws Throwable {
        return completeTestDeployment(packageName, filename,
                getTestStartupTimeout(),
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
    protected TestCompletedEvent completeTestDeployment(String packageName,
                                                        String filename,
                                                        int startupTimeout,
                                                        int executeTimeout)
            throws Throwable {
        LifecycleEvent event = runTestDeployment(packageName, filename,
                startupTimeout,
                executeTimeout);
        String eventHistory = getEventSink().dumpHistory();
        conditionalFail(event instanceof TestInterruptedEvent,
                "Test run interrupted without completing the tests", eventHistory, event);
        conditionalFail(event instanceof TerminatedEvent,
                "Test run TerminatedEvent received before the TestCompletedEvent", eventHistory, event);
        conditionalFail(!(event instanceof TestCompletedEvent),
                "Test run terminated with an unexpected event", eventHistory, event);
        //if not a terminated event, its test results
        TestCompletedEvent results = (TestCompletedEvent) event;
        conditionalFail(results.isForcedTimeout(),
                "Forced timeout", event);
/*        if (results.isFailed() && !results.isSkipped()) {
            String message = "Test is marked as failed: " + '\n' + results;
            throw new TerminationRecordException(message, results.getStatus());
        }*/
        return results;
    }


    /**
     * Deploy a component, expecting a smartfrog exception. You can also specify the classname of a contained fault -which, if specified, must be contained, and
     * some text to be searched for in this exception.
     *
     * @param testURL                URL to test
     * @param appName                name of test app
     * @param exceptionName          name of the exception thrown (can be null)
     * @param searchString           string which must be found in the exception message (can be null)
     * @param containedExceptionName optional classname of a contained exception; does not have to be the full name; a fraction will suffice.
     * @param containedExceptionText optional text in the contained fault.
     *
     * @return the exception that was returned
     *
     * @throws RemoteException in the event of remote trouble.
     * @throws SmartFrogException The component did not deploy with some other exception
     * @throws SFGeneralSecurityException security trouble
     * @throws UnknownHostException hostname is wrong
     */
    @SuppressWarnings({"NestedAssignment"})
    public Throwable deployExpectingException(String testURL,
                                              String appName,
                                              String exceptionName,
                                              String searchString,
                                              String containedExceptionName,
                                              String containedExceptionText) throws SmartFrogException,
            RemoteException, UnknownHostException, SFGeneralSecurityException {
        ConfigurationDescriptor cfgDesc =
                createDeploymentConfigurationDescriptor(appName, testURL);

        Object deployedApp = null;
        Throwable resultException = null;
        try {
            //Deploy and don't throw exception. Exception will be contained
            // in a ConfigurationDescriptor.
            deployedApp = SFSystem.runConfigurationDescriptor(cfgDesc, false);
            ConfigurationDescriptor deployedCD;
            if ((deployedApp instanceof ConfigurationDescriptor) &&
                    (deployedCD = (ConfigurationDescriptor) deployedApp).getResultException() != null) {
                searchForExpectedExceptions(deployedCD, cfgDesc, exceptionName,
                        searchString, containedExceptionName, containedExceptionText);
                resultException = ((ConfigurationDescriptor) deployedApp).getResultException();
                return resultException;
            } else {
                //clean up
                String deployedAppString = deployedApp.toString();
                if (deployedApp instanceof Prim) {
                    terminateApplication((Prim) deployedApp);
                }
                //then fail
                fail("We expected an exception here:"
                        + (exceptionName != null ? exceptionName : "(anonymous)")
                        + " but got this deployment " + deployedAppString);
            }
        } catch (Exception fault) {
            fail(fault.toString());
        }
        return null;
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
            setName("loader thread for " + configuration);
            setDaemon(true);
        }

        /**
         * load the configuration
         *
         * @throws Throwable if anything went wrong
         */
        @Override
        public void execute() throws Throwable {
            loaded = SFSystem.runConfigurationDescriptor(configuration, throwException);
        }

        public Object getLoaded() {
            return loaded;
        }
    }

}
