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
import java.util.HashMap;
import java.util.Map;

import org.smartfrog.services.anubis.locator.AnubisLocator;
import org.smartfrog.services.anubis.locator.AnubisProvider;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class AnubisCompound
        extends CompoundImpl
        implements Compound {


    private class Provider extends AnubisProvider {
        private Provider(String str) {
            super(str);
        }
        public boolean anubisLivenessPoll() {
            return true;
        }
    }

    private Map            childListeners = new HashMap();
    private AnubisLocator  locator        = null;
    private AnubisProvider provider       = null;
    private String         myName         = null;

    public AnubisCompound() throws RemoteException {
        super();
    }

    public void sfDeploy() throws SmartFrogException, RemoteException  {
        try {
            super.sfDeploy();

            try { myName = sfResolve("anubisName").toString(); }
            catch(Exception ex) { myName = null; }
            if( myName == null)
                myName = sfCompleteName().toString();

            locator  = (AnubisLocator)sfResolve("locator");
            provider = new Provider(myName);
            provider.setValue("deployed");
            locator.registerProvider(provider);
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }

    }


    public void sfStart() throws SmartFrogException, RemoteException  {
        try {
            super.sfStart();
            provider.setValue("started");
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }


    public void sfTerminateWith(TerminationRecord terminationRecord) {
        locator.deregisterProvider(provider);
        super.sfTerminateWith(terminationRecord);
    }

}
