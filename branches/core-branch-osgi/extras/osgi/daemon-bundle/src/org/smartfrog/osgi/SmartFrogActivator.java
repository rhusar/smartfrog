package org.smartfrog.osgi;

import org.osgi.framework.*;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.smartfrog.SFSystem;
import org.smartfrog.osgi.logging.LogServiceProxy;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.ShutdownHandler;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Starts the SmartFrog daemon with options appropriate to running inside OSGi.
 *
 * The protected methods are called reflectively by Declarative Services. 
 */
public class SmartFrogActivator {
    private ProcessCompound processCompound = null;
    private boolean alreadyStopping = false;
    private final Object lock = new Object();

    private static LogServiceProxy logService = new LogServiceProxy();


    /** @noinspection FeatureEnvy*/
    protected void activate(final ComponentContext componentContext) throws Exception {
        logService.info("Starting smartfrog...");

        loadProperties();

        Thread startDaemon = new Thread(new Runnable() {
            /** @noinspection FeatureEnvy*/
            public void run() {
                final BundleContext bundleContext = componentContext.getBundleContext();
                final Bundle bundle = bundleContext.getBundle();

                try {
                    processCompound = SFSystem.runSmartFrog();
                    configureProcessCompound(bundleContext);

                    logService.info("SmartFrog daemon running...");
                } catch (Exception e) {
                    logService.error("Error during daemon startup", e);
                    try {
                        // Normally not needed, as the termination of the root process
                        // call stop() already. Only useful if startup fails early, before
                        // the root process is completely initialized.
                        bundle.stop();
                    } catch (BundleException e1) {
                        // Fails if the activate method has not returned yet
                        logService.error("Could not stop after startup error", e1);
                    }
                }
            }
        });

        startDaemon.setName("SmartFrog Daemon Startup Thread");
        startDaemon.start();
    }

    /** @noinspection FeatureEnvy*/
    private void configureProcessCompound(BundleContext bundleContext) throws RemoteException, SmartFrogRuntimeException {
        // Danger: If runSmartFrog() fails after the processCompound
        // has been created, it might call System.exit() as the shutdown handler
        // has not been replaced yet.
        if (processCompound.sfIsRoot())
            processCompound.replaceShutdownHandler(new ShutdownHandlerOSGi(bundleContext.getBundle()));
        // If we're a subprocess, this is a throwaway OSGi framework anyway, so calling System.exit is OK.

        addBundleContextAttribute(bundleContext);
        processCompound.replaceSubprocessStarter(new EquinoxSubprocessStarterImpl());
    }

    private void addBundleContextAttribute(BundleContext bundleContext) throws SmartFrogRuntimeException, RemoteException {
        processCompound.sfAddAttribute(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT, new BundleContextWrapper(bundleContext));
    }

    private void loadProperties() throws IOException {
        // We need to keep the sfProcessName if it was passed by a -D flag
        // (which means we're being started as a subprocess)
        String sfProcessName = System.getProperty(SmartFrogCoreProperty.sfProcessName);

        System.getProperties().load(
                getClass().getResourceAsStream("system.properties")
        );
        if (sfProcessName != null)
            System.setProperty(SmartFrogCoreProperty.sfProcessName, sfProcessName);
    }

    protected void setLog(LogService log) {
        logService.setLog(log);
    }

    protected void unsetLog(LogService log) {
        logService.unsetLog(log);
    }

    public static LogService getLogServiceProxy() {
        return logService;
    }

    protected void deactivate(final ComponentContext componentContext) throws Exception {
        synchronized (lock) {
            if (!alreadyStopping) {
                alreadyStopping = true;
                logService.info("Stopping SmartFrog...");

                processCompound.sfTerminate(new TerminationRecord("normal", "Stopping daemon", null));
                logService.info("SmartFrog daemon stopped.");

                processCompound = null; // Triggers garbage collection, hopefully
            }
        }
    }

    private class ShutdownHandlerOSGi implements ShutdownHandler {
        private Bundle bundle;

        private ShutdownHandlerOSGi(Bundle bundle) { this.bundle = bundle; }

        public void shutdown(ProcessCompound processCompound) {
            try {
                synchronized (lock) {
                    if (!alreadyStopping) bundle.stop();
                }
                logService.info("Bundle stopped.");
            } catch (Exception e) {
                logService.error("Could not stop bundle", e);
            }
        }
    }

}
