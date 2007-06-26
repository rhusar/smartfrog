package org.smartfrog.osgi;

import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.Reference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Bundle;

import java.net.URL;
import java.rmi.RemoteException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;

public class BundleClassLoadingEnvironmentImpl extends PrimImpl implements ClassLoadingEnvironment {
    public static final String LOCATION_ATTRIBUTE = "location";

    private Bundle bundle;

    public BundleClassLoadingEnvironmentImpl() throws RemoteException {
    }

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        BundleContext daemonBundleContext = (BundleContext) sfResolve(new Reference(
                ReferencePart.attrib(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT)
        ));
        String location = (String) sfResolve(LOCATION_ATTRIBUTE);

        try {
            bundle = daemonBundleContext.installBundle(location);
        } catch (BundleException e) {
            throw SmartFrogException.forward
                    ("Error when installing bundle from location: " + location, e);
        }
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        try {
            bundle.start();
        } catch (BundleException e) {
            SmartFrogException.forward("Error when starting bundle", e);
        }
    }

    public void sfTerminate(TerminationRecord status) {
        try {
            bundle.uninstall();
        } catch (BundleException e) {
            sfLog().error("Error when uninstalling bundle", e);
        }

        super.sfTerminate(status);
    }

    public URL getResource(String location) throws RemoteException {
        return bundle.getResource(location);
    }

    public InputStream getResourceAsStream(String location) throws RemoteException {
        try {
            return getResource(location).openStream();
        } catch (IOException e) {
            return null; // Same as in java.lang.ClassLoader to be consistent
        }
    }

    public Prim getComponent(ComponentDescription askedFor) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SmartFrogResolutionException
    {
        String sfClass = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
        Class primClass = bundle.loadClass(sfClass);
        return (Prim) primClass.newInstance();
    }


    public Enumeration getResources(String location) throws IOException {
        return bundle.getResources(location);
    }
}
