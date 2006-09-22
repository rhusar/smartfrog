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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.logging.LogSF;

import java.rmi.RemoteException;
import java.lang.ref.WeakReference;

/**
 * created 22-Sep-2006 16:43:35
 */

public class TestCompoundImpl extends EventCompoundImpl implements TestCompound {
    protected Prim teardown;

    protected Prim assertions;

    protected static final String ACTION_RUNNING = "_actionRunning";
    private long undeployAfter;
    private boolean expectTerminate;
    private DelayedTerminator actionTerminator;

    public TestCompoundImpl() throws RemoteException {
    }

    /**
     * Deploys and reads the basic configuration of the component.
     * Overrides EventCompoundImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                         In case of any error while
     *                         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        checkActionDefined();
        name = sfCompleteNameSafe();
        teardown=sfResolve(ATTR_TEARDOWN,teardown,false);
        if(teardown!=null) {
            throw new SmartFrogException("Not yet supported "+ATTR_TEARDOWN);
        }
        assertions = sfResolve(ATTR_ASSERTIONS, assertions, false);
        if (assertions != null) {
            throw new SmartFrogException("Not yet supported " + ATTR_ASSERTIONS);
        }
        undeployAfter = sfResolve(ATTR_UNDEPLOY_AFTER, 0,true);
        expectTerminate = sfResolve(ATTR_EXPECT_TERMINATE,false,true);
    }

    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        LogSF logSF = sfLog();
        //TODO: deploy the action under a terminator, then the assertions, finally teardown afterwards.
        Prim child =deployAction();
        actionTerminator = new DelayedTerminator(child, undeployAfter, sfLog(), null, !expectTerminate);
    }

    protected Prim deployAction() throws RemoteException, SmartFrogDeploymentException {
        Prim child = sfCreateNewChild(ACTION_RUNNING, action, null);
        return child;
    }

}
