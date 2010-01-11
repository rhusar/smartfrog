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

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.MasterWorkerRoles;
import org.smartfrog.services.cloudfarmer.client.web.clusters.masterworker.hadoop.descriptions.TemplateNames;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created 01-Sep-2009 13:48:11
 */

public class PhysicalClusterController extends ClusterController implements TemplateNames {

    public PhysicalClusterController() {
    }

    /**
     * {@inheritDoc}
     *
     * @return the cluster description
     */
    @Override
    public String getDescription() {
        return "Physical host controller";
    }

    @Override
    public String getRemoteDescription() throws IOException, SmartFrogException {
        return getDescription();
    }

    /**
     * Lists the hosts
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public void refreshHostList() throws IOException, SmartFrogException {
        //build the list of roles only
        refreshRoleList();
    }

    /**
     * {@inheritDoc}
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public void refreshRoleList() throws IOException, SmartFrogException {
        HashMap<String, ClusterRoleInfo> roles = new HashMap<String, ClusterRoleInfo>(2);
        ClusterRoleInfo master = new ClusterRoleInfo(MasterWorkerRoles.MASTER);
        master.setRecommendedSize(1,1);
        master.setRoleSize(1, 1);
        roles.put(master.getName(), master);
        ClusterRoleInfo worker = new ClusterRoleInfo(MasterWorkerRoles.WORKER);
        worker.setRecommendedSize(3, -1);
        worker.setRoleSize(1, -1);
        roles.put(worker.getName(), worker);
        replaceRoles(roles);
    }


    /**
     * Delete a host
     *
     * @param hostID a host ID
     * @return true if the request has been queued
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public synchronized boolean deleteHost(String hostID) throws IOException, SmartFrogException {
        HostInstance instance = lookupHost(hostID);
        if (instance != null) {
            //terminate the application
            removeHostInstance(instance);
            instance.terminateApplication();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Say yes, named hosts are allowed
     *
     * @return true;
     */
    @Override
    public boolean canAddNamedHost() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public HostInstance addNamedHost(String hostname, boolean isMaster, boolean isWorker)
            throws IOException, SmartFrogException {
        HostInstance instance = super.addNamedHost(hostname, isMaster, isWorker);
        addHostInstance(instance);
        //now install the role
        installRole(instance, isMaster, isWorker);
        return instance;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        network trouble
     * @throws SmartFrogException SF trouble
     */
    @Override
    public void shutdownCluster() throws IOException, SmartFrogException {
        try {
            for (HostInstance host : this) {
                try {
                    host.terminateApplication();
                } catch (IOException e) {
                    log.error("When terminating " + host + ": " + e, e);
                }
            }
            clearHostList();
        } finally {
            stopCluster();
        }
    }

}
