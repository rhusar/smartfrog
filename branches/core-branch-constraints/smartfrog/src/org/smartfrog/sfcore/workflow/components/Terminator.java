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

package org.smartfrog.sfcore.workflow.components;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


/**
 * This component sends an sfTerminate to the component referenced by its
 * "kill" attribute. If it succeeded, it  terminates normally, otherwise
 * returns the failure Attributes are documented in Terminator.sf
 */
public class Terminator extends EventPrimImpl implements Prim {
    private TerminationRecord term = null;
    public static final String TYPE = "type";
    public static final String SELFTYPE = "selftype";
    public static final String DESCRIPTION = "description";
    public static final String DETACH_FIRST = "detachFirst";
    public static final String KILL = "kill";

    /**
     * Constructs Terminator.
     *
     * @throws RemoteException The exception description.
     */
    public Terminator() throws RemoteException {
        super();
    }

    /**
     * On start, kills and terminates itself.
     *
     * @throws SmartFrogException In case of smartfrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();

        Reference id = sfCompleteName();
        String type = (String) sfResolve(TYPE);
        String selftype = sfResolve(SELFTYPE,TerminationRecord.NORMAL,false);
        String description = (String) sfResolve(DESCRIPTION);
        boolean detachFirst = ((Boolean) sfResolve(DETACH_FIRST)).
                                                            booleanValue();
        term = new TerminationRecord(selftype,description,id);
        Prim kill = sfResolve(KILL, (Prim) null, false);
        if (kill != null) {
            try {
                String killName = kill.sfCompleteName().toString();
                String terminator = sfCompleteNameSafe().toString();
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("Terminating: " + killName + " by terminator: " + terminator, null, term);
                }

                TerminationRecord targetRecord = new TerminationRecord(type, description, id);
                if (detachFirst) {
                    kill.sfDetachAndTerminate(targetRecord);
                } else {
                    kill.sfTerminate(targetRecord);
                }
                if (sfLog().isTraceEnabled()) {
                    sfLog().trace("Terminated: " + killName + " by terminator: " + sfCompleteNameSafe(), null,
                            targetRecord);
                }
            } catch (Exception e) {
                term = TerminationRecord.abnormal(e.toString(), id);
            }
        }

        //now we terminate ourself.
        TerminatorThread terminator = new TerminatorThread(this,term);
        terminator.start();
    }
}
