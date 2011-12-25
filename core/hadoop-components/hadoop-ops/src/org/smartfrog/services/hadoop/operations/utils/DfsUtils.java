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
package org.smartfrog.services.hadoop.operations.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.ReflectionUtils;
import org.smartfrog.services.hadoop.operations.conf.HadoopConfiguration;
import org.smartfrog.services.hadoop.operations.conf.ManagedConfiguration;
import org.smartfrog.services.hadoop.operations.exceptions.SFHadoopException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.LogFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * helper class of DFS Utilities
 */

public class DfsUtils {
    public static final String ERROR_INVALID_FILESYSTEM_URI = "Invalid " + HadoopConfiguration.FS_DEFAULT_NAME
            + " URI: ";
    public static final String ERROR_FAILED_TO_INITIALISE_FILESYSTEM = "Failed to initialise filesystem: ";
    public static final String ERROR_FAILED_TO_DELETE_PATH = "Failed to delete path ";
    public static final String ERROR_FAILED_TO_CLOSE = "Failed to close ";
    public static final String ERROR_MKDIR_FAILED = "Unable to create the destination directories for ";
    public static final String ERROR_MISSING_SOURCE_FILE = "Missing source file : ";
    public static final String ERROR_COPY_FAILED = "Unable to copy ";
    public static final String ERROR_NO_DIRECTORY_COPY = "Directory copy is not supported for ";
    public static final String ERROR_NO_STAT = "Unable to stat ";
    public static final String ERROR_CANNOT_COPY = "Cannot copy ";
    /**
     * Error string {@value}
     */
    public static final String FAILED_TO_COPY = "Failed to copy ";

    private DfsUtils() {
    }

    /**
     * Close the DFS quietly
     *
     * @param dfs the dfs reference; can be null
     */
    public static void closeQuietly(FileSystem dfs) {
        if (dfs != null) {
            try {
                dfs.close();
            } catch (IOException e) {
                LogFactory.getLog(DfsUtils.class).info("Failed to close DFS", e);
            }
        }
    }

    /**
     * This is the non-quiet close operation
     *
     * @param dfs filesystem
     * @throws SmartFrogRuntimeException if the filesystem does not close
     */
    public static void closeDfs(FileSystem dfs) throws SmartFrogRuntimeException {
        try {
            dfs.close();
        } catch (IOException e) {
            if (isFilesystemClosedException(e)) {
                LogFactory.getLog(DfsUtils.class).info("DFS has already closed", e);
            } else {
                throw (SmartFrogRuntimeException) SmartFrogRuntimeException
                        .forward(ERROR_FAILED_TO_CLOSE + dfs.getUri(),
                                e);
            }
        }
    }

    private static boolean isFilesystemClosedException(IOException e) {
        return "Filesystem closed".equals(e.getMessage());
    }

    /**
     * Create a DFS Instance and initialise it from the configuration
     *
     * @param conf configuration
     * @return a new DFS
     * @throws SFHadoopException if things go wrong
     */
    public static FileSystem createFileSystem(ManagedConfiguration conf)
            throws SFHadoopException {
        String filesystemURL = conf.get(HadoopConfiguration.FS_DEFAULT_NAME);
        if (filesystemURL == null) {
            SFHadoopException hadoopException = new SFHadoopException(
                    "No filesystem URL " + HadoopConfiguration.FS_DEFAULT_NAME);
            hadoopException.addConfiguration(conf);
            throw hadoopException;
        }
        return createFileSystem(filesystemURL, conf);
    }

    /**
     * Create a DFS client instance from a given URL; initialize it from the configuration.
     * <p/>
     * This method uses {@link FileSystem#newInstance(URI, Configuration)} to create a new
     * instance, rather than return a cached one. This is to eliminate race conditions
     * across threads of the kind that surfaced in
     * <a href="http://jira.smartfrog.org/jira/browse/SFOS-1208">SFOS-1208</a>.
     * <p/>
     * Accordingly, filesystems should be closed unless you want to leak them
     *
     * @param filesystemURL the URL of the filesystem
     * @param conf          configuration used when constructing the FS
     * @return a filesystem client
     * @throws SFHadoopException if things go wrong
     */
    public static FileSystem createFileSystem(String filesystemURL, ManagedConfiguration conf)
            throws SFHadoopException {
        URI uri;
        try {
            uri = new URI(filesystemURL);
        } catch (URISyntaxException e) {
            SFHadoopException hadoopException = SFHadoopException
                    .forward(ERROR_INVALID_FILESYSTEM_URI + filesystemURL,
                            e);
            hadoopException.addConfiguration(conf);
            throw hadoopException;
        }
        try {
            return createInstance(uri, conf);
        } catch (Throwable e) {
            SFHadoopException hadoopException = SFHadoopException
                    .forward(ERROR_FAILED_TO_INITIALISE_FILESYSTEM + '"' + filesystemURL + '"',
                            e);
            hadoopException.addConfiguration(conf);
            throw hadoopException;
        }
    }


