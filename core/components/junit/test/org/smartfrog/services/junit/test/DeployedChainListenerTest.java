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
package org.smartfrog.services.junit.test;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.junit.TestRunner;
import org.smartfrog.services.junit.TestListenerFactory;
import org.smartfrog.services.junit.Statistics;
import org.smartfrog.services.junit.listeners.StatisticsTestListener;

/**
 * created Nov 22, 2004 4:45:26 PM
 */

public class DeployedChainListenerTest extends TestRunnerTestBase {

    public DeployedChainListenerTest(String name) {
        super(name);
    }

    public void testSuccess() throws Throwable {
        Prim deploy = null;

        int seconds = getTimeout();
        try {
            deploy = deployExpectingSuccess("/files/chain-all.sf", "ChainTest");
            TestRunner runner = (TestRunner) deploy;
            assertTrue(runner != null);
            TestListenerFactory listener = null;
            listener =
                    (TestListenerFactory) deploy.sfResolve(
                            "tests:listener",
                            listener,
                            true);
            boolean finished = spinTillFinished(runner, seconds);
            assertTrue("Test run timed out", finished);

            StatisticsTestListener statsListener=null;
            statsListener = (StatisticsTestListener) deploy.sfResolve(
                    "tests:statistics",
                    statsListener,
                    true);

            Statistics statistics = runner.getStatistics();
            Statistics statistics2 = statsListener.getStatistics();
            assertTrue("statistics don't match", statistics.isEqual(statistics2));
        } finally {
            terminateApplication(deploy);
        }

    }
}
