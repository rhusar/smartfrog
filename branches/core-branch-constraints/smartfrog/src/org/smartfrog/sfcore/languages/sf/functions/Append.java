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

package org.smartfrog.sfcore.languages.sf.functions;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Defines the Append function that takes the vector attributes and puts them
 * together into a vector. Ignores non-vector attributes.
 */
public class Append extends BaseFunction {
    /**
     * Creates a vector containing all the context vector's elements.
     *
     * @return Vector
     */
    protected Object doFunction() {
        Vector result = new Vector();
        Object objValue;
        Vector value;

        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            objValue = context.get(key); 

            if (objValue instanceof Vector) {
                value = (Vector) objValue;
                for (Enumeration v = value.elements(); v.hasMoreElements();) {
                    result.add(v.nextElement());
                }
            }
        }

        return result;
    }
}
