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
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.smartfrog.services.filesystem.FileExists;
import org.smartfrog.services.hadoop.components.cluster.CheckableCondition;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.rmi.RemoteException;
import java.net.URI;

/**
 * Created 27-May-2008 15:42:48
 */

public class DfsPathExistsImpl extends DfsPathOperationImpl
        implements CheckableCondition, DfsPathOperation, FileExists {

    private boolean canBeFile = false;
    private boolean canBeDir = false;
    private long minFileSize = 0;
    private long maxFileSize = -1;

    private boolean checkOnLiveness;
    private boolean verbose;
    public static final String ATTR_VERBOSE="verbose";

    private DistributedFileSystem dfs;


    public DfsPathExistsImpl() throws RemoteException {
    }

    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        boolean checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, true, false);
        dfs = createFileSystem();

        canBeFile = sfResolve(FileExists.ATTR_CAN_BE_FILE, true, true);
        canBeDir = sfResolve(FileExists.ATTR_CAN_BE_DIR, true, true);
        minFileSize = sfResolve(FileExists.ATTR_MIN_SIZE, minFileSize, true);
        maxFileSize = sfResolve(FileExists.ATTR_MAX_SIZE, maxFileSize, true);
        verbose = sfResolve(ATTR_VERBOSE, verbose, true);
        if (checkOnStartup) {
            checkPathExists();
        }
        //Workflow integration
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "PathExists",
                null,
                null);
        checkOnLiveness = sfResolve(ATTR_CHECK_ON_LIVENESS,
                true,
                false);
    }

    /**
     * get the absolute path of this file
     *
     * @return String
     * @throws RemoteException In case of network/rmi error
     */
    public String getAbsolutePath() throws RemoteException {
        return getPath().toString();
    }


    /**
     * get the URI of this file
     *
     * @return URI
     * @throws RemoteException In case of network/rmi error
     */
    public URI getURI() throws RemoteException {
        return getPath().toUri();
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior. Deregisters component from local process
     * compound (if ever registered)
     *
     * @param status termination status
     */
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            if (dfs != null) {
                dfs.close();
            }
        } catch (IOException e) {
            sfLog().info("When closing the file system " + e, e);
        } finally {
            dfs = null;
        }
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        try {
            checkPathExists();
            return true;
        } catch (SmartFrogLivenessException ignored) {
            return false;
        }
    }

    protected boolean doesPathExist() throws IOException {
        return dfs.exists(getPath());
    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            In case of network/rmi error
     */
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        super.sfPing(source);
        if (checkOnLiveness) {
            checkPathExists();
        }
    }

    private void checkPathExists() throws SmartFrogLivenessException {
        String filename = getPathName() + " in " + dfs.toString();
        try {
            if (!doesPathExist()) {
                throw new SmartFrogLivenessException("Missing path " + filename);
            }
            FileStatus status = dfs.getFileStatus(getPath());
            if(verbose) {
                sfLog().info("Path "+getPath() +" size "+status.getLen()
                        +" last modified:"+status.getModificationTime());
            }
            if (status.isDir()) {
                if (!canBeDir) {
                    throw new SmartFrogLivenessException("Not allowed to be a directory: " + filename);
                }
            } else {
                if (!canBeFile) {
                    throw new SmartFrogLivenessException("Not allowed to be a file: " + filename);
                }
                long size = status.getLen();
                if (size < minFileSize) {
                    throw new SmartFrogLivenessException(" File " + filename + " is too small at " + size
                            + " bytes for the minimum size " + minFileSize);
                }
                if (maxFileSize >= 0 && size > maxFileSize) {
                    throw new SmartFrogLivenessException(" File " + filename + " is too big at " + size
                            + " bytes for the maximum size " + minFileSize);
                }

            }
        } catch (IOException e) {
            throw new SmartFrogLivenessException("Missing path " + filename, e);
        }
    }
}
