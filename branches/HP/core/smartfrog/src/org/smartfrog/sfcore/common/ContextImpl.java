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

package org.smartfrog.sfcore.common;

import java.io.Serializable;
import java.util.Enumeration;


/**
 * Implements the context interface. This implementation relies on the
 * OrderedHashtable class in the Utilities, but another class can be used. The
 * important thing for any implementation is the fact that the order in which
 * entries are added to the context should be maintained even through the
 * enumeration returning methods.
 *
 */
public class ContextImpl extends OrderedHashtable implements Context,
    Serializable {

    /**
     * Creates an empty context with default capacity.
     */
    public ContextImpl() {
    }

    /**
     * Constructs a context with initial capacity and a load trigger for
     * expansion.
     *
     * @param cap initial capacity
     * @param load load capacity trigger
     */
    public ContextImpl(int cap, float load) {
        super(cap, load);
    }

    /**
     * Returns the first key for which the value is the given one.
     *
     * @param value value to look up
     *
     * @return key for value or null if not found
     */
    public Object keyFor(Object value) {
        if (!contains(value)) {
            return null;
        }

        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object theKey = e.nextElement();

            if (get(theKey).equals(value)) {
                return theKey;
            }
        }

        return null;
    }
}
