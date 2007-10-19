/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.services.www.cargo.test.system;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.www.LivenessPage;
import org.smartfrog.services.www.cargo.CargoServer;

import java.rmi.RemoteException;

/**

 */
public abstract class CargoTestBase extends SmartFrogTestBase {


    /** Node of any deployed application */
    private Prim application;
    private CargoServer server;



    /** location for files. {@value} */
    public static final String FILE_BASE = "/org/smartfrog/services/www/cargo/test/system/";

    public static final String CODEBASE_PROPERTY = "org.smartfrog.codebase";
    public static final int TIMEOUT_FOR_STARTUP = 30;
    private LivenessPage happyPage;


    public CargoTestBase(String name) {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() throws Exception {
        super.setUp();
        //assertSystemPropertySet(CODEBASE_PROPERTY);
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

    public CargoServer getServer() {
        return server;
    }

    protected void deployAppServer(String resource,String name) throws
            Throwable {
        setApplication(deployExpectingSuccess(resource, name));
        Prim serverAsPrim = application.sfResolve("server", (Prim) null, true);
        server=(CargoServer) serverAsPrim;
        happyPage = (LivenessPage) application.sfResolve("liveness", (Prim) null, true);
        //allow a bit of a startup timeout here
        long timeout = System.currentTimeMillis() + TIMEOUT_FOR_STARTUP * 1000;
        try {
            do {
                serverAsPrim.sfPing(null);
            } while (System.currentTimeMillis() < timeout);
        } catch (java.rmi.NoSuchObjectException terminated) {
            //we get here if the thing terminated during the run
            throw terminated;
        }
        checkWebSite();
    }

    protected void checkWebSite() throws SmartFrogException, RemoteException{
        happyPage.checkPage();
    }
}
