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

package org.smartfrog.sfcore.languages.sf.sfcomponentdescription;

import org.smartfrog.sfcore.reference.Reference;


/**
 * Stores an unresolved value and the source of the unresolved value. This is
 * used by ResolutionState to store unresolved values and their source
 *
 */
public class UnresolvedValue {
    /** Unresolved value. */
    public Object value;

    /** Source of unresolved value. */
    public Reference source;

    /**
     * Constructor.
     *
     * @param val unresolved value
     * @param source source of unresolved value, can be null
     */
    public UnresolvedValue(Object val, Reference source) {
        this.value = val;
        this.source = source;
    }

    /**
     * Hashcode for this object. Uses the hascode of the value
     *
     * @return hascode of value
     */
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Cheks for equality. Two unresolved values are equal if their values are
     * equal and both their sources are the same (null or equal)
     *
     * @param o object to compare against
     *
     * @return true if object are equal else false
     */
    public boolean equals(Object o) {
        try {
            UnresolvedValue u = (UnresolvedValue) o;

            return value.equals(u.value) &&
            (((source == null) && (u.source == null)) ||
            source.equals(u.source));
        } catch (ClassCastException cex) {
            return false;
        }
    }

    /**
     * Prints out a string representation of an unresolved value. Prints out
     * the value, and the source if not null
     *
     * @return string representation of this unresolved value
     */
    public String toString() {
        return value.toString() +
        (((source != null) && (source.size() > 0)) ? (" in: " + source) : "");
    }
}
