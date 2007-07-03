package org.smartfrog.osgi;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.AbstractSubprocessStarter;

import java.util.List;

public class EquinoxSubprocessStarterImpl extends AbstractSubprocessStarter {    

    protected void addParameters(ProcessCompound parentProcess, List runCmd, String name, ComponentDescription cd) throws Exception {
        // http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html

        // Need to give a working directory so that Equinox can store unpacked bundles, etc.
        // Should probably be a parameter ?

        addProcessAttributes(runCmd, name, cd);
        runCmd.add("-jar " + cd.sfResolveHere("equinoxJarLocation"));
        runCmd.add("-console " + cd.sfResolveHere("equinoxConsolePort", false));
        runCmd.add("-configuration " + cd.sfResolveHere("equinoxConfigurationArea"));        
    }
}
