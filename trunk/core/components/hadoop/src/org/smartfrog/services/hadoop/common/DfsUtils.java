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
package org.smartfrog.services.hadoop.common;

import org.apache.hadoop.dfs.DistributedFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.components.HadoopConfiguration;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * helper class of DFS Utilities
 */

public class DfsUtils {
    public static final String ERROR_INVALID_FILESYSTEM_URI = "Invalid " + HadoopConfiguration.FS_DEFAULT_NAME + " URI: ";
    public static final String ERROR_FAILED_TO_INITIALISE_FILESYSTEM = "Failed to initialise filesystem";
    public static final String ERROR_FAILED_TO_DELETE_PATH = "Failed to delete path ";
    public static final String ERROR_FAILED_TO_CLOSE = "Failed to close ";


    private DfsUtils() {
    }

    public static void closeQuietly(DistributedFileSystem dfs) {
        if (dfs != null) {
            try {
                dfs.close();
            } catch (IOException e) {
                LogFactory.getLog(DfsUtils.class);
            }
        }
    }

    /**
     * Create a DFS Instance and initialise it from the configuration
     * @param conf configuration
     * @return a new DFS
     * @throws SmartFrogRuntimeException if anything goes wrong
     */
    public static DistributedFileSystem createFileSystem(ManagedConfiguration conf) throws SmartFrogRuntimeException {
        String filesystemURL = conf.get(HadoopConfiguration.FS_DEFAULT_NAME);
        URI uri = null;
        try {
            uri = new URI(filesystemURL);
        } catch (URISyntaxException e) {
            throw (SmartFrogRuntimeException) SmartFrogRuntimeException.forward(ERROR_INVALID_FILESYSTEM_URI + filesystemURL,
                    e);
        }
        DistributedFileSystem dfs = new DistributedFileSystem();
        try {
            dfs.initialize(uri, conf);
        } catch (IOException e) {
            throw (SmartFrogRuntimeException) SmartFrogRuntimeException
                    .forward(ERROR_FAILED_TO_INITIALISE_FILESYSTEM,e);

        }
        return dfs;
    }

    /**
     * Delete a DFS directory. Cleans up afterwards
     * @param conf DFS configuration
     * @param dir directory to delete
     * @param recursive recurseive delete?
     * @throws SmartFrogRuntimeException if anything goes wrong
     */
    public static void deleteDFSDirectory(ManagedConfiguration conf, String dir, boolean recursive) throws SmartFrogRuntimeException {
        DistributedFileSystem dfs = createFileSystem(conf);
        URI dfsURI = dfs.getUri();
        Path path = new Path(dir);
        try {
            dfs.delete(path, recursive);
        } catch (IOException e) {
            closeQuietly(dfs);
            throw (SmartFrogRuntimeException) SmartFrogRuntimeException
                    .forward(ERROR_FAILED_TO_DELETE_PATH + path + " on " + dfsURI,
                            e);
        }
        try {
            dfs.close();
        } catch (IOException e) {
            throw (SmartFrogRuntimeException) SmartFrogRuntimeException
                    .forward(ERROR_FAILED_TO_CLOSE + dfsURI,
                            e);
        }
    }

    /**
     * Get information about a path.
     * @param fileSystem filesystem to work with
     * @param path path to use
     * @return the status or null for no such path
     * @throws IOException for communications problems
     */
    public static FileStatus stat(DistributedFileSystem fileSystem, Path path) throws IOException {
        try {
            if (fileSystem.exists(path)) {
                return fileSystem.getFileStatus(path);
            } else {
                return null;
            }
        } catch (FileNotFoundException ignored) {
            return null;
        }
    }
}
