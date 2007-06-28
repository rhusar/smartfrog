package org.smartfrog.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

import java.rmi.RemoteException;
import java.util.Dictionary;

public class OSGiUtilities {

    private OSGiUtilities() {}


    static BundleContext getDaemonBundleContext(RemoteReferenceResolver resolver) throws SmartFrogResolutionException, RemoteException {
        return (BundleContext) resolver.sfResolve(
                Reference.fromString("PROCESS:" + SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT)
        );
    }

    static boolean isNotFragment(Bundle bundle) {
        Dictionary headers = bundle.getHeaders();
        return headers.get("Fragment-Host") == null;
    }
}
