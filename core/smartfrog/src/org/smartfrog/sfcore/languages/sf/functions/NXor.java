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

import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;

/**
 * Defines the conjunction function 
 */
public class NXor extends BaseOperator {
    /**
     * Conjoins two booleans.
     * @param a first boolean
     * @param b second boolean
     *
     * @return the conjunction
     * @throws SmartFrogFunctionResolutionException if the types of the attributes are not correct
     */
    protected Object doOperator(Object a, Object b) throws SmartFrogFunctionResolutionException {
    if (!(a instanceof Boolean))
        throw new SmartFrogFunctionResolutionException(MessageUtil.formatMessage("ILLEGAL_BOOLEAN_PARAMETER"),
                              null, name,a.getClass().toString() + " (" + a + ")");
    if (!(b instanceof Boolean))
        throw new SmartFrogFunctionResolutionException(MessageUtil.formatMessage("ILLEGAL_BOOLEAN_PARAMETER"),
                              null, name, b.getClass().toString() + " (" + b + ")");

    return Boolean.valueOf(((Boolean) a).booleanValue()==((Boolean) b).booleanValue());
    }
}
