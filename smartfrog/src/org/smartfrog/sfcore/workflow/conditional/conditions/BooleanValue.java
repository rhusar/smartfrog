/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.workflow.conditional.conditions;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * The BooleanValue condition returns whatever is in the ATTR_VALUE attribute
 * at the time of evaluation. it is re-evaluated every test, so in a WaitFor operation
 * it can be linked to something else that is changing
 */

public class BooleanValue extends PrimImpl implements Condition {

    public static final String ATTR_VALUE="value";


    public BooleanValue() throws RemoteException {
    }

    /**
     * Evaluate the condition.
     *
     * @return true always
     * @throws java.rmi.RemoteException for network problems
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        boolean value = sfResolve(ATTR_VALUE, false, true);
        return value;
    }
}
