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

import java.util.Vector;

import org.smartfrog.sfcore.common.SFMarshalledObject;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;


/**
 * Implements the attrib reference part. This part resolves to the nearest
 * container which has the given attribute (then the rest of the reference is
 * forwarded to that attribute value)
 *
 */
public class AttribReferencePart extends HereReferencePart {
    /** Base string representation of this part (ATTRIB). */
    public static final String ATTRIB = "ATTRIB";

    /**
     * Constructs AttribReferencePart with a attribute.
     *
     * @param v value for attrib part
     */
    public AttribReferencePart(Object v) {
        super(v);
    }

    /**
     * Returns this object since this is already an attrib reference part.
     * Overrides HereReferencePart.asAttribReferencePart.
     * @return this part
     */
    public ReferencePart asAttribReferencePart() {
        return this;
    }

    /**
     * Returns a string representation of the reference part.
     * Overrides HereReferencePart.toString.
     *
     * @return stringified reference part
     */
    public String toString() {
        return ATTRIB + ' ' + getValue().toString();
    }

    /**
     * Return a string which is a representation of the reference part in a reference in the
     * index position given.
     *
     * @param index the position in the reference
     * @return the representation
     */
    public String toString(int index) {
        if (index == 0) {
            return getValue().toString();
        }
        else {
            return ATTRIB + ' ' + getValue().toString();
        }
    }

    /**
     * Returns hashcode of this part. This is the hashCode of the stored value
     * plus the ATTRIB hashcode
     *
     * @return hash code for part
     */
    public int hashCode() {
        return ATTRIB.hashCode() + super.hashCode();
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
        // Find here
        Object result = rr.sfResolveHere(getValue(),false);

        if (result == null) {
            if (rr.sfResolveParent() == null) {
                throw SmartFrogResolutionException.notFound(r, null);
            }
            return forwardReference(rr.sfResolveParent(), r, index);
        }

        try {
            // if reference ask rr to resolve it (chaining)
            if (result instanceof Reference && !((Reference)result).getData()) {
                result = rr.sfResolve((Reference)result);
                // if vector ask rr to resolve any contained reference(chaining)
            } else if (result instanceof Vector) {
                result = sfResolveVector(rr, (Vector)result);
            }
        }  catch (SmartFrogResolutionException ex) {
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(r.toString(),ex);
        }

        // If the end we are there!
        if (index == (r.size() - 1)) {
            return result;
        }

        // Else forward on to result
        return forwardReference(result, r, index + 1);
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
            // Find here
            Object result = rr.sfResolveHere(getValue(),false);

            if (result == null) {  //not here
                if (rr.sfResolveParent() == null) {
                    throw SmartFrogResolutionException.notFound(r, null);
                }
                return forwardReference(rr.sfResolveParent(), r, index);
            }

            //is here
            try {
                // if reference ask rr to resolve it (chaining)
                if (result instanceof Reference && !((Reference)result).getData()) {
                    result = rr.sfResolve((Reference)result);
                } else if (result instanceof Vector) {
                    result = sfResolveVector(rr, (Vector)result);
                }
            } catch (SmartFrogResolutionException ex) {
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(r.toString(),ex);
            }

            // If the end we are there!
            if (index == (r.size() - 1)) {
                //Marshall!
                if (!(result instanceof SFMarshalledObject)) {
                    return new SFMarshalledObject(result);
                } else {
                    return result;
                }
            }

            // Else forward on to result
            return forwardReference(result, r, index + 1);

        } catch (Exception ex){
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward(ex);
        }
    }
}
