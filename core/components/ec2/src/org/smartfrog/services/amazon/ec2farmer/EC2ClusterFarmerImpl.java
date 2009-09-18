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
package org.smartfrog.services.amazon.ec2farmer;

import org.smartfrog.services.amazon.ec2.EC2ComponentImpl;
import org.smartfrog.services.farmer.AbstractClusterFarmer;
import org.smartfrog.services.farmer.ClusterNode;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.Reference;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements an EC2 cluster farmer. It is still unimplemented
 */
public class EC2ClusterFarmerImpl extends EC2ComponentImpl implements EC2ClusterFarmer {


    public EC2ClusterFarmerImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        AbstractClusterFarmer.validateClusterRange(min, max);
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String id) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(String[] nodes) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public void delete(ClusterNode[] nodes) throws IOException, SmartFrogException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public int deleteAllInRole(String role) throws IOException, SmartFrogException {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list() throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookup(String id) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public int deleteAll() throws IOException, SmartFrogException {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return a possibly empty list of role names
     * @throws IOException IO/network problems
     * @throws SmartFrogException other problems
     */
    @Override
    public String[] listAvailableRoles() throws IOException, SmartFrogException {
        //get the component containing the roles
        ComponentDescription roles = sfResolve(ATTR_ROLES, (ComponentDescription) null, true);
        //and build a list of all that is a CD itself
        List<String> rolelist = new ArrayList<String>();
        Iterator attrs = roles.sfAttributes();
        while (attrs.hasNext()) {
            Object key = attrs.next();
            Object value = roles.sfResolve(new Reference(key), true);
            if (value instanceof ComponentDescription) {
                rolelist.add(key.toString());
            }
        }
        return rolelist.toArray(new String[rolelist.size()]);
    }
}
