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

import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.MessageUtil;


/**
 * Defines the less than binary opeeration that compares its numeric attributes
 *  left < right
 */
public class LT extends BaseBinaryOperator {
    /**
     * Compares two numbers.
     * @param a first number
     * @param b second number
     * @return boolean whether a<b
     * @throws  SmartFrogFunctionResolutionException if one of the parameters is not a number
     */
    protected Object doOperator(Object a, Object b) throws SmartFrogFunctionResolutionException {
    if (!(a instanceof Number))
        throw new SmartFrogFunctionResolutionException(MessageUtil.formatMessage("ILLEGAL_NUMERIC_PARAMETER"),
                              null, name, a.getClass().toString() + " (" + a + ")");
    if (!(b instanceof Number))
        throw new SmartFrogFunctionResolutionException(MessageUtil.formatMessage("ILLEGAL_NUMERIC_PARAMETER"),
                              null, name, b.getClass().toString() + " (" + b + ")");

    if (a instanceof Double) {
        if (b instanceof Double)
        return new Boolean(((Double) a).doubleValue() < ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Boolean(((Double) a).doubleValue() < ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Boolean(((Double) a).doubleValue() < ((Long) b).longValue());
        else
        return new Boolean(((Double) a).doubleValue() < ((Integer) b).intValue());

    } else if (a instanceof Float) {
        if (b instanceof Double)
        return new Boolean(((Float) a).floatValue() < ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Boolean(((Float) a).floatValue() < ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Boolean(((Float) a).floatValue() < ((Long) b).longValue());
        else
        return new Boolean(((Float) a).floatValue() < ((Integer) b).intValue());

    } else if (a instanceof Long) {
        if (b instanceof Double)
        return new Boolean(((Long) a).longValue() < ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Boolean(((Long) a).longValue() < ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Boolean(((Long) a).longValue() < ((Long) b).longValue());
        else
        return new Boolean(((Long) a).longValue() < ((Integer) b).intValue());
    } else {
        if (b instanceof Double)
        return new Boolean(((Integer) a).intValue() < ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Boolean(((Integer) a).intValue() < ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Boolean(((Integer) a).intValue() < ((Long) b).longValue());
        else
        return new Boolean(((Integer) a).intValue() < ((Integer) b).intValue());
    }
    }
}
