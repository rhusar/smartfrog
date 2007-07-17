package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.deployer.CodeRepository;

import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;

public class CodeRepositoryWrapper implements Serializable, CodeRepository {
    private transient CodeRepository wrapped;

    public CodeRepositoryWrapper(CodeRepository wrapped) {
        this.wrapped = wrapped;
    }

    public InputStream getResourceAsStream(String pathname) throws IOException {
        if (wrapped != null) return wrapped.getResourceAsStream(pathname);
        else throw new IllegalStateException("This should not be used on a remote host");
    }
}
