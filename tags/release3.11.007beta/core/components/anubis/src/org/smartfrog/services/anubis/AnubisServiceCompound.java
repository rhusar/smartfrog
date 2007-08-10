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
package org.smartfrog.services.anubis;


import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.processcompound.SFProcess;

public class AnubisServiceCompound
        extends CompoundImpl
        implements Compound {

    private boolean         namedInSFProcess = false;
    private String          serviceName      = null;
    private ProcessCompound processCompound  = null;

    public AnubisServiceCompound() throws RemoteException {
        super();
    }

    public void sfDeploy() throws  SmartFrogException, RemoteException {
        try {
            super.sfDeploy();

            Object obj = sfResolveHere("anubisServiceName",false);
            if( obj == null )
                return;

            serviceName     = obj.toString();
            processCompound = SFProcess.getProcessCompound();

            if( processCompound.sfContext().containsKey(serviceName) )
                throw new SmartFrogDeploymentException("anubisServiceName " + serviceName + " is already in use");

            processCompound.sfAddAttribute(serviceName, this);
            namedInSFProcess = true;
        }
        catch (Exception ex) {
            throw (SmartFrogException)SmartFrogException.forward(ex);
        }
    }


    public void sfStart() throws  SmartFrogException, RemoteException {
        super.sfStart();
    }


    public void sfTerminateWith(TerminationRecord terminationRecord) {

        if( namedInSFProcess )
            try { processCompound.sfRemoveAttribute(serviceName); }
        catch(Exception ex) {  }

        super.sfTerminateWith(terminationRecord);
    }

}
