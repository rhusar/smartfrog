package org.smartfrog.osgi;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.AbstractSubprocessStarter;
import org.smartfrog.sfcore.common.SmartFrogCoreProperty;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.ExportedPackage;

import java.util.*;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class EquinoxSubprocessStarterImpl extends AbstractSubprocessStarter {
        
    private static final String EQUINOX_JAR_FILE = "equinoxJarFile";
    private static final String EQUINOX_CONSOLE_PORT = "equinoxConsolePort";
    private static final String EQUINOX_CONFIGURATION_AREA = "equinoxConfigurationArea";
    
    private Integer consolePort;

    private PackageAdmin packageAdmin;
    private Bundle daemonBundle;
    // List<Bundle>
    private List bundlesToInstall;

    public EquinoxSubprocessStarterImpl(Bundle daemonBundle) {
        this.daemonBundle = daemonBundle;
    }

    void setPackageAdmin(PackageAdmin packageAdmin) {
        this.packageAdmin = packageAdmin;
    }

    void unsetPackageAdmin(PackageAdmin packageAdmin) {
        this.packageAdmin = null;
    }

    protected void addParameters(ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd) throws Exception {
        // Equinox startup options:
        // http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html

        bundlesToInstall = retrieveDaemonBundleDependencies();
        bundlesToInstall.add(daemonBundle);

        consolePort = (Integer) cd.sfResolveHere(EQUINOX_CONSOLE_PORT);                
        String configurationArea = (String) cd.sfResolveHere(EQUINOX_CONFIGURATION_AREA, false);
        if (configurationArea == null) configurationArea = createTempDir(name);

        // TODO: This is supposed to be done by addProcessAttributes, but it adds nothings, whereas only this makes things work.
        runCmd.add("-D" + SmartFrogCoreProperty.sfProcessName + "=" + name);

        runCmd.add("-jar");
        runCmd.add(cd.sfResolveHere(EQUINOX_JAR_FILE).toString());
        runCmd.add("-console");
        runCmd.add(consolePort.toString());
        runCmd.add("-configuration");
        runCmd.add(configurationArea);
    }

    private List retrieveDaemonBundleDependencies() {
        // List<Bundle>
        List dependencies = new ArrayList();

        // Eeek.
        // No other way to get imported packages (?)
        Dictionary dict = daemonBundle.getHeaders();
        String importsString = (String) dict.get(Constants.IMPORT_PACKAGE);
        String[] imports = importsString.split(",");
        for (int i=0; i<imports.length; i++) {
            String[] nameAndAttrs = imports[i].split(";");
            String packageName = nameAndAttrs[0].trim();
            dependencies.add(getExportingBundle(packageName));
        }

        return dependencies;
    }

    private Bundle getExportingBundle(String packageName) {
        ExportedPackage[] exportedPackages = packageAdmin.getExportedPackages(packageName);
        for (int i=0; i<exportedPackages.length; i++) {
            if (contains(exportedPackages[i].getImportingBundles(), daemonBundle))
                return exportedPackages[i].getExportingBundle();
        }
        throw new IllegalStateException("Exporting bundle not found for package: " + packageName);
    }

    private boolean contains(Object[] objects, Object o) {
        for (int i=0; i<objects.length; i++)
            if (objects[i].equals(o)) return true;
        return false;
    }

    private String createTempDir(String name) throws IOException {
        File tempFile = File.createTempFile("smartfrog-" + name + "-equinox-config", null);
        if (! tempFile.delete()) throw new IOException("Could not delete temporary file");
        if (!tempFile.mkdir()) throw new IOException("Could not create temporary directory");
        return tempFile.getAbsolutePath();
    }

   protected void doPostStartupSteps() throws IOException, InterruptedException {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            // So that Equinox has enough time to start... Ideally we'd like to have a notification instead
            Thread.sleep(1000);

            socket = new Socket("localhost", consolePort.intValue());
            writer = new PrintWriter(socket.getOutputStream());

            for (int i=0; i<bundlesToInstall.size(); i++) {
                final Bundle bundle = (Bundle) bundlesToInstall.get(i);
                writer.println("install " + bundle.getLocation());
                if (OSGiUtilities.isNotFragment(bundle))
                    writer.println("start " + i);
            }
        } finally {
            if (writer != null) writer.close();
            if (socket != null) socket.close();            
        }
    }
}
