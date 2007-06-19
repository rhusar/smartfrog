package org.smartfrog.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.security.SFSynchronousUserBundleListener;

public class SmartFrogActivator {
    private ProcessCompound rootProcess = null;
    private LogService logService;

    public void activate(ComponentContext componentContext) throws Exception {

        info("Starting smartfrog...");

        System.getProperties().load(
                getClass().getResourceAsStream("system.properties")
        );
        
        debug("Current thread context CL: " + Thread.currentThread().getContextClassLoader());
        debug("System CL " + ClassLoader.getSystemClassLoader());
        ClassLoader bundleCL = getClass().getClassLoader();
        printClassLoaderHierarchy(bundleCL);

// Not needed in Equinox as it deals with this nicely :
// http://wiki.eclipse.org/index.php/Context_Class_Loader_Enhancements
// Needed in Knopflerfish, not checked in Felix
// Seems to be needed even in Eclipse after all : start / uninstall / install / start
// doesn't work without it. 
        debug("Setting the current thread context CL to the bundle's CL.");

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(bundleCL);

        rootProcess = SFSystem.runSmartFrog();
        rootProcess.vmExitOnTermination(false);
        final BundleContext bundleContext = componentContext.getBundleContext();
        rootProcess.sfAddAttribute(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT, bundleContext);

        bundleContext.addBundleListener(new SFSynchronousUserBundleListener(bundleContext));

        info("SmartFrog daemon running...");

        Thread.currentThread().setContextClassLoader(oldClassLoader);
        
    }

    private void printClassLoaderHierarchy(final ClassLoader bundleCL) {
        ClassLoader curr = bundleCL;
        int depth = 0;
        while (curr != null) {
            debug("Classloader of depth " + depth + " : " + curr);
            depth++;
            curr = curr.getParent();
        }
    }

    public void deactivate(ComponentContext componentContext) throws Exception {
        info("Stopping smartfrog...");

        rootProcess.sfTerminate(new TerminationRecord("normal", "Stopping daemon", null));
        rootProcess = null; // Triggers garbage collection
        info("SmartFrog daemon stopped.");
    }

    public void setLog(LogService log) {
        logService = log;
    }

    public void unsetLog(LogService log) {
        logService = null;
    }

    private void debug(final String message) {
        if (logService != null)
            logService.log(LogService.LOG_DEBUG, message);
        else
            System.out.println("DEBUG: " + message);
    }

    private void info(final String message) {
        if (logService != null)
            logService.log(LogService.LOG_INFO, message);
        else
            System.out.println("INFO: " + message);
    }

}
