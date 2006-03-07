package org.smartfrog.services.services.www.jetty.test.system;

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

    public JettyWarTest(String name, Prim application) {
        super(name);
        this.application = application;
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
    }

    public void testWarDeployed() throws Throwable {
        deployApplication("/org/smartfrog/services/services/www/jetty/test/system/jetty-does-war.sf",
                "JettyDoesWar");
        Liveness liveness=(Liveness)getApplication();
        liveness.sfPing(this);
        liveness.sfPing(this);
    }
}
