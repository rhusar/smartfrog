package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.RemoteException;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;

public class DefaultClassLoadingEnvironmentImpl extends PrimImpl implements ClassLoadingEnvironment {


    public DefaultClassLoadingEnvironmentImpl() throws RemoteException {
    }

    public Prim getComponent(ComponentDescription askedFor) throws SmartFrogResolutionException,
            ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        ComponentDescription meta = (ComponentDescription)
                askedFor.sfResolveHere(SmartFrogCoreKeys.SF_METADATA);
        String className = (String) meta.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
        Class primClass = Class.forName(className);
        return (Prim) primClass.newInstance();
    }

    public URL getResource(String location) throws RemoteException {
        return getClass().getResource(location);
    }

    public InputStream getResourceAsStream(String location) throws RemoteException {
        return getClass().getResourceAsStream(location);
    }

    public Enumeration getResources(String location) throws IOException {
        return getClass().getClassLoader().getResources(location);
    }
}
