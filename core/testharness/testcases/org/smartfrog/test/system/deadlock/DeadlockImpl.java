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

package org.smartfrog.test.system.deadlock;

import java.rmi.RemoteException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.Serializable;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;


/**
 *  Basic example component
 */
public class DeadlockImpl extends PrimImpl implements Prim,Deadlock, Serializable {
   String attr1 = "TEST2";
    /*  Constructor for the Counter object
     *
     *@exception  RemoteException  Description of the Exception
     */
    public DeadlockImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *  sfDeploy: reads Counter attributes and configures counter thread.
     *
     *@exception  Exception  Description of the Exception
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        System.out.println("DEPLOYED "+this.sfCompleteNameSafe().toString());
    }

    /**
     *  sfStart: starts counter thread
     *
     *@exception  Exception  Description of the Exception
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        System.out.println("STARTED "+this.sfCompleteNameSafe().toString());
        //optional
        attr1 = sfResolve(ATTR1,attr1,false);
        System.out.println("ATTRIBUTE-1 (OPT):" + attr1);
        //mandatory
        attr1 = sfResolve(ATTR1,attr1,true);
        System.out.println("ATTRIBUTE-1 (MAND):" + attr1);
    }

    /**
     *  sfTerminate
     *
     *@param  t  Description of the Parameter
     */
    public void sfTerminateWith(TerminationRecord t) {
        StringBuffer msg = new StringBuffer("TERMINATED ");
        msg.append(this.sfCompleteNameSafe().toString());
        msg.append("; ");
        msg.append(t.toString());
        System.out.println(msg);
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods
}
