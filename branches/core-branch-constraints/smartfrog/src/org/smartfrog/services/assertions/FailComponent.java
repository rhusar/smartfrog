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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * Class to fail on startup, either normally or abnormally, and with or without
 * a scheduled delay. Useful for testing child-death-handling logic
 * of containers and workflow.
 */
public class FailComponent extends PrimImpl implements Fail,Runnable {

    private boolean normal;
    private int delay=0;
    private String message;
    private boolean detach;
    private boolean notifyParent;


    public FailComponent() throws RemoteException {
    }

    /**
     * Start up by spawning a thread to kill ourselves if
     * the condition attribute is true. If it is false, do nothing
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart()
        throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean condition = sfResolve(ATTR_CONDITION, false, true);
        delay = sfResolve(ATTR_DELAY, 0, true);
        detach= sfResolve(ATTR_DETACH, false, true);
        message = sfResolve(ATTR_MESSAGE, "", true);
        normal = sfResolve(ATTR_NORMAL, false, true);
        notifyParent = sfResolve(ATTR_NOTIFY, false, true);
        if(condition) {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Entry point for the run
     */
    public void run() {
        if (delay > 0) {
            try {
                Thread.sleep(delay * 1000L);
            } catch (InterruptedException e) {
                //do nothing if interrupted
                return;
            }
        }
        TerminationRecord record = createTerminationRecord();
        TerminatorThread terminator = new TerminatorThread(this, record);
        terminator.setShouldDetach(detach);
        terminator.setNotifyParent(notifyParent);
        terminator.start();
    }

    private TerminationRecord createTerminationRecord() {
        TerminationRecord record;
        Reference name = sfCompleteNameSafe();
        record=new TerminationRecord(normal?TerminationRecord.NORMAL:TerminationRecord.ABNORMAL,
                message,name);
        return record;
    }
}
