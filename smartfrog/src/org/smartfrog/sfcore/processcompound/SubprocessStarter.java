package org.smartfrog.sfcore.processcompound;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

public interface SubprocessStarter {
    Process startProcess(ProcessCompound parentProcess, String name, ComponentDescription cd) throws Exception;
}
