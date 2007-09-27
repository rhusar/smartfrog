/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.jetty;

import org.mortbay.component.LifeCycle;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 *
 * Something to bridge ping and stop operations to jetty
 * Created 27-Sep-2007 16:25:34
 *
 */

public class JettyToSFLifecycle implements Liveness {

    private String name;
    private LifeCycle jettyComponent;
    /** Error string raised in liveness checks. {@value} */
    public static final String LIVENESS_ERROR_NOT_STARTED = " is not active";
    public static final String LIVENESS_ERROR_NOT_RUNNING = "is not running";
    public static final String LIVENESS_ERROR_FAILED = " has failed";

    public JettyToSFLifecycle(String name, LifeCycle jettyComponent) {
        this.name = name;
        this.jettyComponent = jettyComponent;
    }

    /**
     * liveness test verifies the server is started
     *
     * @param source caller
     * @throws SmartFrogLivenessException the server is  not started
     * @throws RemoteException            network trouble
     */
    public synchronized void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        if (jettyComponent == null) {
            throw new SmartFrogLivenessException(name+LIVENESS_ERROR_NOT_STARTED);
        }
        if (jettyComponent.isFailed()) {
            throw new SmartFrogLivenessException(name +LIVENESS_ERROR_FAILED);
        }
        if (!jettyComponent.isRunning()) {
            throw new SmartFrogLivenessException(name +LIVENESS_ERROR_NOT_RUNNING);
        }
    }

    public synchronized void stop() throws Exception {
        if(jettyComponent!=null) {
            jettyComponent.stop();
            jettyComponent=null;
        }
    }

    public synchronized void start() throws SmartFrogException {
        try {
            if(jettyComponent!=null) {
                jettyComponent.start();
            }
        } catch (Exception e) {
            throw SmartFrogException.forward(e);
        }
    }

}
