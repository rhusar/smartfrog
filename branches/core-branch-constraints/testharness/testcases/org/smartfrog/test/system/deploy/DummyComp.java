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

package org.smartfrog.test.system.deploy;

import java.rmi.RemoteException;
import java.io.*;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;


/**
 *  Basic example component
 */
public class DummyComp extends CompoundImpl implements Compound {
    
    /**
     * Default Constructor 
     *
     *@exception  RemoteException  Description of the Exception
     */
    public DummyComp() throws RemoteException {
    }

    // LifeCycle methods

    /**
     * sfDeploy: default implementation
     *
     *@exception  Exception  Description of the Exception
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    /**
     *  sfStart: starts counter thread
     *
     *@exception  Exception  Description of the Exception
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     *  sfTerminate
     *
     *@param  t  Description of the Parameter
     */
    public void sfTerminateWith(TerminationRecord t) {
        super.sfTerminateWith(t);
    }
    // End LifeCycle methods
}

