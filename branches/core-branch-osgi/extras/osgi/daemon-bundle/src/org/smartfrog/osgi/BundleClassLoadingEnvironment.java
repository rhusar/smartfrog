package org.smartfrog.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.AbstractClassLoadingEnvironment;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.security.SFSecurity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;


public class BundleClassLoadingEnvironment extends AbstractClassLoadingEnvironment {
    public static final String LOCATION_ATTRIBUTE = "location";
    private Bundle hostBundle = null;
    private ClassLoader bundleClassLoaderProxy = null;

    public BundleClassLoadingEnvironment() throws RemoteException {}

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        BundleContext daemonBundleContext = OSGiUtilities.getDaemonBundleContext(this);
        String location = (String) sfResolve(LOCATION_ATTRIBUTE);

        try {
            hostBundle = daemonBundleContext.installBundle(location);
        } catch (BundleException e) {
            throw SmartFrogException.forward(
                "Error when installing bundle from location: " + location, e);
        }

        bundleClassLoaderProxy = new ClassLoader() {
            public Class loadClass(String name) throws ClassNotFoundException {
                return hostBundle.loadClass(name);
            }
        };
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

    protected void sfTerminateWith(TerminationRecord status) {
        try {
            hostBundle.uninstall();
        } catch (BundleException e) {
            sfLog().error("Error when uninstalling bundle", e);
        }

        super.sfTerminate(status);
    }

    protected Prim getComponentImpl(ComponentDescription askedFor)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            SmartFrogResolutionException
    {
        String sfClass = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
        return (Prim) newInstance(sfClass);
    }

    protected Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class clazz = hostBundle.loadClass(className);
        SFSecurity.checkSecurity(clazz);
        return clazz.newInstance();
    }

    public InputStream getResourceAsStream(String pathname) {
        URL url = hostBundle.getResource(pathname);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
            return null;
        }
    }

    protected URL getResource(String pathname) {
        return hostBundle.getResource(pathname);
    }

    /**
     * Returns a crippled class loader, that does only one thing properly : load classes from the user bundle.
     * Thus the only functional method is loadClass().
     * @return
     */
    public ClassLoader getClassLoader() {
        return bundleClassLoaderProxy;
    }
}
