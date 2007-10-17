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

import org.smartfrog.sfcore.common.SmartFrogResolutionException;

/**
 * Defines reference resolving. It abstracts the target of the resolution so
 * references do not need to know about it. This way, implementations can also
 * decide the mechanism used to resolve each part. An example of a difference
 * resolver would be one that walks a directory service, or a web server to
 * locate the reference. ReferenceParts will call back to resolve themselves,
 * causing the reference resolver to forward the rest of the resolution to the
 * next component.
 */
public interface ReferenceResolver {
    /**
     * Resolve a given reference. Generally forwards directly to indexed
     * resolve with index 0
     *
     * @param r reference to resolve
     *
     * @return resolved reference
     *
     * @throws SmartFrogResolutionException if error occurred while resolving
     */
    public Object sfResolve(Reference r)
        throws SmartFrogResolutionException;

    /**
     * Resolves a reference starting at a given index.
     *
     * @param reference reference to be resolved
     * @param index starting index
     *
     * @return Object attribute at resolved reference
     *
     * @throws SmartFrogResolutionException if error occurred while resolving
     */
    public Object sfResolve(Reference reference, int index)
        throws SmartFrogResolutionException;

    /**
     * Call in to resolve to the parent reference resolver of this one.
     *
     * @return parent referencee resolver or null if none
     *
     * @throws SmartFrogResolutionException if error occurred while resolving
     */
    public Object sfResolveParent()
        throws SmartFrogResolutionException;


    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     *
     * @return resolved attribute
     *
     * @throws SmartFrogResolutionException failed to find attribute
     */
    public Object sfResolveHere(Object name) throws SmartFrogResolutionException;

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
     */
    public Object sfResolveHere(Object name, boolean mandatory)
        throws SmartFrogResolutionException;

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
     */
    public Object sfResolveHereNonlocal(Object name, boolean mandatory)
        throws SmartFrogResolutionException;
}
