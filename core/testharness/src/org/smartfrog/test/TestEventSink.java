/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.test;

import org.smartfrog.sfcore.workflow.eventbus.EventSink;
import org.smartfrog.sfcore.workflow.eventbus.EventRegistration;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;

/**
 * Handler for test lifecycle events.
 *
 * This class can register interest in remote events, and subscribe to their lifecycle events.
 *
 * Important: this component does not unsubscribe in its finalizer, because finalizers are so unreliable. Manually
 * {@link #unsubscribe()} or use {@link #unsubscribeQuietly()} in the teardown
 *
 * Created 10-Jul-2007 12:23:27
 */

public class TestEventSink implements EventSink {

    private static Log log = LogFactory.getLog(TestEventSink.class);

    /**
     * for java1.5+; a queue would be much nicer
     */
    private List events=new ArrayList();


    /**
     * The source of events
     */
    private EventRegistration source;


    /**
     * Simple constructor
     */
    public TestEventSink() {
    }

    /**
     * Create and subscribe
     * @param source
     * @throws RemoteException for network problems
     */
    public TestEventSink(EventRegistration source) throws RemoteException {
        subscribe(source);
    }


    /**
     * Cast an application to an EventRegistration interface and subscribe
     * to its events
     * @param application
     */
    public TestEventSink(Prim application) throws RemoteException {
        this((EventRegistration) application);
    }

    /**
     * Unsubscribe from the current event source
     * @throws RemoteException for network problems
     */
    public void unsubscribe() throws RemoteException {
        if(source!=null) {
            source.deregister(this);
        }
    }

    /**
     * Unsubscribe from the current event source
     * Any network problems are logged at warn. level
     */
    public void unsubscribeQuietly() {
        try {
            unsubscribe();
        } catch (RemoteException e) {
            log.warn("When unsubscribing from a remote component",e);
        }
    }

    /**
     * Subscribe to an event source
     * @param target what to subscribe to
     * @throws RemoteException for network problems
     */
    public void subscribe(EventRegistration target) throws RemoteException {
        unsubscribe();
        setSource(target);
        if(target!=null) {
            target.register(this);
        }
    }

    public EventRegistration getSource() {
        return source;
    }

    /**
     * Get the application/event source we are bonded to.
     * @return
     */
    public Prim getApplication() {
        return (Prim)source;
    }

    private void setSource(EventRegistration source) {
        this.source = source;
    }

    /**
     * return the object at the head of the event queue, or null.
     * The event is removed from the queue.
     * @return the polled object.
     */
    public synchronized Object poll() {
        if(events.size()==0) {
            return null;
        } else {
            Object event=events.remove(0);
            return event;
        }
    }

    /**
     * block for any event
     * @param timeout timeout delay; can be 0
     * @return the polled object
     * @throws InterruptedException if the thread waiting was interrupted
     */

    Object waitForEvent(long timeout) throws InterruptedException {
        wait(timeout);
        return poll();
    }

    /**
     * Handle an event by adding it to the log, then raising a notification
     * @param event the received event
     */

    public void event(Object event)  {
        synchronized(this) {
            events.add(event);
            notifyAll();
        }
    }

    public void invokeDeploy() throws SmartFrogException, RemoteException {
        getApplication().sfDeploy();
    }

    public void invokeStart() throws SmartFrogException, RemoteException {
        getApplication().sfStart();
    }

}
