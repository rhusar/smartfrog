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
package org.smartfrog.services.junit.test;

import org.smartfrog.services.junit.Statistics;
import org.smartfrog.services.junit.TestRunner;
import org.smartfrog.services.junit.listeners.XmlListener;
import org.smartfrog.sfcore.prim.Prim;

/**
 * created Nov 22, 2004 4:31:45 PM
 */

public class DeployedXMLListenerTest extends TestRunnerTestBase {

    public DeployedXMLListenerTest(String name) {
        super(name);
    }

    public void testSuccess() throws Throwable {
        String url;
        Prim deploy = null;
        url = "/files/xml-all.sf";
        final String appName = "XmlTest";

        int seconds = getTimeout();
        try {
            deploy = deployExpectingSuccess(url, appName);
            TestRunner runner = (TestRunner) deploy;
            assertTrue(runner != null);
            XmlListener listener = null;
            listener =
                    (XmlListener) deploy.sfResolve(TestRunner.ATTR_LISTENER,
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

            //TODO: more tests

        } finally {
            terminateApplication(deploy);
        }

    }

}
