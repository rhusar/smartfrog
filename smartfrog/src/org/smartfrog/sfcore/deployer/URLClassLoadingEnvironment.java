package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;

import java.io.InputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Enumeration;

/**
 * Loads classes and creates components from remote code repositories.
 * The attribute of name {@link this.ATTR_CODEBASE} should be a list of URLs to code repositories.
 * Actual classloading is handled by {@link URLClassLoader}.  
 */
public class URLClassLoadingEnvironment extends AbstractClassLoadingEnvironment {

    private ClassLoader urlClassLoader;
    public static final String ATTR_CODEBASE = "codebase";

    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        List urls = (List) sfResolve(ATTR_CODEBASE);
        URL[] urlArray = new URL[urls.size()];
        try {
        for (int i=0; i<urlArray.length; i++)
            urlArray[i] = new URL((String) urls.get(i));
        } catch (MalformedURLException e) {
            throw SmartFrogException.forward
                    ("Malformed URL in attribute \"" + ATTR_CODEBASE + "\"", e);
        }

        // The parent class loader is the one that loaded SmartFrog core classes,
        // so user components will be linked properly with the core classes/interfaces they use.
        urlClassLoader = new URLClassLoader(urlArray, getClass().getClassLoader());
    }

    public ClassLoader getClassLoader() {
        return urlClassLoader;
    }
}
