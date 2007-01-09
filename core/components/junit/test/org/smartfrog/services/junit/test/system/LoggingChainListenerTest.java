package org.smartfrog.services.junit.test.system;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestRunner;
import org.smartfrog.services.xunit.listeners.BufferingListener;
import org.smartfrog.services.xunit.listeners.BufferingListenerComponent;
import org.smartfrog.services.xunit.listeners.StatisticsTestListener;
import org.smartfrog.services.xunit.log.TestListenerLog;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.sfcore.prim.Prim;

/**
 * created Nov 22, 2004 4:45:26 PM
 */

public class LoggingChainListenerTest extends TestRunnerTestBase {

    public LoggingChainListenerTest(String name) {
        super(name);
    }


    public void testLogSetup() throws Throwable {
        Prim application = null;

        int seconds = getTimeout();
        try {
            application = deployExpectingSuccess("/files/logging-compound.sf",
                    "LoggingCompound");

            TestListenerLog log = (TestListenerLog) application.sfResolve(
                    "testLog",
                    (Prim) null,
                    true);
            BufferingListener factory =new BufferingListenerComponent();
            TestListener testListener = factory.listen(null,
                    "localhost",
                    "ROOT",
                    "example",
                    0);
            log.clearListeners();
            log.addLogListener(testListener);
            log.info("fact");
        } finally {
            terminateApplication(application);
        }

    }

    public void testSuccess() throws Throwable {
        Prim application = null;

        int seconds = getTimeout();
        try {
            application = deployExpectingSuccess("/files/log-chain-all.sf", "LogChainTest");

            TestRunner runner = (TestRunner) application.sfResolve(
                    "tests",
                    (Prim) null,
                    true);
            boolean finished = spinTillFinished(runner, seconds);
            assertTrue("Test run timed out", finished);

            StatisticsTestListener statsListener = null;
            statsListener = (StatisticsTestListener) application.sfResolve(
                    "statistics",
                    statsListener,
                    true);

            Statistics statistics = runner.getStatistics();
            Statistics statistics2 = statsListener.getStatistics();
            assertStatisticsEqual("runner and stats listener", statistics, statistics2);
            int logCount1=statistics.getLoggedMessages();
            assertTrue("The test runner doesn't see the log", logCount1 == 0);
            int logCount2 = statistics2.getLoggedMessages();
            assertTrue("Messages were not sent to the stats listener: logCount2=="+logCount2, logCount2>0);
        } finally {
            terminateApplication(application);
        }

    }
}
