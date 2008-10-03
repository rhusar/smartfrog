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

import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.server.namenode.ExtDfsUtils;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.components.cluster.FileSystemNodeImpl;
import org.smartfrog.services.hadoop.components.cluster.HadoopComponentImpl;
import org.smartfrog.services.hadoop.conf.ConfigurationAttributes;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Format a file system
 */
public class DfsFormatFileSystemImpl extends DfsOperationImpl implements DfsPathOperation {


    public DfsFormatFileSystemImpl() throws RemoteException {
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
     * Format the name node
     *
     * @param fileSystem the filesystem; this is closed afterwards
     * @param conf the configuration driving this operation
     * @throws Exception on any failure
     */
    protected void performDfsOperation(DistributedFileSystem fileSystem, ManagedConfiguration conf) throws Exception {
        Vector<String> files = HadoopComponentImpl
                .createDirectoryListAttribute(this, FileSystemNodeImpl.NAME_DIRECTORIES,
                        ConfigurationAttributes.DFS_NAME_DIR);
        Vector<File> nameDirs = FileSystem.convertToFiles(files);
        ExtDfsUtils.formatNameNode(nameDirs, conf);
    }
}