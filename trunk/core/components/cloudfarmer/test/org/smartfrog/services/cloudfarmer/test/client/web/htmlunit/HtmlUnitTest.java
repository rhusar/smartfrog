package org.smartfrog.services.cloudfarmer.test.client.web.htmlunit;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class HtmlUnitTest extends DeployingTestBase {
    public static final String FILES = "/org/smartfrog/services/cloudfarmer/test/client/web/htmlunit/";

    public HtmlUnitTest(String name) {
        super(name);
    }

    public void testClusterWebapp() throws Throwable {
        expectSuccessfulTestRunOrSkip(FILES, "testClusterWebapp");
    }

  
}