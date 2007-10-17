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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;

/**
 *  Basic example component.
 *  The Printer component (in printer.sf) is a basic primitive component so
 *  its component description class PrinterImpl extends PrimImpl (the base
 *  class for all the deployed components) which provides the default lifecycle
 *  template methods for a primitive component.
 *  Although PrimImpl itself implements Prim (the base interface for all the
 *  deployed components) PrinterImpl also implements Prim because it is
 *  necessary for RMI that component also does so; the rmic compiler will
 *  otherwise not behave correctly.
 *  The PrinterImpl class needs to be prepared for RMI for remote deployment
 *  This is done by creating and compiling the stubs and skeletons using the
 *  rmic compiler.
 *  This class is included in rmitargets that is read by the rmic compiler.
 */
public class PrinterImpl extends PrimImpl implements Prim, Printer {

    /* any component specific declarations */

    /** My name. */
    String name = "";

    /** Reference to my name attribute. */
    Reference nameRef = new Reference("name");

    /**
     *  Standard remotable constructor - must be provided.
     *
     *  @throws RemoteException In case of network/rmi error
     */  
    public PrinterImpl() throws RemoteException {
    }

     /**
     *  Initialization template method sfDeploy.
     *  Reads the string attribute "name" as the id of the printer, defaulting 
     *  to the sfCompleteNameSafe.
     *  Overrides PrimImpl.sfDeploy.  
     * @exception SmartFrogException In case of error while deploying
     * @exception RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, 
    RemoteException{
        super.sfDeploy();
        /* any component specific initialization code */
        try {
            name = sfResolve(nameRef).toString();
            // stringify since sfResolve returns an object
        }  catch (SmartFrogResolutionException e) {
            // name not provided, so get name from tree - returns a reference
            name = sfCompleteNameSafe().toString();
        }
        // any other error - propagate and hence fail to deploy
    }

    /**
     *  Component specific methods - in this case from the Print interface.
     *  Prints the message to the console with the printer id.
     *
     *  @param message string message
     *  
     *  @exception RemoteException In case of network/rmi error
     */
    public void printIt(String message) throws RemoteException {
        System.out.println(name + ": " + message);
    }
}
