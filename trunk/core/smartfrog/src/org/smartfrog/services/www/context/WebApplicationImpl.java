package org.smartfrog.services.www.context;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.RemoteException;

/**
 */
public class WebApplicationImpl extends ApplicationServerContextImpl {

    public WebApplicationImpl() throws RemoteException {
    }

    /**
     * undeploy us if bound, do nothing if not. the context handle is reset,
     * so we no longer consider ourselves bound
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public void undeploy() throws SmartFrogException, RemoteException {
        super.undeploy();
    }

    public void start() throws SmartFrogException, RemoteException {
        super.start();
    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {
        super.ping();
    }
}
