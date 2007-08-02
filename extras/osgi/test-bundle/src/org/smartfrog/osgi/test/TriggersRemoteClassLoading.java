package org.smartfrog.osgi.test;

public class TriggersRemoteClassLoading {
    static {
        System.out.println("Loaded class TriggersRemoteClassLoading");
    }

    public String toString() {
        return "Impressive, huh ?";
    }
}
