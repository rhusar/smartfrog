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
package org.smartfrog.services.amazon.farmer;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import org.smartfrog.services.amazon.ec2.EC2ComponentImpl;
import org.smartfrog.services.amazon.ec2.EC2Instance;
import org.smartfrog.services.amazon.ec2.SmartFrogEC2Exception;
import org.smartfrog.services.farmer.AbstractClusterFarmer;
import org.smartfrog.services.farmer.ClusterNode;
import org.smartfrog.services.farmer.NoClusterSpaceException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements an EC2 cluster farmer.
 */
public class EC2ClusterFarmerImpl extends EC2ComponentImpl implements EC2ClusterFarmer, EC2Instance {
    private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>(0);
    private int clusterLimit = 10;
    private int nodeCount = 0;
    protected Prim roles;
    public static final String ERROR_NO_VALID_IMAGE_ID = "No valid imageID for role ";
    private HashMap<String, RoleBinding> roleBindings;


    public EC2ClusterFarmerImpl() throws RemoteException {
    }


    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //start the children
        synchCreateChildren();
        //now look at the settings
        clusterLimit = sfResolve(ATTR_CLUSTER_LIMIT, clusterLimit, true);
        sfLog().info("Creating EC2farmer with a limit of " + clusterLimit);
        roles = sfResolve(ATTR_ROLES, roles, true);
        roleBindings = new HashMap<String, RoleBinding>();
        buildRoleBindings();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        List<ClusterNode> nodes = createNodes(role, min, max);
        return nodes.toArray(new ClusterNode[nodes.size()]);
    }

    /**
     * Create the nodelist
     * @param role instance role
     * @param min  min# to create
     * @param max  max# to create; must be equal to or greater than the min value
     * @return a list of created nodes, size between the min and max values
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    private List<ClusterNode> createNodes(String role, int min, int max) throws SmartFrogException, IOException {
        AbstractClusterFarmer.validateClusterRange(min, max);
        int limit = addNodes(min, max);
        LaunchConfiguration launch = createLaunchConfigFromRole(role);
        launch.setMaxCount(limit);
        launch.setMinCount(min);
        List<ClusterNode> nodes = new ArrayList<ClusterNode>(limit);
        try {
            ReservationDescription reservation = getEc2binding().runInstances(launch);
            for (ReservationDescription.Instance instance : reservation.getInstances()) {
                nodes.add(createFromReservationInstance(instance));
            }
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
        return nodes;
    }

    /**
     * Release a number of nodes
     *
     * @param number to release
     * @return the number actually released, which may be less than that passed in
     */
    private synchronized int releaseNodes(int number) {
        nodeCount -= number;
        if (nodeCount < 0) {
            nodeCount = 0;
        }
        return nodeCount;
    }

    /**
     * Add the number of nodes
     *
     * @param min number of nodes to allocate
     * @param max the number to add
     * @return the number actually added. This may be less, if the count is > the limit
     * @throws NoClusterSpaceException there is no room in the cluster
     */
    private synchronized int addNodes(int min, int max) throws NoClusterSpaceException {
        int newCount = nodeCount + max;

        if (newCount > clusterLimit) {
            max = clusterLimit - newCount;
            newCount = clusterLimit;
            if (max < min) {
                throw new NoClusterSpaceException("Local cluster limit is blocking this operation");
            }
        }

        nodeCount = newCount;
        return max;
    }


    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public int deleteAll() throws IOException, SmartFrogException {
        try {
            List<ClusterNode> nodes = listInstances(EMPTY_STRING_LIST);
            List<String> ids = createIdList(nodes);
            getEc2binding().terminateInstances(ids);
            int deleted = nodes.size();
            releaseNodes(deleted);
            return deleted;
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String id) throws IOException, SmartFrogException {
        delete(new String[]{id});
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String[] nodes) throws IOException, SmartFrogException {
        try {
            getEc2binding().terminateInstances(nodes);
            releaseNodes(nodes.length);
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(ClusterNode[] nodes) throws IOException, SmartFrogException {
        try {
            List<String> ids = createIdList(nodes);
            getEc2binding().terminateInstances(ids);
            releaseNodes(nodes.length);
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public int deleteAllInRole(String role) throws IOException, SmartFrogException {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list() throws IOException, SmartFrogException {
        try {
            return listInstancesToArray(EMPTY_STRING_LIST);
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
    }

    /**
     * Take a list of instances and get their details (slow), then return an array of the results
     *
     * @param instanceIDs instance strings
     * @return the possibly empty list
     * @throws EC2Exception on failures
     */
    private ClusterNode[] listInstancesToArray(List<String> instanceIDs) throws EC2Exception {
        List<ClusterNode> nodes = listInstances(instanceIDs);
        return nodes.toArray(new ClusterNode[nodes.size()]);
    }

    /**
     * Take a list of instances and get their details (slow), then return list of the results
     *
     * @param instanceIDs instance strings
     * @return the possibly empty list
     * @throws EC2Exception on failures
     */
    private List<ClusterNode> listInstances(List<String> instanceIDs) throws EC2Exception {
        List<ReservationDescription> reservations = getEc2binding().describeInstances(instanceIDs);
        List<ClusterNode> nodes = new ArrayList<ClusterNode>(reservations.size());
        for (ReservationDescription reservation : reservations) {
            for (ReservationDescription.Instance instance : reservation.getInstances()) {
                nodes.add(createFromReservationInstance(instance));
            }
        }
        return nodes;
    }

    /**
     * Create a cluster node from a reservation instance
     *
     * @param instance instance to work from
     * @return the node
     */
    private static ClusterNode createFromReservationInstance(ReservationDescription.Instance instance) {
        ClusterNode node = new ClusterNode();
        node.setId(instance.getInstanceId());
        node.setHostname(instance.getPrivateDnsName());
        node.setExternallyVisible(true);
        node.setExternalHostname(instance.getDnsName());
        node.setDetails(instance.toString());
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookup(String id) throws IOException, SmartFrogException {
        try {
            List<ReservationDescription> list = getEc2binding().describeInstances(new String[]{id});
            if (list.isEmpty()) {
                return null;
            }
            List<ReservationDescription.Instance> instances = list.get(0).getInstances();
            if (instances.isEmpty()) {
                return null;
            }
            return createFromReservationInstance(instances.get(0));
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * Create a list of IDs from a node list
     *
     * @param nodes nodes to work on
     * @return a list of all the IDs in the nodes
     */
    private List<String> createIdList(List<ClusterNode> nodes) {
        List<String> ids = new ArrayList<String>(nodes.size());
        for (ClusterNode node : nodes) {
            ids.add(node.getId());
        }
        return ids;
    }

    /**
     * Create a list of IDs from a node list
     *
     * @param nodes nodes to work on
     * @return a list of all the IDs in the nodes
     */
    private List<String> createIdList(ClusterNode[] nodes) {
        List<String> ids = new ArrayList<String>(nodes.length);
        for (ClusterNode node : nodes) {
            ids.add(node.getId());
        }
        return ids;
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
        //get the component containing the roles

        //and build a list of all that is a CD itself
        List<String> rolelist = new ArrayList<String>();
        Iterator attrs = roles.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            String role = key.toString();
            Object value = roles.sfResolve(new Reference(role), true);
            if (value instanceof Prim) {
                Prim targetRole = (Prim) value;
                LaunchConfiguration launch = createLaunchConfiguration(role, targetRole);
                String configString = EC2ClusterRole.convertToString(launch);
                sfLog().info("Role " + role + " " + configString);
                rolelist.add(role);
            } else {
                sfLog().debug("Ignoring roles attribute " + role + " which maps to " + value);
            }
        }
        return rolelist.toArray(new String[rolelist.size()]);
    }

    /**
     * Look up a role, create a launch config from it
     *
     * @param role role to look up
     * @return the role
     * @throws RemoteException    IO/network problems
     * @throws SmartFrogException other problems
     */
    private LaunchConfiguration createLaunchConfigFromRole(String role)
            throws SmartFrogException, RemoteException {
        Prim targetRole = resolveRole(role);
        return createLaunchConfiguration(role, targetRole);
    }

    private LaunchConfiguration createLaunchConfiguration(String role, Prim targetRole)
            throws SmartFrogResolutionException, RemoteException {
        sfLog().info("Creating a Launch config from " + targetRole);
        LaunchConfiguration lc = EC2ClusterRole.createLaunchConfiguration(role, targetRole);
        return lc;
    }

    private Prim resolveRole(String role) throws SmartFrogResolutionException, RemoteException {
        return roles.sfResolve(role, (Prim) null, true);
    }

    /**
     * {@inheritDoc}
     *
     * @return the number of roles
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public int buildRoleBindings() throws RemoteException, SmartFrogException {

        //and build a list of all that is a CD itself
        Iterator attrs = roles.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            String role = key.toString();
            Object value = roles.sfResolve(new Reference(role), true);
            if (value instanceof Prim) {
                Prim targetRole = (Prim) value;
                LaunchConfiguration launch = createLaunchConfiguration(role, targetRole);
                RoleBinding binding = new RoleBinding(role, targetRole, launch);
                roleBindings.put(role, binding);
                sfLog().info(binding.toString());
                
            } else {
                sfLog().debug("Ignoring roles attribute " + role + " which maps to " + value);
            }
        }
        return roleBindings.size();
    }

}
