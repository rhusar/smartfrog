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


package org.smartfrog.services.junit.test;

import org.smartfrog.services.junit.JUnitTestSuiteImpl;
import org.smartfrog.services.junit.Statistics;
import org.smartfrog.services.junit.TestRunner;
import org.smartfrog.services.junit.listeners.BufferingListener;
import org.smartfrog.sfcore.common.SmartFrogInitException;
import org.smartfrog.sfcore.prim.Prim;

import java.util.ArrayList;
import java.util.List;

/**
 * Test deploying against a localhost Date: 06-Jul-2004 Time: 21:54:25
 */
public class LocalhostTest extends TestRunnerTestBase {

    public LocalhostTest(String name) {
        super(name);
    }

    public void testListFlattening() throws Exception {
        List l1 = new ArrayList();
        List l2 = new ArrayList();
        List l3 = new ArrayList();
        l2.add("1");
        l2.add("2");
        l3.add("3");
        l3.add("4");
        l2.add(l3);
        List flat = new ArrayList();
        flat.add("1");
        flat.add("2");
        flat.add("3");
        flat.add("4");
        List flat2 = JUnitTestSuiteImpl.flattenStringList(l2, "l2");
        assertEquals(flat, flat);
        assertEquals(flat, flat2);
        l2.add(new ArrayList());
        l1.add(l2);
        List flat3 = JUnitTestSuiteImpl.flattenStringList(l3, "l3");
        assertEquals(flat, flat3);
        List l4 = new ArrayList();
        l4.add(l1);
        l4.add(new Integer("3"));
        try {
            List flat4 = JUnitTestSuiteImpl.flattenStringList(l4, "l5");
            fail("should have thrown something");
        } catch (SmartFrogInitException e) {
            //expected
        }
        List flat5 = JUnitTestSuiteImpl.flattenStringList(null, "flat5");
        assertEquals(0, flat5.size());
    }

    public void testSuccess() throws Throwable {
        String url;
        Prim deploy = null;
        url = "/files/success.sf";
        final String appName = "localhostTest";

        int seconds = getTimeout();
        try {
            deploy = deployExpectingSuccess(url, appName);
            TestRunner runner = (TestRunner) deploy;
            assertTrue(runner != null);
            BufferingListener listener = null;
            listener =
                    (BufferingListener) deploy.sfResolve(
                            TestRunner.ATTR_LISTENER,
                            listener,
                            true);
            boolean finished = spinTillFinished(runner, seconds);
            assertTrue("Test run timed out", finished);
            Statistics statistics = runner.getStatistics();
            assertEquals("statistics.testRun==1", 1, statistics.getTestsRun());
            assertEquals("statistics.errors==0", 0, statistics.getErrors());
            assertEquals("statistics.failures==0",
                    0,
                    statistics.getFailures());
            assertTrue("expected tests to run", listener.getStartCount() == 1);
            assertTrue("session started",
                    listener.getSessionStartCount() == 1);
            assertTrue("session ended",
                    listener.getSessionEndCount() == 1);
            assertTrue("all tests passed", listener.testsWereSuccessful());

        } finally {
            terminateApplication(deploy);
        }

    }

}
