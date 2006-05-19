package org.smartfrog.services.junit.test;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.junit.TestRunner;
import org.smartfrog.services.junit.TestListenerFactory;
import org.smartfrog.services.junit.data.Statistics;
import org.smartfrog.services.junit.listeners.StatisticsTestListener;

/**
 * created Nov 22, 2004 4:45:26 PM
 */

public class LoggingChainListenerTest extends TestRunnerTestBase {

    public LoggingChainListenerTest(String name) {
        super(name);
    }

    public void testSuccess() throws Throwable {
        Prim deploy = null;

        int seconds = getTimeout();
        try {
            deploy = deployExpectingSuccess("/files/log-chain-all.sf", "ChainTest");
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
            int logCount1=statistics.getLoggedMessages();
            assertTrue("The test runner doesnt see the log", logCount1 == 0);
            int logCount2 = statistics2.getLoggedMessages();
            assertTrue("Messages not logged to the stats listener", logCount2>0);
        } finally {
            terminateApplication(deploy);
        }

    }
}
