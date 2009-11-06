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

package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterFarmer;
import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Intermediate class for cluster farmer implementations contains any helper methods that they should be sharing.
 *
 * This class is a compound,
 */
public abstract class AbstractClusterFarmer extends CompoundImpl implements ClusterFarmer {

    /**
     * {@value}
     */
    public static final String WRONG_MACHINE_COUNT
            = "The maximum number of machines requested was less than the minimum";
    /**
     * {@value}
     */
    public static final String NEGATIVE_VALUES_NOT_SUPPORTED = "Negative values not supported";
    protected Map<String, ClusterRoleInfo> roleInfoMap = new HashMap<String, ClusterRoleInfo>();

    protected int clusterLimit = 1000;

    protected AbstractClusterFarmer() throws RemoteException {
    }


    /**
     * check the min and max arguments
     *
     * @param min minimum number of nodes desired
     * @param max maximumum number  desired
     * @throws SmartFrogDeploymentException if the parameters are somehow invalid
     */
    public static void validateClusterRange(int min, int max) throws SmartFrogDeploymentException {
        if (max < min) {
            throw new SmartFrogDeploymentException(WRONG_MACHINE_COUNT);
        }
        if (min < 0) {
            throw new SmartFrogDeploymentException(NEGATIVE_VALUES_NOT_SUPPORTED);
        }
    }


    /**
     * Get the local limit on the cluster size
     *
     * @return the limit on the cluster
     */
    public int getClusterLimit() {
        return clusterLimit;
    }

    public void setClusterLimit(int clusterLimit) {
        this.clusterLimit = clusterLimit;
    }

    /**
     * Resolve the cluster limit attribute
     *
     * @throws SmartFrogResolutionException resolution failure
     * @throws RemoteException              network problems
     */
    protected void resolveClusterLimit() throws SmartFrogResolutionException, RemoteException {
        clusterLimit = sfResolve(ATTR_CLUSTER_LIMIT, clusterLimit, true);
    }


    /**
     * Run through the roles attribute and build a role map. For every role found, {@link #processRole(String,
     * ClusterRoleInfo)} is called after the role has been added to the map.
     *
     * @throws SmartFrogException SF problems
     * @throws RemoteException    network problems
     */
    protected void buildRoleMap() throws SmartFrogException, RemoteException {
        StringBuilder rolenames = new StringBuilder();
        Prim rolesChild = sfResolve(ATTR_ROLES, (Prim) null, true);
        Iterator attrs = rolesChild.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            String roleName = key.toString();
            Reference roleRef = new Reference(roleName);
            Object value = rolesChild.sfResolve(roleRef, true);
            if (value instanceof ClusterRole) {
                ClusterRole targetRole = (ClusterRole) value;
                ClusterRoleInfo roleInfo = targetRole.resolveRoleInfo(roleName);
                roleInfo = processRole(roleName, roleInfo);
                roleInfoMap.put(roleName, roleInfo);
                rolenames.append(roleName);
                rolenames.append(" ");
            } else {
                if (value instanceof Prim) {
                    throw new SmartFrogResolutionException(roleRef,
                            rolesChild.sfCompleteName(),
                            "Expected a component implementing ClusterRole",
                            value);
                } else {
                    sfLog().debug("Ignoring roles attribute " + roleName + " which maps to " + value);
                }
            }
        }
        sfLog().info("Roles; " + rolenames);
    }

    /**
     * Override point: process a role. A new role info can be returned here, or parts of the configuration validated
     *
     * @param roleName name of the role
     * @param roleInfo information about the role
     * @return the role info passed in, or a newly createdone
     * @throws SmartFrogException SF problems
     * @throws RemoteException    network problems
     */
    protected ClusterRoleInfo processRole(String roleName, ClusterRoleInfo roleInfo)
            throws SmartFrogException, RemoteException {
        return roleInfo;
    }


    /**
     * Returns true iff the role is in range
     *
     * @param role     role to look for
     * @param quantity quantity to allocate
     * @return true if all roles are allowed, or
     */
    public boolean roleInRange(String role, int quantity) {
        ClusterRoleInfo info = lookupRoleInfo(role);
        if (info == null) {
            return false;
        }
        return info.isInRange(quantity);
    }

    /**
     * Look up a role in the map
     *
     * @param role role to look up
     * @return the info or null
     */
    protected ClusterRoleInfo lookupRoleInfo(String role) {
        return roleInfoMap.get(role);
    }

    /**
     * {@inheritDoc}
     *
     * @return a possibly empty list of role names
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized String[] listAvailableRoles() throws IOException, SmartFrogException {
        return roleInfoMap.keySet().toArray(new String[roleInfoMap.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @return a possibly empty list of roles
     * @throws IOException        IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public synchronized ClusterRoleInfo[] listClusterRoles() throws IOException, SmartFrogException {
        ClusterRoleInfo[] roleInfo = new ClusterRoleInfo[roleInfoMap.size()];
        int count = 0;
        for (String role : roleInfoMap.keySet()) {
            ClusterRoleInfo info = new ClusterRoleInfo(role);
            roleInfo[count++] = info;
        }
        return roleInfo;
    }

    /**
     * {@inheritDoc }
     *
     * @return true always
     */
    @Override
    public boolean isFarmerAvailable() throws IOException, SmartFrogException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() throws IOException, SmartFrogException {
        return "Cloud Farmer";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDiagnosticsText() throws IOException, SmartFrogException {
        return getDescription()
                + "\nclusterLimit:" + clusterLimit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startCluster() throws IOException, SmartFrogException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopCluster() throws IOException, SmartFrogException {

    }
}
