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

    public void testAbnormalFailedRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testAbnormalFailedRepeat.sf",
                "testAbnormalFailedRepeat");
    }

    public void testEmptyRepeat() throws Throwable {
        deployExpectingException(FILES + "testEmptyRepeat.sf",
                "testEmptyRepeat",
                "SmartFrogDeploymentException", null,
                "SmartFrogResolutionException",
                "non-optional attribute");
    }

    public void testNegativeRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testNegativeRepeat.sf",
                "testNegativeRepeat");
    }

    public void testNormalFailedRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testNormalFailedRepeat.sf",
                "testNormalFailedRepeat");
    }

    public void testWorkingRepeat() throws Throwable {
        application = deployExpectingSuccess(FILES + "testWorkingRepeat.sf",
                "testWorkingRepeat");
    }

}
