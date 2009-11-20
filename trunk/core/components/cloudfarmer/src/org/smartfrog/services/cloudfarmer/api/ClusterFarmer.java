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

package org.smartfrog.services.cloudfarmer.api;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.rmi.Remote;

/**
 * This interface is an (unstable) interface for a cluster farmer, a component that provides cluster management of
 * virtual clusters.
 * 
 * The model is that you first may start a cluster; at the end of use stop it.
 * 
 * You may opt to release all resources, which means release all clusters belonging to the authenticated user
 * This is to prevent leakage of resources from farmer restarts
 * 
 */
public interface ClusterFarmer extends Remote {

    /**
     * Attribute used in deployments {@value}
     */
    String ATTR_ROLES = "roles";

    /**
     * This is there to stop users accidentally running up large bills. If <0, it means ignore {@value}
     */
    String ATTR_CLUSTER_LIMIT = "clusterLimit";

    /**
     * {@value}
     */
    String ATTR_DEPLOYMENT_FACTORY = "deploymentFactory";
    
    /**
     * Create a number of instances. The operation will return when the instance is created, but it may not be live.
     *
     * @param role instance role
     * @param min  min# to create
     * @param max  max# to create; must be equal to or greater than the min value
     * @return an array of created nodes, size between the min and max values
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public ClusterNode[] create(String role, int min, int max)
            throws IOException, SmartFrogException;

    /**
     * Delete a node by ID This is an async operation.
     *
     * @param id node ID
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public void delete(String id)
            throws IOException, SmartFrogException;

    /**
     * Delete a list of nodes This is an async operation.
     *
     * @param nodes node IDs
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public void delete(String[] nodes)
            throws IOException, SmartFrogException;

    /**
     * Delete a list of nodes This is an async operation.
     *
     * @param nodes nodes by instance
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public void delete(ClusterNode[] nodes)
            throws IOException, SmartFrogException;

    /**
     * Delete all nodes in a specific role. This is an async operation.
     *
     * @param role role of the nodes
     * @return the number scheduled for deletion
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public int deleteAllInRole(String role)
            throws IOException, SmartFrogException;


    /**
     * Shut down everything. All nodes are shut down, regardless of role.
     * 
     *
     * @return the number scheduled for deletion
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */

    public int deleteAll()
            throws IOException, SmartFrogException;


    /**
     * Idempotent call to shut down all nodes and any other resources used in the farm.
     * 
     * The expectation is that after this call is returned, no machines, virtual clusters etc exist. 
     * Persistent storage is untouched
     *
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */

    public void releaseAllResources()
            throws IOException, SmartFrogException;

    /**
     * Get a list of nodes
     *
     * @return a possibly empty list of nodes
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public ClusterNode[] list()
            throws IOException, SmartFrogException;

    /**
     * Get a list of nodes in a role
     *
     * @param role role of the nodes
     * @return a possibly empty list of nodes
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public ClusterNode[] list(String role)
            throws IOException, SmartFrogException;

    /**
     * Get a node by ID
     *
     * @param id ID to search on
     * @return an instance or null
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public ClusterNode lookup(String id)
            throws IOException, SmartFrogException;


    /**
     * Get a node by hostname
     *
     * @param hostname hostname to search on
     * @return an instance or null
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public ClusterNode lookupByHostname(String hostname)
            throws IOException, SmartFrogException;

    /**
     * Create a list of available roles. The list may vary during the life of a farmer.
     *
     * @return a possibly empty list of role names
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public String[] listAvailableRoles() throws IOException, SmartFrogException;

    /**
     * More powerful API call than {@link #listAvailableRoles()} this lists a description and range for every role. This
     * lets client apps display more details and do some in-gui validation
     *
     * @return a list of roles, possibly empty
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public ClusterRoleInfo[] listClusterRoles() throws IOException, SmartFrogException;

    /**
     * Query the farmer to see if it is live. 
     * @return true if the service considers itself available. If not, it can return false or throw
     * an exception.
     * @throws IOException something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public boolean isFarmerAvailable() throws IOException, SmartFrogException;

    /**
     * Caller can return diagnostics text for use in bug reports 
     * @return a short description (e.g. name)
     * @throws IOException something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public String getDescription() throws IOException, SmartFrogException;

    /**
     * Caller can return diagnostics text for use in bug reports, use \n between lines and
     * expect this printed as preformatted text (with all angle brackets stripped) 
     * @return a diagnostics text string.
     * @throws IOException something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public String getDiagnosticsText() throws IOException, SmartFrogException;

    /**
     * Call to start the cluster if it is not already live
     * @throws IOException something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public void startCluster() throws IOException, SmartFrogException;

    /**
     * Call to stop the cluster. This may be a no-op, it may shut down the entire cluster.
     * Some infrastructures require this to release allocations.
     * @throws IOException something went wrong
     * @throws SmartFrogException something different went wrong
     */
    public void stopCluster() throws IOException, SmartFrogException;

    /**
     * Create a node deployment service for the specific node
     * @param node node to deploy to
     * @return a deployment service
     * @throws IOException IO problems
     * @throws SmartFrogException Other problems
     */
    public NodeDeploymentService createNodeDeploymentService(ClusterNode node) throws IOException, SmartFrogException; 
}
