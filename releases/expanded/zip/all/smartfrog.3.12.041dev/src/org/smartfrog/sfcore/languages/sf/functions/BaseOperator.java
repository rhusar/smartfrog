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

import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;

/**
 * Defines the baseoperator for the operator functions.
 * The operator is assumed to be left associative (earlier attributes are "left"),
 * and placed between all relevant attributes defined in the
 * component description
 */
public abstract class BaseOperator extends BaseFunction {

    /**
     * The method to implement the functionality of any operator.
     *
     * @param a Object 1 for operator
     * @param b Object 2 for operator
     *
     * @return an Object
     *  */
    protected abstract Object doOperator(Object a, Object b) throws SmartFrogFunctionResolutionException;

    /** Implements the functionality of base operator. */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
        Object result = null;

        for (Enumeration e = context.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            if (result == null) {
                result = context.get(key);
            } else {
                    result = doOperator(result, context.get(key));
            }
        }

        return result;
    }
}
