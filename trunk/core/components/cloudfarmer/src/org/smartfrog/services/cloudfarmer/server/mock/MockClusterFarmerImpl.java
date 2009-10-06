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
import org.smartfrog.services.cloudfarmer.api.UnsupportedClusterRoleException;
import org.smartfrog.services.cloudfarmer.server.AbstractClusterFarmer;
import org.smartfrog.services.cloudfarmer.server.common.ClusterRole;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This is a mock cluster, very simple. A counter tracks the number of machines allocated, and whenever you ask for new
 * machines, it gets incremented. The system tracks the number of machines currently allocated, and rejects requests to
 * get more
 */
public class MockClusterFarmerImpl extends AbstractClusterFarmer implements ClusterFarmer {

    private int clusterLimit = 1000;
    private int counter;

    private String domain = "internal";
    private String externalDomain = "external";
    private boolean allRolesAllowed;

    private Map<String, ClusterRoleInfo> roleInfoMap = new HashMap<String, ClusterRoleInfo>();

    private List<ClusterNode> nodes = new LinkedList<ClusterNode>();

    /**
     * {@value}
     */
    public static final String ATTR_ROLES = "roles";
    /**
     * {@value}
     */
    public static final String ATTR_CLUSTER_LIMIT = "clusterLimit";
    /**
     * {@value}
     */
    public static final String ATTR_DOMAIN = "domain";
    /**
     * {@value}
     */
    public static final String ATTR_EXTERNAL_DOMAIN = "externalDomain";

    public static final String ATTR_ALL_ROLES_ALLOWED = "allRolesAllowed";


    public MockClusterFarmerImpl() throws RemoteException {
    }

    /**
     * for unit tests: fix up the cloud farmer name
     *
     * @param name new name
     */
    public void fixupCompleteName(String name) {
        sfCompleteName = new Reference();
        sfCompleteName.addElement(new HereReferencePart(name));
    }

    public int getClusterLimit() {
        return clusterLimit;
    }

    public void setClusterLimit(int clusterLimit) {
        this.clusterLimit = clusterLimit;
    }

    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        StringBuilder rolenames = new StringBuilder();
        allRolesAllowed = sfResolve(ATTR_ALL_ROLES_ALLOWED, allRolesAllowed, true);
        Prim rolesChild = sfResolve(ATTR_ROLES, (Prim) null, true);
        Iterator attrs = rolesChild.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            String roleName = key.toString();
            Reference roleRef = new Reference(roleName);
            Object value = rolesChild.sfResolve(roleRef, true);
            if (value instanceof ClusterRole) {
                ClusterRole targetRole = (ClusterRole) value;
                roleInfoMap.put(roleName, targetRole.resolveRoleInfo(roleName));
                rolenames.append(roleName);
                rolenames.append(" ");
            } else {
                if (value instanceof Prim) {
                    throw new SmartFrogResolutionException(roleRef,
                            sfCompleteName,
                            "Expected a component implementing ClusterRole",
                            value);
                } else {
                    sfLog().debug("Ignoring roles attribute " + roleName + " which maps to " + value);
                }
            }
        }

        clusterLimit = sfResolve(ATTR_CLUSTER_LIMIT, clusterLimit, true);
        domain = sfResolve(ATTR_DOMAIN, "", true);
        externalDomain = sfResolve(ATTR_EXTERNAL_DOMAIN, "", true);
        sfLog().info("Creating Mock farmer with a limit of " + clusterLimit
                + " and roles: " + rolenames);
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
        if (clusterSpace() < min) {
            sfLog().info("Rejecting request for " + min + " nodes of role " + role + " : no space in cluster");
            throw new NoClusterSpaceException();
        }
        //now check the role is allowed
        if (!roleAllowed(role)) {
            throw new UnsupportedClusterRoleException(role, this);
        }
        List<ClusterNode> newnodes = new ArrayList<ClusterNode>(max);
        for (int i = 0; i < max; i++) {
            ClusterNode clusterInstance = createOneInstance(role);
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
     * Create a node and add it to the list
     *
     * @param role role to create
     * @return the instance, or null if there is no room
     */
    private synchronized ClusterNode createOneInstance(String role) {
        if (clusterSpace() > 0) {
            ClusterNode node = new ClusterNode();
            String machinename = "host" + counter;
            node.setId(machinename);
            node.setHostname(machinename + "." + domain);
            node.setExternalHostname(machinename + "." + externalDomain);
            node.setRole(role);
            nodes.add(node);
            sfLog().info("added " + node);
            counter++;
            return node;
        } else {
            return null;
        }
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
        return isAllRolesAllowed() || roleInfoMap.containsKey(role);
    }

    public boolean isAllRolesAllowed() {
        return allRolesAllowed;
    }

    public void setAllRolesAllowed(boolean allRolesAllowed) {
        this.allRolesAllowed = allRolesAllowed;
    }

    /**
     * Returns true iff the role is in range
     *
     * @param role     role to look for
     * @param quantity quantity to allocate
     * @return true if all roles are allowed, or
     */
    public boolean roleInRange(String role, int quantity) {
        if (isAllRolesAllowed()) {
            return true;
        }
        ClusterRoleInfo info = roleInfoMap.get(role);
        if (info == null) {
            return false;
        }
        return info.isInRange(quantity);
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
     * @throws IOException        IO/network problems
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
     * @throws IOException        IO/network problems
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
     * Count the number of nodes in a role
     *
     * @param role role to look for
     * @return number of nodes in this role
     */
    protected int nodesInRole(String role) {
        int count = 0;
        for (ClusterNode node : nodes) {
            if (role.equals(node.getRole())) {
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
    public void delete(String id) throws IOException, SmartFrogException {
        ClusterNode node = lookup(id);
        nodes.remove(node);
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
        //this is very inefficient, O(nodes)*O(nodesToDelete)
        for (ClusterNode node : nodesToDelete) {
            nodes.remove(node);
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
        int deleted = nodes.size();
        nodes = new ArrayList<ClusterNode>();
        return deleted;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
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
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list() throws IOException, SmartFrogException {
        return listToArray("", nodes);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        List<ClusterNode> result = new ArrayList<ClusterNode>(nodes.size());
        for (ClusterNode node : nodes) {
            if (role.equals(node.getRole())) {
                result.add(node);
            }
        }
        return listToArray(role, result);
    }

    private ClusterNode[] listToArray(String role, List<ClusterNode> result) {
        sfLog().info("list(" + role + ") returning " + nodes.size() + " nodes");
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
    public String[] listAvailableRoles() throws IOException, SmartFrogException {
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
    public ClusterRoleInfo[] listClusterRoles() throws IOException, SmartFrogException {
        ClusterRoleInfo[] roleInfo = new ClusterRoleInfo[roleInfoMap.size()];
        int count = 0;
        for (String role : roleInfoMap.keySet()) {
            ClusterRoleInfo info = new ClusterRoleInfo(role);
            roleInfo[count++] = info;
        }
        return roleInfo;
    }
}
