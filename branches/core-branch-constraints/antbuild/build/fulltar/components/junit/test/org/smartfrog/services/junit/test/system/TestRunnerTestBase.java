/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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

import org.smartfrog.services.xunit.base.TestRunner;
import org.smartfrog.services.xunit.listeners.BufferingListener;
import org.smartfrog.services.xunit.serial.Statistics;
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
    private static final String BASE = "/files/";

    public TestRunnerTestBase(String name) {
        super(name);
    }

    /** Sets up the fixture, for example, open a network connection. This method is called before a test is executed. */
    protected void setUp() throws Exception {
        super.setUp();
        assertSystemPropertySet(CODEBASE_PROPERTY);
    }

    /**
     * Spin till a component is finished
     *
     * @param runner
     * @param timeoutSeconds
     * @return whether it finished or false for timeout
     * @throws InterruptedException
     * @throws RemoteException
     */
    protected boolean spinTillFinished(TestRunner runner,
                                       int timeoutSeconds) throws InterruptedException,
            RemoteException {

        try {
            do {
                Thread.sleep(1000);
                timeoutSeconds--;
            } while (!runner.isFinished() && timeoutSeconds >= 0);
            return runner.isFinished();
        } catch (NoSuchObjectException e) {
            //if the object is here it has terminated.
            return true;
        }
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
//        factory.setValidating(true);
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
     * execute a test run to a buffer
     * @param name the test to run (base-relative; no .sf extension needed)
     * @param run number of tests to run; -1 means no
     * @param errors
     * @param failures
     * @throws Throwable
     */
    protected void executeBufferedTestRun(String name, int run, int errors, int failures) throws Throwable {
        application = deployExpectingSuccess(BASE+name+".sf", name);
        int seconds = getTimeout();
        TestRunner runner = (TestRunner) application;
        assertTrue(runner != null);
        BufferingListener listener = null;
        listener =
                (BufferingListener) application.sfResolve(
                        TestRunner.ATTR_LISTENER,
                        listener,
                        true);
        boolean finished = spinTillFinished(runner, seconds);
        assertTrue("Test run timed out", finished);
        if(run>=0) {
            assertTrue("expected tests to run", listener.getStartCount() == 1);
            assertTrue("session started",
                    listener.getSessionStartCount() == 1);
            assertTrue("session ended",
                    listener.getSessionEndCount() == 1);
            //assertTrue("all tests passed", listener.testsWereSuccessful());
            Statistics statistics = runner.getStatistics();
            assertEquals("statistics.testRun"+run, run, statistics.getTestsRun());
            assertEquals("statistics.errors", errors, statistics.getErrors());
            assertEquals("statistics.failures",failures,
                    statistics.getFailures());
        } else {
            assertEquals("expected tests to be skipped and startcount==0",
                    0,listener.getStartCount());
        }
    }
}
