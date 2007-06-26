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

package org.smartfrog.test.system.reference;

import java.rmi.RemoteException;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 *  Basic example component
 */
public class ReferenceHelper extends PrimImpl implements Prim{
    
    /* Constructs the ReferenceHelper Object.
     *
     *@throws  RemoteException 
     */
    public ReferenceHelper() throws RemoteException {
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
        String tcName = sfResolve("testcasename", "tcn39", true);
        if (tcName.equals("tcn39")) {
            // get boolean attribute
            boolean bool1 = sfResolve("bool1", true, true);
            System.out.println("Value ==>" + bool1);
        } else if (tcName.equals("tcn40")) {
            double attrDouble = sfResolve("dob1", 0.0, true);
        } else if (tcName.equals("tcn41")) {
            int intAttr = sfResolve("intAttr", 15, new Integer(12),
                    new Integer(20), true);
        } else if (tcName.equals("tcn42")) {
            int intAttr = sfResolve("intAttr", 8, new Integer(5),
                    new Integer(9), true);
        } else if (tcName.equals("tcn62")) {
            int integer = ((Integer) sfResolve("integer1")).intValue();
        } else if (tcName.equals("tcn63")) {
            String name = sfResolve("name1", true).toString();
        }
    }

    // End LifeCycle methods
}
