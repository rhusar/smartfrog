package org.smartfrog.sfcore.deployer;

import java.io.IOException;
import java.io.InputStream;

public interface ClassLoadingEnvironment {
    /**
     * Loads a resource from this environment.
     * The resource name must not start with a /.
     * @param pathname The path to the resource.
     * @return An InputStream to the resource.
     * @throws IOException If the resource cannot be accessed.
     */
    InputStream getResourceAsStream(String pathname) throws IOException;

    /**
     * Returns a class loader that has access to the resources of this environment. It is needed to allow RMI
     * to create instances of classes coming from this factory through deserialization.
     * The ClassLoader returned must stay the same for the lifetime of this object. Otherwise ClassCastExceptions may appear.
     *
     * @return A classloader for this environment.
     */
    ClassLoader getClassLoader();
}
