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

package org.smartfrog.examples.helloworld;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

/**
 *  Basic example component.
 *  The Generator component (in generator.sf) is a basic primitive component so
 *  its component description class GeneratorImpl extends PrimImpl (the base
 *  class for all the deployed components) which provides the default lifecycle
 *  template methods for a primitive component.
 *  Although PrimImpl itself implements Prim (the base interface for all the
 *  deployed components) CounterImpl also implements Prim because it is
 *  necessary for RMI that component also does so; the rmic compiler will
 *  otherwise not behave correctly.
 *  The GeneratorImpl class needs to be prepared for RMI for remote deployment
 *  This is done by creating and compiling the stubs and skeletons using the
 *  rmic compiler.
 *  This class is included in rmitargets that is read by the rmic compiler.
 */
public class GeneratorImpl extends PrimImpl implements Prim,Runnable {

    /**
     *  Any component specific declarations
     */

    /** Reference to messages attribute. */
    Reference messagesRef = new Reference("messages");
    /** Reference to printer attribute. */
    Reference printerRef = new Reference("printer");
    /** Reference to frequency attribute. */
    Reference frequencyRef = new Reference("frequency");

    /** Printer object. */
    Printer printer;
    /** Vector for messages. */
    Vector messages;
    /** Frequency of generating numbers. */
    int frequency;

    /** Thread object. */
    Thread sender;
    /** Flag notifying the thread to terminate. */
    boolean terminated = false;

    /**
     *  Constructor for the Generator object.
     *
     *@exception  RemoteException In case of network/rmi error
     */
    public GeneratorImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     * sfDeploy: reads Generator attributes.
     *
     * @exception  SmartFrogException In case of error while deploying
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
        super.sfDeploy();

        // cast resolutions since sfResolve returns Object
        printer = (Printer) sfResolve(printerRef);
        messages = (Vector) sfResolve(messagesRef);
        // extract int from Integer and multiple by 1000 to turn to seconds
        frequency = ((Integer)sfResolve(frequencyRef)).intValue() * 1000;
    }

    /**
     * sfStart: starts genrator thread.
     *
     * @exception  SmartFrogException In case of error while starting
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        super.sfStart();

        // create and start the thread
        sender = new Thread(this);
        sender.start();
    }

    /**
     *  sfTerminate: terminate the thread nicely if needed could interrupt its
     *  sleep, but not necessary in general, since the thread initiates the
     *  termination, this will be irrelevant but to do so in case it is through
     *  error or management action.
     *
     * @param  tr TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        terminated = true;

        super.sfTerminateWith(tr);
    }

    // End LifeCycle methods

    // Main component action method

    /**
     *  Main processing method for the Generator object (implements 'Runnable').
     */
    public void run() {
        // the body of the thread
        try {
            for (Enumeration en = messages.elements();
                 en.hasMoreElements() && !terminated; ) {
                printer.printIt(en.nextElement().toString());
                if (frequency > 0) Thread.sleep (frequency);
            }
        } catch (Exception e) {
        } finally {
            sfTerminate(TerminationRecord.normal(null));
        }
    }
}
