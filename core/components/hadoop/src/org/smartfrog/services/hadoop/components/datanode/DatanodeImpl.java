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
package org.smartfrog.services.hadoop.components.datanode;

import org.apache.hadoop.hdfs.server.datanode.ExtDataNode;
import org.apache.hadoop.util.Service;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.FileSystemNodeImpl;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.core.SFHadoopException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created 06-May-2008 16:31:49
 */

public class DatanodeImpl extends FileSystemNodeImpl implements HadoopCluster {
    private static final String NAME = "DataNode";
    public static final String ERROR_FAILED_TO_START_DATANODE = "Failed to create "+NAME;

    public DatanodeImpl() throws RemoteException {
    }

    @Override
    protected String getName() {
        return NAME;
    }

    /**
     * Create the datanode
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //get the list of data directories
        Vector<String> dataDirs = createDirectoryListAttribute(DATA_DIRECTORIES, DFS_DATA_DIR);
        //convert them to a list of files
        Vector<File> dataDirFiles = FileSystem.convertToFiles(dataDirs);
        //get the rest of the configuration
        ManagedConfiguration conf = createConfiguration();
        //create a data node from the configuration
        ExtDataNode dataNode;
        try {
            dataNode = new ExtDataNode(this, conf, dataDirFiles);
        } catch (IOException e) {
            throw SFHadoopException.forward(ERROR_FAILED_TO_START_DATANODE,
                    e,
                    this,
                    conf);
        }
        deployService(dataNode,conf);
    }


}
