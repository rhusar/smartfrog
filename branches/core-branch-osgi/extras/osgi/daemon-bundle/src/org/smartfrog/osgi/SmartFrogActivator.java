package org.smartfrog.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.security.SFSynchronousUserBundleListener;

public class SmartFrogActivator {
    private ProcessCompound rootProcess = null;
    private LogServiceProxy logService = new LogServiceProxy();
    private boolean alreadyStopping;

    public synchronized void activate(final ComponentContext componentContext) throws Exception {

        logService.info("Starting smartfrog...");

        System.getProperties().load(
                getClass().getResourceAsStream("system.properties")
        );

        Thread startDaemon = new Thread(new Runnable() {
            public void run() {
                final BundleContext bundleContext = componentContext.getBundleContext();
                final Bundle bundle = bundleContext.getBundle();

                try {
                    rootProcess = SFSystem.runSmartFrog();
                    rootProcess.replaceShutdownHook(new Runnable() {
                        public void run() {
                            try {
                                bundle.stop();
                                logService.info("Bundle stopped.");
                            } catch (Exception e) {
                                logService.error("Could not stop bundle", e);
                            }
                        }
                    });
                    rootProcess.sfAddAttribute(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT, bundleContext);

                    bundleContext.addBundleListener(new SFSynchronousUserBundleListener(bundleContext));

                    logService.info("SmartFrog daemon running...");
                } catch (Exception e) {
                    logService.error("Error during daemon startup", e);
                    try {
                        bundle.stop();
                    } catch (BundleException e1) {
                        // Fails if the activate method has not returned yet
                        logService.error("Could not stop after startup error", e1);
                    }
                }
            }
        });
        
        ClassLoader bundleCL = getClass().getClassLoader();
        printClassLoaderDebug(bundleCL);
        startDaemon.setContextClassLoader(bundleCL);
        startDaemon.start();
    }

    public synchronized void deactivate(final ComponentContext componentContext) throws Exception {
        if (!alreadyStopping) {
            alreadyStopping = true;
            logService.info("Stopping smartfrog...");

            rootProcess.sfTerminate(new TerminationRecord("normal", "Stopping daemon", null));
            rootProcess = null; // Triggers garbage collection
            logService.info("SmartFrog daemon stopped.");
        }
    }

    private void printClassLoaderDebug(ClassLoader bundleCL) {
        logService.debug("Current thread context CL: " + Thread.currentThread().getContextClassLoader());
        logService.debug("System CL " + ClassLoader.getSystemClassLoader());
        printClassLoaderHierarchy(bundleCL);
        logService.debug("Setting the startup thread context CL to the bundle's CL.");
    }

    private void printClassLoaderHierarchy(final ClassLoader bundleCL) {
        ClassLoader curr = bundleCL;
        int depth = 0;
        while (curr != null) {
            logService.debug("Classloader of depth " + depth + " : " + curr);
            depth++;
            curr = curr.getParent();
        }
    }

    public void setLog(LogService log) {
        logService.setLog(log);
    }

    public void unsetLog(LogService log) {
        logService.unsetLog(log);
    }

}
