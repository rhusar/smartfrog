package org.smartfrog.sfcore.security.rmispi;

import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;

public final class AnnotatedClassLoader extends ClassLoader {
    private ClassLoader realLoader;
    private String annotation;

    public AnnotatedClassLoader(ClassLoader realLoader, String annotation) {
        super(realLoader.getParent());
        this.realLoader = realLoader;
        this.annotation = annotation;
    }

    protected Class findClass(String name) throws ClassNotFoundException {
        // NB: loadClass, not findClass (which is protected anyway).
        // This is to ensure that we get ownership of the loaded class,
        // so that clients can call getClassLoader() on it and retrieve us,
        // because they want to access our getAnnotation() method.
        return realLoader.loadClass(name);
    }

    public Enumeration getResources(String name) throws IOException {
        return realLoader.getResources(name);
    }

    public InputStream getResourceAsStream(String name) {
        return realLoader.getResourceAsStream(name);
    }

    public URL getResource(String name) {
        return realLoader.getResource(name);
    }

    public String getAnnotation() {
        return annotation;
    }
}
