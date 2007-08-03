package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public interface SubprocessStarter {
    Process startProcess
            (ProcessCompound parentProcess, String name,
            int id, ComponentDescription cd) throws Exception;
}
