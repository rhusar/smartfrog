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
 * This component waits for an event to arrive, deploys the appropriate event
 * handler and when this terminates, also terminate. OnEvent is normally used
 * as part of a sequence of other components to provide synchronization.
 * Attributes are documented in onEvent.sf
 */
public class OnEvent extends EventCompoundImpl implements Compound {

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
    public void handleEvent(String event) {
        ComponentDescription act;

        try {
            String name = "otherwise";
            try {
                act = (ComponentDescription) sfResolve(event);
                name = event;
            } catch (SmartFrogResolutionException e) {
                act = (ComponentDescription) sfResolve(name);
            }

	    sfCreateNewChild(name+"_actionRunning", act, null);

        } catch (Exception e) {
            sfTerminate(TerminationRecord.abnormal(
                    "error in event handler for event " + event, null));
        }
    }
}
