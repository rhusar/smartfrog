package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * The component factory that should be used for framework components.
 * Those come from the same classloader as this class.
 */
public class CoreClassesClassLoadingEnvironment extends AbstractClassLoadingEnvironment {


  /**
     * @throws RemoteException In case of network/rmi error
     */
    public CoreClassesClassLoadingEnvironment() {
    }

    protected void doSfDeploy() throws SmartFrogException, RemoteException {        }

    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }
}
