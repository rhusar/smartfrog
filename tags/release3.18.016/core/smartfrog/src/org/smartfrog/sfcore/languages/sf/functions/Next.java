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

/**
 * Defines the Next function that returns a monotonically increasing value, 
 * guaranteed never to return the same number twice within a single description.
 */ 
public class Next extends BaseFunction {
    /**
     * default value of last.
     */
    static int last = -1;
    /**
     * Returns the next integer in the sequence.
     * @return next integer object
     */
    protected Object doFunction() {
        int base;

        if (context.containsKey("base")) {
            base = ((Integer) context.get("base")).intValue();
        } else {
            base = 0;
        }

        if (base > last) {
            last = base;
        } else {
            last++;
        }

        return new Integer(last);
    }
}
