/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.scripting.javax;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.workflow.conditional.Condition;

import java.rmi.RemoteException;

/**
 * A condition with accessors for the script to read/write the value
 */


public interface JavaxScriptingCondition extends Condition {

    /**
     * The attribute which is set for a true condition: self.condition = true
     */
    String ATTR_CONDITION = "condition";


    /**
     * Evaluate the condition status
     *
     * @return true iff the condition is set
     * @throws RemoteException network problems
     * @throws SmartFrogException SF problems
     */
    boolean isCondition() throws RemoteException, SmartFrogException;

    /**
     * Set the condition attribute
     *
     * @param condition new value
     * @throws RemoteException network problems
     * @throws SmartFrogException SF problems
     */
    void setCondition(boolean condition) throws RemoteException, SmartFrogException;
}
