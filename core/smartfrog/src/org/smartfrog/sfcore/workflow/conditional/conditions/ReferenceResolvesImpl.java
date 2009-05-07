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
package org.smartfrog.sfcore.workflow.conditional.conditions;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.rmi.RemoteException;

/**
 * This condition checks that a reference resolves, if not it fails. Useful in waiting/testing for values
 * <p/>
 * Created 07-May-2009 15:20:51
 */

public class ReferenceResolvesImpl extends AbstractConditionPrim {

    /** {@value} */
    public static final String ATTR_REFERENCE ="reference";
    /** {@value} */
    public static final String ATTR_RESULT = "result";

    public ReferenceResolvesImpl() throws RemoteException {
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            Object result = sfResolve(ATTR_REFERENCE, true);
            sfReplaceAttribute(ATTR_RESULT,result);
            return true;
        } catch (SmartFrogResolutionException e) {
            setFailureCause(e);
            return false;
        } catch (RemoteException e) {
            setFailureCause(e);
            return false;
        }
    }
}
