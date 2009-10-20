package org.smartfrog.services.cloudfarmer.test.client.web.workflow;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class WorkflowWebappTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/workflow/";

    public WorkflowWebappTest(String name) {
        super(name);
    }

    public void testWebappHappy() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testWebappHappy");
    }

    public void testWorkflowList() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testWorkflowList");
    }

    public void testSubmitMRJob() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testSubmitMRJob");
    }

    public void testSubmitTool() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testSubmitTool");
    }

    public void testQueueWorkflow() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testQueueWorkflow");
    }

    public void testWorkflowAdmin() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testWorkflowAdmin");
    }

    public void testWorkflowServerHappy() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testWorkflowServerHappy");
    }
}