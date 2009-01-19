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

import java.util.Vector;
import org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException;

import java.util.Enumeration;

/**
 * Function that validates all the attributes that represent assertions - these are attributes that do
 * not start with the prefix "sf".
 */
public class CheckAssertions extends BaseFunction {
    /**
     * Check all attributes that represent assertions - these are attributes that do
     * not start with the prefix "sf" .
     *
     * @return the conjunction
     * @throws org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException if an assertion is not "true"
     */
    protected Object doFunction() throws SmartFrogAssertionResolutionException {

        Vector failedAssertions = new Vector();
        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            Object value = context.get(key);

            if (!key.toString().startsWith("sf")) {
                if (value instanceof Boolean) {
                    if (!((Boolean) value).booleanValue()) {
                        failedAssertions.add(key.toString() + ": evaluates to false");
                    }
                } else {
                    failedAssertions.add(key + ": has non-boolean " + value.getClass());
                }
            }
        }
        if (failedAssertions.size() > 0) {
            throw new SmartFrogAssertionResolutionException("assertions have failed for: " + failedAssertions);
        } else {
            return Boolean.TRUE;
        }
    }
}
