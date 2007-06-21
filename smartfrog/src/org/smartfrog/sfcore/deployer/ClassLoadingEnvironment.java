package org.smartfrog.sfcore.deployer;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;

public interface ClassLoadingEnvironment extends ComponentFactory {
    URL getResource(String location) throws RemoteException;
    InputStream getResourceAsStream(String location) throws RemoteException;
    Enumeration getResources(String location) throws IOException;
}
