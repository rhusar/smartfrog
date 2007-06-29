package org.smartfrog.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;


public class BundleClassLoadingEnvironmentImpl extends AbstractClassLoadingEnvironment {
    public static final String LOCATION_ATTRIBUTE = "location";
    private Bundle hostBundle = null;

    public BundleClassLoadingEnvironmentImpl() throws RemoteException {}

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

    public void sfTerminateWith(TerminationRecord status) {
        try {
            hostBundle.uninstall();
        } catch (BundleException e) {
            sfLog().error("Error when uninstalling bundle", e);
        }

        super.sfTerminate(status);
    }

    public URL getResource(String location) throws RemoteException {
        return hostBundle.getResource(location);
    }

    protected Prim getComponentImpl(ComponentDescription askedFor)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException,
            SmartFrogResolutionException
    {
        String sfClass = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
        Class primClass = hostBundle.loadClass(sfClass);
        return (Prim) primClass.newInstance();
    }

    public Enumeration getResources(String location) throws IOException {
        return hostBundle.getResources(location);
    }
}
