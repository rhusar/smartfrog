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
import java.util.HashSet;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


/**
 * This component waits for events, counts them, and when a certain number have
 * arrived, terminates Attributes are documented in eventCounter.sf.
 */
public class EventCounter extends EventPrimImpl implements Prim {
    private int count = 1;
    private boolean allDifferent = true;
    private HashSet events;
    private Reference id;
    public static final String COUNT = "count";
    public static final String ALL_DIFFERENT = "allDifferent";

    /**
     * Constructs EventCounter.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public EventCounter() throws RemoteException {
        super();
    }
    /**
     * Deploys the component and reads the configuration attributes.
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        count = ((Integer) sfResolve(COUNT)).intValue();
        allDifferent = ((String) sfResolve(ALL_DIFFERENT)).equals("true");

        if (allDifferent) {
            events = new HashSet(count);
        }
        id = sfCompleteName();
        // Should it be replaced with sfCompleteNameSafe()?
    }

    /**
     * On receipt of any event, obtains handler and deploys.
     *
     * @param event The event
     */
    public synchronized void handleEvent(Object event) {
        if (allDifferent) {
            if (!events.contains(event)) {
                count--;
                events.add(event);
            }
        } else {
            count--;
        }
        if (count == 0) {
            sfTerminate(TerminationRecord.normal(id));
        }
    }
}
