

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
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;
import org.smartfrog.sfcore.workflow.eventbus.EventSink;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * An extension of Prim providing the Primitive SmartFrog Component with the
 * required event handling.
 */
public class EventQueue extends EventPrimImpl implements Prim {
    private SenderThread sender = null;

    private class SenderThread extends Thread {
	private boolean finished = false;

	public void finished() {
	    finished = true;
	}

	private void doAll() {
        int index = messageIndex;
        Hashtable t = (Hashtable)registrationMessages.clone(); // save the keys and order...

        for (Enumeration e = t.keys(); e.hasMoreElements(); ) {
            EventSink s = (EventSink)e.nextElement();
            int soFar = ((Integer)t.get(s)).intValue();
            if (soFar<index) {
                for (int i = soFar; i<index; i++) {
                    //send it to the sink;
                    try {
                        s.event(messages.get(i));
                    } catch (Exception ex) {
                        sfLog().error(ex);
                    }
                }
                synchronized (registrationMessages) {
                    registrationMessages.put(s, new Integer(index));
                }
            }
        }
	}

	private boolean allDone() {
    // note: synchronized with messages already
        synchronized (registrationMessages) {
            for (Enumeration e = registrationMessages.keys(); e.hasMoreElements(); ) {
                EventSink s = (EventSink)e.nextElement();
                int soFar = ((Integer)registrationMessages.get(s)).intValue();
                if (soFar<messageIndex) {
                    return false;
                }
            }

        }
        return true;
	}

        public void run() {
            while (!finished) {
                synchronized (messages) {
                    if (allDone()) {
                        try {
                            messages.wait();
                        } catch (InterruptedException e) {
                            //ignored
                        }
                    }
                }
                if (!finished) {
                    doAll();
                }
            }
        }
    }


    private Vector messages = new Vector();
    private int messageIndex = 0;

    private Hashtable registrationMessages = new Hashtable();



    /**
     * Constructs EventQueue.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public EventQueue() throws RemoteException {
        super();
    }



    /**
     * Registers an EventSink for forwarding of events.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink
     * @see org.smartfrog.sfcore.workflow.eventbus.EventRegistration
     */
    public synchronized void register(EventSink sink) {
        super.register(sink);
        if (!registrationMessages.containsKey(sink)) {
            synchronized (messages) {
                synchronized (registrationMessages) {
                    registrationMessages.put(sink, new Integer(0));
                }
                messages.notify();
            }
        }
    }


    /**
     * Deregisters an EventSink for forwarding of events.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink
     * @see org.smartfrog.sfcore.workflow.eventbus.EventRegistration
     */
    public synchronized void deregister(EventSink sink) {
        super.deregister(sink);
        if (registrationMessages.containsKey(sink)) {
            synchronized (registrationMessages) {
                registrationMessages.remove(sink);
            }
        }
    }

    /**
     * Default implementation of the EventBus event method to
     * forward an event to this component
     *
     * @param event java.lang.Object
     */
    public synchronized void event(Object event) {
        synchronized (messages) {
            messages.add(event);
            messageIndex += 1;
            messages.notify();
        }
    }

    /**
     * Registers components referenced in the SendTo sub-component registers
     * itself with components referenced in the RegisterWith sub-component.
     * Also starts forwarding thread.
     * Overrides PrimImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        sender = new SenderThread();
        sender.start();
    }

    /**
     * Deregisters from all current registrations and kills sender thread
     *
     * @param status Record having termination details of the component
     * @param comp The terminated component
     */
    public synchronized void sfTerminateWith(TerminationRecord status, Prim comp) {
        try {
            sender.finished();
            sender.interrupt();
        } catch (Exception e) {
            //ignored
        }

        super.sfTerminatedWith(status, comp);
    }
}
