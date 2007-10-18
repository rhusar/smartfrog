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
 * Defines the function that  divides its "left" attribute by its "right" attribute.
 * Each of its attributes mst be a number.
 */
public class Divide extends BaseBinaryOperator {
    /**
     * Sums two numbers.
     * @param a first number
     * @param b second number
     * @return division
     * @throws SmartFrogFunctionResolutionException if one of the parameters is not a number
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
        return new Double(((Double) a).doubleValue() / ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Double(((Double) a).doubleValue() / ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Double(((Double) a).doubleValue() / ((Long) b).longValue());
        else
        return new Double(((Double) a).doubleValue() / ((Integer) b).intValue());

    } else if (a instanceof Float) {
        if (b instanceof Double)
        return new Double(((Float) a).floatValue() / ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Float(((Float) a).floatValue() / ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Float(((Float) a).floatValue() / ((Long) b).longValue());
        else
        return new Float(((Float) a).floatValue() / ((Integer) b).intValue());

    } else if (a instanceof Long) {
        if (b instanceof Double)
        return new Double(((Long) a).longValue() / ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Float(((Long) a).longValue() / ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Long(((Long) a).longValue() / ((Long) b).longValue());
        else
        return new Long(((Long) a).longValue() / ((Integer) b).intValue());
    } else {
        if (b instanceof Double)
        return new Double(((Integer) a).intValue() / ((Double) b).doubleValue());
        else if (b instanceof Float)
        return new Float(((Integer) a).intValue() / ((Float) b).floatValue());
        else if (b instanceof Long)
        return new Long(((Integer) a).intValue() / ((Long) b).longValue());
        else
        return new Integer(((Integer) a).intValue() / ((Integer) b).intValue());
    }
    }
}
