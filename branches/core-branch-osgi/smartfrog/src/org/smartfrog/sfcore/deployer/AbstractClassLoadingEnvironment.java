package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.security.rmispi.ClassLoaderRegistry;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.HostReferencePart;
import org.smartfrog.sfcore.reference.ProcessReferencePart;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;

public abstract class AbstractClassLoadingEnvironment extends PrimImpl
        implements ClassLoadingEnvironment
{
    public final synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        doSfDeploy();

        Reference noHostNoProcess = getRelativeRefence();
        ClassLoaderRegistry.registerClassLoader(getClassLoader(), noHostNoProcess.toString());
    }

    private Reference getRelativeRefence() throws RemoteException {
        Reference referenceNoHost = sfCompleteName();
        if (referenceNoHost.firstElement() instanceof HostReferencePart)
            referenceNoHost.removeElement(referenceNoHost.firstElement());
        if (referenceNoHost.firstElement() instanceof ProcessReferencePart)
            referenceNoHost.removeElement(referenceNoHost.firstElement());
        return referenceNoHost;
    }

    protected abstract void doSfDeploy() throws SmartFrogException, RemoteException;

    public InputStream getResourceAsStream(String pathname) throws IOException {
        URL resourceURL = getClassLoader().getResource(pathname);
        if (resourceURL == null) throw new IOException("Resource not found: " + pathname + " in repository: " + this);
        return SFSecurity.getSecureInputStream(resourceURL);
    }
}
