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
    private int minFileCount;
    private int maxFileCount;
    public static final String ATTR_MIN_FILE_COUNT = "minFileCount";
    public static final String ATTR_MAX_FILE_COUNT = "maxFileCount";

    public DfsListDirImpl() throws RemoteException {
    }


    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        minFileCount = sfResolve(ATTR_MIN_FILE_COUNT, 0, true);
        maxFileCount = sfResolve(ATTR_MAX_FILE_COUNT, 0, true);
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
        try {
            long size = 0;
            FileStatus[] stats = fileSystem.listStatus(getPath());
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
            sfLog().info(builder.toString());
            int count = stats.length;
            sfLog().info("Files: " + count + "  total size=" + size);
            if (count < minFileCount) {
                throw new SmartFrogLivenessException(
                        "File count " + count + " is below the minFileCount value of " + minFileCount
                                + "\n" + builder.toString());
            }
            if (maxFileCount > -1 && count > maxFileCount) {
                throw new SmartFrogLivenessException(
                        "File count " + count + " is above the maxFileCount value of " + minFileCount
                                + "\n" + builder.toString());
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
