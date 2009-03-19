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
package org.smartfrog.sfcore.workflow.conditional;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;

import java.rmi.RemoteException;

/**
 * The base conditional event compound deploys itself and, on startup the condition. Evaluation
 * of the condition is left to the childrend
 * created 29-Nov-2006 11:08:41
 */

public class ConditionCompound extends EventCompoundImpl implements Conditional, Condition {

    //the deployed condition
    private Condition condition;


    public ConditionCompound() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     *
     * @return false
     */
    protected boolean isOldNotationSupported() {
        return false;
    }

    /**
     * Starts the component by deploying the condition
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        deployConditionAtStartup();
    }

    /**
     * Override point: where the condition is deployed at startup.
     * The default action is to call {@link #deployCondition()}
     *
     * @throws SmartFrogException in case of problems creating the child
     * @throws RemoteException    In case of network/rmi error
     */
    protected void deployConditionAtStartup() throws SmartFrogException, RemoteException {
        deployCondition();
    }

    /**
     * Override point: is the condition required. IF not, there is no attempt to deploy it at startup
     *
     * @return true
     */
    protected boolean isConditionRequired() {
        return true;
    }

    /**
     * Deploy the condition.
     *
     * @throws SmartFrogResolutionException if the condition is required, and it is not there, or the condition
     *                                      itself fails to deploy.
     * @throws RemoteException              network problems
     * @throws SmartFrogDeploymentException deployment problems
     * @see #isConditionRequired()
     */
    protected void deployCondition() throws SmartFrogResolutionException, RemoteException, SmartFrogDeploymentException {
        condition = (Condition) deployChildCD(ATTR_CONDITION, isConditionRequired());
    }

    /**
     * Get the condition
     *
     * @return the deployed condition or null if there is none active
     */
    public Condition getCondition() {
        return condition;
    }


    /**
     * Evaluate the condition by delegating to the underlying condition.
     * throws an exception if there is no deployed condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public synchronized boolean evaluate() throws RemoteException, SmartFrogException {
        if (condition == null) {
            return onEvaluateNoCondition();
        }
        return condition.evaluate();
    }

    /**
     * Override point. Handle a missing condition
     *
     * @return the value of the condition.
     * @throws SmartFrogException throws an exception as there is no deployed condition.
     */
    protected boolean onEvaluateNoCondition() throws SmartFrogException {
        throw new SmartFrogException("Cannot evaluate the condition as it is not present, or has not been deployed");
    }

    /**
     * called after our work is done to trigger termination if the sfTerminate attribute
     * recommends it.
     */
    protected synchronized void finish() {
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(TerminationRecord.normal(getName()));
    }


}
