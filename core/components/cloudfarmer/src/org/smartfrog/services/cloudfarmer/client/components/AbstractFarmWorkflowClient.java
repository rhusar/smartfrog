/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.client.components;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 20-Nov-2009 12:06:37
 */

public abstract class AbstractFarmWorkflowClient extends PrimImpl {
    private ClusterFarmer farmer;


    protected AbstractFarmWorkflowClient() throws RemoteException {
    }

    public ClusterFarmer getFarmer() {
        return farmer;
    }

    public void setFarmer(ClusterFarmer farmer) {
        this.farmer = farmer;
    }

    /**
     * Create the nodes on startup
     *
     * @throws SmartFrogException
     * @throws RemoteException
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        farmer = (ClusterFarmer) sfResolve(FarmCustomer.ATTR_FARMER, (Prim) null, true);
        try {
            startupAction();
        } catch (RemoteException e) {
            throw e;
        } catch (IOException e) {
            throw SmartFrogLifecycleException.forward(e);
        }
        
        //workflow termination
        ComponentHelper helper = new ComponentHelper(this);
        helper.sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                getSuccessText(),
                sfCompleteName,
                null);

    }

    protected abstract String getSuccessText();

    protected abstract void startupAction() throws IOException, SmartFrogException;
}
