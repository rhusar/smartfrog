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
import org.smartfrog.services.cloudfarmer.client.web.model.RemoteDaemon;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.ProcessCompound;
import org.smartfrog.sfcore.reference.Reference;

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

        //bind to this reference
        try {
            farmer = resolveFarmer(daemon.getBoundProcess(), path);
        } catch (SmartFrogResolutionException e) {
            log.error("Failed to bind to " + path + ": " + e, e);
            throw e;
        } catch (RemoteException e) {
            log.error("Failed to bind to " + path + ": " + e, e);
            throw e;
        }
    }

    /**
     * code to resolve the farmer. This is kept separate just to make testing easier
     * @param process
     * @param path
     * @return
     * @throws SmartFrogResolutionException
     * @throws IOException
     */
    public static ClusterFarmer resolveFarmer(ProcessCompound process, String path)
            throws SmartFrogResolutionException, IOException {
        String newpath = convertPath(path);
        Reference ref = new Reference(newpath, true);
        Prim farmerPrim;
        farmerPrim = process.sfResolve(ref, (Prim) null, true);
        if (!(farmerPrim instanceof ClusterFarmer)) {
            throw new SmartFrogResolutionException(
                    "There is no ClusterFarmer at " + newpath + " instead an instance of "
                            + farmerPrim.getClass(), farmerPrim);
        }
        return (ClusterFarmer) farmerPrim;
    }

    /**
     * Do any path conversion to make it easier to resolve references
     * @param path path to convert
     * @return processed path. Default expansion and / to : conversion will have taken place, leading / stripped
     */
    public static String convertPath(String path) {
        String newpath;
        newpath = path.replace('/', ':');
        while (newpath.startsWith(":")) {
            newpath = newpath.substring(1);
        }
        while (newpath.endsWith(":")) {
            newpath = newpath.substring(0,newpath.length()-1);
        }
        if (newpath.isEmpty()) {
            newpath = FARMER_PATH;
        }
        return newpath;
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
