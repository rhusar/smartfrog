package org.smartfrog.osgi;

import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Bundle;

import java.rmi.RemoteException;

public class OSGiBundleCompound extends CompoundImpl implements Compound {
    private static final String BUNDLE_URL = "bundleURL";
    private Bundle childBundle = null;

    public OSGiBundleCompound() throws RemoteException {}

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        BundleContext daemonBundleContext = (BundleContext) sfResolve(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT);
        String bundleURL = (String) sfResolve(BUNDLE_URL);

        try {
            childBundle = daemonBundleContext.installBundle(bundleURL);
        } catch(BundleException e) {
            throw new SmartFrogException("Bundle installation from URL : "
                    + bundleURL
                    + " failed", e);
        }
    }


    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        try {
            childBundle.uninstall();
        } catch (BundleException e) {
            sfLog().error("Failed to uninstall child bundle", new SmartFrogException(e), status);
        }
    }
}
