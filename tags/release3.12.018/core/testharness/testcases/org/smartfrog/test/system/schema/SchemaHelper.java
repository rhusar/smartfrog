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

package org.smartfrog.test.system.schema;

import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;


/**
 *  Basic example component
 */
public class SchemaHelper extends PrimImpl implements Prim{
    
    /* Constructs the SchemaHelper Object.
     *
     *@throws  RemoteException 
     */
    public SchemaHelper() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *Reads attributes and deploys the component
     *
     *@throws  SmartFrogException If unable to deploy the component
     *@throws  RemoteException in case of network/RMI error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    /**
     * Starts the component.
     *
     *@throws  Exception  Description of the Exception
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     * Terminats the component
     *
     *@param  t  Termination Record
     */
    public void sfTerminateWith(TerminationRecord t) {
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods
}
