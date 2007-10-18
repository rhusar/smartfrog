/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.workflow.components;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventCompoundImpl;


/**
 * This component waits for a event to arrive, deploys the appropriate event
 * handler and when this terminates, also terminate. OnEvent is normally used
 * as part of a sequence of other components to provide synchronization.
 * Attributes are documented in onEvent.sf
 */
public class OnEvent extends EventCompoundImpl implements Compound {

    /* whether to handle a single event, or multiple */
    private boolean singleEvent = true;

    /* whether the event handler should continue to handle events */
    private boolean finished = false;

    /* ensure that each child event handler is differently named */
    private int index = 0;
    public static final String ATTR_OTHERWISE = "otherwise";

    /**
     * Constructs OnEvent.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public OnEvent() throws RemoteException {
        super();
    }

    /**
     * On receipt of any event, obtains handler and deploys.
     *
     * @param event event to be handled
     */
    public void handleEvent(Object event) {
        ComponentDescription act;

        try {
            String name = ATTR_OTHERWISE;
            try {
                act = (ComponentDescription)sfResolve(event.toString());
                name = event.toString();
            } catch (SmartFrogResolutionException e) {
                act = (ComponentDescription)sfResolve(name);
            }

            synchronized (this) {
                if (finished) {
                    return;
                }
                if (singleEvent) {
                    finished = true;
                }
            }

            sfCreateNewChild(name+index++, act, null);

        } catch (SmartFrogResolutionException e) {
            // no handler - log and ignore
            if (sfLog().isIgnoreEnabled()) {
                sfLog().ignore(sfCompleteNameSafe()+ " - ignoring unknown event "+event, e);
            }
        } catch (Exception e) {
            // error in  handler - terminate...
            if (sfLog().isErrorEnabled()) {
                sfLog().error(sfCompleteNameSafe()+ " - error in event handler for event "+event, e);
            }
            sfTerminate(TerminationRecord.abnormal("error in event handler for event "+event, null));
        }
    }

    public synchronized void sfDeploy() throws SmartFrogException,
        RemoteException {
        super.sfDeploy();
        singleEvent = sfResolve("singleEvent", true, false);
    }

    public synchronized void sfTerminateWith(TerminationRecord tr) {
        finished = true;
        super.sfTerminateWith(tr);
    }


    /**
     * It is invoked by sub-components at termination. If normal termiantion,
     * and OnEvent is not in single event mode, then it simply accepts
     * the child termination. Otherwsie it terminates itself as well,
     * propagating child event handler termination status.
     *
     * @param status termination status of sender
     * @param comp sender of termination
     */
    public void sfTerminatedWith(TerminationRecord status, Prim comp) {
        if (sfContainsChild(comp)) {
            try {
                if (singleEvent) {
                    super.sfTerminatedWith(status, comp);
                    return;
                }

                if (!(status.errorType.equals("normal".intern()))) {
                    super.sfTerminatedWith(status, comp);
                } else {
                    sfRemoveChild(comp);
                }
            } catch (Exception e) {
                if (sfLog().isErrorEnabled()) {
                    sfLog().error(sfCompleteNameSafe()+ " - error handling child event handler termination ", e);
                }
                sfTerminate(TerminationRecord.abnormal( "error handling child event handler termination "+e, sfCompleteNameSafe()));
            }
        }
    }
}
