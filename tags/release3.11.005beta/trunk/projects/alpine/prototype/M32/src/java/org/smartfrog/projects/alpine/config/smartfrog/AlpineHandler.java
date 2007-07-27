package org.smartfrog.projects.alpine.config.smartfrog;

import java.rmi.Remote;

/**
 * A handler
 */
public interface AlpineHandler extends Remote {
    public static final String ATTR_ENDPOINT = "endpoint";
    public static final String ATTR_CLASSNAME = "classname";

}
