package org.smartfrog.tools.ant.test.system;

import org.smartfrog.tools.ant.test.TaskTestBase;
import org.smartfrog.tools.ant.PingTask;
import org.smartfrog.tools.ant.SmartFrogTask;
import org.smartfrog.sfcore.common.ActionPing;

/**
 */
public class PingTest extends TaskTestBase{

    public PingTest(String s) {
        super(s);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "ping.xml";
    }


    /**
     * shut down anything running
     */
    public void tearDown() throws Exception {
        super.tearDown();
        executeTarget("teardown");
    }

    /**
     * testNoParams:
     * <p/>
     * defaults to localhost &c, so fails if there is no daemon
     */
    public void testNoParams() {
        expectBuildExceptionContaining("testNoParams", "testNoParams",
                PingTask.ERROR_FAILED_TO_PING);
    }

    /**
     * failonerror is not deemed to affect the no apps configuration, as that is
     * a fundamental configuration error
     */
    public void testNoFailure() {
        executeTarget("testNoFailure");
        assertPropertyUnset("pinged");
    }

    public void testEmptyApplication() {
        expectBuildExceptionContaining("testEmptyApplication", "failed to terminate",
                SmartFrogTask.ERROR_MISSING_APPLICATION_NAME);
    }

    public void testBadHost() {
        expectBuildExceptionContaining("testBadHost", "unknown host",
                PingTask.ERROR_FAILED_TO_PING);
        assertInLog("Unable to locate IP address of the host: no-such-hostname");

    }


    public void testRootProcess() {
        executeTarget("testRootProcess");
        assertPropertySet("pinged");
    }

    public void testUnknownComponent() {
        executeTarget("testUnknownComponent");
        assertPropertyUnset("pinged");
    }

}
