package org.smartfrog.osgi;

import org.smartfrog.sfcore.processcompound.SubprocessStarter;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

// TODO: Implement.
public class EquinoxSubprocessStarterImpl implements SubprocessStarter {
    public Process startProcess(ProcessCompound parentProcess, String name, ComponentDescription cd) throws Exception {

        // http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html

        // Need to give a working directory so that Equinox can store unpacked bundles, etc.
        // Should probably be a parameter ?

        return null;
    }
}
