package org.smartfrog.services.www.jetty.test.system.testwar;

import org.smartfrog.services.www.jetty.test.system.JettyTestBase;

/**
 *
 */
public class JettyWarTest extends JettyTestBase {


    private static final String FILES ="/org/smartfrog/services/www/jetty/test/system/testwar/";

    public JettyWarTest(String name) {
        super(name);
    }


    public void testWarDeployed() throws Throwable {
        deployWebApp(FILES + "testWarDeployed.sf",
                "testWarDeployed");
    }

    public void testNestedWar() throws Throwable {
        deployWebApp(FILES + "testNestedWar.sf",
                "testNestedWar");
    }

    public void testErrorPage() throws Throwable {
        expectLivenessFailure(FILES, "testErrorPage");
    }


}
