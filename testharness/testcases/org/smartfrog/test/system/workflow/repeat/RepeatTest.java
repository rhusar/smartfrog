package org.smartfrog.test.system.workflow.repeat;

import org.smartfrog.test.DeployingTestBase;

/**
 * test delays
 */
public class RepeatTest extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/repeat/";

    public RepeatTest(String s) {
        super(s);
    }

    /**
     * test case
     * @throws Throwable on failure
     */

    public void testAbnormalFailedRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testAbnormalFailedRepeat.sf",
                "testAbnormalFailedRepeat");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testEmptyRepeat() throws Throwable {
        deployExpectingException(FILES + "testEmptyRepeat.sf",
                "testEmptyRepeat",
                EXCEPTION_DEPLOYMENT, null,
                EXCEPTION_LINKRESOLUTION,
                "non-optional attribute");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testNegativeRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testNegativeRepeat.sf",
                "testNegativeRepeat");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testNormalFailedRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testNormalFailedRepeat.sf",
                "testNormalFailedRepeat");
    }

    /**
     * test case
     * @throws Throwable on failure
     */
    public void testWorkingRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testWorkingRepeat.sf",
                "testWorkingRepeat");
    }

}
