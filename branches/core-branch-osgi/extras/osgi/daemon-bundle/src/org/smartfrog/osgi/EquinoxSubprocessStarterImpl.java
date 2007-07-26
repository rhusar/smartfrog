package org.smartfrog.osgi;

import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.PlatformAdmin;
import org.eclipse.osgi.service.resolver.State;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.processcompound.AbstractSubprocessStarter;
import org.smartfrog.sfcore.processcompound.ProcessCompound;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EquinoxSubprocessStarterImpl extends AbstractSubprocessStarter {
    private final LogSF log = LogFactory.sfGetProcessLog();
    private static final String EQUINOX_CONSOLE_PORT_START = "baseEquinoxConsolePort";
    private static final String EQUINOX_CONFIGURATION_AREA = "equinoxConfigurationArea";
    // In milliseconds.
    private static final int STARTUP_TIMEOUT = 4000;

    private int consolePort = 0;

    // Used to increment base port so that several subprocesses can be started.
    // Getting a subprocess ID from caller would be nicer
    private static int subprocessNumber = 0;

    private BundleDescription[] toInstall;
    private ProcessCompound parentProcess;
    private ServiceReference platformSR;

    protected void addParameters(ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd) throws Exception {
        this.parentProcess = parentProcess;

        consolePort = Integer.parseInt(getSystemProperty(EQUINOX_CONSOLE_PORT_START)) + subprocessNumber;
        initRequiredBundlesLocations();

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
        
        subprocessNumber++;
    }

    protected void doPostStartupSteps() throws IOException, InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("Installing bundles: " + Arrays.toString(toInstall));
        }

        Socket socket = null;
        PrintWriter writer = null;
        try {
            // So that Equinox has enough time to start... Ideally we'd like to have a notification instead
            Thread.sleep(STARTUP_TIMEOUT);

            if (log.isDebugEnabled())
                log.debug("Trying to connect to Equinox subprocess. Host: 'localhost', port: " + consolePort);
            
            socket = new Socket("localhost", consolePort);
            writer = new PrintWriter(socket.getOutputStream());

            for (int i=0; i<toInstall.length; i++) {
                if (log.isDebugEnabled()) log.debug("Installing: " + toInstall[i].getLocation());
                writer.println("install " + toInstall[i].getLocation());
                if (isNotFragment(i)) {
                    // 0 is the bundle ID of the system bundle, which is already there
                    writer.println("start " + (i + 1));
                }
            }

        } finally {
            if (writer != null) writer.close();
            if (socket != null) socket.close();
        }
    }

    private boolean isNotFragment(int i) {
        return toInstall[i].getHost() == null;
    }

    private String getConfigurationArea(String name) throws IOException {
        String configArea = getSystemProperty(EQUINOX_CONFIGURATION_AREA);
        if (configArea == null) configArea = createTempDir(name);
        return configArea;
    }

    private void initRequiredBundlesLocations() throws Exception {
        
        BundleDescription daemonDescription = getDaemonDescription();
        BundleDescription[] prerequisites = getPrerequisites();

        toInstall = new BundleDescription[prerequisites.length + 1];
        System.arraycopy(prerequisites, 0, toInstall, 0, prerequisites.length);
        toInstall[toInstall.length - 1] = daemonDescription;

        getDaemonBundleContext().ungetService(platformSR);
    }

    private BundleDescription[] getPrerequisites() throws Exception {
        // StateHelper.getPrerequisites returns weird things, and only takes into account static dependencies
        // (while we need a LogService implementation). So we do it by hand for now
        List prereq = new ArrayList();
        prereq.add(getBundleDescription("org.eclipse.osgi.services"));
        prereq.add(getBundleDescription("org.eclipse.equinox.log"));
        prereq.add(getBundleDescription("org.eclipse.equinox.ds"));
        prereq.add(getBundleDescription("org.smartfrog.extension.security"));
        return (BundleDescription[])
                prereq.toArray(new BundleDescription[prereq.size()]);
    }

    private BundleDescription getBundleDescription(final String symbolicName) throws Exception {
        // Can have several bundles with the same symbolic name but different versions
        BundleDescription[] bundles = getPlatformState().getBundles(symbolicName);
        if (bundles.length == 0) throw new IllegalStateException("Bundle not installed: " + symbolicName);
        return bundles[0];
    }

    private BundleDescription getDaemonDescription() throws Exception {
        final BundleContext context = getDaemonBundleContext();
        return getPlatformState().getBundle(context.getBundle().getBundleId());
    }

    private State getPlatformState() throws Exception {
        PlatformAdmin platformAdmin = getPlatformAdminService();
        // false : State is immutable, will throw exceptions if changes are attempted
        return platformAdmin.getState(false);
    }

    private PlatformAdmin getPlatformAdminService() throws Exception {
        final BundleContext daemonBC = getDaemonBundleContext();
        platformSR = daemonBC.getServiceReference("org.eclipse.osgi.service.resolver.PlatformAdmin");
        if (platformSR == null) throw new SmartFrogException
                ("The Equinox PlatformAdmin service is not available. Cannot start a new Equinox instance.", parentProcess);
        return (PlatformAdmin) daemonBC.getService(platformSR);
    }

    private String getSystemProperty(final String property) {
        return System.getProperty(SmartFrogCoreProperty.propBaseSFProcess + property);
    }

    private String getEquinoxBundleLocation() throws Exception {
        String bundleURL = System.getProperty("osgi.framework");

        File jarFile = new File(new URL(bundleURL).getFile());
        if (jarFile.canRead()) return jarFile.getAbsolutePath();
        else {
            // On Windows, the path is messed up and starts with /
            jarFile = new File(jarFile.getPath().substring(1));
            if (jarFile.canRead()) return jarFile.getAbsolutePath();
            else throw new IOException("The Equinox JAR file cannot be read. Location: " + jarFile.getPath());
        }
    }

    private BundleContext getDaemonBundleContext() throws Exception {
        return (BundleContext) parentProcess.sfResolveHere(SmartFrogCoreKeys.SF_CORE_BUNDLE_CONTEXT);
    }

    private String createTempDir(String name) throws IOException {
        File tempFile = File.createTempFile("smartfrog-" + name + "-equinox-config", null);
        if (!tempFile.delete()) throw new IOException("Could not delete temporary file");
        if (!tempFile.mkdir()) throw new IOException("Could not create temporary directory");
        return tempFile.getAbsolutePath();
    }

    public static void cleanShutdown() {
        subprocessNumber = 0;
    }
}
