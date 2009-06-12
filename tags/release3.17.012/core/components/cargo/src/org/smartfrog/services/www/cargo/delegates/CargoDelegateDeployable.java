package org.smartfrog.services.www.cargo.delegates;

import org.codehaus.cargo.container.deployable.Deployable;
import org.smartfrog.services.www.JavaWebApplication;
import org.smartfrog.services.www.cargo.CargoServerImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * The cargo delegate web application.
 * <p/>
 * One problem with cargo is that it can only do cold deployment; deployment to
 * a running server is out of scope.
 * <p/>
 * this has forced a bit of a rethink on the lifecycle of the parent
 */
public abstract class CargoDelegateDeployable implements JavaWebApplication {

    private CargoServerImpl owner;
    private Prim application;
    private Deployable deployable;

    public CargoServerImpl getOwner() {
        return owner;
    }

    public Prim getApplication() {
        return application;
    }

    public CargoDelegateDeployable(Prim webApplication, CargoServerImpl owner) {
        this.application = webApplication;
        this.setOwner(owner);
    }

    public Deployable getDeployable() {
        return deployable;
    }

    public void setDeployable(Deployable deployable) {
        this.deployable = deployable;
    }

    /**
     * Save our deployable and queue it on the server for deployment
     * @param webapp
     */
    public void queue(Deployable webapp) {
        setDeployable(deployable);
        getOwner().addDeployable(webapp);
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
    public void terminate() throws RemoteException, SmartFrogException {

    }

    /**
     * liveness check
     *
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    public void ping() throws SmartFrogLivenessException, RemoteException {

    }

    public void setOwner(CargoServerImpl owner) {
        this.owner = owner;
    }



}
