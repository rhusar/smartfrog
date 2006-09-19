package org.smartfrog.services.database.core;

import java.rmi.Remote;

/**

 */
public interface JdbcBinding extends Remote {


    /**
     * Driver
     * {@value}
     */
    public static final String ATTR_DRIVER="driver";
    /**
     * URL of the system
     * {@value}
     */
    public static final String ATTR_URL="url";
    /**
     * username
     * {@value}
     */
    public static final String ATTR_USERNAME="username";
    //
    /**
     * password
     * {@value}
     */
    public static final String ATTR_PASSWORD="password";
    /**
     * any extra properties
     * {@value}
     */
    public static final String ATTR_PROPERTIES="properties";
}
