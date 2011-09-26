/* (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.junit.test.system;

import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.events.TestCompletedEvent;
import org.smartfrog.services.xunit.base.TestRunner;
import org.smartfrog.services.xunit.listeners.BufferingListener;
import org.smartfrog.services.xunit.listeners.xml.XmlListenerFactory;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.RemoteToString;
import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.test.TestHelper;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;

/**
 * this is a parent class for our tests, so that we can share logic about running tests, blocking till they finish, etc.
 * Date: 07-Jul-2004 Time: 20:04:27
 */
public abstract class TestRunnerTestBase extends DeployingTestBase {
    public static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";
    public static final String TIMEOUT_PROPERTY = "timeout";
    public static final int TIMEOUT_DEFAULT = 10;
    protected static final String BASE = "/files/";
    public static final int DELAY = 1000;

    public TestRunnerTestBase(String name) {
        super(name);
    }

    /** Sets up the fixture, for example, open a network connection. This method is called before a test is executed. */
    protected void setUp() throws Exception {
        super.setUp();
        assertSystemPropertySet(CODEBASE_PROPERTY);
    }

    /**
     * Get the application as a test runner. 
     * If the application is null: a JUnit exception is thrown.
     * There is no guarantee how long this reference will remain valid, if a test run is in progress (or has recently finished).
     * @return the application as a test runner.
     */
    protected TestRunner getApplicationAsTestRunner() {
        TestRunner runner = (TestRunner) application;
        assertNotNull("Null application", runner);
        return runner;
    }




    /**
     * Spin till a component is finished. This is unreliable and prone to timeouts -avoid.
     *
     * @param runner test runner
     * @param timeoutSeconds timeout in seconds
     * @return whether it finished or false for timeout
     * @throws InterruptedException if interrupted
     * @throws RemoteException on network problems
     */
    protected boolean spinTillFinished(TestBlock runner,
                                       int timeoutSeconds) throws Throwable {
        spinUntilFinished(runner, timeoutSeconds * 1000);
        return true;
    }

    protected int getTimeout() {
        int seconds = TIMEOUT_DEFAULT;
        String timeout = TestHelper.getTestProperty(TIMEOUT_PROPERTY, null);
        if (timeout != null) {
            seconds = Integer.valueOf(timeout).intValue();
        }
        return seconds;
    }

    public Document validateXmlLog(File file) throws Exception {
        assertTrue(file.exists());
        DocumentBuilder builder;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
        factory.setValidating(false);
        builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        return document;
    }

    /**
     * assert that statistics entries are equal, fail if not. Uses {@link org.smartfrog.services.xunit.serial.Statistics#isEqual(org.smartfrog.services.xunit.serial.Statistics)}
     * for the comparison.
     *
     * @param text text to prepand to all messages
     * @param s1   first set of stats, must not be null
     * @param s2   second set of stats, must not be null
     */
    protected void assertStatisticsEqual(String text, Statistics s1, Statistics s2) {
        assertNotNull(text + ": empty statistics (first set) ", s1);
        assertNotNull(text + ": empty statistics (second set) ", s2);
        if (!s1.isEqual(s2)) {
            fail(text + ": not equal: [" + s1 + "] [" + s2 + "]");
        }
    }

    /**
     * execute a test run to a buffer, and make assertions about the results
     * @param name the test to run (base-relative; no .sf extension needed)
     * @param run number of tests to run; -1 means no
     * @param errors number of errors expected
     * @param failures number of failures expected
     * @throws Throwable on any failure
     */
    protected void executeBufferedTestRun(String name, int run, int errors, int failures) throws Throwable {
        runTestsToCompletion(BASE, name);
        TestRunner runner = getApplicationAsTestRunner();
        BufferingListener listener = null;
        listener =
                (BufferingListener) application.sfResolve(
                        TestRunner.ATTR_LISTENER,
                        listener,
                        true);

        if (run >= 0) {
            assertTrue("expected tests to run", listener.getStartCount() == 1);
            assertTrue("session started",
                    listener.getSessionStartCount() == 1);
            assertTrue("session ended",
                    listener.getSessionEndCount() == 1);
            //assertTrue("all tests passed", listener.testsWereSuccessful());
            Statistics statistics = runner.getStatistics();
            assertEquals("statistics.testRun" + run, run, statistics.getTestsRun());
            assertEquals("statistics.errors", errors, statistics.getErrors());
            assertEquals("statistics.failures", failures,
                    statistics.getFailures());
        } else {
            assertEquals("expected tests to be skipped and startcount==0",
                    0, listener.getStartCount());
        }
    }

    protected void ping(final String component, final Liveness runner) throws SmartFrogLivenessException, RemoteException {
        try {
            runner.sfPing(null);
        } catch (NoSuchObjectException e) {
            //if we get here then the test runner finished, but it isn't hanging around. This usually indicates some kind of
            //deployment failure
            SmartFrogLivenessException exception = new SmartFrogLivenessException(
                    "The " + component + " is no longer live", e);
            logThrowable(e.getMessage(), e);
            throw exception;
        }
    }

    protected TestCompletedEvent executeTestFile(final String filename) throws Throwable {
        return runTestsToCompletion(BASE, filename);
    }

    protected void resolveAndValidateXMLListenerFile(final String suitename) throws Exception {
        TestRunner runner = getApplicationAsTestRunner();
        XmlListenerFactory listenerFactory = null;
        Thread.sleep(DELAY);
        listenerFactory =
                (XmlListenerFactory) application.sfResolve(
                        TestRunner.ATTR_LISTENER,
                        listenerFactory,
                        true);
        getLog().info("Listener Proxy is " + listenerFactory);
        getLog().info("Listener remote info is " + listenerFactory.sfRemoteToString());
        Thread.sleep(DELAY);
        String path = listenerFactory.lookupFilename("localhost", suitename);
        assertNotNull("path of test suite " + suitename, path);

        File xmlfile = new File(path);
        assertTrue("File does not exist " + path, xmlfile.exists());

        getLog().info("XML output file: " + xmlfile);

        //validate the file
        validateXmlLog(xmlfile);
    }
}
