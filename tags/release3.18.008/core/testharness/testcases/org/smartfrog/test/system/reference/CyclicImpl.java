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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;


/**
 * a component that resolves one attribute
 */
public class CyclicImpl extends PrimImpl implements Prim, Cyclic {
    private String attr1 = "TEST2";
    /**  Constructor
     *
     * @throws RemoteException network trouble
     */
    public CyclicImpl() throws RemoteException {
    }

    // LifeCycle methods

    /**
     *  sfDeploy:
     *
     * @throws SmartFrogException startup trouble
     * @throws RemoteException network trouble
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
            super.sfDeploy();
        sfLog().info("DEPLOYED");
    }

    /**
     *  sfStart:
     *
     * @throws SmartFrogException startup trouble
     * @throws RemoteException network trouble
     */
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        sfLog().info("STARTED");
        attr1 = sfResolve(ATTR1, attr1, true);
        sfLog().info("ATTRIBUTE-1:" + attr1);
    }

    /**
     * sfTerminate
     *
     * @param t termination record
     */
    public void sfTerminateWith(TerminationRecord t) {
        sfLog().info("TERMINATED");
        super.sfTerminateWith(t);
    }

    // End LifeCycle methods
}
