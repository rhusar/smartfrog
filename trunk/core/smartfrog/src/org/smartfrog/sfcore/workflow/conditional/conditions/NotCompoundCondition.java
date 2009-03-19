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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.workflow.conditional.Conditional;

import java.rmi.RemoteException;

/**
 * created 30-Nov-2006 15:59:15
 */

public class NotCompoundCondition extends ConditionCompound implements Conditional {


    public NotCompoundCondition() throws RemoteException {
    }


    /**
     * The Not condition is the opposite of a normal condition.
     * So we delegate to super.evaluate(), then invert the result.
     *
     * @return true if it is successful, false if not
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized boolean evaluate() throws RemoteException, SmartFrogException {
        return !super.evaluate();
    }
}
