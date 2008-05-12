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
package org.smartfrog.services.hadoop.components.namenode;

import org.smartfrog.services.hadoop.components.cluster.FileSystemNodeImpl;
import org.smartfrog.services.hadoop.components.cluster.FileSystemNode;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.apache.hadoop.dfs.ExtNameNode;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created 06-May-2008 16:31:49
 */

public class NamenodeImpl extends FileSystemNodeImpl implements
        FileSystemNode {

    private ExtNameNode namenode;

    public NamenodeImpl() throws RemoteException {
    }

    /**
     * Create the datanode
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        createDirectoryListAttribute(DATA_DIRECTORIES, DFS_DATA_DIR);
        createDirectoryListAttribute(NAME_DIRECTORIES, DFS_NAME_DIR);
        File logDir = FileSystem.lookupAbsoluteFile(this,
                ATTR_LOG_DIR, null, null, true, null);
        logDir.mkdirs();
        sfReplaceAttribute(HADOOP_LOG_DIR, logDir.getAbsolutePath());
        ManagedConfiguration conf = createConfiguration();
        try {
            namenode = ExtNameNode.createNameNode(this, conf);

        } catch (IOException e) {
            throw new SmartFrogException("Failed to start namenode: "
                    + e.getMessage() + "\n" + conf.dumpQuietly(), e);
        } catch (IllegalArgumentException e) {
            throw new SmartFrogException("Failed to start namenode: "
                    + e.getMessage() + "\n" + conf.dumpQuietly(), e);
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (namenode != null) {
            namenode.stop();
            namenode = null;
        }
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     *
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException for consistency with the {@link Liveness}
     * interface
     */
    public void sfPing(Object source)
            throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        synchronized (this) {
            if (namenode != null) {
                namenode.ping();
            }
        }
    }
}