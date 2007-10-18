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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.workflow.eventbus.EventPrimImpl;


/**
 * DoNothing is a test routine for SmartFlow systems. It does nothing for a
 * period of time then terminates according to the attributes given at
 * deployment. Attributes are documented in the file doNothing.sf
 */
public class DoNothing extends EventPrimImpl implements Prim {
    private String myId;
    private int time;
    private String terminationType;
    private String message;
    private boolean printEvents;
    public static final String TIME = "time";
    public static final String TERMINATION_TYPE = "terminationType";
    public static final String MESSAGE = "message";
    public static final String PRINT_EVENTS = "printEvents";

    /**
     * Constructs DoNothing.
     *
     * @throws RemoteException The required remote exception
     */
    public DoNothing() throws RemoteException ,SmartFrogException{
        super();
    }


    /**
     * Print the receipt of any event for debugging.
     * Overrides EventPrimImpl.handleEvent.
     *
     * @param event The event
     */
    public void handleEvent(Object event) {
        if (printEvents) {
            if (sfLog().isInfoEnabled()){
                String infoStr=myId + " received event " + event.toString();
                sfLog().info(infoStr);
            }
        }
    }

    /**
     * Deploys the components and reads time attribute and terminationType
     * attribute. If they don't exists, delays 10 seconds and terminates
     * normally.
     *
     * @throws SmartFrogException In case of SmartFrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        myId = sfCompleteNameSafe().toString();

        try {
            time = ((Integer) sfResolve(TIME)).intValue();
        } catch (Exception e) {
            time = 10000;
        }

        try {
            terminationType = (String) sfResolve(TERMINATION_TYPE);
        } catch (Exception e) {
            terminationType = "normal";
        }

        try {
            message = myId + "- " + (String) sfResolve(MESSAGE);
        } catch (Exception e) {
            message = null;
        }

        try {
            printEvents = ((Boolean) sfResolve(PRINT_EVENTS)).
                                                        booleanValue();
        } catch (Exception e) {
            printEvents = true;
        }
    }

    /**
     * Kicks off the timer and terminates when it fires.
     *
     * @throws SmartFrogException In case of SmartFrog system error
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (message != null) {

            if (sfLog().isInfoEnabled()) {
                String infoStr=message;
                sfLog().info(infoStr);
            }
        }

        if (!terminationType.equals("none")) {
            Runnable terminator = new Runnable() {
                    public void run() {
                        if (sfLog().isDebugEnabled()) { sfLog().debug("Timer set:" +time+". Going to sleep "+myId);}
                        try { if (time > 0) { Thread.sleep(time); } } catch (Exception ex) { }
                        String terminationMessage = "Timer '"+time+"' expired . Terminating "+myId;
                        if (sfLog().isDebugEnabled()) { sfLog().debug(terminationMessage);}
                        sfTerminate(new TerminationRecord(terminationType, terminationMessage , null));
                    }
            };
            Thread thread = new Thread(terminator);
            thread.setName(myId+"_DoNothingTerminator");
            thread.start();
        }
    }
}
