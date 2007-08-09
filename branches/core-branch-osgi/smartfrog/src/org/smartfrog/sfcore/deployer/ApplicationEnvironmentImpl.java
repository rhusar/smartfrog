package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.CompoundImpl;

import java.rmi.RemoteException;

public class ApplicationEnvironmentImpl extends CompoundImpl implements ApplicationEnvironment {
    private static final String PARSER = "parser";
    private static final String TARGET = "target";
    
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        ComponentDescription parserEnv = (ComponentDescription) sfResolve(PARSER);
        ComponentDescription targetEnv = (ComponentDescription) sfResolve(TARGET);

        sfCreateNewApp(null, parserEnv, null);
        sfCreateNewApp(null, targetEnv, null);
    }
}
