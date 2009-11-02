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
package org.smartfrog.services.cloudfarmer.server.manual;

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.server.common.AbstractFarmNodeClusterFarmer;
import org.smartfrog.services.cloudfarmer.server.common.FarmNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.io.IOException;

/**
 * The manual cluster farmer
 */

public class ManualClusterFarmerImpl extends AbstractFarmNodeClusterFarmer {

    public static final String ATTR_HOSTS = "hosts";
    public static final String ERROR_NO_HOSTNAME = "No hostname for host ";
    public static final String ERROR_WRONG_TYPE = "Expected a component implementing ManualHost";
    public static final String ERROR_DUPLICATE_HOSTNAME = "Duplicate hostname for node ";

    public ManualClusterFarmerImpl() throws RemoteException {
    }

    /**
     * set up the cluster
     *
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        buildRoleMap();
        buildNodeFarm();
    }

    /**
     * Build the node farm by looking for children of the hosts child
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    @Override
    protected void buildNodeFarm() throws SmartFrogException, RemoteException {

        Prim child = sfResolve(ATTR_HOSTS, (Prim) null, true);
        nodeFarm = new HashMap<String, FarmNode>(clusterLimit);
        StringBuilder names = new StringBuilder();
        Iterator attrs = child.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            String name = key.toString();
            Reference keyRef = new Reference(name);
            Object value = child.sfResolve(keyRef, true);
            if (value instanceof ManualHost) {
                ManualHost targetRole = (ManualHost) value;
                FarmNode node = createFarmNode(name, targetRole);
                //reject null IDs
                String id = node.getId();
                if (id.isEmpty()) {
                    throw new SmartFrogResolutionException(
                            keyRef,
                            child.sfCompleteName(),
                            ERROR_NO_HOSTNAME + name
                                    + " : " + node);

                }
                //check the node is not there
                FarmNode existingNode = nodeFarm.get(id);
                if (existingNode != null) {
                    throw new SmartFrogResolutionException(
                            keyRef,
                            child.sfCompleteName(),
                            ERROR_DUPLICATE_HOSTNAME
                                    + name + " hostname " + node.getHostname() + " clashes with "
                                    + existingNode);

                }
                nodeFarm.put(id, node);
                names.append(name);
                names.append(" ");
            } else {
                if (value instanceof Prim) {
                    throw new SmartFrogResolutionException(keyRef,
                            child.sfCompleteName(),
                            ERROR_WRONG_TYPE,
                            value);
                } else {
                    sfLog().debug("Ignoring roles attribute " + name + " which maps to " + value);
                }
            }
        }
        rebuildClusterLimit();

    }

    /**
     * Rebuild the cluster limit after adding or removing nodes
     * @throws SmartFrogRuntimeException  SmartFrog problems
     * @throws RemoteException  network problems
     */
    private void rebuildClusterLimit() throws SmartFrogRuntimeException, RemoteException {
        //the cluster limit is the size of the farm
        replaceClusterLimit(nodeFarm.size());
    }

    /**
     * Creates a farm node entry
     *
     * @param host the host
     * @param name name of the host's prim
     * @return a new farm node
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException    network problems
     */
    protected FarmNode createFarmNode(String name, ManualHost host) throws SmartFrogException, RemoteException {
        ClusterNode node = new ClusterNode();
        String hostname = host.getHostname().trim().toLowerCase(Locale.ENGLISH);
        node.setId(hostname);
        node.setHostname(hostname);
        node.setExternalHostname(hostname);
        node.setDetails(host.getDescription());
        FarmNode fnode = new FarmNode(node, null, host);
        return fnode;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() throws IOException, SmartFrogException {
        return "Manual Farmer";
    }    

}
