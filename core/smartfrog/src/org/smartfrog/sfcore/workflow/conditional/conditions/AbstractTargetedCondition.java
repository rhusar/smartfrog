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
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * created 30-Nov-2006 12:48:34
 */

public abstract class AbstractTargetedCondition extends PrimImpl implements TargetedCondition,
        ConditionWithFailureCause {

    private Throwable failureCause;
    private String failureText;

    protected AbstractTargetedCondition() throws RemoteException {
    }

    protected void setFailureCause(String text, Throwable t) {
        failureCause = t;
        setFailureText(text);
    }

    protected void setFailureCause(Throwable t) {
        failureCause = t;
        setFailureText(failureCause.toString());
    }

    public void setFailureText(String failureText) {
        this.failureText = failureText;

    }

    public String getFailureText() throws RemoteException {
        return failureText;
    }

    public Throwable getFailureCause() throws RemoteException {
        return failureCause;
    }

    protected final boolean evalOrFail(boolean state, String failureText) {
        if(!state) {
            setFailureText(failureText);
        }
        return state;
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean resolveTargetOnStartup = sfResolve(ATTR_RESOLVE_TARGET_ON_STARTUP,true,true);
        if(resolveTargetOnStartup) {
            resolveTargetOnStartup();
        }
    }

    /**
     * startup-time resolution
     * @throws RemoteException              for network problems
     * @throws SmartFrogResolutionException if the target does not resolve
     */
    protected void resolveTargetOnStartup() throws SmartFrogResolutionException, RemoteException {
        getTarget();
    }

    /**
     * Get the target.
     * This resolves every time it is called, so conditions may call it repeatedly
     *
     * @return the target prim
     * @throws RemoteException              for network problems
     * @throws SmartFrogResolutionException if the target does not resolve
     */
    public Prim getTarget() throws SmartFrogResolutionException, RemoteException {
        return sfResolve(ATTR_TARGET, (Prim) null, true);
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        throw new SmartFrogException("Not implemented" + getClass());
    }
}
