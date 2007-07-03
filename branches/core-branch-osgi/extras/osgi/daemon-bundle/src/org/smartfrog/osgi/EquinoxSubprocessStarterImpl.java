package org.smartfrog.osgi;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.AbstractSubprocessStarter;

import java.util.List;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;

public class EquinoxSubprocessStarterImpl extends AbstractSubprocessStarter {
    
    private static final String SMARTFROG_BUNDLE_LOCATION = "smartFrogBundleLocation";
    private static final String SMARTFROG_EXT_BUNDLE_LOCATION = "smartFrogExtensionBundleLocation";
    private static final String EQUINOX_JAR_FILE = "equinoxJarFile";
    private static final String EQUINOX_CONSOLE_PORT = "equinoxConsolePort";
    private static final String EQUINOX_CONFIGURATION_AREA = "equinoxConfigurationArea";

    private Integer consolePort;
    private String smartFrogBundleLocation;
    private String smartFrogExtBundleLocation;

    protected void addParameters(ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd) throws Exception {
        // http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html

        consolePort = (Integer) cd.sfResolveHere(EQUINOX_CONSOLE_PORT);
        smartFrogBundleLocation = (String) cd.sfResolveHere(SMARTFROG_BUNDLE_LOCATION);
        smartFrogExtBundleLocation = (String) cd.sfResolveHere(SMARTFROG_EXT_BUNDLE_LOCATION);

        addProcessAttributes(runCmd, name, cd);
        runCmd.add("-jar " + cd.sfResolveHere(EQUINOX_JAR_FILE));
        runCmd.add("-console " + consolePort);
        runCmd.add("-configuration " + cd.sfResolveHere(EQUINOX_CONFIGURATION_AREA));
    }

    protected void doPostStartupSteps() throws IOException {
        Socket socket = null;
        PrintWriter writer = null;
        try {
            socket = new Socket("localhost", consolePort.intValue());
            writer = new PrintWriter(socket.getOutputStream());
            writer.println("install " + smartFrogExtBundleLocation);
            writer.println("install " + smartFrogBundleLocation);
            writer.println("start 2");
        } finally {
            if (writer != null) writer.close();
            if (socket != null) socket.close();            
        }
    }
}
