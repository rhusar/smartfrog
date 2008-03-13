/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 *
 * Created 30-Nov-2007 11:48:49
 *
 */

public class PasswordHelper {
    /**
     * Start of the 'not a password' error: {@value}
     */
    public static final String ERROR_NOT_A_PASSWORD = "Not a password: ";


    /**
     * Extract a password from an attribute
     * @param component the component used
     * @param refname the string value of the reference
     * @param required is the password required
     * @return the password the extracted password
     * @throws SmartFrogException failure to get the password
     * @throws SmartFrogResolutionException if the value is not a string or a password provider
     * @throws RemoteException network problems
     */

    public static String resolvePassword(Prim component,String refname,boolean required)
            throws SmartFrogException, RemoteException {
        return resolvePassword(component,new Reference(refname),required); 
    }

    /**
     * Extract a password from an attribute
     * @param component the component used
     * @param reference the reference it came from
     * @param required is the password required
     * @return the password the extracted password
     * @throws SmartFrogException failure to get the password
     * @throws SmartFrogResolutionException if the value is not a string or a password provider
     * @throws RemoteException network problems
     */

    public static String resolvePassword(Prim component, Reference reference, boolean required)
            throws SmartFrogException, RemoteException {
        Object value = component.sfResolve(reference, required);
        return extractPassword(component, reference, value);
    }

    /**
     * Extract a password from a resolved entry
     * @param component the component used
     * @param reference the reference it came from
     * @param entry the entry to extract the password from
     * @return the password the extracted password
     * @throws SmartFrogException failure to get the password
     * @throws SmartFrogResolutionException if the value is not a string or a password provider
     * @throws RemoteException network problems
     */
    public static String extractPassword(Prim component, Reference reference, Object entry)
            throws SmartFrogException, RemoteException {
        if (entry == null) {
            return null;
        }
        if (entry instanceof String) {
            return entry.toString();
        }
        if (entry instanceof PasswordProvider) {
            PasswordProvider pp = (PasswordProvider) entry;
            return pp.getPassword();
        }
        throw new SmartFrogResolutionException(reference, component.sfCompleteName(), ERROR_NOT_A_PASSWORD + entry);
    }

}
