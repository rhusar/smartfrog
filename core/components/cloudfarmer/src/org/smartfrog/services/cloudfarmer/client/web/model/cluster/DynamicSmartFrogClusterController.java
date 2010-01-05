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
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.NodeDeploymentService;
import org.smartfrog.services.cloudfarmer.client.common.BaseRemoteDaemon;
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Created 10-Sep-2009 17:20:57
 *
 * this controller uses the {@link ClusterFarmer} API to ask for remote machines by given roles.
 */

public class DynamicSmartFrogClusterController extends DynamicClusterController {

    private ClusterFarmer farmer;

    /**
     * Create a cluster controller bound to a farmer instance
     * @param baseURL URL of the farmer
     */
    public DynamicSmartFrogClusterController(String baseURL) {
        super(baseURL);
    }

    /**
     * This is for testing only
     * @param farmer farmer to bind to
     */
    public DynamicSmartFrogClusterController(ClusterFarmer farmer) {
        super("bound");
        this.farmer = farmer;
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
        String path = server.getPath();
        RemoteDaemon daemon = new RemoteDaemon(getBaseURL());
        daemon.bindOnDemand();
        //now work out the farmer reference using the path, or, if empty, the
        //default path

        //bind to this reference
        try {
            farmer = BaseRemoteDaemon.resolveFarmer(daemon.getBoundProcess(), path);
        } catch (SmartFrogResolutionException e) {
            log.error("Failed to bind to " + path + ": " + e, e);
            throw e;
        } catch (RemoteException e) {
            log.error("Failed to bind to " + path + ": " + e, e);
            throw e;
        }
        //call the parent class, which will then start the cluster
        super.bind();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void startCluster() throws IOException, SmartFrogException {
        farmer.startCluster();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void stopCluster() throws IOException, SmartFrogException {
        if (checkFarmer()) {
            farmer.stopCluster();
        }
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
     * Query the farmer to see if it is live.
     *
     * @return true if the service considers itself available. If not, it can return false or throw an exception.
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public boolean isFarmerAvailable() throws IOException, SmartFrogException {
        return farmer != null && farmer.isFarmerAvailable();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshHostList() throws IOException, SmartFrogException {
        if (!checkFarmer()) {
            return;
        }
        //build the nodes
        ClusterNode[] clusterNodes = farmer.list();
        HostInstanceList newHostList = new HostInstanceList(clusterNodes.length);
        for (ClusterNode node : clusterNodes) {
            //need to look it up in the existing list; if it is there we copy it over
            HostInstance existing = lookupHost(node.getId());
            if (existing == null) {
                HostInstance instance = new HostInstance(node.getId(), node, true);
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
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public void refreshRoleList() throws IOException, SmartFrogException {
        if (!checkFarmer()) {
            return;
        }
        ClusterRoleInfo[] rolelist = farmer.listClusterRoles();
        HashMap<String, ClusterRoleInfo> roles = new HashMap<String, ClusterRoleInfo>(rolelist.length);
        for(ClusterRoleInfo roleInfo: rolelist) {
            roles.put(roleInfo.getName(), roleInfo);
        }
        replaceRoles(roles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownCluster() throws IOException, SmartFrogException {
        try {
            if (checkFarmer()) {
                farmer.deleteAll();
            }
        } finally {
            stopCluster();
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
        if(log.isInfoEnabled()) {
            logClusterNodes(clusterNodes);
        }
        HostInstanceList newHostList = new HostInstanceList(clusterNodes);
        synchronized (this) {
            for(HostInstance instance:newHostList) { 
                
                addHostInstance(instance);
            }
        }
        return newHostList;
    }

    /**
     * Caller can return diagnostics text for use in bug reports
     *
     * @return a short description (e.g. name)
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public String getRemoteDescription() throws IOException, SmartFrogException {
        return farmer.getDescription();
    }

    /**
     * Caller can return diagnostics text for use in bug reports, use \n between lines and expect this printed as
     * preformatted text (with all angle brackets stripped)
     *
     * @return a diagnostics text string.
     * @throws IOException        something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException {
        return farmer.getDiagnosticsText();
    }

    /**
     * Create a node deployment service for this node
     * @param hostInstance the host to work with
     * @return the node deployment service
     * @throws SmartFrogException trouble creating the service
     * @throws IOException        something went wrong
     */
    public NodeDeploymentService createNodeDeploymentService(HostInstance hostInstance)
            throws SmartFrogException, IOException {
        ClusterNode clusterNode = hostInstance.getClusterNode();
        return farmer.createNodeDeploymentService(clusterNode);
    }
}
