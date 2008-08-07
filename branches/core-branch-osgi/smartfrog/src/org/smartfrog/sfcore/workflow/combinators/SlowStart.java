/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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


package org.smartfrog.sfcore.workflow.combinators;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;

import java.rmi.RemoteException;

/**
 * A component that starts its child on deployment, but gives it a certain amount of time for it to start being live,
 * before liveness failures of the child are propagated up.
 *
 * Once the child succeeds its liveness once, the compound considers itself 'live' and from then on liveness tests are
 * relayed.
 */
public class SlowStart extends EventCompoundImpl implements Compound {

    public static final String ATTR_DELAY = "delay";
    private int timeout;
    private long endTime;
    private boolean live = false;
    private Prim actionPrim;

    public SlowStart() throws RemoteException {

    }


    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        timeout = sfResolve(ATTR_DELAY, 0, true);
        endTime = System.currentTimeMillis() + timeout;
        //deploy the action
        actionPrim = deployChildCD(ATTR_ACTION, true);
    }


    protected void sfPingChild(Liveness child)
            throws SmartFrogLivenessException, RemoteException {
        if (!live) {
            long now = System.currentTimeMillis();
            if (now > endTime) {
                //timeout time is reached, time to go live
                sfLog().info("Going live at end of timeout");
                live = true;
            }
        }
        try {
            super.sfPingChild(child);
            // if we get here, liveness kicks in
            if (!live) {
                sfLog().info("Child is now live");
                live = true;
            }
        } catch (SmartFrogLivenessException e) {
            if (live) {
                //rethrow the exception when we are live
                throw e;
            } else {
                sfLog().ignore("We are not yet live", e);
            }
        }
    }
}
