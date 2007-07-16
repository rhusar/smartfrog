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
package org.smartfrog.services.assertions;

import org.smartfrog.sfcore.workflow.eventbus.EventSink;
import org.smartfrog.sfcore.workflow.eventbus.EventRegistration;
import org.smartfrog.sfcore.workflow.events.StartedEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.assertions.TestCompletedEvent;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.RemoteStub;
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
 *
 * Currently this tool uses simple RMI to register itself. At some point it will need to switch to smartfrog
 * registration when running under a daemon, so that all the security kicks in.
 */

public class TestEventSink implements EventSink {


    /**
     * for java1.5+; a queue would be much nicer
     */
    private List incoming =new ArrayList();

    private List history=new ArrayList();

    /**
     * The source of events
     */
    private EventRegistration source;
    public static final String ERROR_STARTUP_TIMEOUT = "Timeout waiting for the application to start";
    public static final String ERROR_TEST_RUN_TIMEOUT = "Timeout waiting for a test run to complete";
    private RemoteStub remoteStub;
    private static final String ERROR_PREMATURE_TERMINATION = "Test component terminated before starting up";


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
     * @return true if we unsubscribe
     */
    public synchronized boolean unsubscribe() throws RemoteException {
        boolean shouldUnexport=false;
        if(source!=null) {
            source.deregister(this);
            source=null;
            remoteStub = null;
            shouldUnexport=true;
        }
        if(shouldUnexport) {
            return UnicastRemoteObject.unexportObject(this,true);
        } else {
            return true;
        }
    }

    /**
     * Unsubscribe from the current event source
     * @return true if we unsubscribe
     */
    public boolean unsubscribeQuietly() {
        try {
            return unsubscribe();
        } catch (RemoteException ignored) {
            return false;
        }
    }

    /**
     * Subscribe to an event source
     * @param target what to subscribe to
     * @throws RemoteException for network problems
     */
    public synchronized void subscribe(EventRegistration target) throws RemoteException {
        unsubscribe();
        remoteStub = UnicastRemoteObject.exportObject(this);
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


    public RemoteStub getRemoteStub() {
        return remoteStub;
    }

    /**
     * return the object at the head of the event queue, or null.
     * The event is removed from the queue.
     * @return the polled object.
     */
    public synchronized Object poll() {
        if(incoming.size()==0) {
            return null;
        } else {
            Object event= incoming.remove(0);
            history.add(event);
            return event;
        }
    }


    /**
     * Get at the history of past events
     * @return
     */
    public List getHistory() {
        return history;
    }

    /**
     * block for any event
     * @param timeout timeout delay; can be 0
     * @return the polled object or null for a timeout
     * @throws InterruptedException if the thread waiting was interrupted
     */

    public synchronized Object waitForEvent(long timeout) throws InterruptedException {
        Object event;
        event = poll();
        if(event==null) {
            wait(timeout);
            event = poll();
            if (event == null) {
                return null;
            }
        }
        return event;
    }

    /**
     * Wait for an event of a specific type
     * @param clazz classname
     * @param timeout time to wait between incoming events
     * @return the event or null for a timeout
     * @throws InterruptedException if the thread waiting was interrupted
     */
    public synchronized Object waitForEvent(Class clazz,long timeout) throws InterruptedException {
        Object event;
        do {
            event=waitForEvent(timeout);
            if(event==null) {
                return null;
            }
        } while (!clazz.isInstance(event));
        return event;
    }


    /**
     * Handle an event by adding it to the log, then raising a notification
     * @param event the received event
     */

    public void event(Object event)  {
        synchronized(this) {
            incoming.add(event);
            notifyAll();
        }
    }

    public void invokeDeploy() throws SmartFrogException, RemoteException {
        getApplication().sfDeploy();
    }

    public void invokeStart() throws SmartFrogException, RemoteException {
        getApplication().sfStart();
    }

    /**
     * Start the application and block until the component reports itself as started.
     * If the component terminates during this time,
     * @param timeout time in ms to wait
     * @return the startup event or null if it didn't start
     * @throws SmartFrogException for deployment problems
     * @throws TestTimeoutException if a timeout occurred
     * @throws TestFailureException if the component terminated before starting up
     * @throws RemoteException  for network problems
     * @throws InterruptedException if the thread waiting was interrupted
     */
    public StartedEvent startApplication(long timeout) throws SmartFrogException, RemoteException, InterruptedException {
        invokeDeploy();
        invokeStart();
        LifecycleEvent event;
        do {
            event = (LifecycleEvent) waitForEvent(LifecycleEvent.class, timeout);
            if(event instanceof TerminatedEvent) {
                throw new TestFailureException(ERROR_PREMATURE_TERMINATION,event);
            }
        } while(event!=null && !(event instanceof StartedEvent));
        if(event==null) {
            throw new TestTimeoutException(ERROR_STARTUP_TIMEOUT, timeout);
        }
        return (StartedEvent) event;
    }

    /**
     * Run the tests to completion, return the lifecycle event
     * @param startupTimeout time to wait in millis for startup
     * @param executeTimeout time to wait in millis for execution
     * @return the {@link TestCompletedEvent} or {@link TerminatedEvent} at the end of the run
     * @throws SmartFrogException deployment problems including timeout
     * @throws TestTimeoutException if a timeout occurred
     * @throws RemoteException  for network problems
     * @throws InterruptedException if the thread waiting was interrupted
     */
    public LifecycleEvent runTestsToCompletion(long startupTimeout,long executeTimeout)
            throws SmartFrogException, InterruptedException, RemoteException {
        startApplication(startupTimeout);
        LifecycleEvent event;
        do {
            event = (LifecycleEvent) waitForEvent(LifecycleEvent.class, executeTimeout);
            if (event == null) {
                throw new TestTimeoutException(ERROR_TEST_RUN_TIMEOUT, executeTimeout);
            }
        } while(!(event instanceof TerminatedEvent) && !(event instanceof TestCompletedEvent));
        return event;
    }


}
