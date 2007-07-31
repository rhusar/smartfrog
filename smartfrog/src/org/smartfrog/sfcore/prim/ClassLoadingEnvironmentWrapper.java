package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class ClassLoadingEnvironmentWrapper implements Serializable, ClassLoadingEnvironment {
    private transient ClassLoadingEnvironment wrapped;

    public ClassLoadingEnvironmentWrapper(ClassLoadingEnvironment wrapped) {
        this.wrapped = wrapped;
    }

    public InputStream getResourceAsStream(String pathname) throws IOException {
        if (wrapped != null) return wrapped.getResourceAsStream(pathname);
        else throw error();
    }

    public ClassLoader getClassLoader() {
        if (wrapped != null) return wrapped.getClassLoader();
        else throw error();
    }

    private IllegalStateException error() {
        return new IllegalStateException("This should not be used on a remote host");
    }

}
