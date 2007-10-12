package org.smartfrog.services.www.jetty.test.system;

/**
 *
 */
public class JettyWarTest extends JettyTestBase {


    public JettyWarTest(String name) {
        super(name);
    }


    public void testWarDeployed() throws Throwable {
        deployWebApp(SYSTEM_FILES + "testWarDeployed.sf",
                "testWarDeployed");
    }

    public void testNestedWar() throws Throwable {
        deployWebApp(SYSTEM_FILES + "testNestedWar.sf",
                "testNestedWar");
    }


}
