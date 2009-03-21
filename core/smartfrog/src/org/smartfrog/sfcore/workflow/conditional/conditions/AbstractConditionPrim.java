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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

public abstract class AbstractConditionPrim extends PrimImpl implements ConditionWithFailureCause {
    private Throwable failureCause;
    private String failureText;

    protected AbstractConditionPrim() throws RemoteException {
    }

    protected void setFailureCause(String text, Throwable t) {
        failureCause = t;
        setFailureText(text);
    }

    /**
     * Set the text cause of failure. The failureText is set to the
     * toString() value
     * @param throwable the cause.
     */
    protected void setFailureCause(Throwable throwable) {
        failureCause = throwable;
        setFailureText(failureCause.toString());
    }

    /**
     * Set the text cause of failure
     * @param failureText failure text
     */
    public void setFailureText(String failureText) {
        this.failureText = failureText;
    }

    /**
     * {@inheritDoc}
     * @throws RemoteException network problems
     */
    public String getFailureText() throws RemoteException {
        return failureText;
    }

    /**
     * {@inheritDoc}
     * @throws RemoteException network problems
     */
    public Throwable getFailureCause() throws RemoteException {
        return failureCause;
    }

    /**
     * Evaluate the variable and set the failure text to the given string if it is false
     * @param state result of the evaluation
     * @param failureText the reason for a failure
     * @return the state that was passed in.
     */
    protected final boolean evalOrFail(boolean state, String failureText) {
        if(!state) {
            setFailureText(failureText);
        }
        return state;
    }

}
