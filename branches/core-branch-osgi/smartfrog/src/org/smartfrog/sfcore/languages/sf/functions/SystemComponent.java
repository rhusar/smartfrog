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

import org.smartfrog.sfcore.common.SFNull;

/**
 * Defines the System function that takes the attribute "system" and uses
 * its value as the result of the function, or SFNull if not present.
 */
public class SystemComponent extends BaseFunction {
    /**
     * returns the value of the attribute "system" or SFNull if not present
     *
     * @return system
     */
    protected Object doFunction() {
        Object value = context.get("system");
        if (value == null) value = SFNull.get();
        return value;
    }
}