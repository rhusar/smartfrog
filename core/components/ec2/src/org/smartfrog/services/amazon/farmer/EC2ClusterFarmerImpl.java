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
import com.xerox.amazonws.ec2.ReservationDescription;
import org.smartfrog.services.amazon.ec2.EC2ComponentImpl;
import org.smartfrog.services.amazon.ec2.SmartFrogEC2Exception;
import org.smartfrog.services.farmer.AbstractClusterFarmer;
import org.smartfrog.services.farmer.ClusterNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements an EC2 cluster farmer.
 *
 */
public class EC2ClusterFarmerImpl extends EC2ComponentImpl implements EC2ClusterFarmer {
    private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>(0);


    public EC2ClusterFarmerImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        AbstractClusterFarmer.validateClusterRange(min, max);
        return new ClusterNode[0];
    }


    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public int deleteAll() throws IOException, SmartFrogException {
        try {
            List<ClusterNode> nodes = listInstances(EMPTY_STRING_LIST);
            List<String> ids = createIdList(nodes);
            getEc2binding().terminateInstances(ids);
            return nodes.size();
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e,this);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String id) throws IOException, SmartFrogException {
        delete(new String[]{id});
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String[] nodes) throws IOException, SmartFrogException {
        try {
            getEc2binding().terminateInstances(nodes);
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(ClusterNode[] nodes) throws IOException, SmartFrogException {
        try {
            List<String> ids = createIdList(nodes);
            getEc2binding().terminateInstances(ids);
        } catch (EC2Exception e) {
            throw new SmartFrogEC2Exception(e, this);
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
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
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
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
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
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * Create a list of IDs from a node list
     * @param nodes nodes to work on
     * @return a list of all the IDs in the nodes
     */
    private List<String> createIdList(List<ClusterNode> nodes) {
        List<String> ids = new ArrayList<String>(nodes.size());
        for (ClusterNode node:nodes) {
            ids.add(node.getId());
        }
        return ids;
    }

    /**
     * Create a list of IDs from a node list
     * @param nodes nodes to work on
     * @return a list of all the IDs in the nodes
     */
    private List<String> createIdList(ClusterNode [] nodes) {
        List<String> ids = new ArrayList<String>(nodes.length);
        for (ClusterNode node:nodes) {
            ids.add(node.getId());
        }
        return ids;
    }

    /**
     * {@inheritDoc}
     *
     * @return a possibly empty list of role names
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public String[] listAvailableRoles() throws IOException, SmartFrogException {
        //get the component containing the roles
        ComponentDescription roles = sfResolve(ATTR_ROLES, (ComponentDescription) null, true);
        //and build a list of all that is a CD itself
        List<String> rolelist = new ArrayList<String>();
        Iterator attrs = roles.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            Object value = roles.sfResolve(new Reference(key), true);
            if (value instanceof ComponentDescription) {
                rolelist.add(key.toString());
            }
        }
        return rolelist.toArray(new String[rolelist.size()]);
    }


}
