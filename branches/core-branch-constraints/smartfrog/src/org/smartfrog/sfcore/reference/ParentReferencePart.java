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
import org.smartfrog.sfcore.languages.sf.functions.Constraint.SmartFrogConstraintBacktrackError;


/**
 * Implements the parent reference part. This resolves the parent of the
 * given reference resolver.
 *
 */
public class ParentReferencePart extends ReferencePart {
    /** String representation of this part. */
    public static final String PARENT = "PARENT";

    /**
     * Constructs a ParentReferencePart.
     */
    public ParentReferencePart() {
    }

    /**
     * Returns a string representation of the reference part.
     * Implements abstratc method ReferencePart.toString.
     * @return stringified reference part
     */
    public String toString() {
        return PARENT;
    }

    /**
     * Returns hashcode of this part.
     * Implements abstratc method ReferencePart.hashCode.
     *
     * @return hash code for part
     */
    public int hashCode() {
        return PARENT.hashCode();
    }

    /**
     * Compares this reference part with another one. Equality means that the
     * other part is a ParentReferencePart.
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object refPart) {
        return (refPart instanceof ParentReferencePart);
    }

    /**
     * Resolves this reference part using the reference resolver. The
     * originating reference and index are needed to enable request forwarding
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    public Object resolve(ReferenceResolver rr, Reference r, int index)
        throws SmartFrogResolutionException {
        if (rr.sfResolveParent() == null) {
            throw SmartFrogResolutionException.notFound(r, null);
        }

        return forwardReference(rr.sfResolveParent(), r, index + 1);
    }

    /**
     * Resolves this reference part using the remote reference resolver. The
     * originating reference and index are needed to enable request forwarding
     *
     * @param rr reference resolver
     * @param r reference which this part sits in
     * @param index index of this reference part in r
     *
     * @return the attribute found on resolution
     *
     * @throws SmartFrogResolutionException if failed to resolve reference
     */
    public Object resolve(RemoteReferenceResolver rr, Reference r, int index)
    throws SmartFrogResolutionException {
    try {
        if (rr.sfResolveParent() == null) {
            throw SmartFrogResolutionException.notFound(r, null);
        }

        return forwardReference(rr.sfResolveParent(), r, index + 1);
    } catch (SmartFrogConstraintBacktrackError sfcbe){
    	throw sfcbe;
    } catch (Exception ex){
        throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(ex);
    }
}
}
