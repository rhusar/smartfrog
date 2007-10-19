package org.smartfrog.services.database.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Properties;

/**

 */
public interface JdbcBinding extends Remote {


    /**
     * Driver {@value}
     */
    public static final String ATTR_DRIVER = "driver";
    /**
     * URL of the system {@value}
     */
    public static final String ATTR_URL = "url";
    /**
     * username {@value}
     */
    public static final String ATTR_USERNAME = "username";

    /**
     * password {@value}
     */
    public static final String ATTR_PASSWORD = "password";
    /**
     * any extra properties {@value}
     */
    public static final String ATTR_PROPERTIES = "properties";

    /**
     * Get the properties of the connection
     * @return the properties of this JDBC binding
     * @throws RemoteException
     */
    Properties createConnectionProperties() throws RemoteException;

    /**
     * Get the driver
     *
     * @return the driver
     */
    String getDriver() throws RemoteException;

    /**
     * get the username
     *
     * @return username or null
     */
    String getUser() throws RemoteException;

    /**
     * get the password
     *
     * @return password or null
     */
    String getPassword() throws RemoteException;

    /**
     * get the jdbc url
     *
     * @return JDBC url
     */
    String getUrl() throws RemoteException;
}
