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
import org.smartfrog.services.cloudfarmer.server.common.AbstractFarmNodeClusterFarmer;
import org.smartfrog.services.cloudfarmer.server.common.FarmNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.io.IOException;

/**
 * This is a mock cluster, very simple. A counter tracks the number of machines allocated, and whenever you ask for new
 * machines, it gets incremented. The system tracks the number of machines currently allocated, and rejects requests to
 * get more
 */
public class MockClusterFarmerImpl extends AbstractFarmNodeClusterFarmer implements ClusterFarmer {


    /**
     * {@value}
     */
    public static final String ATTR_DOMAIN = "domain";

    /**
     * {@value}
     */
    public static final String ATTR_EXTERNAL_DOMAIN = "externalDomain";
    /**
     * {@value}
     */
    public static final String ATTR_AVAILABLE = "available";

    public static final String ATTR_NODE_STARTUP_DELAY_MILLISECONDS = "nodeStartupDelayMilliseconds";
    private String domain = "internal";
    private String externalDomain = "external";
    private boolean available = true;
    private int nodeStartupDelayMilliseconds;

    public MockClusterFarmerImpl() throws RemoteException {
    }


    /**
     * set up the mock cluster
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        domain = sfResolve(ATTR_DOMAIN, "", true);
        externalDomain = sfResolve(ATTR_EXTERNAL_DOMAIN, "", true);
        available = sfResolve(ATTR_AVAILABLE, true, true);
        nodeStartupDelayMilliseconds = sfResolve(ATTR_NODE_STARTUP_DELAY_MILLISECONDS, 0, true);
        resolveClusterLimit();
        sfLog().info("Creating Farmer with a limit of " + clusterLimit);
        buildRoleMap();
        buildNodeFarm();
    }

    /**
     * Entry point for some mock tests, fixes up enough internal data structures to avoid NPEs
     * @param size cluster size
     * @param localDomain local domain suffix
     * @param externalDomain external domain suffix
     * @param isLive is the farmer live
     * @param startupDelay how long should it take per node to start up
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void initForMockUse(int size, String localDomain, String externalDomain, boolean isLive,
                               int startupDelay) throws SmartFrogException, RemoteException {
        sfCompleteName = new Reference();
        sfCompleteName.addElement(new HereReferencePart("farmer"));
        setClusterLimit(size);
        domain = localDomain;
        this.externalDomain = externalDomain;
        available = isLive;
        nodeStartupDelayMilliseconds = startupDelay;
        buildNodeFarm();
    }

    /**
     * Build the node farm. It is up to specific implementations to implement this
     * @throws SmartFrogException other problems
     * @throws RemoteException    network problems
     */
    protected void buildNodeFarm() throws SmartFrogException, RemoteException {
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
     * Add a role to the list of allowed roles. No way to remove them.
     *
     * @param role     role to add
     * @param roleInfo role information
     */
    public void addRole(String role, ClusterRoleInfo roleInfo) {
        roleInfoMap.put(role, roleInfo);
    }


    @Override
    public void startCluster() throws IOException, SmartFrogException {
        available = true;
    }

    @Override
    public void stopCluster() throws IOException, SmartFrogException {
        available = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFarmerAvailable() throws IOException, SmartFrogException {
        return available;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkClusterAvailable() throws IOException, SmartFrogException {
        if(!available) {
            throw new SmartFrogDeploymentException("Cluster is not available");
        }
    }

    /**
     * Optionally sleep before calling the superclass to allocate things
     * @param role role required
     * @return the node
     * @throws SmartFrogException
     * @throws IOException
     */
    @Override
    protected ClusterNode allocateOneNode(ClusterRoleInfo role) throws SmartFrogException, IOException {
        if (nodeStartupDelayMilliseconds > 0) {
            try {
                Thread.sleep(nodeStartupDelayMilliseconds);
            } catch (InterruptedException e) {
                throw new SmartFrogException(e);
            }
        }
        return super.allocateOneNode(role);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() throws IOException, SmartFrogException {
        return "Mock Farmer";
    }

}
