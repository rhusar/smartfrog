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

import org.smartfrog.sfcore.workflow.conditional.ConditionCompound;
import org.smartfrog.sfcore.workflow.conditional.Condition;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.util.Enumeration;

/**
 * This is an abstract base class for compound conditions; those that nest
 * other conditions internally -and, or, xor, etc.
 * created 30-Nov-2006 14:13:51
 */

public abstract class AbstractCompoundCondition extends ConditionCompound {


    protected AbstractCompoundCondition() throws RemoteException {
    }


    protected boolean accumulator;

    /**
     * Override point; reset the accumulator to its starting value
     */
    protected void resetAccumulator() {
        accumulator=false;
    }

    /**
    * Apply the next part of the operation;
    * @param next the next value to apply to the accumulator
    * @return true if the test is to continue; false if short circuiting indicates we could exit now
    */
    protected abstract boolean apply(boolean next);

    /**
    * Starts the component by deploying the condition
    *
    * @throws SmartFrogException  in case of problems creating the child
    * @throws RemoteException In case of network/rmi error
    */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //create all the children
        synchCreateChildren();
        verifyConditional();
    }

    /**
     * Check that everything is a condition
     * @throws RemoteException for network problems
     * @throws SmartFrogException for any other problem
     */
    public synchronized void verifyConditional() throws RemoteException, SmartFrogException {
    	for (Prim prim:sfChildList()) {
    		if (!(prim instanceof Condition)) {
    			//but require everything to be a condition
    			throw new SmartFrogDeploymentException("Not a Condition", prim);
    		}
        }
    }


    /**
     * Stop the parent from deploying a condition
     * @throws SmartFrogResolutionException not thrown
     * @throws SmartFrogDeploymentException not thrown 
     * @throws RemoteException for network problems
     * @throws SmartFrogException for any other problem
     */
    protected void deployCondition()
            throws SmartFrogResolutionException, RemoteException, SmartFrogDeploymentException {
        //do nothing
    }

    /**
     * Evaluate the condition by delegating to the underlying condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException for network problems
     * @throws SmartFrogException for any other problem
     */
    public synchronized boolean evaluate() throws RemoteException, SmartFrogException {
        Enumeration children = sfChildren();
        boolean keepGoing = true;
        resetAccumulator();
        while (keepGoing && children.hasMoreElements()) {
            Object o = children.nextElement();
            if (o instanceof Condition) {
                Condition cond = (Condition) o;
                boolean next = cond.evaluate();
                keepGoing = apply(next);
            }
        }
        return accumulator;
    }
}
