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

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Date;

/**
 * Created 27-May-2008 14:29:51
 */

public class DfsListDirImpl extends DfsPathOperationImpl implements DfsPathOperation {

    public DfsListDirImpl() throws RemoteException {
    }


    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
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
    @Override
    protected void performDfsOperation(FileSystem fileSystem, ManagedConfiguration conf) throws Exception {
        Path path = getPath();
        if (path == null) {
            throw new SmartFrogLivenessException("No path for the DfsListDir operation", this);
        }
        int minFileCount = sfResolve(ATTR_MIN_FILE_COUNT, 0, true);
        int maxFileCount = sfResolve(ATTR_MAX_FILE_COUNT, 0, true);
        long minTotalFileSize = sfResolve(ATTR_MIN_TOTAL_FILE_SIZE, 0L, true);
        long maxTotalFileSize = sfResolve(ATTR_MAX_TOTAL_FILE_SIZE, 0L, true);
        try {
            long size = 0;
            FileStatus[] stats = fileSystem.listStatus(path);
            if (stats == null) {
                throw new SmartFrogLivenessException("Path not found in the remote filesystem: " + path, this);
            }
            StringBuilder builder = new StringBuilder();
            for (FileStatus file : stats) {
                size += file.getLen();
                builder.append(file.getPath().getName());
                builder.append("\n  size=").append(file.getLen());
                builder.append("\n  replication=").append(file.getReplication());
                builder.append("\n  last modified=").append(new Date(file.getModificationTime()).toString());
                builder.append("\n  owner=").append(file.getOwner());
                builder.append("\n  group=").append(file.getGroup());
                builder.append("\n  permissions=").append(file.getPermission()).append('\n');
            }
            String listing = builder.toString();
            sfLog().info(listing);
            int count = stats.length;
            sfLog().info("Files: " + count + "  total size=" + size);
            if (count < minFileCount) {
                throw new SmartFrogLivenessException(
                        "File count " + count + " is below the minFileCount value of " + minFileCount
                                + "\n" + listing,
                        this);
            }
            if (maxFileCount > -1 && count > maxFileCount) {
                throw new SmartFrogLivenessException(
                        "File count " + count + " is above the maxFileCount value of " + minFileCount
                                + "\n" + listing,
                        this);
            }
            if (size < minTotalFileSize) {
                throw new SmartFrogLivenessException(
                        "File size " + size + " is below the minTotalFileSize value of " + minTotalFileSize
                                + "\n" + listing,
                        this);
            }
            if (maxFileCount > -1 && size > maxFileCount) {
                throw new SmartFrogLivenessException(
                        "File size " + size + " is above the maxTotalFileSize value of " + maxTotalFileSize
                                + "\n" + listing,
                        this);
            }

        } catch (IOException e) {
            if (isIdempotent()) {
                sfLog().info("Failed to stat " + path, e);
            } else {
                throw e;
            }
        }
    }

}
