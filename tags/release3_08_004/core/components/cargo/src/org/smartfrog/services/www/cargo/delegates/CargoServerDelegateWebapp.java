package org.smartfrog.services.www.cargo.delegates;

import org.smartfrog.services.www.ApplicationServerContext;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 */
public class CargoServerDelegateWebapp implements JavaWebApplication {
    public CargoServerDelegateWebapp(Prim webApplication) {

    }


    /**
     * start the component
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void start() throws SmartFrogException, RemoteException {

    }

    /**
     * this method is here for server-specific implementation classes,
     *
     * @throws RemoteException
     * @throws SmartFrogException
     */
    public void undeploy() throws RemoteException, SmartFrogException {

    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {

    }
}
