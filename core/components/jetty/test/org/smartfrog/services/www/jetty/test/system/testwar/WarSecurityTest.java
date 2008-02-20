package org.smartfrog.services.www.jetty.test.system.testwar;

import org.smartfrog.services.www.jetty.test.system.JettyTestBase;

/**
 *
 */
public class WarSecurityTest extends JettyTestBase {


    private static final String FILES ="/org/smartfrog/services/www/jetty/test/system/testwar/";

    public WarSecurityTest(String name) {
        super(name);
    }

    public void testSecurity() throws Throwable {
        expectSuccessfulTestRun(FILES, "testSecurity");
    }

    public void testSecurityUnauth() throws Throwable {
        expectSuccessfulTestRun(FILES, "testSecurityUnauth");
    }

    public void testRealm() throws Throwable {
        expectSuccessfulTestRun(FILES, "testRealm");
    }
}