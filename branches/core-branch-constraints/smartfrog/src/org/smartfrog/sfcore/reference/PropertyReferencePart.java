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

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.SmartFrogConstraintBacktrackError;


/**
 * Implements the property reference part. This part resolves to the system
 * property with given value.
 * References are not forwarded from here, so
 * having this part in the middle of a reference does NOT make sense!
 *
 */
public class PropertyReferencePart extends ReferencePart {
    /** Base string representation of this part (@value). */
    public static final String PROPERTY = "PROPERTY";

    public Object value;

    /**
     * Constructs PropertyReferencePart with a property.
     *
     * @param v value for property
     */
    public PropertyReferencePart(Object v) {
        value = v;
    }

    /**
     * Returns a string representation of the reference part.
     * Overrides HereReferencePart.toString.
     * @return stringified reference part
     */
    public String toString() {
        return PROPERTY + ' ' + value.toString();
    }

    /**
     * Returns hashcode of this part. This is the hashCode of the stored value
     * plus the ATTRIB hashcode.
     *
     * @return hash code for part
     */
    public int hashCode() {
        return PROPERTY.hashCode() + value.hashCode();
    }

    /**
     * Compares this reference part with another one. Equality means that the
     * type and value are equal
     *
     * @param refPart to be compared to
     *
     * @return true if equal, false if not
     */
    public boolean equals(Object refPart) {
        return refPart.getClass().equals(this.getClass()) &&
        ((PropertyReferencePart) refPart).value.equals(value);
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
        try {
            String v = SFSystem.getProperty((String) value, null);
            if (v==null)
                throw SmartFrogResolutionException.notFound (r,null);
            return v;
        } catch (SmartFrogConstraintBacktrackError sfcbe){
        	throw sfcbe;
        } catch (Throwable ex) {
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward (ex.toString(),r,ex);
        }
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
          String v = SFSystem.getProperty((String) value, null);
            if (v==null)
                throw SmartFrogResolutionException.notFound (r,null);
          return v;
        } catch (SmartFrogConstraintBacktrackError sfcbe){
        	throw sfcbe;
        } catch (Throwable ex) {
            throw (SmartFrogResolutionException)SmartFrogResolutionException.forward (ex.toString(),r,ex);
        }
    }
}
