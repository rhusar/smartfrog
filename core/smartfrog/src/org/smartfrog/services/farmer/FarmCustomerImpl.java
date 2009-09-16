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
package org.smartfrog.services.farmer;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a workflow component that is bound to a farmer, and which creates/destroys nodes through its lifecycle.
 *
 * It is primarily for testing, but it can be used in production. A key weakness is that it is synchronous -it relies on
 * the operation to complete rapidly
 */

public class FarmCustomerImpl extends PrimImpl implements FarmCustomer {
    protected ClusterFarmer farmer;
    private ClusterNode[] nodes = new ClusterNode[0];
    protected String role;
    protected int min;
    protected int max;
    boolean deleteOnTerminate;

    public FarmCustomerImpl() throws RemoteException {
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
        role = sfResolve(ATTR_ROLE, "", true);
        min = sfResolve(ATTR_MIN, 0, true);
        max = sfResolve(ATTR_MAX, 0, true);
        deleteOnTerminate = sfResolve(ATTR_DELETE_ON_TERMINATE, true, true);
        farmer = (ClusterFarmer) sfResolve(ATTR_FARMER, (Prim) null, true);

        if (max > 0) {
            try {
                ClusterNode[] nodes = farmer.create(role, min, max);
                this.nodes = nodes;
            } catch (RemoteException e) {
                throw e;
            } catch (IOException e) {
                throw SmartFrogException.forward(e);
            }
        }
        int created = nodes.length;
        sfReplaceAttribute("created", created);
        String info = "Created " + created + " nodes of role " + role;
        sfLog().info(info);
        //check the expected value
        int expected = sfResolve("expected", -1, true);
        if (expected >= 0 && expected != created) {
            throw new SmartFrogDeploymentException(info
                    + " - instead of the expected number " + expected);
        }
        ComponentHelper helper = new ComponentHelper(this);
        helper.sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL,
                info,
                sfCompleteName,
                null);
    }


    /**
     * Check the nodes are there on a liveness call
     *
     * @param source source of call
     * @throws SmartFrogLivenessException
     * @throws RemoteException
     */
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        try {
            ClusterNode[] listed = farmer.list(role);
            Map<String, ClusterNode> map = new HashMap<String, ClusterNode>(listed.length);
            for (ClusterNode node : listed) {
                map.put(node.getId(), node);
            }
            for (ClusterNode node : nodes) {
                if (map.get(node.getId()) == null) {
                    throw new SmartFrogLivenessException("Cannot find entry for " + node);
                }
            }
        } catch (Exception e) {
            throw (SmartFrogLivenessException) SmartFrogLivenessException.forward(e);
        }
    }

    /**
     * Terminate nodes on shutdown
     *
     * @param status termination status
     */
    @Override
    public void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (deleteOnTerminate) {
            try {
                farmer.delete(nodes);
            } catch (Exception e) {
                sfLog().info(e);
            }
        }
        nodes = null;
    }
}
