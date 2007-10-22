/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.components.examples;



import java.rmi.RemoteException;

import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisStability;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;


public class StabilityNotices
        extends PrimImpl
        implements Prim {

    public class Stability extends AnubisStability {
        public void stability(boolean isStable, long timeRef) {
            if( isStable ) {
                System.out.println("****** Partition has stablized with time reference " + timeRef );
            } else {
                System.out.println("****** Partition is UNSTABLE");
            }
        }
    }

    static private Reference LOCATOR_REF = new Reference("locator");
           private AnubisLocator locator = null;
           private AnubisStability stability = null;

    public StabilityNotices() throws Exception { super(); }

    public void sfDeploy() throws SmartFrogException, RemoteException  {
        try {
            super.sfDeploy();
            locator = (AnubisLocator)sfResolve(LOCATOR_REF);
            stability = new Stability();
            locator.registerStability(stability);
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }

    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }

    public void sfTerminateWith(TerminationRecord tr) {
        locator.deregisterStability(stability);
        super.sfTerminateWith(tr);
    }
}
