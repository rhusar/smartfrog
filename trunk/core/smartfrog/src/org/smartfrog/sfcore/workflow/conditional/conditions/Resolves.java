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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * created 30-Nov-2006 11:36:34
 */

public class Resolves extends AbstractTargetedCondition implements TargetedCondition {


    public Resolves() throws RemoteException {
    }

    /**
     * Try to resolve the target
     * @return
     * @throws RemoteException
     * @throws SmartFrogException
     */
    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            Prim target = getTarget();
            return true;
        } catch (SmartFrogResolutionException e) {
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }
}
