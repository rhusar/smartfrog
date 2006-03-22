/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.www.jetty.test.system;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.SmartFrogTestBase;

/**
 */
public abstract class WebappTestBase extends SmartFrogTestBase {
    /**
     * Node of any deployed application
     */
    protected Prim application;

    protected WebappTestBase(String name) {
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

    public void deployWebApp(String resource, String name) throws
            Throwable {
        setApplication(deployExpectingSuccess(resource, name));
        Prim liveness = (Prim)getApplication().sfResolve("ping");
        for (int i = 0; i < 100; i++) {
            liveness.sfPing(null);
        }
    }
}
