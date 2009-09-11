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

package org.smartfrog.services.farmer;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * This is a mock cluster, very inefficent, but as it is only for testing, there is no point adding any form of speedup,
 * not even a little bit. Simpler is best.
 */
public class MockClusterFarmerImpl extends AbstractClusterFarmer implements ClusterFarmer {

    private int clusterLimit = 1000;
    private int counter;
    private String domain = "internal";
    private String externalDomain = "external";

    private Map<String, String> roles = new HashMap<String, String>();

    private List<ClusterNode> nodes = new LinkedList<ClusterNode>();


    public MockClusterFarmerImpl() throws RemoteException {
    }

    public int getClusterLimit() {
        return clusterLimit;
    }

    public void setClusterLimit(int clusterLimit) {
        this.clusterLimit = clusterLimit;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        validateClusterRange(min, max);
        if (clusterSpace() < min) {
            throw new NoClusterSpaceException();
        }
        List<ClusterNode> newnodes = new ArrayList<ClusterNode>(max);
        for (int i = 0; i < max; i++) {
            ClusterNode clusterInstance = createOneInstance(role);
            if (clusterInstance == null) {
                break;
            }
            newnodes.add(clusterInstance);
        }
        return newnodes.toArray(new ClusterNode[newnodes.size()]);
    }

    /**
     * Create a node and add it to the list
     *
     * @param role role to create
     * @return the instance, or null if there is no room
     */
    private synchronized ClusterNode createOneInstance(String role) {
        if (clusterSpace() > 0) {
            ClusterNode node = new ClusterNode();
            nodes.add(node);
            counter++;
            String machinename = "host" + counter;
            node.setId(machinename);
            node.setHostname(machinename + "." + domain);
            node.setExternalHostname(machinename + "." + externalDomain);
            node.setRole(role);
            return node;
        } else {
            return null;
        }
    }

    /**
     * Add a role to the list of allowed roles. No way to remove them.
     * @param role role to add
     */
    public void addRole(String role) {
        roles.put(role, role);
    }

    /**
     * Is the role allowed
     * @param role to ask for
     * @return true iff the role is supported
     */
    public boolean roleAllowed(String role) {
        return roles.size()==0 || roles.containsKey(role);
    }

    /**
     * Get the current cluster space
     *
     * @return the number of machines between the current size and that of the cluster limit. This may be less than
     *         zero, if the cluster limit was reduced
     */
    private synchronized int clusterSpace() {
        return clusterLimit - nodes.size();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookup(String id) throws IOException, SmartFrogException {
        for (ClusterNode node : nodes) {
            if (id.equals(node.getId())) {
                return node;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        for (ClusterNode node : nodes) {
            if (hostname.equals(node.getHostname())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Find a node in a role
     *
     * @param role role to look for
     * @return a node in this role, or null
     */
    protected ClusterNode nodeInRole(String role) {
        for (ClusterNode node : nodes) {
            if (role.equals(node.getRole())) {
                return node;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String id) throws IOException, SmartFrogException {
        ClusterNode node = lookup(id);
        nodes.remove(node);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
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
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(ClusterNode[] nodesToDelete) throws IOException, SmartFrogException {
        for (ClusterNode node : nodesToDelete) {
            nodes.remove(node);
        }
    }


    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public int deleteAllInRole(String role) throws IOException, SmartFrogException {
        ClusterNode node;
        int count = 0;
        while ((node = nodeInRole(role)) != null) {
            nodes.remove(node);
            count++;
        }
        return count;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list() throws IOException, SmartFrogException {
        return nodes.toArray(new ClusterNode[nodes.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        List<ClusterNode> result = new ArrayList<ClusterNode>(nodes.size());
        for (ClusterNode node: nodes) {
            if (role.equals(node.getRole())) {
                result.add(node);
            }
        }
        return result.toArray(new ClusterNode[result.size()]);
    }
}
