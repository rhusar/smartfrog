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
package org.smartfrog.sfcore.utils;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Contains methods for helping components; a factoring out of common functionality.
 * Component helpers must be bound to Prim classes before use.
 * created 18-May-2004 11:26:15
 */

public class ComponentHelper {

    private Prim owner;

    public ComponentHelper(Prim owner) {
        this.owner = owner;
    }

    /**
     * mark this task for termination by spawning a separate thread to do it.
     * as {@link Prim#sfTerminate} and {@link Prim#sfStart()} are synchronized,
     * the thread blocks until sfStart has finished.
     * Note that we detach before terminating; this stops our timely end propagating.
     * @todo what about TerminatorThread; does that do this better?
     */
    public void targetForTermination() {
        //spawn the thread to terminate normally
        Runnable terminator = new Runnable() {
            public void run() {
                Reference name;
                try {
                    name = owner.sfCompleteName();
                } catch (RemoteException e) {
                    name = null;

                }
                try {
                    owner.sfDetachAndTerminate(TerminationRecord.normal(name));
                } catch (RemoteException e) {
                    //we cannot rethrow this as it is not in the signature of the interface
                    logIgnoredException(e);
                }
            }
        };

        new Thread(terminator).start();
    }

    /**
     * get the relevant java1.4 logger for this component.
     * When logging against a remote class, this is probably the classname of the proxy.
     * @return
     */
    public Logger getLogger() {
        String classname=owner.getClass().getName();
        //todo: we could opt to use the name of the prim instead.
        //String primname=owner.sfCompleteNameSafe();
        return Logger.getLogger(classname);
    }

    /**
     * ignore an exception by logging it at the fine level.
     * @param thrown
     */
    public void logIgnoredException(Throwable thrown) {
        Logger log=getLogger();
        log.log(Level.FINE,"ignoring ",thrown);
    }
}
