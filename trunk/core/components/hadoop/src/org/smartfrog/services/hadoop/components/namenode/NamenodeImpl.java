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

import org.smartfrog.services.hadoop.components.HadoopCluster;
import org.smartfrog.services.hadoop.components.cluster.FileSystemNodeImpl;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.apache.hadoop.dfs.ExtNameNode;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created 06-May-2008 16:31:49
 */

public class NamenodeImpl extends FileSystemNodeImpl implements HadoopCluster {
    private ExtNameNode namenode;
    public static final String ATTR_DATA_DIRECTORIES = "dataDirectories";
    private static final String DFS_DATA_DIR = "dfs.data.dir";
    /** {@value} */
    public static final String ATTR_CHECK_RUNNING = "checkRunning";
    private static final Reference DATA_DIRECTORIES = new Reference(
            ATTR_DATA_DIRECTORIES);

    public NamenodeImpl() throws RemoteException {
    }

    /**
     * Create the datanode
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Vector<String> dataDirs;
        ManagedConfiguration conf = new ManagedConfiguration(this);
        dataDirs= FileSystem.resolveFileList(this,DATA_DIRECTORIES,null,true,null);
        StringBuilder path = new StringBuilder();
        for (String dir : dataDirs) {
            File directory = new File(dir);
            directory.mkdirs();
            if (path.length() > 0) {
                path.append(',');
            }
            path.append(directory.getAbsolutePath());
        }
        conf.set(DFS_DATA_DIR, path.toString());
        try {
            namenode = ExtNameNode.createNameNode(conf);

        } catch (IOException e) {
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