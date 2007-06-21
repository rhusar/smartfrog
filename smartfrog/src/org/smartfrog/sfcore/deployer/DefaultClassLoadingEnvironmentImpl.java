package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.rmi.RemoteException;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;

public class DefaultClassLoadingEnvironmentImpl extends PrimImpl implements ClassLoadingEnvironment {


    public DefaultClassLoadingEnvironmentImpl() throws RemoteException {
    }

    public Prim getComponent(ComponentDescription askedFor) throws RemoteException, SmartFrogDeploymentException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public URL getResource(String location) throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public InputStream getResourceAsStream(String location) throws RemoteException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Enumeration getResources(String location) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
