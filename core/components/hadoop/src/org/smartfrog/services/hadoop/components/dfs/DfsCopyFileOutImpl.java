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
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Component to copy a file into DFS Created 17-Jun-2008 15:06:23
 */

public class DfsCopyFileOutImpl extends DfsOperationImpl implements DfsCopyOperation {

    public DfsCopyFileOutImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
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
        Path source = resolveDfsPath(ATTR_SOURCE);
        File dest = FileSystem.lookupAbsoluteFile(this, ATTR_DEST, null, null, true, null);
        Path destPath = new Path(dest.toURI().toString());
        boolean overwrite = sfResolve(ATTR_OVERWRITE, false, true);
        if(!overwrite && dest.exists()) {
            //bail out if the dest file exists and overwrite==false
            return;
        }
        try {
            fileSystem.copyToLocalFile(source, destPath);
        } catch (IOException e) {
            throw new SmartFrogRuntimeException(
                    DfsUtils.FAILED_TO_COPY + source + " to " + dest, e, this);
        }
    }
}