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
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;

/**
 * Defines the baseoperator for the binary operator functions.
 */
public abstract class BaseBinaryOperator extends BaseFunction implements MessageKeys {

    /**
     * The method to implement the functionality of any operator.
     *
     * @param a Object 1 for operator
     * @param b Object 2 for operator
     *
     * @return an Object
     *  */
    protected abstract Object doOperator(Object a, Object b) throws SmartFrogFunctionResolutionException;


    /** Implements the functionality of base operator.
     *  it expects to find two attributes: left and right
     * if these are not present it will throw an exception
     *
     * @return the result of the operation
     * @throws SmartFrogFunctionResolutionException if either of the required attributes are not present or the operation throws an exception
     */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
    Object left = context.get("left");
    Object right = context.get("right");

    if (left == null)
        throw new SmartFrogFunctionResolutionException(MessageUtil.formatMessage(MISSING_PARAMETER, "left"),
                              null, name, null);
    if (right == null)
        throw new SmartFrogFunctionResolutionException(MessageUtil.formatMessage(MISSING_PARAMETER, "right"),
                              null, name, null);

    return doOperator(left, right);
    }
}
