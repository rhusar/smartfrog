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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.Log;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 *  JDBC binding class
 * The sole deployment action is to check that the driver class is there
 * (and so implicitly load the class) 
 */
public class JdbcBindingImpl extends PrimImpl implements JdbcBinding {

    private String driver;

    private String user;

    private String password;

    private Vector properties;

    private String url;

    public JdbcBindingImpl() throws RemoteException {
    }


    /**
     * Read the properties and start.
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        driver = sfResolve(ATTR_DRIVER, "", true);
        url = sfResolve(ATTR_USERNAME, "", true);
        user = sfResolve(ATTR_USERNAME, "", false);
        password = sfResolve(ATTR_PASSWORD, "", false);
        properties = sfResolve(ATTR_PROPERTIES, (Vector) null, false);
        Log log = LogFactory.getOwnerLog(this);
        try {
            Class aClass = SFClassLoader.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SmartFrogException("Could not load "+driver,e);
        }
    }
}
