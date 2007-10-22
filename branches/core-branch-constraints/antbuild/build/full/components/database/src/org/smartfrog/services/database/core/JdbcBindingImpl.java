/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.services.database.core;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;
import java.util.Properties;
import java.util.Vector;

/**
 * JDBC binding class The sole deployment action is to check that the driver
 * class is there (and so implicitly load the class)
 */
public class JdbcBindingImpl extends PrimImpl implements JdbcBinding {

    private String driver;

    private String user;

    private String password;

    private Vector properties;

    private Properties connectionProperties;

    private String url;


    public JdbcBindingImpl() throws RemoteException {
    }


    /**
     * Read the properties and load the driver.
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        bindToDatabaseProperties();
    }

    /**
     * Startup time binding to the database
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws SmartFrogDeploymentException
     */
    protected void bindToDatabaseProperties()
            throws SmartFrogResolutionException, RemoteException, SmartFrogDeploymentException {
        ComponentHelper helper = new ComponentHelper(this);

        driver = sfResolve(ATTR_DRIVER, "", false);
        url = sfResolve(ATTR_URL, "", true);
        user = sfResolve(ATTR_USERNAME, "", false);
        password = sfResolve(ATTR_PASSWORD, "", false);
        properties = sfResolve(ATTR_PROPERTIES, (Vector) null, false);

        if (driver != null) {
            helper.loadClass(driver);
        }
        sfLog().info("Binding to "+url+" with driver "+driver+" as "+user!=null?"user":"(anonymous)");

        connectionProperties = new Properties();
        if (user != null) {
            connectionProperties.setProperty("user", user);
        }

        if (password != null) {
            connectionProperties.setProperty("password", password);
        }

        if (properties != null && properties.size() != 0) {
            throw new SmartFrogDeploymentException(
                    "Properties are not yet implemented");
        }
    }

    /**
     * Get the connection properties.
     *
     * @return the new connection information
     */
    public Properties createConnectionProperties() {
        Properties connProps = (Properties) connectionProperties.clone();
        return connProps;
    }

    /**
     * Get the driver
     *
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * get the username
     *
     * @return username or null
     */
    public String getUser() {
        return user;
    }

    /**
     * get the password
     *
     * @return password or null
     */
    public String getPassword() {
        return password;
    }

    /**
     * get the jdbc url
     *
     * @return JDBC url
     */
    public String getUrl() {
        return url;
    }


}
