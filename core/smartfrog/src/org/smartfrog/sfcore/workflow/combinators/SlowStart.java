package org.smartfrog.sfcore.workflow.combinators;

import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;

/**
 * A component that starts its child on deployment, but gives
 * it a certain amount of time for it to start being live, before
 * liveness failures of the child are propagated up.
 *
 * Once the child succeeds its liveness once, the compound considers itself
 * 'live' and from then on liveness tests are relayed.
 */
public class SlowStart extends EventCompoundImpl implements Compound {

    public static final String ATTR_TIMEOUT="timeout";
    public static final String ATTR_LIVE="live";
    private int timeout;
    private long endTime;
    private boolean live=false;
    private Prim action;

    public SlowStart() throws RemoteException {

    }


    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();
        timeout=sfResolve(ATTR_TIMEOUT, 0, true);
        endTime=System.currentTimeMillis()+timeout;
        //deploy the action
        action = deployChildCD(ATTR_ACTION, true);
    }


    /**
     *
     */
    protected void sfPingChildren() {
        super.sfPingChildren();
    }


    protected void sfPingChild(Liveness child)
        throws SmartFrogLivenessException, RemoteException {
        if (!live) {
            long now = System.currentTimeMillis();
            if (now > endTime) {
                //timeout time is reached, time to go live
                if (sfLog().isInfoEnabled()) sfLog().info("going live at end of timeout");
                live = true;
            }
        }
        try {
            super.sfPingChild(child);
            // if we get here, liveness kicks in
            if(!live) {
                if (sfLog().isInfoEnabled())  sfLog().info("child is live");
                live=true;
            }
        } catch (SmartFrogLivenessException e) {
            if(live) {
                //rethrow the exception when we are live
                throw e;
            }
        }

        //if(child ==)
    }
}
