package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.security.rmispi.ClassLoaderRegistry;
import org.smartfrog.sfcore.processcompound.SFProcess;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;

public abstract class AbstractClassLoadingEnvironment extends PrimImpl implements ClassLoadingEnvironment
{



   /**

     * @throws RemoteException In case of network/rmi error
     */
    public AbstractClassLoadingEnvironment() {
    }

    public final synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        doSfDeploy();
        Reference relative = sfCompleteName().makeRelative(SFProcess.getProcessCompound().sfCompleteName());
       
        ClassLoaderRegistry.registerClassLoader(getClassLoader(), relative.toString());
        if (sfLog().isDebugEnabled())
            sfLog().debug("Registering class loader: " + getClassLoader() + " with annotation: " + relative);
    }

    protected abstract void doSfDeploy() throws SmartFrogException, RemoteException;

    public InputStream getResourceAsStream(String pathname) throws IOException {
        URL resourceURL = getClassLoader().getResource(pathname);
        if (resourceURL == null) throw new IOException("Resource not found: " + pathname + " in repository: " + this);
        return SFSecurity.getSecureInputStream(resourceURL);
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        return getClassLoader().loadClass(className);
    }
}
