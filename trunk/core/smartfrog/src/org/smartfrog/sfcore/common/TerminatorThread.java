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


/**
 * TerminatorThread is used by components for asynchronous termination. Caller
 * component should create the object and then call method start() to start
 * this thread.
 *
 * Example:
 *       TerminatorThread terminator = new TerminatorThread(targetComponent,
 *              excp, componentId).quietly();
 *       // start the thead
 *       terminator.start();
 *
 */
public class TerminatorThread extends Thread {
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
     * terminating itself.
     */
    private boolean shouldDetach    = false; //It will not detach by default

    /**
     * Boolean flag to indicate that the component should terminate itself
     */
    private boolean shouldTerminate  = true; //It will terminate by default

    /**
     * Boolean flag to indicate that the component should notify its parent 
     * before terminating itself.
     */
    private boolean notifyParent    = true; //It will use:: true=sfTeminate(), false=sfTerminateQuietlyWith()

    /**
     * Constructs the TerminatorThread object using the component reference and
     * termination record.
     *
     * @param target of the component that has to be terminated.
     * @param record record why the termination
     */
    public TerminatorThread(Prim target, TerminationRecord record) {
        this.target = target;
        this.record = record;
    }

    /**
     * Constructs the TerminatorThread object using the component reference,
     * the exception object component identifier.
     *
     * @param target of the component that has to be terminated.
     * @param t exception that caused the termination
     * @param compId component identifier
     */
    public TerminatorThread(Prim target, Throwable t, Reference compId) {
        this.target = target;
        this.record = TerminationRecord.abnormal(t.toString(), compId);
    }
    
    /**
     * Utility method to create/terminate the TerminatorThread object quietly.
     * 
     * @return TerminatorThread object
     */  
    public TerminatorThread quietly(){
        this.notifyParent =false;
        return this;
    }

    /**
     * Utility method to detach TerminatorThread object from parent.
     * 
     * @return TerminatorThread object
     */  
    public TerminatorThread detach(){
        this.shouldDetach =true;
        return this;
    }

    /**
     * Utility method that does not terminates the TerminatorThread object.
     * 
     * @return TerminatorThread object
     */  
    public TerminatorThread dontTerminate(){
        this.shouldTerminate =false;
        return this;
    }

    /**
     * Run method used to trigger the terminatation of the component.
     */
    public void run() {
        try {
            if (shouldDetach && shouldTerminate && notifyParent){
                try {
                   target.sfDetachAndTerminate(record);
               } catch (Throwable thr) {
                   Logger.log("TerminatorThread.sfDetachAndTerminate failed ["+ record.toString()+"]", thr);
               }
                return;
            }
            if (shouldDetach) {
                 try {
                    target.sfDetach();
                } catch (Throwable thr) {
                    Logger.log("TerminatorThread.sfDetach failed ["+ record.toString()+"]", thr);
                }
            }
            if (shouldTerminate) {
                if (notifyParent){
                    try {
                        target.sfTerminate(record);
                    } catch (Throwable thr) {
                        Logger.log("TerminatorThread.sfTerminate failed ["+ record.toString()+"]", thr);
                    }

                } else {
                    try {
                        target.sfTerminateQuietlyWith(record);
                    } catch (Throwable thr) {
                        Logger.log("TerminatorThread.sfTerminateQuietlyWith failed ["+ record.toString()+"]", thr);
                    }
                }
            }
        } catch (Throwable t) {
            Logger.log("TerminatorThread.Exception occured during termination:", t);
        }
    }
}


//end TerminatorThread
