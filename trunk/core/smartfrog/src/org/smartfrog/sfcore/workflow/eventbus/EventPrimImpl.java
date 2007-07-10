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

package org.smartfrog.sfcore.workflow.eventbus;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 * An extension of Prim providing the Primitive SmartFrog Component with the
 * required event handling.
 */
public class EventPrimImpl extends PrimImpl implements EventRegistration,
    EventSink, EventBus, Prim {
    static Reference receiveRef = new Reference("registerWith");
    static Reference sendRef = new Reference("sendTo");
    Vector receiveFrom = new Vector();
    Vector sendTo = new Vector();

    /**
     * Constructs EventPrimImpl.
     *
     * @throws RemoteException In case of RMI or network failure.
     */
    public EventPrimImpl() throws RemoteException {
        super();
    }

    /**
     * Registers an EventSink for forwarding of events.
     *
     * @param sink org.smartfrog.sfcore.workflow.eventbus.EventSink
     * @see EventRegistration
     */
    synchronized public void register(EventSink sink) {
        if (sfLog().isDebugEnabled()) {
           sfLog().debug(sfCompleteNameSafe().toString()  + " had registration from " + sink.toString());
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
    synchronized public void deregister(EventSink sink) {
        if (sfLog().isDebugEnabled()) {
           sfLog().debug(sfCompleteNameSafe().toString()  + " had deregistration from " + sink.toString());
        }
        if (sendTo.contains(sink)) {
            sendTo.removeElement(sink);
        }
    }

    /**
     * Handles the event locally then forwards to all registered EventSinks.
     *
     * @param event java.lang.Object
     */
    synchronized public void event(Object event) {
        handleEvent(event);
        sendEvent(event);
    }

    /**
     * Default implmentation of the event Handler hook to be overridden in
     * sub-classes. The default implementation does nothing.
     *
     * @param event java.lang.Object The event
     */
    protected void handleEvent(Object event) {
        if (sfLog().isDebugEnabled()) {
           sfLog().debug(sfCompleteNameSafe().toString() + " saw " + event);
        }
    }

    /**
     * Default implementation of the EventBus sendEvent method to forward all
     * events to registered EventSinks. Errors are ignored.
     *
     * @param event java.lang.Object
     */
    synchronized public void sendEvent(Object event) {
        for (Enumeration e = sendTo.elements(); e.hasMoreElements();) {
            EventSink s = (EventSink) e.nextElement();
            try {
                if (sfLog().isDebugEnabled()) {
                    String infoStr = "'"+sfCompleteNameSafe().toString()+"' sending '"+ event+"' to '"+s+"'";
                    sfLog().debug(infoStr);
                }

                s.event(event);
            } catch (Exception ex1) {
                String evStr="null event";
                if (event!=null ) {
                    evStr=event.toString()+"["+event.getClass().toString()+"]";
                }
                String sStr="null eventSink";
                if (s!=null ) {
                     sStr=s.toString()+"["+s.getClass().toString()+"]";
                }
                if (sfLog().isErrorEnabled()) {
                   sfLog().error("Failed to send event: '"+evStr+"' to '"+sStr+"', cause: "+ex1.getMessage(),ex1);
               }
            }
        }
    }

    /**
     * Registers components referenced in the SendTo sub-component registers
     * itself with components referenced in the RegisterWith sub-component.
     * Overrides PrimImpl.sfDeploy.
     *
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException In case of any error while
     *         deploying the component
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        /* find local registrations and register them */
        ComponentDescription sends = (ComponentDescription) sfResolve(sendRef);
        Context scxt = sends.sfContext();

        for (Enumeration e = scxt.keys(); e.hasMoreElements();) {
            Object k = e.nextElement();
            Reference l = (Reference) scxt.get(k);
            //Protection against wrong descriptions
            Object s =  sfResolve(l);
            if (s instanceof EventSink){
                sendTo.addElement((EventSink)s);
            } else {
               if (sfLog().isErrorEnabled()){
                   sfLog().error("'"+ l + "' in '"+sendRef+"' does not implement EventSink and cannot be registered.");
               }
            }
        }

        /* find own registrations, and register remotely */
        ComponentDescription regs = (ComponentDescription) sfResolve(receiveRef);
        Context rcxt = regs.sfContext();

        for (Enumeration e = rcxt.keys(); e.hasMoreElements();) {
            Object k = e.nextElement();
            Reference l = (Reference) rcxt.get(k);
            //Protection against wrong descriptions
            Object s = sfResolve(l);
            if (s instanceof EventRegistration){
                receiveFrom.addElement(s);
                ((EventRegistration)s).register(this);
            } else {
               if (sfLog().isErrorEnabled()){
                   sfLog().error("'"+ l + "' in '"+receiveRef+"' does not implement EventRegistration");
               }
            }
        }
    }

    /**
     * Deregisters from all current registrations.
     *
     * @param status Record having termination details of the component
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        /* unregister from all remote registrations */
        for (Enumeration e = receiveFrom.elements(); e.hasMoreElements();) {
            EventRegistration s = (EventRegistration) e.nextElement();

            try {
                s.deregister(this);
            } catch (RemoteException ex) {
                sfLog().error(ex);
            }
        }

        super.sfTerminateWith(status);
    }
}
