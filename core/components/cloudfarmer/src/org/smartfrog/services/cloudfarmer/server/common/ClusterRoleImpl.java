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
package org.smartfrog.services.cloudfarmer.server.common;

import org.smartfrog.services.cloudfarmer.api.ClusterRoleInfo;
import org.smartfrog.services.cloudfarmer.api.Range;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;


/**
 * Prim that defines a cluster role
 */

public class ClusterRoleImpl extends PrimImpl implements ClusterRole {

    private ClusterRoleInfo info;

    public ClusterRoleImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        info = resolveRoleInfo(this);
        info.setName(sfCompleteName.lastElement().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClusterRoleInfo buildClusterRoleInfo() throws RemoteException {
        return info.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Cluster Role " + info;
    }

    /**
     * This will build the role information
     *
     * @return role info -without any name
     * @throws RemoteException              network trouble
     * @throws SmartFrogResolutionException resolution problems
     */
    public ClusterRoleInfo resolveRoleInfo(String name) throws RemoteException, SmartFrogResolutionException {
        final ClusterRoleInfo roleInfo = resolveRoleInfo(this);
        roleInfo.setName(name);
        return roleInfo;
    }


    /**
     * This will build the role information
     *
     * @param target info target
     * @return role info -without any name
     * @throws RemoteException              network trouble
     * @throws SmartFrogResolutionException resolution problems
     */
    public static ClusterRoleInfo resolveRoleInfo(Prim target) throws RemoteException, SmartFrogResolutionException {
        ClusterRoleInfo role = new ClusterRoleInfo();
        role.setDescription(target.sfResolve(ATTR_DESCRIPTION, "", true));
        role.setLongDescription(target.sfResolve(ATTR_LONG_DESCRIPTION, "", true));
        role.setRoleSize(resolveRange(target, ATTR_MIN, ATTR_MAX));
        role.setRecommendedSize(resolveRange(target, ATTR_RECOMMENDED_MIN, ATTR_RECOMMENDED_MAX));
        return role;
    }

    /**
     * resolve a range pair
     *
     * @return the new range
     * @throws RemoteException              network trouble
     * @throws SmartFrogResolutionException resolution problems
     */
    public static Range resolveRange(Prim target, String minName, String maxName)
            throws RemoteException, SmartFrogResolutionException {
        int min = target.sfResolve(minName, 0, true);
        int max = target.sfResolve(maxName, 0, true);
        Range range = new Range(min, max);
        return range;
    }
}
