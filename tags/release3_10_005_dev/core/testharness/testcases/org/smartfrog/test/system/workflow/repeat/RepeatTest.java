package org.smartfrog.test.system.workflow.repeat;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;

/**
 * test delays
 */
public class RepeatTest extends SmartFrogTestBase {
    protected static final String FILES = "org/smartfrog/test/system/workflow/repeat/";

    public RepeatTest(String s) {
        super(s);
    }

    protected Prim application;

    protected void tearDown() throws Exception {
        super.tearDown();
        terminateApplication(application);
    }


    public void testAbnormalFailedRepeat() throws Throwable {
        application=deployExpectingSuccess(FILES +"testAbnormalFailedRepeat.sf",
            "testAbnormalFailedRepeat");
    }

    public void NotestEmptyRepeat() throws Throwable {
        deployExpectingException(FILES + "testEmptyRepeat.sf",
            "testEmptyRepeat",
        "SmartFrogDeploymentException",null,
        "SmartFrogResolutionException",
        "non-optional attribute 'action' is missing");
    }

    public void testNegativeRepeat() throws Throwable {
        application =deployExpectingSuccess(FILES + "testNegativeRepeat.sf",
            "testNegativeRepeat");
    }

    public void testNormalFailedRepeat() throws Throwable {
        application =deployExpectingSuccess(FILES + "testNormalFailedRepeat.sf",
            "testNormalFailedRepeat");
    }

    public void testWorkingRepeat() throws Throwable {
        application =deployExpectingSuccess(FILES + "testWorkingRepeat.sf",
            "testWorkingRepeat");
    }

}
