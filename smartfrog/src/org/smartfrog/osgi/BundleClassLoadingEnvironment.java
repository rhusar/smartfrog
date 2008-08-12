package org.smartfrog.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.deployer.AbstractClassLoadingEnvironment;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;


public class BundleClassLoadingEnvironment extends AbstractClassLoadingEnvironment {
    public static final String LOCATION_ATTRIBUTE = "location";
    private Bundle hostBundle = null;
    private ClassLoader bundleClassLoaderProxy = null;


  /**
     * @throws RemoteException In case of network/rmi error
     */
    public BundleClassLoadingEnvironment() throws RemoteException {
    }

    protected synchronized void doSfDeploy() throws SmartFrogException, RemoteException {

        BundleContext daemonBundleContext = OSGiUtilities.getDaemonBundleContext(this);
        String location = (String) sfResolve(LOCATION_ATTRIBUTE);

        try {
            hostBundle = daemonBundleContext.installBundle(location);
        } catch (BundleException e) {
            throw SmartFrogException.forward(
                "Error when installing bundle from location: " + location, e);
        }

        bundleClassLoaderProxy = new BundleClassLoaderProxy(hostBundle);
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        if (OSGiUtilities.isNotFragment(hostBundle)) {
            try {
                hostBundle.start();
            } catch (BundleException e) {
                SmartFrogException.forward("Error when starting bundle", e);
            }
        }
    }

    protected synchronized void sfTerminateWith(TerminationRecord status) {
        try {
            hostBundle.uninstall();
        } catch (BundleException e) {
            sfLog().error("Error when uninstalling bundle", e);
        }

        super.sfTerminateWith(status);
    }

    /**
     * Returns a wrapper class loader that delagates to this bundle.
     * @return
     */
    public synchronized ClassLoader getClassLoader() {
        if (bundleClassLoaderProxy == null)
            throw new IllegalStateException("The class loader is not available, this environment has not been deployed yet");
        return bundleClassLoaderProxy;
    }

}
