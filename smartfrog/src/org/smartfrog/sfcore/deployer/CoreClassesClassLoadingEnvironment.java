package org.smartfrog.sfcore.deployer;

/**
 * The component factory that should be used for framework components.
 * Those come from the same classloader as this class.
 */
public class CoreClassesClassLoadingEnvironment extends AbstractClassLoadingEnvironment {    

    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }
}
