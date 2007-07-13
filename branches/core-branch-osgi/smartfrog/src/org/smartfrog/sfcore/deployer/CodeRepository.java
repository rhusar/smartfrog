package org.smartfrog.sfcore.deployer;

import java.io.InputStream;
import java.io.IOException;

public interface CodeRepository {
    /**
     * Loads a resource from this repository.
     * The resource name must not start with a /.
     * @param pathname The path to the resource.
     * @return An InputStream to the resource.
     * @throws IOException If the resource cannot be accessed.
     */
    InputStream getResourceAsStream(String pathname) throws IOException;
}
