package org.smartfrog.sfcore.deployer;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * A ComponentFactory that has access to a complete class loading environment.
 * Implementations of the {@link super.getComponent(org.smartfrog.sfcore.componentdescription.ComponentDescription)}
 * method will thus typically load a class and create an instance of it.
 *
 * Since a class loader is accessible, this interface includes resource-access methods. These have the same behaviour
 * as their counterparts in {@link ClassLoader}.
 */
public interface ClassLoadingEnvironment extends ComponentFactory {
    URL getResource(String location) throws RemoteException;
    InputStream getResourceAsStream(String location) throws RemoteException;
    Enumeration getResources(String location) throws IOException;
}
