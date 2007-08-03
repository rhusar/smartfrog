package org.smartfrog.osgi.test;

import java.io.Serializable;

public class TriggersRemoteClassLoading implements Serializable {
    static {
        System.out.println("Loaded class TriggersRemoteClassLoading");
    }

    public String toString() {
        return "Impressive, huh ?";
    }
}
