package org.smartfrog.sfcore.deployer;

import org.smartfrog.sfcore.security.rmispi.AnnotatedClassLoader;

/**
 * The component factory that should be used for framework components.
 * Those come from the same classloader as this class.
 */
public class CoreClassesClassLoadingEnvironment extends AbstractClassLoadingEnvironment {
    private AnnotatedClassLoader classLoader = new AnnotatedClassLoader(getClass().getClassLoader(), "");

    public AnnotatedClassLoader getClassLoader() {
        return classLoader;
    }
}
