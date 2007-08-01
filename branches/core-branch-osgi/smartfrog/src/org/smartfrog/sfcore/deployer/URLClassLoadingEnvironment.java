package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

/**
 * Loads classes and creates components from remote code repositories.
 * The attribute of name {@link this.ATTR_CODEBASE} should be a list of URLs to code repositories.
 * Actual classloading is handled by {@link URLClassLoader}.  
 */
public class URLClassLoadingEnvironment extends AbstractClassLoadingEnvironment {

    private ClassLoader urlClassLoader = null;
    public static final String ATTR_CODEBASE = "codebase";
    private URL[] urlArray = null;

    protected void doSfDeploy() throws SmartFrogException, RemoteException {        
        final Object codebase = sfResolve(ATTR_CODEBASE);
        List urls;
        if (codebase instanceof String) urls = Arrays.asList(new Object[] {codebase});
        else urls = (List) codebase;

        urlArray = new URL[urls.size()];
        try {
            for (int i=0; i<urlArray.length; i++) {
                urlArray[i] = new URL((String) urls.get(i));
                check(urlArray[i]);
            }
        } catch (MalformedURLException e) {
            throw SmartFrogException.forward
                    ("Malformed URL in attribute \"" + ATTR_CODEBASE + '\"', e);
        }

        // The parent class loader is the one that loaded SmartFrog core classes,
        // so user components will be linked properly with the core classes/interfaces they use.
        urlClassLoader = new URLClassLoader(urlArray, getClass().getClassLoader());
    }

    private void check(URL url) throws SmartFrogException {
        try {
            url.openStream();
        } catch (Exception e) {
            // Throws IOExceptions, but also NPEs when the URL is well-formed but invalid according to a specific scheme
            // (eg. http:foo/bar instead of http://foo/bar), in Sun Java 6 at least. 
            throw new SmartFrogException("The URL is unreachable: " + url, e);
        }
    }

    public ClassLoader getClassLoader() {
        if (urlClassLoader == null)
            throw new IllegalStateException("The class loader is not available, this environment has not been deployed yet");
        return urlClassLoader;
    }

    public String toString() {
        return "URLClassLoadingEnvironment@" + System.identityHashCode(this)
                + "[ urls=" + Arrays.toString(urlArray) + " ]";
    }
}
