package org.smartfrog.sfcore.prim;

import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.security.rmispi.AnnotatedClassLoader;

import java.io.Serializable;
import java.io.InputStream;
import java.io.IOException;

public class ClassLoadingEnvironmentWrapper implements Serializable, ClassLoadingEnvironment {
    private transient ClassLoadingEnvironment wrapped;

    public ClassLoadingEnvironmentWrapper(ClassLoadingEnvironment wrapped) {
        this.wrapped = wrapped;
    }

    public InputStream getResourceAsStream(String pathname) throws IOException {
        if (wrapped != null) return wrapped.getResourceAsStream(pathname);
        else throw error();
    }

    public AnnotatedClassLoader getClassLoader() {
        if (wrapped != null) return wrapped.getClassLoader();
        else throw error();
    }

    private IllegalStateException error() {
        return new IllegalStateException("This should not be used on a remote host");
    }

}
