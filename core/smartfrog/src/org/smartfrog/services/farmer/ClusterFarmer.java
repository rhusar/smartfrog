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
import java.rmi.Remote;

/**
 * This interface is an (unstable) interface for a cluster farmer, a component that provides cluster management of
 * virtual clusters.
 */
public interface ClusterFarmer extends Remote {

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
     * You can get into trouble here if the farm controller is on a node manageable in the same list. Safer to delete by
     * role
     *
     * @return the number scheduled for deletion
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */

    public int deleteAll()
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
     * Create a list of available roles. 
     * The list may vary during the life of a farmer.
     * @return a possibly empty list of role names
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    public String[] listAvailableRoles() throws IOException, SmartFrogException;
}
