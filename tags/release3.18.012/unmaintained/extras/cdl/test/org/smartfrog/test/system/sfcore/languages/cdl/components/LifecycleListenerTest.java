package org.smartfrog.test.system.sfcore.languages.cdl.components;

import org.smartfrog.test.system.sfcore.languages.cdl.execute.CdlDeployingTestBase;
import org.smartfrog.services.assertions.TestCompound;

/**
 * Test the lifecycle of an CdlCompound
 */
public class LifecycleListenerTest extends CdlDeployingTestBase {
    protected static final String FILES = "/org/smartfrog/test/system/sfcore/languages/cdl/components/";

    public LifecycleListenerTest(String name) {
        super(name);
    }

    public void testNormalWorkflow() throws Throwable {
        application = deployExpectingSuccess(FILES + "testNormalWorkflow.sf",
                "testNormalWorkflow");
        TestCompound block = (TestCompound) application;
        expectSuccessfulTermination(block);
        assertAttributeEquals(application, "value", true);
    }

    public void testNormalWorkflowEvent() throws Throwable {
        expectSuccessfulTestRun(FILES,"testNormalWorkflow");
        assertAttributeEquals(application, "value", true);
    }
}
