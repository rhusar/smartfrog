package org.smartfrog.services.cddlm.cdl.base;

import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * This component logs events.
 */
public class LifecycleLoggerImpl extends PrimImpl
        implements LifecycleLogger, Iterable {

    private List<LifecycleEvent> events = new ArrayList<LifecycleEvent>(4);

    private LifecycleSource listenTo;

    public LifecycleLoggerImpl() throws RemoteException {
    }

    public int size() {
        return events.size();
    }

    public LifecycleEvent getEvent(int count) {
        return events.get(count);
    }

    public void enterStateNotifying(LifecycleStateEnum newState, String info) {
        LifecycleEvent event = new LifecycleEvent(newState, info);
        events.add(event);
        sfLog().info(event);
    }

    public void enterTerminatedStateNotifying(TerminationRecord record) {
        LifecycleEvent event = new LifecycleEvent(record);
        events.add(event);
        sfLog().info(event);
        //if the listen to source is not null, then we assume it came from there and terminate
        unsubscribe();
    }

    public void clear() {
        events.clear();
    }


    /**
     * Iterate over all events. Not remotable.
     *
     * @return a new iterator
     */
    public ListIterator<LifecycleEvent> iterator() {
        return events.listIterator();
    }

    /**
     * unsubscribe iff the source is not null. all exceptions thrown are ignored
     * but log.
     */
    private synchronized void unsubscribe() {
        if (listenTo != null) {
            try {
                LifecycleSource source = listenTo;
                listenTo = null;
                source.unsubscribe(this);
            } catch (SmartFrogException e) {
                sfLog().ignore("Unsubscribing", e);
            } catch (RemoteException e) {
                sfLog().ignore("Unsubscribing", e);
            }
        }
    }


    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
        Object listenToObject = sfResolve(ATTR_LISTEN_TO, (Object) null, false);
        if (listenToObject != null) {
            if (listenToObject instanceof ComponentDescription) {
                throw new SmartFrogDeploymentException(ATTR_LISTEN_TO + " reference is not yet deployed");
            }
            Prim listenToRef = null;
            listenToRef = sfResolve(ATTR_LISTEN_TO, listenToRef, false);
            if (!(listenToRef instanceof LifecycleSource)) {
                throw new SmartFrogDeploymentException("Not a Lifecycle Source " + listenToRef
                        .sfCompleteName());
            }
            listenTo = (LifecycleSource) listenToRef;
            listenTo.subscribe("", this);
        }
    }

    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();

    }


    public void sfTerminate(TerminationRecord status) {
        super.sfTerminate(status);
        unsubscribe();
    }
}
