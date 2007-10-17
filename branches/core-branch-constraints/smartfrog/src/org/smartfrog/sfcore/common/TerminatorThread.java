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


package org.smartfrog.sfcore.common;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.utils.SmartFrogThread;

import java.rmi.RemoteException;


/**
 * TerminatorThread is used by components for asynchronous termination. Caller
 * component should create the object and then call method start() to start
 * this thread.
 *
 * Example:
 * <pre>
 *       TerminatorThread terminator = new TerminatorThread(targetComponent,
 *              excp, componentId).quietly();
 *       // start the thead
 *       terminator.start();
 * </pre>
 * 
 * It can also be used for synchronous termination in a background thread,
 * in which case the {@link #run()} method can be called directly. 
 * That method has error handling and logging that it is hard to justyf  
 *
 */
public class TerminatorThread extends SmartFrogThread {
    /**
     * Reference to component.
     */
    private Prim target = null;

    /**
     * The object having the details of event that caused the component
     * termination.
     */
    private TerminationRecord record = null;

    /**
     * Boolean flag to indicate that the component should detach before
     * terminating itself. Default: false.
     */
    private boolean shouldDetach    = false; //It will not detach by default

    /**
     * Boolean flag to indicate that the component should terminate itself
     * Default: true
     */
    private boolean shouldTerminate  = true; //It will terminate by default

    /**
     * Boolean flag to indicate that the component should notify its parent
     * before terminating itself. Default: true.
     * true implies a call to {@link Prim#sfTerminate(TerminationRecord)} ,
     * false={@link Prim#sfTerminateQuietlyWith(TerminationRecord)}. 
     */
    private boolean notifyParent    = true;
    private static final String NOTE_POSSIBLY_HARMLESS = "This may be harmless -and caused by the far end closing down";

    /**
     * Constructs the TerminatorThread object using the component reference and
     * termination record.
     *
     * @param target of the component that has to be terminated.
     * @param record record why the termination
     */
    public TerminatorThread(Prim target, TerminationRecord record) {
        setName("TerminatorThread");
        this.target = target;
        this.record = record;
    }

    /**
     * Constructs the TerminatorThread object using the component reference,
     * the exception object component identifier.
     *
     * @param target of the component that has to be terminated.
     * @param thrown exception that caused the termination
     * @param compId component identifier
     */
    public TerminatorThread(Prim target, Throwable thrown, Reference compId) {
        this(target,TerminationRecord.abnormal(thrown.toString(), compId, thrown));
    }



    /**
     * Utility method to stop the TerminatorThread object from 
     * notifying the parent object on termination.
     * @return this
     */
    public TerminatorThread quietly(){
        setNotifyParent(false);
        return this;
    }

    /**
     * Utility method to detach TerminatorThread object from parent.
     *
     * @return TerminatorThread object
     */
    public TerminatorThread detach(){
        setShouldDetach(true);
        return this;
    }

    /**
     * Utility method that does not terminates the TerminatorThread object.
     *
     * @return TerminatorThread object
     */
    public TerminatorThread dontTerminate(){
        setShouldTerminate(false);
        return this;
    }

    public Prim getTarget() {
        return target;
    }

    public void setTarget(Prim target) {
        this.target = target;
    }

    public TerminationRecord getRecord() {
        return record;
    }

    public void setRecord(TerminationRecord record) {
        this.record = record;
    }

    public boolean isShouldDetach() {
        return shouldDetach;
    }

    public void setShouldDetach(boolean shouldDetach) {
        this.shouldDetach = shouldDetach;
    }

    public boolean isShouldTerminate() {
        return shouldTerminate;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }

    public boolean isNotifyParent() {
        return notifyParent;
    }

    public void setNotifyParent(boolean notifyParent) {
        this.notifyParent = notifyParent;
    }

    /**
     * Run method used to trigger the terminatation of the component.
     */
    public synchronized void execute() throws Throwable {
        if (shouldDetach && shouldTerminate && notifyParent) {
            try {
                target.sfDetachAndTerminate(record);
            } catch (RemoteException thr) {
                logThrownException("sfDetachAndTerminate", thr);
            }
            return;
        }
        if (shouldDetach) {
            try {
                target.sfDetach();
            } catch (RemoteException thr) {
                logThrownException("sfDetach", thr);
            }
        }
        if (shouldTerminate) {
            if (notifyParent) {
                try {
                    target.sfTerminate(record);
                } catch (RemoteException thr) {
                    logThrownException("sfTerminate", thr);
                }

            } else {
                try {
                    target.sfTerminateQuietlyWith(record);
                } catch (RemoteException thr) {
                    logThrownException("sfTerminateQuietlyWith", thr);
                }
            }
        }
    }

    /**
     * Handle a network exception by logging it and adding that it is (possibly) harmless
     * @param operation what was happening
     * @param thr what was caught
     */
    private void logThrownException(String operation, RemoteException thr) {
        if (sfLog().isErrorEnabled()) {
            sfLog().error("TerminatorThread."+ operation +" failed [" + record.toString() + "]", thr);
            sfLog().error(NOTE_POSSIBLY_HARMLESS);
        }
        setThrown(thr);
    }

    /**
     *  To get the sfCore logger
     * @return Logger implementing LogSF and Log
     */
    private LogSF sfLog() {
      return LogFactory.sfGetProcessLog();
    }

}


//end TerminatorThread
