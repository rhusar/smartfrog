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
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Defines the Vector function that takes the attribute parameters and puts them
 * together into a vector.
 */   
public class Vector extends BaseFunction {
    /**
     * Creates a vector containing all the context elements.
     *
     * @return Vector
     */
    protected Object doFunction() {
        java.util.Vector result = new java.util.Vector();
        Object value;

        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            value = context.get(key);

            if (value != null) {
		if (value instanceof ComponentDescription) ((ComponentDescription) value).setParent(null);
                result.add(value);
            }
        }

        return result;
    }
}
