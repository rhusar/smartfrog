package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ComponentFactory extends Remote {
    /**
     * Creates the component instance from a given description. It is not mandatory that the given description
     * be that of the whole component: it can be a sub-attribute of the component description for example.
     * This is left for implementations to decide.
     *
     * This method throws loads of exceptions so that implementations don't have to do exception wrapping themselves.
     * We probably want to change that.
     *
     * @param askedFor The ComponentDescription to work off.
     * @return The newly created component instance.
     * @throws RemoteException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SmartFrogResolutionException
     * @throws SmartFrogDeploymentException
     */
    Prim getComponent(ComponentDescription askedFor) throws RemoteException,
            ClassNotFoundException, InstantiationException, IllegalAccessException, SmartFrogResolutionException, SmartFrogDeploymentException;
}
