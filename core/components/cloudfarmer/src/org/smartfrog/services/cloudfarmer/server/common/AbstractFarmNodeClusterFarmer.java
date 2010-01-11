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
package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.NoClusterSpaceException;
import org.smartfrog.services.cloudfarmer.api.Range;
import org.smartfrog.services.cloudfarmer.api.UnsupportedClusterRoleException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is a cluster farmer which manages a local pool of FarmNode nodes, from which it allocates/frees nodes.
 *
 * How the cluster is built up, how allocation works, is something for the subclasses
 */

public abstract class AbstractFarmNodeClusterFarmer extends AbstractClusterFarmer {
    protected Map<String, FarmNode> nodeFarm;

    protected AbstractFarmNodeClusterFarmer() throws RemoteException {
    }

    /**
     * Build the node farm
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    protected abstract void buildNodeFarm() throws SmartFrogException, RemoteException;

    /**
     * Get an iterator over all the farm nodes. Access this in a synchronized context only
     *
     * @return a collection
     */
    protected Collection<FarmNode> farmNodes() {
        return nodeFarm.values();
    }

    /**
     * Free a node
     *
     * @param node node to release
     */
    protected void releaseNode(FarmNode node) {
        node.free();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String[] nodesToDelete) throws IOException, SmartFrogException {
        for (String nodeID : nodesToDelete) {
            delete(nodeID);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(ClusterNode[] nodesToDelete) throws IOException, SmartFrogException {
        for (ClusterNode node : nodesToDelete) {
            delete(node.getId());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized int deleteAll() throws IOException, SmartFrogException {
        int deleted = nodeFarm.size();
        for (FarmNode node : farmNodes()) {
            releaseNode(node);
        }
        return deleted;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized int deleteAllInRole(String role) throws IOException, SmartFrogException {
        int count = 0;
        for (FarmNode node : farmNodes()) {
            if (node.isInRole(role)) {
                releaseNode(node);
                count++;
            }
        }
        return count;
    }
    
    

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterNode[] list() throws IOException, SmartFrogException {
        List<ClusterNode> result = new ArrayList<ClusterNode>(nodeFarm.values().size());
        for (FarmNode node : farmNodes()) {
            if (!node.isFree()) {
                result.add(node.getClusterNode());
            }
        }
        return nodesToArray("", result);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterNode[] list(String role) throws IOException, SmartFrogException {
        List<ClusterNode> result = new ArrayList<ClusterNode>(nodeFarm.values().size());
        for (FarmNode node : farmNodes()) {
            if (node.isInRole(role)) {
                result.add(node.getClusterNode());
            }
        }
        return nodesToArray(role, result);
    }

    /**
     * Helper to turn a collection into an array
     *
     * @param role  role to work with for log messages
     * @param nodes the collection to work with
     * @return the nodes as an array
     */
    private ClusterNode[] nodesToArray(String role, Collection<ClusterNode> nodes) {
        sfLog().info("list(" + role + ") returning " + nodes.size() + " nodes");
        return nodes.toArray(new ClusterNode[nodes.size()]);
    }

    /**
     * Count the #of free nodes
     *
     * @return free node count
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public synchronized int countFreeNodes() throws IOException, SmartFrogException {
        int count = 0;
        for (FarmNode node : farmNodes()) {
            if (node.isFree()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Find a node in a role
     *
     * @param role role to look for
     * @return a node in this role, or null
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    protected ClusterNode nodeInRole(String role) throws IOException, SmartFrogException {
        for (FarmNode node : farmNodes()) {
            if (node.isInRole(role)) {
                return node.getClusterNode();
            }
        }
        return null;
    }

    /**
     * Count the number of nodes in a role
     *
     * @param role role to look for
     * @return number of nodes in this role
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    protected synchronized int nodesInRole(String role) throws IOException, SmartFrogException {
        int count = 0;
        for (FarmNode node : farmNodes()) {
            if (node.isInRole(role)) {
                count++;
            }
        }
        return count;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized void delete(String id) throws IOException, SmartFrogException {
        FarmNode node = nodeFarm.get(id);
        if (node != null) {
            releaseNode(node);
        }
    }

    /**
     * Is the role allowed
     *
     * @param role to ask for
     * @return true iff the role is supported
     */
    public boolean roleAllowed(String role) {
        return roleInfoMap.containsKey(role);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookup(String id) throws IOException, SmartFrogException {
        FarmNode fnode = nodeFarm.get(id);
        return fnode != null ? fnode.getClusterNode() : null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        for (FarmNode node : farmNodes()) {
            if (hostname.equals(node.getHostname())) {
                return node.getClusterNode();
            }
        }
        return null;
    }

    /**
     * Check for the cluster being available before working with it, throw something if it
     * is not considered live
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    protected void checkClusterAvailable() throws IOException, SmartFrogException{
        
    }

    /**
    * {@inheritDoc}
    *
    * @throws IOException        IO/network problems
    * @throws SmartFrogException other problems
    */
    @Override
    public synchronized ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        ClusterFarmerUtils.validateClusterRange(min, max);
        String rejectionText = "Rejecting request for "
                + "[" + min + ", " + max + "]"
                + " nodes of role " + role;

        //now check the role is allowed
        ClusterRoleInfo info = lookupRoleInfo(role);
        if (info == null) {
            throw new UnsupportedClusterRoleException(role, this);
        }
        checkClusterAvailable();
        //get the number of nodes already in that role
        int nodesInRole = nodesInRole(role);
        Range roleRange = info.getRoleSize();
        //put the top limit on the allocation
        int nodesToAllocate = roleRange.calculateMaximumAllocatable(min, max, nodesInRole);

        if (nodesToAllocate <= 0 && min == 0) {
            //no nodes left, but the minimum was zero. 
            // Well, you ask for none, you get none
            // THIS IS NOT AN ERROR!
            return new ClusterNode[0];
        }
        if (nodesToAllocate <= 0) {
            String message = rejectionText + " : "
                    + " there are already " + nodesInRole
                    + " nodes in that role, and the allowed range is "
                    + roleRange.toString();
            sfLog().info(message);
            throw new NoClusterSpaceException(message);
        }

        //now check the cluster space 
        int freespace = countFreeNodes();
        if (freespace < min) {
            String message = rejectionText + " : no space in cluster -only " + freespace + " nodes left";
            sfLog().info(message);
            throw new NoClusterSpaceException(message);
        }
        List<ClusterNode> newnodes = new ArrayList<ClusterNode>(nodesToAllocate);
        for (int i = 0; i < nodesToAllocate; i++) {
            ClusterNode clusterInstance = allocateOneNode(info);
            if (clusterInstance == null) {
                //we have run out of space, but as it is in range, the overall operation will succeed
                break;
            }
            newnodes.add(clusterInstance);
        }
        sfLog().info("Created " + newnodes.size() + " nodes of role " + role);
        return newnodes.toArray(new ClusterNode[newnodes.size()]);
    }

    /**
     * Allocate a node and add it to the list
     *
     * @param role role required
     * @return the instance, or null if there is no room
     * @throws SmartFrogException other problems
     * @throws IOException        network problems
     */
    protected synchronized ClusterNode allocateOneNode(ClusterRoleInfo role) throws SmartFrogException, IOException {
        FarmNode target = selectFarmNodeForAllocation(role);
        if (target != null) {
            assignNodeToRole(target, role);
            return target.getClusterNode();
        } else {
            return null;
        }
    }

    /**
    * Override point: select a node for allocating to a role
    *
    * @param role role required
    * @return the chosen node, or null for no match
    * @throws SmartFrogException other problems
    * @throws IOException        network problems
    */
    protected FarmNode selectFarmNodeForAllocation(ClusterRoleInfo role) throws SmartFrogException, IOException {
        for (FarmNode node : farmNodes()) {
            if (node.isFree()) {
                return node;
            }
        }
        return null;
    }


    /**
     * Assign a node
     *
     * @param node node that has been chosen
     * @param role role required
     * @throws SmartFrogException other problems
     * @throws IOException        network problems
     */
    protected void assignNodeToRole(FarmNode node, ClusterRoleInfo role) throws SmartFrogException, IOException {
        //allocate and use
        node.setRoleInfo(role);
    }

    /**
     * Update the cluster limit -also sets the matching attribute
     * @param limit new limit
     * @throws SmartFrogRuntimeException problems setting the limit
     * @throws RemoteException    network problems
     */
    protected void replaceClusterLimit(int limit) throws SmartFrogRuntimeException, RemoteException {
        clusterLimit = limit;
        sfReplaceAttribute(ATTR_CLUSTER_LIMIT, clusterLimit);
    }


}
