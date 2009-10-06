/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.cloudfarmer.server.mock;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.NoClusterSpaceException;
import org.smartfrog.services.cloudfarmer.api.Range;
import org.smartfrog.services.cloudfarmer.api.UnsupportedClusterRoleException;
import org.smartfrog.services.cloudfarmer.server.AbstractClusterFarmer;
import org.smartfrog.services.cloudfarmer.server.common.FarmNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a mock cluster, very simple. A counter tracks the number of machines allocated, and whenever you ask for new
 * machines, it gets incremented. The system tracks the number of machines currently allocated, and rejects requests to
 * get more
 */
public class MockClusterFarmerImpl extends AbstractClusterFarmer implements ClusterFarmer {


    private String domain = "internal";
    private String externalDomain = "external";
    protected Map<String, FarmNode> nodeFarm;

    /**
     * {@value}
     */
    public static final String ATTR_DOMAIN = "domain";
    /**
     * {@value}
     */
    public static final String ATTR_EXTERNAL_DOMAIN = "externalDomain";


    public MockClusterFarmerImpl() throws RemoteException {
    }

    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        resolveClusterLimit();
        sfLog().info("Creating Farmer with a limit of " + clusterLimit);
        domain = sfResolve(ATTR_DOMAIN, "", true);
        externalDomain = sfResolve(ATTR_EXTERNAL_DOMAIN, "", true);
        buildRoleMap();
        buildNodeFarm();
    }

    public void initForMockUse(int size) {
        sfCompleteName = new Reference();
        sfCompleteName.addElement(new HereReferencePart("farmer"));
        clusterLimit = size;
        buildNodeFarm();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        validateClusterRange(min, max);
        String rejectionText = "Rejecting request for "
                + "[" + min + ", " + max + "]"
                + " nodes of role " + role;

        //now check the role is allowed
        ClusterRoleInfo info = lookupRoleInfo(role);
        if (info == null) {
            throw new UnsupportedClusterRoleException(role, this);
        }
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
        if (countFreeNodes() < min) {
            String message = rejectionText + " : no space in cluster";
            sfLog().info(message);
            throw new NoClusterSpaceException(message);
        }
        List<ClusterNode> newnodes = new ArrayList<ClusterNode>(nodesToAllocate);
        for (int i = 0; i < nodesToAllocate; i++) {
            ClusterNode clusterInstance = createOneInstance(info);
            if (clusterInstance == null) {
                //we have run out of space, but as it is in range, the overall operation will succeed
                break;
            }
            newnodes.add(clusterInstance);
        }
        sfLog().info("Created " + newnodes.size() + " nodes of role " + role);
        return newnodes.toArray(new ClusterNode[newnodes.size()]);
    }

    protected void buildNodeFarm() {
        nodeFarm = new HashMap<String, FarmNode>(clusterLimit);
        for (int i = 0; i < clusterLimit; i++) {
            FarmNode node = createFarmNode(i);
            nodeFarm.put(node.getId(), node);
        }
    }

    /**
     * Creates a farm node entry. The mock implementation just creates a stub one
     *
     * @param nodeCounter position in the farm (just a helper)
     * @return a new farm node
     */
    protected FarmNode createFarmNode(int nodeCounter) {
        ClusterNode node = new ClusterNode();
        String machinename = "host" + nodeCounter;
        node.setId(machinename);
        node.setHostname(machinename + "." + domain);
        node.setExternalHostname(machinename + "." + externalDomain);
        FarmNode fnode = new FarmNode(node, null, null);
        return fnode;
    }

    /**
     * Create a node and add it to the list
     *
     * @param role role to create
     * @return the instance, or null if there is no room
     */
    private synchronized ClusterNode createOneInstance(ClusterRoleInfo role) {
        for (FarmNode node : farmNodes()) {
            if (node.isFree()) {
                //allocate and use
                node.setRoleInfo(role);
                return node.getClusterNode();
            }
        }
        return null;
    }

    /**
     * Add a role to the list of allowed roles. No way to remove them.
     *
     * @param role     role to add
     * @param roleInfo role information
     */
    public void addRole(String role, ClusterRoleInfo roleInfo) {
        roleInfoMap.put(role, roleInfo);
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
     * Returns true iff the role is in range
     *
     * @param role     role to look for
     * @param quantity quantity to allocate
     * @return true if all roles are allowed, or
     */
    public boolean roleInRange(String role, int quantity) {
        ClusterRoleInfo info = lookupRoleInfo(role);
        if (info == null) {
            return false;
        }
        return info.isInRange(quantity);
    }

    private ClusterRoleInfo lookupRoleInfo(String role) {
        return roleInfoMap.get(role);
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

    protected Collection<FarmNode> farmNodes() {
        return nodeFarm.values();
    }

    /**
     * Find a node in a role
     *
     * @param role role to look for
     * @return a node in this role, or null
     */
    protected ClusterNode nodeInRole(String role) {
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
     */
    protected synchronized int nodesInRole(String role) {
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
            node.free();
        }
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
            node.free();
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
                node.free();
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
            if(!node.isFree()) {
                result.add(node.getClusterNode());
            }
        }
        return collectionToArray("", result);
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
        return collectionToArray(role, result);
    }

    private ClusterNode[] collectionToArray(String role, Collection<ClusterNode> result) {
        sfLog().info("list(" + role + ") returning " + result.size() + " nodes");
        return result.toArray(new ClusterNode[result.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @return a possibly empty list of role names
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized String[] listAvailableRoles() throws IOException, SmartFrogException {
        return roleInfoMap.keySet().toArray(new String[roleInfoMap.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @return a possibly empty list of roles
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterRoleInfo[] listClusterRoles() throws IOException, SmartFrogException {
        ClusterRoleInfo[] roleInfo = new ClusterRoleInfo[roleInfoMap.size()];
        int count = 0;
        for (String role : roleInfoMap.keySet()) {
            ClusterRoleInfo info = new ClusterRoleInfo(role);
            roleInfo[count++] = info;
        }
        return roleInfo;
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

}
