package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

/**
 * The component factory that should be used for framework components.
 * Those come from the same classloader as this class.
 */
public class FrameworkClassLoadingEnvironmentImpl extends AbstractPrimFactoryUsingClassLoader {

    public FrameworkClassLoadingEnvironmentImpl() {}

    protected Prim getComponentImpl(ComponentDescription askedFor) throws SmartFrogResolutionException,
            ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        String className = (String) askedFor.sfResolveHere(SmartFrogCoreKeys.SF_CLASS);
        Class primClass = Class.forName(className);
        return (Prim) primClass.newInstance();
    }

}
