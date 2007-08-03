package org.smartfrog.osgi;

import org.osgi.framework.Bundle;

import java.net.URL;

/**
 * Equals changes during lifecycle. No hasCode() provided because it would change during lifecycle too.
 * DO NOT USE IN HASHTABLES
 * @noinspection EqualsAndHashcode
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

    public URL getResource(String name) {
        return hostBundle.getResource(name);
    }

    public boolean equals(Object other) {
        if (realCL == null) return super.equals(other);
        else return realCL.equals(other);
    }

    public String toString() {
        if (realCL == null) return super.toString();
        else return super.toString() + " delegating to " + realCL.toString();
    }

    private boolean isRealCL(ClassLoader loader) {
        return loader != null // bootstrap
                && !loader.equals(ClassLoader.getSystemClassLoader().getParent()) // extension
                && !loader.equals(ClassLoader.getSystemClassLoader()) // classpath
                && !loader.equals(getClass().getClassLoader()); // SmartFrog core
    }
}
