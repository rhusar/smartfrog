/** (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.conditional.conditions.AbstractConditionPrim;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Lists the number of deployed instances; checks that they are in range
 */
public class FarmIsAvailable extends AbstractConditionPrim implements FarmCustomer {

    protected ClusterFarmer farmer;

    public FarmIsAvailable() throws RemoteException {
    }

    /**
     * Create the nodes on startup
     *
     * @throws RemoteException    network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        farmer = (ClusterFarmer) sfResolve(ATTR_FARMER, (Prim) null, true);
        boolean checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, true, true);
        try {
            setFailureText(farmer.getDiagnosticsText());
        } catch (RemoteException e) {
            throw e;
        } catch (IOException e) {
            throw SmartFrogException.forward(e);
        }
        ComponentHelper helper = new ComponentHelper(this);

        if (checkOnStartup) {
            boolean success = evaluate();
            if (!success) {
                TerminationRecord tr = helper.createTerminationRecord(
                        TerminationRecord.ABNORMAL,
                        getFailureText(),
                        sfCompleteName,
                        null);
                helper.targetForTermination(tr, false, false);
            }
        }
        //no problems, so check the workflow settings
        helper.sfSelfDetachAndOrTerminate(
                TerminationRecord.NORMAL,
                "the Farm is available",
                sfCompleteName,
                null);

    }


    /**
     * Fetch the list of roles, assert that the chosen one is available
     *
     * @return true if the named role is listed.
     * @throws RemoteException    network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            return farmer.isFarmerAvailable();
        } catch (RemoteException e) {
            setFailureCause(e);
            throw e;
        } catch (IOException e) {
            setFailureCause(e);
            throw SmartFrogException.forward(e);
        }
    }
}