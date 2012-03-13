/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.assertions.events;

import org.smartfrog.services.assertions.TestBlock;
import org.smartfrog.services.assertions.TestFailureException;
import org.smartfrog.services.assertions.TestTimeoutException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.RemoteToString;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SecureRemoteObject;
import org.smartfrog.sfcore.security.SmartFrogSecurityException;
import org.smartfrog.sfcore.utils.SmartFrogThread;
import org.smartfrog.sfcore.workflow.eventbus.EventRegistration;
import org.smartfrog.sfcore.workflow.eventbus.EventSink;
import org.smartfrog.sfcore.workflow.events.LifecycleEvent;
import org.smartfrog.sfcore.workflow.events.StartedEvent;
import org.smartfrog.sfcore.workflow.events.TerminatedEvent;

import java.rmi.RemoteException;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Handler for test lifecycle events.
 *
 * This class can register interest in remote events, and subscribe to their lifecycle events.
 *
 * Important: this component does not unsubscribe in its finalizer, because finalizers are so unreliable. Manually
 * {@link #unsubscribe()} or use {@link #unsubscribeQuietly()} in the teardown
 *
 * This tool uses simple RMI to register itself, so that it can be used in JUnit test clients running outside SmartFrog.
 *
 * Important: be very careful with synchronisation here, because RMI calls come in on different threads. It is easy to
 * deadlock, especially during teardown operations.
 */

public final class TestEventSink implements EventSink, RemoteToString {


    /**
     * Queue of incoming events
     */
    private BlockingQueue<LifecycleEvent> incoming = new LinkedBlockingQueue<LifecycleEvent>();

    /**
     * History of events
     */
    private List<LifecycleEvent> history = new ArrayList<LifecycleEvent>();

    /**
     * The source of events
     */
    private volatile EventRegistration source;

    /**
     * This is a remote stub to ourselves, which is set after exporting the instance using RMI
     */
    private volatile RemoteStub remoteStub;


    /**
     * Name used for diagnostics in the {@link #sfRemoteToString()} operation
     */
    private volatile String name = "TestEventSink";

    /**
     * error message : {@value}
     */
    public static final String ERROR_STARTUP_TIMEOUT = "Timeout waiting for the application to start";
    /**
     * error message : {@value}
     */
    public static final String ERROR_TEST_RUN_TIMEOUT = "Timeout waiting for a test run to complete";
    /**
     * error message : {@value}
     */
    private static final String ERROR_PREMATURE_TERMINATION = "Test component terminated before starting up";
    public static final String ERROR_WRONG_TYPE
            = "Cannot cast a component to an EventRegistration instance, as it is of the wrong type: ";

    private Log log = LogFactory.getLog(getClass());

    /** This is the lock used for synchronised operations */
    private final Lock lock = new ReentrantLock();


    /**
     * Simple constructor
     */
    public TestEventSink() {
    }

    /**
     * Create and subscribe
     *
     * @param source source of events
     * @throws RemoteException for network problems
     * @throws SmartFrogSecurityException security problems
     */
    public TestEventSink(EventRegistration source) throws RemoteException, SmartFrogSecurityException {
        subscribe(source);
    }


    /**
     * Cast an application to an EventRegistration interface and subscribe to its events
     *
     * @param application the application (which must implement{@link EventRegistration})
     * @throws RemoteException if something goes wrong with the subscription
     * @throws SmartFrogRuntimeException if the class is the wrong type
     * @throws SmartFrogSecurityException security problems
     */
    public TestEventSink(Prim application) throws RemoteException, SmartFrogRuntimeException, SmartFrogSecurityException {
        if (!(application instanceof EventRegistration)) {
            throw new SmartFrogRuntimeException(ERROR_WRONG_TYPE
                                                + application.getClass(),
                                                application);
        }
        subscribe((EventRegistration) application);
    }

    /**
     * Set the name (for remote diagnostics)
     * @param name new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Return the current name attribute
     * @return the name
     */
    @Override
    public String sfRemoteToString() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Unsubscribe from the current event source
     *
     * @return true if we unsubscribe This is not synchronised, to avoid cross-machine deadlocks.
     * @throws RemoteException for network problems
     */
    public boolean unsubscribe() throws RemoteException {
        boolean shouldUnexport = false;
        if (source != null) {
            EventRegistration registration;
            registration = source;
            source = null;
            remoteStub = null;
            registration.deregister(this);
            shouldUnexport = true;
        }
        return !shouldUnexport || UnicastRemoteObject.unexportObject(this, true);
    }


    /**
     * Unsubscribe from the current event source
     *
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
     * unsubscribe in a different thread
     *
     * @return the thread or null if we were already unsubscribed
     */
    public SmartFrogThread asyncUnsubscribe() {
        if (!isListening()) {
            return null;
        }
        SmartFrogThread thread = new SmartFrogThread(new AsyncUnsubscribe(this));
        thread.setName(name + "unsubscriber");
        thread.start();
        return thread;
    }

    /**
     * Export self and subscribe to an event source.
     *
     * @param target what to subscribe to
     * @throws RemoteException for network problems
     */
    private void subscribe(EventRegistration target) throws RemoteException, SmartFrogSecurityException {
        synchronized (this) {
            if (source != null) {
                throw new IllegalStateException("Cannot subscribe more than once");
            }
            try {
                remoteStub = (RemoteStub) SecureRemoteObject.exportObject(this, 0);
            } catch (SFGeneralSecurityException e) {
                throw new SmartFrogSecurityException(e);
            }
            setSource(target);
        }
        if (target != null) {
            target.register(this);
        }
    }

    public EventRegistration getSource() {
        return source;
    }


    /**
     * Is this sink listening
     *
     * @return true iff the source is not null
     */
    public boolean isListening() {
        return source != null;
    }

    /**
     * Get the application/event source we are bonded to.
     *
     * @return the application
     */
    public Prim getApplication() {
        return (Prim) source;
    }

    /**
     * Set the source
     *
     * @param source source
     */
    private void setSource(EventRegistration source) {
        this.source = source;
    }


    /**
     * Get the remote stub of this object
     *
     * @return the remote stub (which is null when we are not exported)
     */
    public RemoteStub getRemoteStub() {
        return remoteStub;
    }

    /**
     * return the object at the head of the event queue, or null. The event is removed from the queue and added to the
     * history. This operation blocks for the specified timeout
     *
     * @return the polled object.
     */
    private LifecycleEvent poll(long timeout) throws InterruptedException {
        LifecycleEvent event = incoming.poll(timeout, TimeUnit.MILLISECONDS);
        if (event != null) {
            history.add(event);
        }
        return event;
    }


    /**
     * Get at the history of past events
     *
     * @return the history
     */
    public List<LifecycleEvent> getHistory() {
        return history;
    }

    /**
     * block for any event
     *
     * @param timeout timeout delay; can be 0
     * @return the polled object or null for a timeout
     * @throws InterruptedException if the thread waiting was interrupted
     */

    public LifecycleEvent waitForEvent(long timeout) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug(toString() + " waiting for an event for " + timeout + " ms");
        }        LifecycleEvent event;
        event = poll(timeout);
        if (log.isDebugEnabled()) {
            log.debug(toString() + " received event: " + event);
        }
        return event;
    }

    /**
     * Wait for an event of a specific type
     *
     * @param clazz   classname
     * @param timeout time to wait between incoming events
     * @return the event or null for a timeout 
     * @throws InterruptedException if the thread waiting was interrupted, or a TestInterruptedEvent was encountered
     */
    public LifecycleEvent waitForEvent(Class clazz, long timeout) throws InterruptedException {
        LifecycleEvent event;
        boolean isNotInstance;
        TimeoutTracker timedout = new TimeoutTracker(timeout);
        do {
            event = waitForEvent(timeout);
            if (event == null) {
                return null;
            }
            if (event instanceof TestInterruptedEvent) {
                throw new InterruptedException();
            }
            isNotInstance = !clazz.isInstance(event);

        } while (isNotInstance && !timedout.isTimedOut());
        return event;
    }


    /**
     * Handle an event by adding it to the log, then raising a notification
     *
     * @param event the received event
     * @throws RemoteException if the event is not a LifecyleEvent
     */
    @Override
    public void event(Object event) throws RemoteException {
        if (!(event instanceof LifecycleEvent)) {
            throw new RemoteException("Only instances of LifecycleEvent are supported");
        }
        incoming.add((LifecycleEvent) event);
    }

    /**
     * Push an interruption onto the event queue.
     * @throws RemoteException on network problems
     */
    public void interrupt() throws RemoteException {
        event(new TestInterruptedEvent());
    }

    /**
     * Deploy by asking the applicaton to deploy
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of Remote/nework error
     */
    public void invokeDeploy() throws SmartFrogException, RemoteException {
        getApplication().sfDeploy();
    }

    /**
     * Deploy by asking the applicaton to deploy
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of Remote/nework error
     */
    public void invokeStart() throws SmartFrogException, RemoteException {
        getApplication().sfStart();
    }

    /**
     * Start the application and block until the component reports itself as started. If the component terminates during
     * this time. If the application is not yet deployed, that is done too.
     *
     * @param timeout time in ms to wait
     * @return the startup event or null if it didn't start
     * @throws SmartFrogException   for deployment problems
     * @throws TestTimeoutException if a timeout occurred
     * @throws TestFailureException if the component terminated before starting up
     * @throws RemoteException      for network problems
     * @throws InterruptedException if the thread waiting was interrupted
     */
    public StartedEvent startApplication(long timeout)
            throws SmartFrogException, RemoteException, InterruptedException {
        Prim app = getApplication();
        Reference appNameRef = app.sfCompleteName();
        if (!app.sfIsDeployed()) {
            invokeDeploy();
        }
        try {
            invokeStart();
        } catch (SmartFrogLifecycleException e) {
            log.warn("On startup: "+ e, e);
            Object termRec;
            TerminationRecord status = null;
            try {
                termRec = app.sfResolveHere("sfTerminateWith", false);
                if (termRec != null && termRec instanceof TerminationRecord) {
                    status = (TerminationRecord) termRec;
                } else {
                    status = TerminationRecord.abnormal("Failure during startup", appNameRef, e);
                }
            } catch (Exception e1) {
                status = TerminationRecord.abnormal("Failure during startup", appNameRef, e);
            }
            TerminatedEvent te = new TerminatedEvent(app, status);
            throw new TestFailureException(ERROR_PREMATURE_TERMINATION + " seen by " + this, te);
        } catch (RemoteException e) {
            String message = ERROR_PREMATURE_TERMINATION + " seen by " + this;
            throw new TestFailureException(message,
                                           new TerminatedEvent(app,
                                                               TerminationRecord.abnormal(message,
                                                               appNameRef,
                                                               e)));
        }
        TimeoutTracker timedout = new TimeoutTracker(timeout);
        LifecycleEvent event;
        do {
            event = waitForEvent(LifecycleEvent.class, timeout);
            if (event == null) {
                String message = ERROR_STARTUP_TIMEOUT + " as seen by " + this
                        +" \nHistory: \n" + dumpHistory();
                throw new TestTimeoutException(message, timeout);
            }
            if (event instanceof TerminatedEvent) {
                throw new TestFailureException(ERROR_PREMATURE_TERMINATION + " seen by " + this, event);
            }
        } while (!(event instanceof StartedEvent) && !timedout.isTimedOut());
        return (StartedEvent) event;
    }

    /**
     * Run the tests to completion, return the lifecycle event
     *
     * @param startupTimeout time to wait in millis for startup
     * @param executeTimeout time to wait in millis for execution
     * @return the {@link TestCompletedEvent} or {@link TerminatedEvent} at the end of the run
     * @throws SmartFrogException   deployment problems including timeout
     * @throws TestTimeoutException if a timeout occurred
     * @throws RemoteException      for network problems
     * @throws InterruptedException if the thread waiting was interrupted
     */
    @SuppressWarnings({"BreakStatement"})
    public LifecycleEvent runTestsToCompletion(long startupTimeout, long executeTimeout)
            throws SmartFrogException, InterruptedException, RemoteException {
        Prim application = getApplication();
        boolean logDebugEnabled = log.isDebugEnabled();
        Reference completeName = application.sfCompleteName();
        if (application.sfIsTerminated()) {
            //we are (somehow) already terminated, so report this as a problem
            LifecycleEvent termEvent = new TerminatedEvent(application,
                    TerminationRecord.abnormal(this + ": test component has already terminated ",
                            completeName));
            return termEvent;
        }
        try {
            if (logDebugEnabled) log.debug("Starting target application " + completeName);
            StartedEvent started = startApplication(startupTimeout);
            if (logDebugEnabled) log.debug("Started target application: " + started);
        } catch (SmartFrogLifecycleException e) {
            //this is caused by a failure to start the application, which invariably triggers
            //termination of one kind or another
            LifecycleEvent termEvent = new TerminatedEvent(application,
                    TerminationRecord.abnormal(this + ": target application terminated during startup",
                            completeName));
            return termEvent;
        } catch (TestFailureException tfe) {
            log.warn("Test Failure Exception " + tfe, tfe);
            //startup failed and was intercepted during startup
            return tfe.getEvent();
        }
        //now deploy the tests if the component is a specific TestBlock
        if (application instanceof TestBlock) {
            if (logDebugEnabled) log.debug("Application is a TestBlock; running its tests");
            TestBlock testBlock = (TestBlock) application;
            testBlock.runTests();
        }


        LifecycleEvent event = null;
        TimeoutTracker timedout = new TimeoutTracker(executeTimeout);
        if (logDebugEnabled) {
            log.debug("Blocking for events from " + completeName
                    + " for " + executeTimeout + " milliseconds");
        }
        while (!timedout.isTimedOut()) {
            event = waitForEvent(LifecycleEvent.class, executeTimeout);
            if (event == null) {
                log.info("Test run timed out after " + executeTimeout + " milliseconds");
                throw new TestTimeoutException(ERROR_TEST_RUN_TIMEOUT
                        + '\n' + dumpHistory(),
                        executeTimeout);
            }
            if (event instanceof TestCompletedEvent) {
                log.info(this + ": TestCompletedEvent received, test run completing");
                break;
            }
            if (event instanceof TerminatedEvent) {
                log.info(this + ": TerminatedEvent received, test run bailing out");
                break;
            }
            if (event instanceof TestInterruptedEvent) {
                log.info(this + ": Test interrupted: "+ event);
                break;
            }
        }
        if (event == null) {
            if (logDebugEnabled) log.debug(this + ": Test run timed out");
            throw new TestTimeoutException(ERROR_TEST_RUN_TIMEOUT + '\n' + dumpHistory(), executeTimeout);
        }
        return event;
    }


    /**
     * Dump the history to a string, one event per line. Used in timeout reports
     *
     * @return the history of recevied events.
     */
    public String dumpHistory() {
        StringBuilder buffer = new StringBuilder(this + ": Event history has " + history.size() + " events\n");
        int counter = 0;
        for (LifecycleEvent event : history) {
            buffer.append('[').append(++counter).append("] ");
            buffer.append(event.toString());
            buffer.append('\n');
        }
        return buffer.toString();
    }


    /**
     * A little source of asynchronous unsubscriptions...this is done to remove re-entrancy on unsub operations.
     */
    private static class AsyncUnsubscribe implements Runnable {

        private TestEventSink owner;
        private RemoteException result;

        private AsyncUnsubscribe(TestEventSink owner) {
            this.owner = owner;
        }

        @Override
        public void run() {
            try {
                owner.unsubscribe();
            } catch (RemoteException e) {
                result = e;
            }
        }


        public RemoteException getResult() {
            return result;
        }
    }
}
