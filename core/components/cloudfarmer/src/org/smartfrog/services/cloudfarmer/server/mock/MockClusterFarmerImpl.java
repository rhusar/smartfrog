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
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * This is a mock cluster, very simple. A counter tracks the number of machines allocated, and whenever you ask for new
 * machines, it gets incremented. The system tracks the number of machines currently allocated, and rejects requests to
 * get more
 */
public class MockClusterFarmerImpl extends AbstractFarmNodeClusterFarmer implements ClusterFarmer {


    private String domain = "internal";
    private String externalDomain = "external";

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
        resolveClusterLimit();
        sfLog().info("Creating Farmer with a limit of " + clusterLimit);
        buildRoleMap();
        buildNodeFarm();
    }

    /**
     * Entry point for some mock tests, fixes up enough internal data structures to avoid NPEs
     * @param size cluster size
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void initForMockUse(int size) throws SmartFrogException, RemoteException {
        sfCompleteName = new Reference();
        sfCompleteName.addElement(new HereReferencePart("farmer"));
        clusterLimit = size;
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


}
