package org.smartfrog.osgi;

import org.osgi.framework.Bundle;

import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;

/**
 * Equals changes during lifecycle.
 * hashCode() throws NotSupportedOperationException because it would change during lifecycle too.
 * DO NOT USE IN HASHTABLES
 * @noinspection RefusedBequest,EqualsAndHashcode
 */
class BundleClassLoaderProxy extends ClassLoader {
    private ClassLoader realCL = null;
    private Bundle hostBundle;

    BundleClassLoaderProxy(Bundle hostBundle) {
        this.hostBundle = hostBundle;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        Class aClass = hostBundle.loadClass(name);
        ClassLoader loader = aClass.getClassLoader();
        if (realCL == null && isRealCL(loader)) {
            realCL = aClass.getClassLoader();
        }
        return aClass;
    }

    public Enumeration getResources(String path) throws IOException {
        return hostBundle.getResources(path);
    }

    public URL getResource(String name) {
        return hostBundle.getResource(name);
    }

    /** @noinspection NonFinalFieldReferenceInEquals*/
    public boolean equals(Object other) {
        if (realCL == null) return super.equals(other);
        else return realCL.equals(other);
    }

    public String toString() {
        if (realCL == null) return super.toString();
        else return super.toString() + " delegating to " + realCL.toString();
    }

    /** @noinspection MethodWithMoreThanThreeNegations*/
    private boolean isRealCL(ClassLoader loader) {
        return loader != null // bootstrap
                && !loader.equals(ClassLoader.getSystemClassLoader().getParent()) // extension
                && !loader.equals(ClassLoader.getSystemClassLoader()) // classpath
                && !loader.equals(getClass().getClassLoader()); // SmartFrog core
    }
}
