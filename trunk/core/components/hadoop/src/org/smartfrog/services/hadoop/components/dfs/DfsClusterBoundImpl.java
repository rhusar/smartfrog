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
package org.smartfrog.services.hadoop.components.dfs;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.conf.ClusterBound;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * This is a base class for components that work with a cluster.
 */

public class DfsClusterBoundImpl extends PrimImpl {

    public DfsClusterBoundImpl() throws RemoteException {
    }

    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        sfResolve(ClusterBound.ATTR_CLUSTER, (Prim) null, true);
    }

    /**
     * Resolve an attribute to a DFS path
     *
     * @param attribute name of the attribute
     * @return the path
     * @throws SmartFrogException          resolution problems
     * @throws SmartFrogLifecycleException for a failure to create the path
     * @throws RemoteException             network problems
     */
    protected Path resolveDfsPath(String attribute) throws SmartFrogException, RemoteException {
        String pathName = sfResolve(attribute, "", true);
        try {
            return new Path(pathName);
        } catch (IllegalArgumentException e) {
            throw new SmartFrogLifecycleException("Failed to create the path defined by attribute " + attribute
                    + " with value " + pathName
                    + " : " + e,
                    e,
                    this);
        }
    }

    /**
     * Create a managed configuration
     *
     * @return a new SF-managed configuration
     * @throws SmartFrogException for any problem creating the FS.
     * @throws RemoteException    network problems
     */
    public ManagedConfiguration createConfiguration() throws SmartFrogException, RemoteException {
        return ManagedConfiguration.createConfiguration(this, true, true, false);
    }

    /**
     * Create a filesystem from our configuration
     *
     * @return a new file system
     * @throws SmartFrogException for any problem creating the FS.
     * @throws RemoteException    network problems
     */
    protected FileSystem createFileSystem()
            throws SmartFrogException, RemoteException {
        ManagedConfiguration conf = createConfiguration();
        FileSystem fileSystem = DfsUtils.createFileSystem(conf);
        return fileSystem;
    }
}
