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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.workflow.conditional.conditions.AbstractConditionPrim;
import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Lists the number of deployed instances; checks that they are in range
 */
public class FarmHasDeployedInstances extends AbstractConditionPrim implements FarmCustomer {

    protected ClusterFarmer farmer;
    protected String role;
    protected int min;
    protected int max;

    protected int deployed = 0;
    protected int expected;

    public FarmHasDeployedInstances() throws RemoteException {
    }

    /**
     * Create the nodes on startup
     *
     * @throws RemoteException network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        role = sfResolve(ATTR_ROLE, "", true);
        farmer = (ClusterFarmer) sfResolve(ATTR_FARMER, (Prim) null, true);
        min = sfResolve(ATTR_MIN, 0, true);
        max = sfResolve(ATTR_MAX, 0, true);
        expected = sfResolve(ATTR_EXPECTED, -1, true);
        boolean checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, true, true);

        ComponentHelper helper = new ComponentHelper(this);
        String outcome = "Instance Check not performed";

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
                "the Farm has deployed " + deployed + " nodes" ,
                sfCompleteName,
                null);
    }


    /**
     * Fetch the list of roles, assert that the chosen one is available
     *
     * @return true if the named role is listed.
     * @throws RemoteException network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            ClusterNode[] nodes;
            String roleText;
            if (!role.isEmpty() ) {
                nodes = farmer.list(role);
                roleText = " role of \""+role+'"';
            } else {
                nodes = farmer.list();
                roleText = "";
            }

            int nodeCount = nodes.length;

            deployed = nodeCount;
            sfReplaceAttribute(ATTR_DEPLOYED, deployed);
            
            if (sfLog().isInfoEnabled()) {
                sfLog().info("There are " + nodeCount + "nodes");
                for (ClusterNode node : nodes) {
                    sfLog().info(node.toString());
                }
            }

            if (expected >= 0 && deployed!=expected) {
                setFailureText("The node count of " + nodeCount + " is not what was expected: " + expected
                 + roleText);
                return false;
            }
            if (max >= 0 && nodeCount > max) {
                setFailureText("The node count of " + nodeCount
                        + " is more than the maximum allowed " + max
                        + roleText);
                return false;
            }
            if (nodeCount < min) {
                setFailureText("The node count of " + nodeCount
                        + " is less than the maximum allowed " + min
                        + roleText);
                return false;
            }

            return true;
        } catch (RemoteException e) {
            setFailureCause(e);
            throw e;
        } catch (IOException e) {
            setFailureCause(e);
            throw SmartFrogException.forward(e);
        }
    }
}
