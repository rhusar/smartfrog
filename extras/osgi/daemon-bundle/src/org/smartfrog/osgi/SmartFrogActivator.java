package org.smartfrog.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import static org.osgi.service.log.LogService.LOG_DEBUG;
import static org.osgi.service.log.LogService.LOG_INFO;
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.ExitCodes;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

public class SmartFrogActivator implements BundleActivator {
    private ProcessCompound rootProcess = null;
    private LogService logService = null;

    public void start(BundleContext bundleContext) throws Exception {
        getLogService(bundleContext);

        info("Starting smartfrog...");

        System.setProperty("org.smartfrog.sfcore.processcompound.sfProcessName", "rootProcess");
        System.setProperty("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault",
                "org/smartfrog/default.sf");
        System.setProperty("org.smartfrog.iniFile",
                "org/smartfrog/default.ini");

        // Uncommenting this triggers an infinite recursion on SecurityManager.checkPermission
        // when run in an OSGi framework.
        // System.setProperty("org.smartfrog.sfcore.security.debug","true");

        System.setProperty("java.security.debug", "scl");
        System.setProperty("sun.rmi.dgc.logLevel", "VERBOSE");
        System.setProperty("sun.rmi.transport.logLevel", "VERBOSE");
        System.setProperty("sun.rmi.transport.logLevel", "VERBOSE");
        System.setProperty("java.rmi.server.logCalls", "true");
        System.setProperty("sun.rmi.loader.logLevel", "VERBOSE");
        System.setProperty("sun.rmi.server.exceptionTrace", "true");

        debug("Current thread context CL: " + Thread.currentThread().getContextClassLoader());
        debug("System CL " + ClassLoader.getSystemClassLoader());
        ClassLoader bundleCL = getClass().getClassLoader();
        printClassLoaderHierarchy(bundleCL);

// Not needed in Equinox as it deals with this nicely :
// http://wiki.eclipse.org/index.php/Context_Class_Loader_Enhancements
// Needed in Knopflerfish, not checked in Felix
//
//        debug("Setting the thread context CL to bundle's CL.");
//
//        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(bundleCL);

        rootProcess = SFSystem.runSmartFrog();
        rootProcess.sfAddAttribute(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT, bundleContext);

        info("SmartFrog daemon running...");
        releaseLogService(bundleContext);

//        Thread.currentThread().setContextClassLoader(oldClassLoader);
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

    public void stop(BundleContext bundleContext) throws Exception {
        getLogService(bundleContext);
        info("Stopping smartfrog...");

        // TODO: Find a nicer way to change shutdown behaviour.
        ExitCodes.exitJVM = false;
        rootProcess.sfTerminate(new TerminationRecord("normal", "Stopping daemon", null));
        SFSystem.cleanShutdown();
        rootProcess = null; // Triggers garbage collection
        
        info("SmartFrog daemon stopped.");
        // Services are released automatically by the OSGi framework
    }

    private void debug(final String message) {
        if (logService != null)
            logService.log(LOG_DEBUG, message);
    }

    private void info(final String message) {
        if (logService != null)
            logService.log(LOG_INFO, message);
    }

    private ServiceReference logServiceReference(BundleContext bundleContext) {
        return bundleContext.getServiceReference("org.osgi.service.log.LogService");
    }

    private void getLogService(BundleContext bundleContext) {
        ServiceReference logServiceReference = logServiceReference(bundleContext);
        if (logServiceReference != null)
            logService = (LogService) bundleContext.getService(logServiceReference);
    }

    private void releaseLogService(BundleContext bundleContext) {
        if (logService != null) {
            bundleContext.ungetService(logServiceReference(bundleContext));
            logService = null;
        }
    }
}
