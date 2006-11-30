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

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * created 30-Nov-2006 13:26:41
 */

public class TestConditionImpl extends ConditionCompound implements TestCondition {
    private String message;


    public TestConditionImpl() throws RemoteException {

    }

    /**
     * Starts the component by deploying the condition
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  in case of problems creating the child
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        message =sfResolve(ATTR_MESSAGE, "",true);
    }

    protected void startupTest() throws SmartFrogException, RemoteException {
        testCondition();
        finish();
    }

    protected void testCondition() throws SmartFrogException, RemoteException {
        if(!evaluate()) {
            throw new SmartFrogException(message);
        }
    }
}
