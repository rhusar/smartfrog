package org.smartfrog.services.www.jetty.test.system;

/**
 *
 */
public class JettyWarTest extends WebappTestBase {


    /**
     * location for files. {@value}
     */
    public static final String FILE_BASE = "/org/smartfrog/services/www/jetty/test/system/";

    public JettyWarTest(String name) {
        super(name);
    }


    public void testWarDeployed() throws Throwable {
        deployWebApp(FILE_BASE + "jetty-does-war.sf",
                "JettyDoesWar");
    }

    public void testNestedWar() throws Throwable {
        deployWebApp(FILE_BASE + "nested-war.sf",
                "nested-war");
    }


}
