package org.smartfrog.services.www.jetty.test.system;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.test.SmartFrogTestBase;

/**
 *
 */
public class JettyWarTest extends SmartFrogTestBase {
    /**
     * Node of any deployed application
     */
    private Prim application;


    /**
     * location for files. {@value}
     */
    public static final String FILE_BASE = "/org/smartfrog/services/www/jetty/test/system/";

    public JettyWarTest(String name) {
        super(name);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        //terminate the node if it is not null.
        terminateApplication(application);
    }

    /**
     * Get the deployed application, or null
     *
     * @return application, if deployed
     */
    public Prim getApplication() {
        return application;
    }

    public void setApplication(Prim application) {
        this.application = application;
    }


    protected void deployApplication(String resource, String name) throws
            Throwable {
        setApplication(deployExpectingSuccess(resource, name));
        Liveness liveness = getApplication();
        for(int i=0;i<100;i++) {
            liveness.sfPing(null);
        }
    }

    public void testWarDeployed() throws Throwable {
        deployApplication(FILE_BASE+"jetty-does-war.sf",
                "JettyDoesWar");
    }

    public void testNestedWar() throws Throwable {
        deployApplication(FILE_BASE + "nested-war.sf",
                "nested-war");
    }
}
