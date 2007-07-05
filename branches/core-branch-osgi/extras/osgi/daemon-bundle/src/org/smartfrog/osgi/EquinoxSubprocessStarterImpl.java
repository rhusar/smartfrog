package org.smartfrog.osgi;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.AbstractSubprocessStarter;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.osgi.framework.BundleContext;

import java.util.List;
import java.net.Socket;
import java.net.URL;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class EquinoxSubprocessStarterImpl extends AbstractSubprocessStarter {
        
    private static final String SMARTFROG_EXT_BUNDLE_LOCATION = "smartFrogExtensionBundleLocation";    
    private static final String EQUINOX_CONSOLE_PORT = "equinoxConsolePort";
    private static final String EQUINOX_CONFIGURATION_AREA = "equinoxConfigurationArea";
    private static final String DS_BUNDLE_LOCATION = "declarativeServicesBundleLocation";
    private static final String SERVICES_BUNDLE_LOCATION = "servicesBundleLocation";
    private static final String LOG_BUNDLE_LOCATION = "logBundleLocation";

    private int consolePort = 0;
    private String smartFrogBundleLocation = null;
    private String smartFrogExtBundleLocation = null;
    private String servicesBundleLocation = null;
    private String logBundleLocation = null;
    private String dsBundleLocation = null;
    // In milliseconds.
    private static final int STARTUP_TIMEOUT = 1000;


    protected void addParameters(ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd) throws Exception {
        consolePort = Integer.parseInt(getSystemProperty(EQUINOX_CONSOLE_PORT));
        initRequiredBundlesLocations(parentProcess);

        addProcessAttributes(runCmd, name, cd);
        // This is supposed to be done by addProcessAttributes, but it adds nothings, whereas only this makes things work.
        runCmd.add("-D"+ SmartFrogCoreProperty.sfProcessName+"="+name);

        // Equinox startup options:
        // http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html
        runCmd.add("-jar");
        runCmd.add(getEquinoxBundleLocation());
        runCmd.add("-console");
        runCmd.add(String.valueOf(consolePort));
        runCmd.add("-configuration");
        runCmd.add(getConfigurationArea(name));
    }

    private String getConfigurationArea(String name) throws IOException {
        String configArea = getSystemProperty(EQUINOX_CONFIGURATION_AREA);
        if (configArea == null) configArea = createTempDir(name);
        return configArea;
    }

    private void initRequiredBundlesLocations(ProcessCompound parentProcess) throws Exception {
        smartFrogBundleLocation = getSmartFrogBundleLocation(parentProcess);

        // TODO: Use the OSGi Bundle Repository for those
        servicesBundleLocation = getSystemProperty(SERVICES_BUNDLE_LOCATION);
        logBundleLocation = getSystemProperty(LOG_BUNDLE_LOCATION);
        dsBundleLocation = getSystemProperty(DS_BUNDLE_LOCATION);
        smartFrogExtBundleLocation = getSystemProperty(SMARTFROG_EXT_BUNDLE_LOCATION);
    }

    private String getSystemProperty(final String property) {
        return System.getProperty(SmartFrogCoreProperty.propBaseSFProcess + property);
    }

    private String getEquinoxBundleLocation() throws Exception {
        String bundleURL = System.getProperty("osgi.framework");
        return new URL(bundleURL).getFile();
    }

    private String getSmartFrogBundleLocation(ProcessCompound parentProcess) throws Exception {
        return getDaemonBundleContext(parentProcess).getBundle().getLocation();
    }

    private BundleContext getDaemonBundleContext(ProcessCompound parentProcess) throws Exception {
        return (BundleContext) parentProcess.sfResolveHere(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT);
    }

    private String createTempDir(String name) throws IOException {
        File tempFile = File.createTempFile("smartfrog-" + name + "-equinox-config", null);
        if (!tempFile.delete()) throw new IOException("Could not delete temporary file");
        if (!tempFile.mkdir()) throw new IOException("Could not create temporary directory");
        return tempFile.getAbsolutePath();
    }
    
    protected void doPostStartupSteps() throws IOException, InterruptedException {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            // So that Equinox has enough time to start... Ideally we'd like to have a notification instead
            Thread.sleep(STARTUP_TIMEOUT);

            socket = new Socket("localhost", consolePort);
            writer = new PrintWriter(socket.getOutputStream());

            writer.println("install " + servicesBundleLocation);
            writer.println("start 1");
            writer.println("install " + logBundleLocation);
            writer.println("start 2");
            writer.println("install " + dsBundleLocation);
            writer.println("start 3");
            writer.println("install " + smartFrogExtBundleLocation);
            // Extension bundles cannot be started
            writer.println("install " + smartFrogBundleLocation);
            writer.println("start 5");
        } finally {
            if (writer != null) writer.close();
            if (socket != null) socket.close();            
        }
    }
}
