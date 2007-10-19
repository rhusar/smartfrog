package org.smartfrog.services.junit.test.system;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestRunner;
import org.smartfrog.services.xunit.listeners.BufferingListener;
import org.smartfrog.services.xunit.listeners.BufferingListenerImpl;
import org.smartfrog.services.xunit.listeners.StatisticsTestListener;
import org.smartfrog.services.xunit.log.TestListenerLog;
import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.sfcore.prim.Prim;

/** created Nov 22, 2004 4:45:26 PM */

public class LoggingChainListenerTest extends TestRunnerTestBase {
    private static final String TEST_LOG = "TestLog";

    public LoggingChainListenerTest(String name) {
        super(name);
    }


    public void testLogSetup() throws Throwable {

        int seconds = getTimeout();
        application = deployExpectingSuccess("/files/logging-compound.sf",
                "LoggingCompound");

        TestListenerLog log = (TestListenerLog) application.sfResolve(
                TEST_LOG,
                (Prim) null,
                true);
        BufferingListener factory = new BufferingListenerImpl();
        TestListener testListener = factory.listen(null,
                "localhost",
                "ROOT",
                "example",
                0);
        log.clearListeners();
        log.addLogListener(testListener);
        log.info("fact");
    }


    public void testSuccess() throws Throwable {

        int seconds = getTimeout();
        application = deployExpectingSuccess("/files/log-all.sf", "LogAllTest");

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
        int logCount1 = statistics.getLoggedMessages();
        assertTrue("The test runner doesn't see the log", logCount1 == 0);
        int logCount2 = statistics2.getLoggedMessages();
        assertTrue("Messages were not sent to the stats listener: logCount2==" + logCount2, logCount2 > 0);
    }
}
