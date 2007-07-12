package org.smartfrog.sfcore.deployer;

import java.io.InputStream;

public interface CodeRepository {
    /**
     * Does not throw IOException to be consistent with the behaviour of {@link Class} and {@link ClassLoader}.
     * @param pathname
     * @return
     */
    InputStream getResourceAsStream(String pathname);
}
