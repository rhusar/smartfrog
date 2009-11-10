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

import org.smartfrog.services.cloudfarmer.api.ClusterNode;
import org.smartfrog.services.cloudfarmer.api.LocalSmartFrogDescriptor;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;

/**
 * Any dynamic cluster controller; not something you can instantiate yourself, as the constructor is private
 */

public class DynamicClusterController extends ClusterController {


    protected DynamicClusterController(String baseURL) {
        super(baseURL);
    }

    /**
     * {@inheritDoc}
     *
     * @return the cluster description
     */
    @Override
    public String getDescription() {
        return "Dynamic Cluster controller bound to " + getBaseURL();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void refreshHostList() throws IOException, SmartFrogException {

    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void refreshRoleList() throws IOException, SmartFrogException {

    }

    /**
     * Override point
     *
     * @return true iff hosts can be added through createHost
     */
    public boolean canCreateHost() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    @Override
    public HostInstance createHost(String hostname, boolean largeInstance, LocalSmartFrogDescriptor descriptor)
            throws IOException, SmartFrogException {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        IO problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void shutdownCluster() throws IOException, SmartFrogException {

    }

    /**
     * log cluster nodes at the info level
     * @param clusterNodes nodes
     */
    protected void logClusterNodes(ClusterNode[] clusterNodes) {
        for (ClusterNode node:clusterNodes) {
            log.info(node.toString());
        }
    }
}