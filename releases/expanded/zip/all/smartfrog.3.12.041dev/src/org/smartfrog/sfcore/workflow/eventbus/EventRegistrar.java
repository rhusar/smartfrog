/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.workflow.eventbus;

import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.util.Vector;
import java.rmi.RemoteException;

/**
 * Helper to manage event registration/deregestration
 */
public class EventRegistrar implements EventRegistration,
        EventBus {

    private LogSF log;
    private PrimImpl owner;
    private EventSink ownerSink;
    private Vector<EventRegistration> receiveFrom = new Vector<EventRegistration>();
    private Vector<EventSink> sendTo = new Vector<EventSink>();
    private String name;

    private LogSF sfLog() {
        return log;
    }

    /**
     * Create a new registrar
     * @param owner the owner, which must implement EventSink
     */
    public EventRegistrar(PrimImpl owner) {
        this.owner = owner;
        this.log = owner.sfLog();
        ownerSink = (EventSink) owner;
        name = this.owner.sfCompleteNameSafe().toString();
    }

    /**
     * Registers an EventSink for forwarding of events.
     *
     * @param sink EventSink
     * @see EventRegistration
     */
    public synchronized void register(EventSink sink) {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(name + " had registration from " + sink.toString());
        }

        if (sink == this || sink == ownerSink) {
            sfLog().error("Ignoring attempt to add " + name + " as a listener to itself");
        }

        if (!sendTo.contains(sink)) {
            sendTo.addElement(sink);
        }
    }

    /**
     * Deregisters an EventSink for forwarding of events.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink
     * @see EventRegistration
     */
    public synchronized void deregister(EventSink sink) {
        if (sfLog().isDebugEnabled()) {
            sfLog().debug(name + " had deregistration from " + sink.toString());
        }
        sendTo.removeElement(sink);
    }

    /**
     * Register for receiving events from a component
     * @param registration event source
     */
    public synchronized void registerToReceiveFrom(EventRegistration registration) {
        receiveFrom.addElement(registration);
    }

    /**
     * For use in termination
     */
    public synchronized void deregisterFromReceivingAll() {
        for (EventRegistration s : receiveFrom) {
            try {
                s.deregister(ownerSink);
            } catch (RemoteException ex) {
                sfLog().ignore(ex);
            }
        }
    }

    /**
     * Handle an event by sending it to the registered destinations
     *
     * @param event event
     */
    public synchronized void event(Object event) {
        sendEvent(event);
    }

    /**
     * Default implementation of the EventBus sendEvent method to forward all
     * events to registered EventSinks. Errors are ignored.
     *
     * @param event the event to send
     */
    public synchronized void sendEvent(Object event) {
        for (EventSink s : sendTo) {
            try {
                if (sfLog().isDebugEnabled()) {
                    String infoStr = "'" + name + "' sending '" + event + "' to '" + s + "'";
                    sfLog().debug(infoStr);
                }

                s.event(event);
            } catch (Exception ex1) {
                String evStr = "null event";
                if (event != null) {
                    evStr = event.toString() + "[" + event.getClass().toString() + "]";
                }
                String sStr = "null eventSink";
                if (s != null) {
                    sStr = s.toString() + "[" + s.getClass().toString() + "]";
                }
                if (sfLog().isErrorEnabled()) {
                    sfLog().error("Failed to send event: '" + evStr + "' to '" + sStr + "', cause: " + ex1.getMessage(), ex1);
                }
            }
        }
    }

    /**
     * Get the receive from vector. This is not cloned; it is raw
     * @return the receiveFrom vector
     */
    public Vector<EventRegistration> getReceiveFrom() {
        return receiveFrom;
    }

    /**
     * The SendTo Vector This is not cloned; it is raw
     * @return all the targets to send to
     */
    public Vector<EventSink> getSendTo() {
        return sendTo;
    }
}


