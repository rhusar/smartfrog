/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.passwords;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * Created 25-Mar-2008 13:55:54
 */

public class PropertyPasswordProvider extends PrimImpl implements PasswordProvider {

    public static final String ATTR_PROPERTY = "property";
    private String property;
    /**
     * Error string when the property is undefined: {@value}
     */
    public static final String ERROR_UNDEFINED_PROPERTY = "Undefined password property: ";


    public PropertyPasswordProvider() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        property = sfResolve(ATTR_PROPERTY, "", true);
    }

    /**
     * Gets the password.
     *
     * @return a password
     * @throws SmartFrogException If unable to get the password
     * @throws RemoteException    in case of network or RMI error
     */
    public String getPassword() throws SmartFrogException, RemoteException {

        String value = System.getProperty(property);
        if (value == null) {
            throw new SmartFrogDeploymentException(ERROR_UNDEFINED_PROPERTY + property);
        }
        return value;
    }
}
