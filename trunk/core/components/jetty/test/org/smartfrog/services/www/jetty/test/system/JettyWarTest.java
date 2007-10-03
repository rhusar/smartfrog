package org.smartfrog.services.www.jetty.test.system;

/**
 *
 */
public class JettyWarTest extends JettyTestBase {


    public JettyWarTest(String name) {
        super(name);
    }


    public void testWarDeployed() throws Throwable {
        deployWebApp(SYSTEM_FILES + "jetty-does-war.sf",
                "JettyDoesWar");
    }

    public void testNestedWar() throws Throwable {
        deployWebApp(SYSTEM_FILES + "nested-war.sf",
                "nested-war");
    }


}
