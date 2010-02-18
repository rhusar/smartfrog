/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.amazon.workflow;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.workflow.conditional.Conditional;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.rmi.RemoteException;

/**
 * <p/>
 * Created: 09-Apr-2008
 */
public class IsWorkCompletedImpl
        extends PrimImpl implements Condition {
    private CompletableWork work;

    public IsWorkCompletedImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Prim target = sfResolve("target", (Prim) null, true);
        work = (CompletableWork) target;
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        if (work == null) {
            throw new SmartFrogDeploymentException("Not bound to a target", this);
        }
        return work.isWorkCompleted();
    }
}
