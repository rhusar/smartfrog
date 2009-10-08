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

package org.smartfrog.services.cloudfarmer.client.web.model.cluster;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;

import java.io.IOException;
import java.net.URL;

/**
 * Created 10-Sep-2009 17:20:57
 *
 * this controller uses the {@link ClusterFarmer} API to ask for remote machines by given roles.
 */

public class DynamicSmartFrogClusterController extends DynamicClusterController {

    private RemoteDaemon daemon;
    private ClusterFarmer farmer;

    public DynamicSmartFrogClusterController(String baseURL) {
        super(baseURL);
    }


    /**
     * Create a remote daemon proxy bound do the base URL of our cluster controller
     *
     * @throws SmartFrogException SF Problems, including resolution
     * @throws IOException        Network and IO trouble
     */
    @Override
    public void bind() throws SmartFrogException, IOException {
        URL server = getTargetURL();
        daemon = new RemoteDaemon(getBaseURL());
        daemon.bindOnDemand();
        //now work out the farmer reference using the path, or, if empty, the 
        //default path
        String path = server.getPath();
        if (!path.isEmpty() && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.isEmpty()) {
            path = FARMER_REFERENCE;
        }
        //bind to this reference
        Prim farmerPrim = daemon.getBoundProcess().sfResolve(path, (Prim) null, true);
        farmer = (ClusterFarmer) farmerPrim;
    }

    public RemoteDaemon getDaemon() {
        return daemon;
    }

    public ClusterFarmer getFarmer() {
        return farmer;
    }

    @Override
    public String getDescription() {
        return "SmartFrog cluster at " + getBaseURL();
    }

    private boolean checkFarmer() {
        if (farmer == null) {
            log.error("Not bound to a farmer");
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshHostList() throws IOException, SmartFrogException {
        if (!checkFarmer()) {
            return;
        }
        ClusterNode[] clusterNodes = farmer.list();
        HostInstanceList newHostList = new HostInstanceList(clusterNodes.length);
        for (ClusterNode node : clusterNodes) {
            //need to look it up in the existing list; if it is there we copy it over
            HostInstance existing = lookupHost(node.getId());
            if (existing == null) {
                HostInstance instance = new HostInstance(node.getId(), node.getHostname(), true);
                //look for an application here?
                newHostList.add(instance);
            } else {
                newHostList.add(existing);
            }
        }
        //now push out the new value
        replaceHostList(newHostList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownCluster() throws IOException, SmartFrogException {
        if (checkFarmer()) {
            farmer.deleteAll();
        }
    }


    /**
     * {@inheritDoc}
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    public HostInstanceList createHosts(String role, int min, int max)
            throws IOException, SmartFrogException {
        ClusterNode[] clusterNodes = farmer.create(role, min, max);
        HostInstanceList newHostList = new HostInstanceList(clusterNodes.length);
        synchronized (this) {
            for (ClusterNode node : clusterNodes) {
                HostInstance instance = new HostInstance(node.getId(), node.getHostname(), true);
                newHostList.add(instance);
                addHostInstance(instance);
            }
        }
        return newHostList;
    }

}
