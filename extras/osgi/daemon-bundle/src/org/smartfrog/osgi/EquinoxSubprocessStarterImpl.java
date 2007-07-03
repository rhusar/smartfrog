package org.smartfrog.osgi;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.AbstractSubprocessStarter;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.osgi.framework.BundleContext;

import java.util.List;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

public class EquinoxSubprocessStarterImpl extends AbstractSubprocessStarter {
        
    private static final String SMARTFROG_EXT_BUNDLE_LOCATION = "smartFrogExtensionBundleLocation";
    private static final String EQUINOX_JAR_FILE = "equinoxJarFile";
    private static final String EQUINOX_CONSOLE_PORT = "equinoxConsolePort";
    private static final String EQUINOX_CONFIGURATION_AREA = "equinoxConfigurationArea";
    private static final String DS_BUNDLE_LOCATION = "declarativeServicesBundleLocation";
    private static final String SERVICES_BUNDLE_LOCATION = "servicesBundleLocation";
    private static final String LOG_BUNDLE_LOCATION = "logBundleLocation";
    
    private Integer consolePort;
    private String smartFrogBundleLocation;
    private String smartFrogExtBundleLocation;
    private String servicesBundleLocation;
    private String logBundleLocation;
    private String dsBundleLocation;


    protected void addParameters(ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd) throws Exception {
        // http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html

        consolePort = (Integer) cd.sfResolveHere(EQUINOX_CONSOLE_PORT);
        smartFrogBundleLocation = getSmartFrogBundleLocation(parentProcess);
        smartFrogExtBundleLocation = (String) cd.sfResolveHere(SMARTFROG_EXT_BUNDLE_LOCATION);
        servicesBundleLocation = (String) cd.sfResolveHere(SERVICES_BUNDLE_LOCATION);
        logBundleLocation = (String) cd.sfResolveHere(LOG_BUNDLE_LOCATION);
        dsBundleLocation = (String) cd.sfResolveHere(DS_BUNDLE_LOCATION);

        // TODO: This is supposed to be done by addProcessAttributes, but it adds nothings, whereas only this makes things work.
        runCmd.add("-D"+ SmartFrogCoreProperty.sfProcessName+"="+name);

        runCmd.add("-jar");
        runCmd.add(cd.sfResolveHere(EQUINOX_JAR_FILE).toString());
        runCmd.add("-console");
        runCmd.add(consolePort.toString());
        runCmd.add("-configuration");
        runCmd.add(cd.sfResolveHere(EQUINOX_CONFIGURATION_AREA).toString());        
    }

    private String getSmartFrogBundleLocation(ProcessCompound parentProcess) throws RemoteException, SmartFrogResolutionException {
        return (
                (BundleContext) parentProcess.sfResolveHere(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT)
        ).getBundle().getLocation();
    }

    protected void doPostStartupSteps() throws IOException, InterruptedException {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            // So that Equinox has enough time to start... Ideally we'd like to have a notification instead
            Thread.sleep(1000);

            socket = new Socket("localhost", consolePort.intValue());
            writer = new PrintWriter(socket.getOutputStream());
            writer.println("install " + servicesBundleLocation);
            writer.println("install " + logBundleLocation);
            writer.println("start 2");
            writer.println("install " + dsBundleLocation);
            writer.println("start 3");
            writer.println("install " + smartFrogExtBundleLocation);
            writer.println("install " + smartFrogBundleLocation);
            writer.println("start 5");
        } finally {
            if (writer != null) writer.close();
            if (socket != null) socket.close();            
        }
    }
}
