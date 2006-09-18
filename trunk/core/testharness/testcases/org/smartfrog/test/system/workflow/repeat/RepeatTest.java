package org.smartfrog.test.system.workflow.repeat;

import org.smartfrog.test.SmartFrogTestBase;

/**
 * test delays
 */
public class RepeatTest extends SmartFrogTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/repeat/";

    public RepeatTest(String s) {
        super(s);
    }
    public void testAbnormalFailedRepeat() throws Throwable {
        deployExpectingSuccess(FILES +"testAbnormalFailedRepeat.sf",
            "testAbnormalFailedRepeat");
    }

    public void testEmptyRepeat() throws Throwable {
        deployExpectingException(FILES + "testEmptyRepeat.sf",
            "testEmptyRepeat",
        "SmartFrogDeploymentException",null,
        "SmartFrogResolutionException",
        "non-optional attribute 'action' is missing");
    }

    public void testNegativeRepeat() throws Throwable {
        deployExpectingSuccess(FILES + "testNegativeRepeat.sf",
            "testNegativeRepeat");
    }

    public void testNormalFailedRepeat() throws Throwable {
        deployExpectingSuccess(FILES + "testNormalFailedRepeat.sf",
            "testNormalFailedRepeat");
    }

    public void testWorkingRepeat() throws Throwable {
        deployExpectingSuccess(FILES + "testWorkingRepeat.sf",
            "testWorkingRepeat");
    }


/*
    public void test() throws Throwable {
        deployExpectingException(FILES +"","",
            "","");
    }
*/

}
