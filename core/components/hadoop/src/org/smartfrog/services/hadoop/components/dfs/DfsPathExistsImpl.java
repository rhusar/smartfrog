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
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.components.cluster.CheckableCondition;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;

/**
 * Created 27-May-2008 15:42:48
 */

public class DfsPathExistsImpl extends DfsPathOperationImpl
        implements CheckableCondition, DfsPathExists {

    private boolean checkOnLiveness;
    private boolean verbose;
    private boolean canBeFile = false;
    private boolean canBeDir = false;
    private long minFileSize = 0;
    private long maxFileSize = -1;
    private long minTotalFileSize = 0;
    private long maxTotalFileSize = -1;
    private int minFileCount = 0;
    private int maxFileCount = -1;
    private int minReplication = 0;
    private int maxReplication = -1;

    private FileSystem dfs;


    public DfsPathExistsImpl() throws RemoteException {
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
        boolean checkOnStartup = sfResolve(ATTR_CHECK_ON_STARTUP, true, false);
        //bind to the fs
        dfs = createFileSystem();

        canBeFile = sfResolve(ATTR_CAN_BE_FILE, true, true);
        canBeDir = sfResolve(ATTR_CAN_BE_DIR, true, true);
        minFileSize = sfResolve(ATTR_MIN_SIZE, minFileSize, true);
        maxFileSize = sfResolve(ATTR_MAX_SIZE, maxFileSize, true);
        minReplication = sfResolve(ATTR_MIN_REPLICATION_FACTOR, minReplication, true);
        maxReplication = sfResolve(ATTR_MAX_REPLICATION_FACTOR, maxReplication, true);
        minFileCount = sfResolve(ATTR_MIN_FILE_COUNT, minFileCount, true);
        maxFileCount = sfResolve(ATTR_MAX_FILE_COUNT, maxFileCount, true);
        minTotalFileSize = sfResolve(ATTR_MIN_TOTAL_FILE_SIZE, minTotalFileSize, true);
        maxTotalFileSize = sfResolve(ATTR_MAX_TOTAL_FILE_SIZE, maxTotalFileSize, true);
        verbose = sfResolve(ATTR_VERBOSE, verbose, true);

        //now maybe check
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
    @Override
    protected synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            DfsUtils.closeDfs(dfs);
        } catch (SmartFrogRuntimeException e) {
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
    @Override
    public void sfPing(Object source) throws SmartFrogLivenessException,
            RemoteException {
        super.sfPing(source);
        if (checkOnLiveness) {
            checkPathExists();
        }
    }

    /**
     * check that a path exists
     *
     * @throws SmartFrogLivenessException if it does not, or it is the wrong type/size
     */
    private void checkPathExists() throws SmartFrogLivenessException {
        String filename = getPathName() + " in " + dfs.toString();
        try {
            if (!doesPathExist()) {
                throw new SmartFrogLivenessException("Missing path " + filename);
            }
            FileStatus status = dfs.getFileStatus(getPath());
            if (verbose) {
                sfLog().info("Path " + getPath() + " size " + status.getLen()
                        + " last modified:" + status.getModificationTime());
            }
            if (status.isDir()) {
                //it is a directory. Run the directory checks

                FileStatus[] statuses = dfs.listStatus(getPath());
                if (statuses == null) {
                    throw new SmartFrogLivenessException("Unable to list the status of " + filename);
                }
                int fileCount = statuses.length;
                StringBuilder filenames = new StringBuilder();

                long totalFileSize = 0;
                for (FileStatus fstat : statuses) {
                    totalFileSize += fstat.getLen();
                    filenames.append(fstat.getPath() + "\t").append('\t').append(fstat.getBlockSize()).append("\n");
                    filenames.append('\n');
                    if (verbose) {
                        sfLog().info(fstat.getPath()+"\t"+ fstat.getBlockSize()+"\n");
                    }
                }

                if (!canBeDir) {
                    throw new SmartFrogLivenessException("Expected a file, got a directory: " + filename
                    +" containing "+fileCount + " file(s):\n"
                    + filenames);
                }
                if (fileCount < minFileCount) {
                    throw new SmartFrogLivenessException("Not enough files under " + filename
                            + " required " + minFileCount + " found " + fileCount
                            + " :\n"
                            + filenames);
                }
                if (maxFileCount >= 0 && fileCount > maxFileCount) {
                    throw new SmartFrogLivenessException("Too many files under " + filename
                            + " maximum " + maxFileCount + " found " + fileCount
                            + " :\n"
                            + filenames);
                }
                if (totalFileSize < minTotalFileSize) {
                    throw new SmartFrogLivenessException("not enough file content " + filename
                            + " required " + minTotalFileSize + " found " + totalFileSize
                            + " :\n"
                            + filenames);
                }
                if (maxTotalFileSize >= 0 && totalFileSize > maxTotalFileSize) {
                    throw new SmartFrogLivenessException("too much enough file content " + filename
                            + " maximum " + minTotalFileSize + " found " + totalFileSize
                            + " :\n"
                            + filenames);
                }
            } else {
                if (!canBeFile) {
                    throw new SmartFrogLivenessException("Not allowed to be a file: " + filename);
                }
                long size = status.getLen();
                if (size < minFileSize) {
                    throw new SmartFrogLivenessException("File " + filename + " is too small at " + size
                            + " bytes for the minimum size " + minFileSize);
                }
                if (maxFileSize >= 0 && size > maxFileSize) {
                    throw new SmartFrogLivenessException("File " + filename + " is too big at " + size
                            + " bytes for the maximum size " + maxFileSize);
                }
                short replication = status.getReplication();
                if (replication < minReplication) {
                    throw new SmartFrogLivenessException("File  " + filename + " has a replication factor of"
                            + replication
                            + " which is less than the minimum value of " + minReplication);
                }
                if (maxReplication >= 0 && replication > maxReplication) {
                    throw new SmartFrogLivenessException("File  " + filename + " has a replication factor of"
                            + replication
                            + " which is less than the maximum value of " + maxReplication);
                }
            }
        } catch (IOException e) {
            throw new SmartFrogLivenessException("Missing path " + filename, e);
        }
    }
}
