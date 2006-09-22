/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.reference.Reference;

import java.lang.ref.WeakReference;
import java.rmi.RemoteException;

/**
 * Component to shut down a task after a delay.
 * We retain a (weak) reference to a prim. The component can go off teh graph
 * and we can still find it, but we dont preclude distributed GC taking place and killing the reference.
 */
public class DelayedTerminator implements Runnable {
    private long time;
    private WeakReference/*<Prim>*/ primref;
    private Throwable terminationFault;
    private Thread self;
    private boolean shutdown;
    private boolean shouldTerminate = true;
    private LogSF log;
    private String description;
    private boolean normalTermination;


    /**
     * Create a delayed time.
     * If the time is -1, then the wait is for {@link Long.MAX_VALUE}, otherwise it
     * is for as many milliseconds as needed.
     * @param prim
     * @param time
     * @param log
     * @param description
     * @param normalTermination
     */
    public DelayedTerminator(Prim prim, long time, LogSF log, String description, boolean normalTermination) {
        if(time<0) {
            time=Long.MAX_VALUE;
        }
        this.time = time;
        this.primref = new WeakReference/*<Prim>*/(prim);
        this.log = log;
        if (description == null) {
            this.description = "Terminate "
                    + new ComponentHelper(prim).completeNameSafe().toString()
                    + " after " + time + " milliseconds";
        } else {
            this.description = description;
        }
        this.normalTermination = normalTermination;
    }

    public Throwable getTerminationFault() {
        return terminationFault;
    }

    public void setTerminationFault(Throwable terminationFault) {
        this.terminationFault = terminationFault;
    }


    /**
     * Call from the owner to shut down the target.
     * @param shouldTerminate
     * @return
     */
    public synchronized void shutdown(boolean shouldTerminate) throws RemoteException {
        if (self != null) {
            shutdown = true;
            shouldTerminate = true;
            self.interrupt();
        }
    }

    /**
     * this is the thread that runs in the background
     */
    public void run() {

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            //we have been interrupted here.
            log.debug("Interrupted " + description);
        }

        if (shutdown == true) {

            //initiated shutdown
            log.debug("initiated shutdown " + description);
        }

        if (shouldTerminate) {
            //termination time
            Prim target = getTarget();
            if (target == null) {
                log.debug("Target no longer exists for " + description);
            } else {
                TerminationRecord record = createTerminationRecord(target);
                try {
                    target.sfTerminate(record);
                } catch (RemoteException e) {
                    terminationFault = e;
                }
            }
        }

        synchronized (this) {
            //cease to exist
            self = null;
            primref = null;
        }

    }

    private TerminationRecord createTerminationRecord(Prim target) {
        Reference ref = new ComponentHelper(target).completeNameSafe();
        TerminationRecord record = new TerminationRecord(
                normalTermination ? TerminationRecord.NORMAL : TerminationRecord.ABNORMAL,
                "termination by delayed terminator",
                ref);
        return record;
    }

    private Prim getTarget() {
        return (Prim) primref.get();
    }
}
