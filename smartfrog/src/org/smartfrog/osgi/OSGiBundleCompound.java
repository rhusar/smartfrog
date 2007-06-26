package org.smartfrog.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Enumeration;

/**
 * @deprecated Use BundleClassLoadingEnvironmentImpl instead.
 */
public class OSGiBundleCompound extends CompoundImpl implements Compound {
    private static final String BUNDLE_URL = "bundleURL";
    private Bundle childBundle = null;

    public OSGiBundleCompound() throws RemoteException {}

    protected synchronized void sfDeployWithChildren() throws SmartFrogDeploymentException {
        LogSF log = sfLog();
        log.debug("Deploying OSGiBundleCompound...");

        String bundleURL;
        BundleContext daemonBundleContext;
        try {
            daemonBundleContext = (BundleContext) sfResolve(new Reference(
                    ReferencePart.attrib(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT)
            ));
            bundleURL = (String) sfResolve(BUNDLE_URL);
        } catch (SmartFrogResolutionException e) {
            throw new SmartFrogDeploymentException(e, this);
        } catch (RemoteException e) {
            throw new SmartFrogDeploymentException(e, this);
        }

        if (log.isDebugEnabled())
            log.debug("Trying to install bundle from URL : "
                    + bundleURL
                    + ". BundleContext for daemon bundle :"
                    + daemonBundleContext);
        try {
            childBundle = daemonBundleContext.installBundle(bundleURL);
            childBundle.start();
            logBundleDetails(log, bundleURL);
        } catch (BundleException e) {

            if (childBundle != null) {
                try {
                    childBundle.uninstall();
                    log.info("Bundle from URL :"
                            + bundleURL
                            + " has been uninstalled because it failed to start properly.");
                } catch (BundleException e2) {
                    log.error("Bundle installed, failed to start but could not be uninstalled.", e2);
                }
            }

            throw new SmartFrogDeploymentException("Bundle installation from URL : "
                    + bundleURL
                    + " failed", e);
        }

        log.debug("Calling parent sfDeployWithChildren to deploy children.");
        log.debug("T.cT().getContextClassLoader() = " + Thread.currentThread().getContextClassLoader());
        log.debug("getClass().getClassLoader() = " + getClass().getClassLoader());
        log.debug("getClass() = " + getClass());

        super.sfDeployWithChildren();

        log.debug("OSGiBundleCompound deployed.");
    }

    /*
     * TODO: Use Declarative Services for log service
     */
    private void logBundleDetails(LogSF log, String bundleURL) {
        if (log.isDebugEnabled()) {
            log.debug("Bundle ID : " + childBundle.getBundleId());
            log.debug("Bundle Headers :");
            Dictionary headers = childBundle.getHeaders();
            Enumeration e = headers.keys();
            while (e.hasMoreElements()) {
                Object key = e.nextElement();
                log.debug(key + " = " + headers.get(key));
            }
        }
        log.info("Bundle from URL : " + bundleURL + " installed properly.");
        log.debug("Bundle state code : " + childBundle.getState());
    }


    /**
     * Performs the compound termination behaviour. Based on sfSyncTerminate
     * flag this gets forwarded to sfSyncTerminate or sfASyncTerminateWith
     * method. Terminates children before self.
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);

        try {
            childBundle.uninstall();
        } catch (BundleException e) {
            sfLog().error("Failed to uninstall child bundle", new SmartFrogException(e), status);
        }
    }

}
