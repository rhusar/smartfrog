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
import java.util.List;
import java.util.ArrayList;

public class MockClusterFarmerImpl extends AbstractClusterFarmer {

    int clusterLimit=1000;
    int counter;
    String domain = "internal";
    String externalDomain = "external";

    List<ClusterInstance> nodes = new ArrayList<ClusterInstance>();

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
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterInstance[] create(String role, int min, int max) throws IOException, SmartFrogException {
        validateClusterRange(min, max);
        if (clusterSpace() < min) {
            throw new NoClusterSpaceException();
        }
        List<ClusterInstance> newnodes = new ArrayList<ClusterInstance>(max);
        for (int i = 0; i < max; i++) {
            ClusterInstance clusterInstance = createOneInstance(role);
            if (clusterInstance == null) {
                break;
            }
            newnodes.add(clusterInstance);
        }
        return newnodes.toArray(new ClusterInstance[newnodes.size()]);
    }

    private void validateClusterRange(int min, int max) throws SmartFrogException {
        if (max < min) {
            throw new SmartFrogException("The maximum number of machines requested was less than the minimum");
        }
    }

    /**
     * Create a node and add it to the list
     *
     * @param role
     *
     * @return the instance, or null if there is no room
     */
    private synchronized ClusterInstance createOneInstance(String role) {
        if (clusterSpace()>0) {
            ClusterInstance node = new ClusterInstance();
            nodes.add(node);
            counter++;
            String machinename = "host" + counter;
            node.id = machinename;
            node.hostname = machinename + "." + domain;
            node.externalHostname = machinename + "." + externalDomain;
            node.role = role;
            return node;
        } else {
            return null;
        }
    }

    /**
     * Get the current cluster space
     * @return the number of machines between the current size and that of the cluster limit.
     * This may be less than zero, if the cluster limit was reduced
     */
    private synchronized int clusterSpace() {
        return clusterLimit-nodes.size();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterInstance lookup(String id) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterInstance lookupByHostname(String hostname) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String id) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String[] nodesToDelete) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(ClusterInstance[] nodesToDelete) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void deleteAllInRole(String role) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterInstance[] list() throws IOException, SmartFrogException {
        return new ClusterInstance[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterInstance[] list(String role) throws IOException, SmartFrogException {
        return new ClusterInstance[0];
    }
}
