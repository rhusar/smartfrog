/** (C) Copyright 2006-2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * created 30-Nov-2006 13:26:41
 */

public class EvaluateConditionImpl extends ConditionCompound implements EvaluateCondition {
    private boolean failOnFalse;


    public EvaluateConditionImpl() throws RemoteException {

    }

    /**
     * Starts the component by deploying the condition
     *
     * @throws SmartFrogException in case of problems
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        failOnFalse = sfResolve(ATTR_FAIL_ON_FALSE, false, true);
        startupTest();
    }

    /**
     * This is the test that runs on startup. Override it to change the startup behaviour
     *
     * @throws SmartFrogException if the test fails and {@link #failOnFalse} is true,
     *                            {@link Condition#evaluate()} trows the exception, or {@link ConditionCompound#finish()} throws it.
     * @throws RemoteException    In case of network/rmi error
     */
    protected void startupTest() throws SmartFrogException, RemoteException {
        testCondition();
        finish();
    }

    /**
     * Test the condition
     *
     * @throws SmartFrogException if the condition evalutes to false and {@link #failOnFalse} is true, or
     *                            {@link Condition#evaluate()} trrows the exception.
     * @throws RemoteException    networking problems
     */
    protected void testCondition() throws SmartFrogException, RemoteException {
        boolean result = evaluate();
        sfReplaceAttribute(ATTR_RESULT, Boolean.valueOf(result));
        if (!result) {
            propagateFailureCause(getCondition());
            if (failOnFalse) {
                String message = sfResolve(ATTR_MESSAGE, "", true);
                if (sfLog().isInfoEnabled()) {
                    sfLog().info("message: " + message);
                }
                throw new SmartFrogException(message);
            }
        }
    }
}
