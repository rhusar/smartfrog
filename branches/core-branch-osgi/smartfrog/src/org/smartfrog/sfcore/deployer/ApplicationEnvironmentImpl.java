package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.rmi.RemoteException;

public class ApplicationEnvironmentImpl extends PrimImpl implements ApplicationEnvironment {
    private static final String PARSER = "parser";
    private static final String TARGET = "target";
    
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        ComponentDescription parserEnv = (ComponentDescription) sfResolve(PARSER);
        ComponentDescription targetEnv = (ComponentDescription) sfResolve(TARGET);

        SFProcess.getProcessCompound().sfCreateNewApp(null, parserEnv, null);
        SFProcess.getProcessCompound().sfCreateNewApp(null, targetEnv, null);
    }
}
