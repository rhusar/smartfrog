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

import org.apache.hadoop.dfs.DistributedFileSystem;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * delete a directory
 */
public class DfsDeleteDirImpl extends DfsPathOperationImpl {

    public static final String ERROR_CANNOT_DELETE_DIRECTORY = "Cannot delete directory";
    public static final String ERROR_NOT_PRESENT = " as it is not present";
    public static final String ERROR_NOT_DIRECTORY = " as it is not a directory";

    public DfsDeleteDirImpl() throws RemoteException {
    }


    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        startWorkerThread();
    }

    /**
     * do the work
     *
     * @param fileSystem the filesystem; this is closed afterwards
     * @param conf       the configuration driving this operation
     * @throws Exception on any failure
     */
    protected void performDfsOperation(DistributedFileSystem fileSystem, ManagedConfiguration conf) throws Exception {
        Path path = getPath();
        boolean recursive = sfResolve(ATTR_RECURSIVE, true, true);
        FileStatus status = DfsUtils.stat(fileSystem, path);
        if (status == null) {
            if (isIdempotent()) {
                return;
            }
            throw new SmartFrogDeploymentException(ERROR_CANNOT_DELETE_DIRECTORY + path.toString() + ERROR_NOT_PRESENT);
        }
        if (status.isDir()) {
            fileSystem.delete(path, recursive);
        } else {
            throw new SmartFrogDeploymentException(
                    ERROR_CANNOT_DELETE_DIRECTORY + path.toString() + ERROR_NOT_DIRECTORY);
        }

    }

}
