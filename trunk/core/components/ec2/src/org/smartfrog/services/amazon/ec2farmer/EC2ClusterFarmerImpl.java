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

import org.smartfrog.services.farmer.AbstractClusterFarmer;
import org.smartfrog.services.farmer.ClusterNode;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.io.IOException;

/**
 * Created 16-Sep-2009 16:06:27
 */

public class EC2ClusterFarmerImpl extends AbstractClusterFarmer implements EC2ClusterFarmer {

    public EC2ClusterFarmerImpl() throws RemoteException {
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public ClusterNode[] create(String role, int min, int max) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public void delete(String id) throws IOException, SmartFrogException {

    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public void delete(String[] nodes) throws IOException, SmartFrogException {

    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public void delete(ClusterNode[] nodes) throws IOException, SmartFrogException {

    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public int deleteAllInRole(String role) throws IOException, SmartFrogException {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public int deleteAll() throws IOException, SmartFrogException {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public ClusterNode[] list() throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }


    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public ClusterNode[] list(String role) throws IOException, SmartFrogException {
        return new ClusterNode[0];
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public ClusterNode lookup(String id) throws IOException, SmartFrogException {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException        Network trouble
     * @throws SmartFrogException SmartFrog problems
     */
    @Override
    public ClusterNode lookupByHostname(String hostname) throws IOException, SmartFrogException {
        return null;
    }
}
