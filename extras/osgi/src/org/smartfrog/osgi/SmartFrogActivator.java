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
        ServiceReference logServiceReference = bundleContext.getServiceReference("org.osgi.service.log.LogService");
        // TODO: Handle missing LogService gracefully
        if (logServiceReference != null)
            logService = (LogService) bundleContext.getService(logServiceReference);

        logService.log(LOG_INFO, "Starting smartfrog...");

        System.setProperty("org.smartfrog.sfcore.processcompound.sfProcessName","rootProcess");
        System.setProperty("org.smartfrog.sfcore.processcompound.sfDefault.sfDefault",
                "org/smartfrog/default.sf");
        System.setProperty("org.smartfrog.iniFile",
                "org/smartfrog/default.ini");
        //System.setProperty("org.smartfrog.sfcore.security.debug","true");
        //System.setProperty("java.security.debug","scl");
        //System.setProperty("java.rmi.server.logCalls","true");
        //System.setProperty("sun.rmi.loader.logLevel","VERBOSE");

        logService.log(LOG_DEBUG, "Current thread context CL: " + Thread.currentThread().getContextClassLoader());
        logService.log(LOG_DEBUG, "System CL " + ClassLoader.getSystemClassLoader());
        ClassLoader bundleCL = this.getClass().getClassLoader();
        logService.log(LOG_DEBUG, "This bundle's CL: " + bundleCL);
        logService.log(LOG_DEBUG, "Parent: " + bundleCL.getParent());
        logService.log(LOG_DEBUG, "Parent of parent: " + bundleCL.getParent().getParent());
        logService.log(LOG_DEBUG, "Parent of parent of parent: " + bundleCL.getParent().getParent().getParent());
        logService.log(LOG_DEBUG, "Setting the thread context CL to bundle's CL.");

        Thread.currentThread().setContextClassLoader(bundleCL);

        rootProcess = SFSystem.runSmartFrog();
        rootProcess.sfAddAttribute(SmartFrogCoreKeys.SF_BUNDLE_CONTEXT, bundleContext);

        logService.log(LOG_INFO, "SmartFrog daemon running...");

        System.out.println("SmartFrog daemon running...");
    }

    public void stop(BundleContext bundleContext) throws Exception {
        logService.log(LOG_INFO, "Stopping smartfrog...");

        // TODO: Find a nicer way to change shutdown behaviour.
        ExitCodes.exitJVM = false;
        rootProcess.sfTerminate(new TerminationRecord("normal", "Stopping daemon", null));

        logService.log(LOG_INFO, "SmartFrog daemon stopped.");
    }
}
