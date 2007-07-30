package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFSecurity;
import org.smartfrog.sfcore.security.rmispi.AnnotatedClassLoader;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;

public abstract class AbstractClassLoadingEnvironment extends PrimImpl
        implements ClassLoadingEnvironment
{
    public ClassLoadingEnvironment getCodeRepository() {
        return this;
    }

    public InputStream getResourceAsStream(String pathname) throws IOException {
        URL resourceURL = getClassLoader().getResource(pathname);
        if (resourceURL == null) throw new IOException("Resource not found: " + pathname + " in repository: " + this);
        return SFSecurity.getSecureInputStream(resourceURL);
    }
}
