package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

import java.io.InputStream;

/**
 * The component factory that should be used for framework components.
 * Those come from the same classloader as this class.
 */
public class CoreClassesClassLoadingEnvironment extends AbstractClassLoadingEnvironment {

    public CoreClassesClassLoadingEnvironment() {}

    protected Prim getComponentImpl(ComponentDescription askedFor) throws SmartFrogResolutionException,
            ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        String className = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
        return (Prim) newInstance(className);
    }

    protected Object newInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return Class.forName(className).newInstance();
    }

    public InputStream getComponentDescription(String pathname) {
        return getClass().getResourceAsStream(pathname);
    }
}
