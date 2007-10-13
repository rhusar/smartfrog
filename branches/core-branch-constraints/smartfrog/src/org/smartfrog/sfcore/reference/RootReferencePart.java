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



import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.SmartFrogConstraintBacktrackError;

/**
 * Implements the root reference part.
 */
public class RootReferencePart extends ReferencePart {

    /** String representation of this part. */
    public static final String ROOT = SmartFrogCoreKeys.SF_ROOT;

    /**
     * Returns a string representation of the reference part.
     * Implements abstract method ReferencePart.toString.
     *
     * @return stringified reference part
     */
    public String toString() {
        return ROOT;
    }

    /**
     * Compares this reference part with another one. Equality means that the
     * other referencePart is a RootReferencePart
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object refPart) {
        return (refPart instanceof RootReferencePart);
    }

    /**
     * Returns hashcode of this part.
     * Implements abstract method ReferencePart.hashCode.
     *
     * @return hash code for part
     */
    public int hashCode() {
        return ROOT.hashCode();
    }

    /**
     * Resolves this reference part using the given reference resolver. This
     * forwards the reference to the parent if it exists or to the actual
     * component if the root is found (parent== null). The originating
     * reference and index are needed to forward the request to the parent or
     * component
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
            return forwardReference(rr, r, index + 1);
        } else {
            return forwardReference(rr.sfResolveParent(), r, index);
        }
    }

    /**
     * Resolves this reference part using the given remote reference resolver.
     * This forwards the reference to the parent if it exists or to the actual
     * component if the root is found (parent== null). The originating
     * reference and index are needed to forward the request to the parent or
     * component
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
                return forwardReference(rr, r, index + 1);
            } else {
                return forwardReference(rr.sfResolveParent(), r, index);
            }
        } catch (SmartFrogConstraintBacktrackError sfcbe){
        	throw sfcbe;
        } catch (Exception ex){
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(ex);
        }
    }
}