    public static FileSystem createInstance(URI uri, Configuration conf) throws IOException {
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = FileSystem.getDefaultUri(conf).getScheme();
        }
        conf.setBoolean("fs." + scheme + ".impl.disable.cache", true);
        FileSystem dfs = FileSystem.get(uri, conf);
        return dfs;
    }

/*
    0.21+ only

    public static FileSystem createInstance(URI uri, Configuration conf) throws IOException {
        FileSystem dfs = FileSystem.newInstance(uri, conf);
        dfs.initialize(uri, conf);
        return dfs;
    }*/

    /**
     * Delete a DFS directory. Cleans up afterwards
     *
     * @param conf      DFS configuration
     * @param dir       directory to delete
     * @param recursive recurseive delete?
     * @throws SmartFrogRuntimeException failure to delete
     * @throws SFHadoopException         filesystem binding failures
     */
    public static void deleteDFSDirectory(ManagedConfiguration conf, String dir, boolean recursive)
            throws SmartFrogRuntimeException, SFHadoopException {
        FileSystem dfs = createFileSystem(conf);
        try {
            deleteDFSDirectory(dfs, dir, recursive);
        } finally {
            closeDfs(dfs);
        }
    }

    /**
     * Delete a DFS directory. Cleans up afterwards
     *
     * @param dfs       DFS configuration
     * @param dir       directory to delete
     * @param recursive recurseive delete?
     * @throws SmartFrogRuntimeException if anything goes wrong
     */
    public static void deleteDFSDirectory(FileSystem dfs, String dir, boolean recursive)
            throws SmartFrogRuntimeException {
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
    }

    /**
     * Get information about a path.
     *
     * @param fileSystem filesystem to work with
     * @param path       path to use
     * @return the status or null for no such path
     * @throws IOException for communications problems
     */
    public static FileStatus stat(FileSystem fileSystem, Path path) throws IOException {
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

    /**
     * Check the dest is not under the source Credit: Apache Hadoop team;
     *
     * @param srcFS source filesystem
     * @param src   source path
     * @param dstFS dest filesystem
     * @param dst   dest path
     * @throws SmartFrogRuntimeException if there is a match.
     */

    public static void assertNotDependent(FileSystem srcFS,
                                          Path src,
                                          FileSystem dstFS,
                                          Path dst)
            throws SmartFrogRuntimeException {
        if (srcFS.getUri().equals(dstFS.getUri())) {
            String srcq = src.makeQualified(srcFS).toString() + Path.SEPARATOR;
            String dstq = dst.makeQualified(dstFS).toString() + Path.SEPARATOR;
            if (dstq.startsWith(srcq)) {
                if (srcq.length() == dstq.length()) {
                    throw new SmartFrogRuntimeException(ERROR_CANNOT_COPY + src + " to itself.");
                } else {
                    throw new SmartFrogRuntimeException(ERROR_CANNOT_COPY + src + " to its subdirectory " +
                            dst);
                }
            }
        }
    }

    /**
     * Create the parent directories of a given path
     *
     * @param fileSystem filesystem to work with
     * @param dest       file
     * @throws SmartFrogRuntimeException failure to create the directories
     */
    public static void mkParentDirs(FileSystem fileSystem, Path dest) throws SmartFrogRuntimeException {
        mkdirs(fileSystem, dest.getParent());
    }

    /**
     * Create the parent directories of a given path
     *
     * @param fileSystem filesystem to work with
     * @param dest       file
     * @throws SmartFrogRuntimeException failure to create the directories
     */
    public static void mkdirs(FileSystem fileSystem, Path dest) throws SmartFrogRuntimeException {
        try {
            if (!fileSystem.mkdirs(dest)) {
                throw new SmartFrogRuntimeException(ERROR_MKDIR_FAILED + dest);
            }
        } catch (IOException e) {
            throw new SmartFrogRuntimeException(ERROR_MKDIR_FAILED + dest
                    + " in " + fileSystem.getUri()
                    + " : " + e, e);
        }
    }


    /**
     * Copy a file
     *
     * @param srcFS     source filesystem
     * @param src       source path
     * @param dstFS     destination filesystem
     * @param dst       destination path
     * @param overwrite overwrite
     * @param blocksize block size
     * @throws SmartFrogRuntimeException for any failure
     */
    public static void copyFile(FileSystem srcFS,
                                Path src,
                                FileSystem dstFS,
                                Path dst,
                                boolean overwrite,
                                int blocksize) throws SmartFrogRuntimeException {
        assertNotDependent(srcFS, src, dstFS, dst);
        FileStatus status;
        URI fsuri = srcFS.getUri();
        try {
            status = srcFS.getFileStatus(src);
        } catch (FileNotFoundException fe) {
            throw new SmartFrogRuntimeException(ERROR_MISSING_SOURCE_FILE + src + " in " + fsuri, fe);
        } catch (IOException e) {
            throw new SmartFrogRuntimeException(ERROR_NO_STAT + src + " in " + fsuri + " : " + e, e);
        }
        if (status.isDir()) {
            throw new SmartFrogRuntimeException(ERROR_NO_DIRECTORY_COPY + src + " in " + fsuri);
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = srcFS.open(src);
            out = dstFS.create(dst, overwrite);
        } catch (IOException e) {
            //close the input stream if it is not already in there
            org.smartfrog.services.filesystem.FileSystem.close(in);
            org.smartfrog.services.filesystem.FileSystem.close(out);
        }
        try {
            IOUtils.copyBytes(in, out, blocksize, true);
        } catch (IOException e) {
            throw new SmartFrogRuntimeException(ERROR_COPY_FAILED + src + " in " + fsuri
                    + " to " + dst + " in " + dstFS.getUri()
                    + " : " + e,
                    e);
        }

    }

    /**
     * Copy a local file into HDFS
     *
     * @param fileSystem filesystem for the destination
     * @param source     source file
     * @param dest       dest path
     * @param overwrite  should there be an overwrite?
     * @throws SmartFrogRuntimeException if the copy failed
     */
    public static void copyLocalFileIn(FileSystem fileSystem, File source, Path dest, boolean overwrite)
            throws SmartFrogRuntimeException {
        if (!source.exists()) {
            throw new SmartFrogRuntimeException(ERROR_MISSING_SOURCE_FILE + source);
        }
        Path localSource = new Path(source.toURI().toString());
        try {
            fileSystem.copyFromLocalFile(false, overwrite, localSource, dest);
        } catch (IOException e) {
            throw new SmartFrogRuntimeException(
                    FAILED_TO_COPY + source + " to " + dest
                            + " on " + fileSystem.getUri(),
                    e);
        }
    }

    /**
     * Move files that match the file pattern <i>srcPath</i>
     * to a destination file.
     * When moving mutiple files, the destination must be a directory.
     * Otherwise, IOException is thrown.
     * Based on {@link org.apache.hadoop.fs.FsShell#rename(String, String)}
     *
     * @param fileSystem filesystem to work with
     * @param srcPath    a file pattern specifying source files
     * @param dstPath    a destination file/directory
     * @throws IOException for any problem
     * @see org.apache.hadoop.fs.FileSystem#globStatus(Path)
     */
    public static void rename(FileSystem fileSystem, Path srcPath, Path dstPath) throws IOException {
        Path[] srcs = FileUtil.stat2Paths(fileSystem.globStatus(srcPath), srcPath);
        FileStatus destStatus = fileSystem.getFileStatus(dstPath);
        if (srcs.length > 1 && !destStatus.isDir()) {
            throw new IOException("When moving multiple files, "
                    + "destination should be a directory.");
        }
        for (Path src : srcs) {
            if (!fileSystem.rename(src, dstPath)) {
                FileStatus srcFstatus;
                FileStatus dstFstatus;
                try {
                    srcFstatus = fileSystem.getFileStatus(src);
                } catch (FileNotFoundException e) {
                    FileNotFoundException fnf = new FileNotFoundException(src +
                            ": No such file or directory in " + fileSystem.getUri());
                    fnf.initCause(e);
                    throw fnf;
                }
                try {
                    dstFstatus = fileSystem.getFileStatus(dstPath);
                } catch (IOException ignored) {
                    dstFstatus = null;
                }
                if ((srcFstatus != null) && (dstFstatus != null)) {
                    if (srcFstatus.isDir() && !dstFstatus.isDir()) {
                        throw new IOException("cannot overwrite non directory "
                                + dstPath + " with directory " + srcPath
                                + " in " + fileSystem.getUri());
                    }
                }
                throw new IOException("Failed to rename '" + srcPath
                        + "' to '" + dstPath + "'"
                        + " in " + fileSystem.getUri());
            }
        }
    }


    /**
     * This loads but does not initialise a filesystem.
     *
     * @param conf
     * @param uri
     * @return
     * @throws IOException
     */
    public static FileSystem loadFS(final Configuration conf, final URI uri) throws IOException {
        String scheme = uri.getScheme();
        String authority = uri.getAuthority();

        if (scheme == null) {                       // no scheme: use default FS
            return FileSystem.get(conf);
        }

        if (authority == null) {                       // no authority
            URI defaultUri = FileSystem.getDefaultUri(conf);
            if (scheme.equals(defaultUri.getScheme())    // if scheme matches default
                    && defaultUri.getAuthority() != null) {  // & default has authority
                return loadFS(conf, defaultUri);              // return default
            }
        }

        Class<?> clazz = conf.getClass("fs." + uri.getScheme() + ".impl", null);
        FileSystem.LOG.debug("Creating filesystem for " + uri);
        if (clazz == null) {
            throw new IOException("No FileSystem for scheme: " + uri.getScheme());
        }
        FileSystem fs = (FileSystem) ReflectionUtils.newInstance(clazz, conf);
        return fs;
    }
}
