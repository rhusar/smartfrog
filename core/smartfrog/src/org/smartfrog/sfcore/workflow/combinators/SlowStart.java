/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
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
    private Prim actionPrim;

    public SlowStart() throws RemoteException {

    }


    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();
        timeout=sfResolve(ATTR_TIMEOUT, 0, true);
        endTime=System.currentTimeMillis()+timeout;
        //deploy the action
        actionPrim = deployChildCD(ATTR_ACTION, true);
    }


    protected void sfPingChild(Liveness child)
        throws SmartFrogLivenessException, RemoteException {
        if (!live) {
            long now = System.currentTimeMillis();
            if (now > endTime) {
                //timeout time is reached, time to go live
                sfLog().info("going live at end of timeout");
                live = true;
            }
        }
        try {
            super.sfPingChild(child);
            // if we get here, liveness kicks in
            if(!live) {
                sfLog().info("child is live");
                live=true;
            }
        } catch (SmartFrogLivenessException e) {
            if(live) {
                //rethrow the exception when we are live
                throw e;
            }
        }
    }
}
