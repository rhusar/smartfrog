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

import java.rmi.RemoteException;
import java.rmi.Remote;

/**
 * This interface is for components that provide bulk access to passwords.
 * Created 20-Feb-2008 13:46:38
 */


public interface BulkPasswordProvider extends Remote {

    /**
     * Gets the password for the given username/domain combination.
     *
     * @param user a username: must not be null
     * @param domain a domain (can be null, in which case it is ignored)
     * @return a password
     * @throws SmartFrogException If unable to get the password
     * @throws RemoteException    in case of network or RMI error
     */
    public String getPassword(String user,String domain) throws SmartFrogException, RemoteException;
}
