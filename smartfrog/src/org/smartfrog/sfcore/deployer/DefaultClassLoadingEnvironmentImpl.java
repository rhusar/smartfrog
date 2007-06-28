package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;

public class DefaultClassLoadingEnvironmentImpl implements ClassLoadingEnvironment {

    public Prim getComponent(ComponentDescription askedFor) throws SmartFrogResolutionException,
            ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        String className = resolveClassName(askedFor);
        Class primClass = Class.forName(className);
        return (Prim) primClass.newInstance();
    }

    private String resolveClassName(ComponentDescription meta) throws SmartFrogResolutionException {
        return (String) meta.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
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
