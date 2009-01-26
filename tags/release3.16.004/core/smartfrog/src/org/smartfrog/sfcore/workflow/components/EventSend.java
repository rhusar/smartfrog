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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


/**
 * A component used to send an event as part of a sequence of activities. On
 * starting, it sends the event to all registered sinks and then terminates.
 */
public class EventSend extends EventPrimImpl implements Prim {
    Object event = "";
    public static final String EVENT = "event";

    /**
     * Constructs EventSend.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public EventSend() throws RemoteException {
    }

    /**
     * Deploys and identifies the message to send.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        event = sfResolve(EVENT);
    }

    /**
     * Starts and sends the message and terminates.
     *
     * @throws SmartFrogException In case of SmartFrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        sendEvent(event);

        Runnable terminator = new Runnable() {
            public void run() {
                sfTerminate(TerminationRecord.normal(null, null));
            }
        };

        new Thread(terminator).start();
    }
}
