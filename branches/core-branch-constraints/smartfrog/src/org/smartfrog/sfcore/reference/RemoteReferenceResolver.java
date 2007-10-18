/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.reference;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;


/**
 * This makes a reference resolver interface available for remotable objects.
 * ReferenceResolver can not extend from Remote since RMI would then try to
 * marshal a stub to component descriptions on the wire, thinking the
 * description is remotable because it indirectly inherits Remote. Interfaces
 * or classes that need to offer reference resolution and be serializable
 * should implement ReferenceResolver while remotable classes or interfaces
 * should implement RemoteReferenceResolver
 *
 */
public interface RemoteReferenceResolver extends Remote {
    /**
     * Resolves a given reference. Generally forwards directly to indexed
     * resolve with index 0
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     * @throws SmartFrogResolutionException if any error occurrs while resolving
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(Reference r)
        throws RemoteException, SmartFrogResolutionException;

    /**
     * Resolves a reference starting at a given index.
     *
     * @param reference reference to be resolved
     * @param index starting index
     *
     * @return Object attribute at resolved reference
     *
     * @throws SmartFrogResolutionException if any error occurrs while resolving
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolve(Reference reference, int index)
        throws RemoteException, SmartFrogResolutionException;

    /**
     * Resolves parent reference of the given reference.
     *
     * @return resolved parent reference
     * @throws SmartFrogResolutionException if any error occurrs while resolving
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolveParent() throws  RemoteException, SmartFrogResolutionException;


    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     *
     * @return resolved attribute
     *
     * @throws SmartFrogResolutionException failed to find attribute
     * @throws RemoteException if there is any network/rmi error
     */
    public Object sfResolveHere(Object name) throws RemoteException, SmartFrogResolutionException;

    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        throws a SmartFrogResolutionException
     *
     * @return Object value for attribute
     *
     * @throws SmartFrogResolutionException failed to find attribute
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolveHere(Object name, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;

    /**
     * Find an attribute in this context, so long as it is visible anywhere.
     *
     * @param name attribute key to resolve
     * @param mandatory boolean that indicates if this attribute must be
     *        present in the description. If it is mandatory and not found it
     *        throws a SmartFrogResolutionException
     *
     * @return Object value for attribute
     *
     * @throws SmartFrogResolutionException failed to find attribute
     * @throws RemoteException In case of network/rmi error
     */
    public Object sfResolveHereNonlocal(Object name, boolean mandatory)
        throws SmartFrogResolutionException, RemoteException;
}
