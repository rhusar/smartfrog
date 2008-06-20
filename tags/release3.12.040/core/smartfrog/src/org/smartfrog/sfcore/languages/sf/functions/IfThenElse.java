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

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;


/**
 * Defines the IfThenElse function.
 */
public class IfThenElse extends BaseFunction implements MessageKeys {
    private static final String IF = "if";
    private static final String THEN = "then";
    private static final String ELSE = "else";

    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
    Object ifObj = context.get(IF);
    Object elseObj = context.get(ELSE);
    Object thenObj = context.get(THEN);

    if (ifObj == null)
        throw new SmartFrogFunctionResolutionException(
                      MessageUtil.formatMessage(MISSING_PARAMETER, IF),
                      null, name, null);
    if (thenObj == null)
        throw new SmartFrogFunctionResolutionException(
                      MessageUtil.formatMessage(MISSING_PARAMETER, THEN),
                      null, name, null);
    if (elseObj == null)
        throw new SmartFrogFunctionResolutionException(
                      MessageUtil.formatMessage(MISSING_PARAMETER, ELSE),
                      null, name,null);
    if (!(ifObj instanceof Boolean))
        throw new SmartFrogFunctionResolutionException(
                      MessageUtil.formatMessage(ILLEGAL_BOOLEAN_PARAMETER),
                      null, name, "Attribute read: "+ ifObj + " ("+ifObj.getClass().toString() + ")");


    Object result;
    if (((Boolean)ifObj).booleanValue())
        result = thenObj;
    else
        result = elseObj;

        return result;
    }
}
