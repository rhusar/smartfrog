package org.smartfrog.sfcore.security;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

public class SFSynchronousUserBundleListener implements BundleListener {

    public static final String USER_BUNDLE_HEADER = "SmartFrog-Components-Bundle";

    public SFSynchronousUserBundleListener(BundleContext daemonBundleContext) {
        for (int i = 0; i < daemonBundleContext.getBundles().length; i++) {
            Bundle bundle = daemonBundleContext.getBundles()[i];
            if ((bundle.getState() & (Bundle.STARTING | Bundle.ACTIVE)) != 0)
                register(bundle);
        }
    }

    public void bundleChanged(BundleEvent bundleEvent) {
        switch(bundleEvent.getType()) {
            case BundleEvent.STARTED:
                register(bundleEvent.getBundle());
                break;
            case BundleEvent.STOPPED:
                unregister(bundleEvent.getBundle());
                break;
        }
    }

    private void register(Bundle bundle) {
//        if (hasManifestHeader(bundle))
//            // HACK : package-visible field
//            synchronized(SFClassLoader.userBundles) {
//                SFClassLoader.userBundles.add(bundle);
//            }
    }

    private void unregister(Bundle bundle) {
//        if (hasManifestHeader(bundle))
//            // HACK
//            synchronized(SFClassLoader.userBundles) {
//                SFClassLoader.userBundles.remove(bundle);
//            }
    }

    private boolean hasManifestHeader(Bundle bundle) {
        // In Dictionary, null is not a legal value (unlike in Map).
        return bundle.getHeaders().get(USER_BUNDLE_HEADER) != null;
    }
}
