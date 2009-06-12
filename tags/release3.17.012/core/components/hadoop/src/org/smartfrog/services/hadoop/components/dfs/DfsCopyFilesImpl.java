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

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;
import org.smartfrog.services.hadoop.common.DfsUtils;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.io.IOException;

/**
 * Component to copy a file within DFS Created 17-Jun-2008 15:06:23
 */

public class DfsCopyFilesImpl extends DfsOperationImpl implements DfsCopyOperation {

    public static final String ATTR_BLOCKSIZE = "blocksize";

    public DfsCopyFilesImpl() throws RemoteException {
    }

    /**
     * Override point for the destination filesystem
     *
     * @return false
     */
    @Override
    protected boolean isClusterRequired() {
        return false;
    }

    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        startWorkerThread();
    }

    /**
     * do the work
     *
     * @throws Exception on any failure
     */
    @Override
    protected void performDfsOperation() throws Exception {
        ManagedConfiguration conf = createConfiguration();

        String sourceFSURL = sfResolve(ATTR_SOURCEFS, "", true);
        String destFSURL = sfResolve(ATTR_DESTFS, "", true);
        Path source = resolveDfsPath(ATTR_SOURCE);
        Path dest = resolveDfsPath(ATTR_DEST);
        boolean overwrite = sfResolve(ATTR_OVERWRITE, false, true);

        String matchPattern = sfResolve(ATTR_PATTERN, "", true);

        int minFileCount = sfResolve(DfsPathOperation.ATTR_MIN_FILE_COUNT, 0, true);
        int maxFileCount = sfResolve(DfsPathOperation.ATTR_MAX_FILE_COUNT, 0, true);

        //URL to the destination filesystem
        FileSystem sourceFS = null;
        FileSystem destFS = null;
        try {
            sourceFS = DfsUtils.createFileSystem(sourceFSURL, conf);
            destFS = DfsUtils.createFileSystem(destFSURL, conf);
            DfsUtils.assertNotDependent(sourceFS, source, destFS, dest);
            DfsUtils.mkParentDirs(destFS, dest);

            //build a list of sourcefiles
            List<Path> sourceFiles = listFiles(sourceFS, source, matchPattern);
            int count = sourceFiles.size();
            if (count < minFileCount) {
                throw new SmartFrogDeploymentException(
                        "File count " + count + " is below the minFileCount value of " + minFileCount,
                        this);
            }
            if (maxFileCount > -1 && count > maxFileCount) {
                throw new SmartFrogDeploymentException(
                        "File count " + count + " is above the maxFileCount value of " + minFileCount,
                        this);
            }
            Path[] sourcePaths = new Path[count];
            sourcePaths = sourceFiles.toArray(sourcePaths);

            boolean deleteSource = false;
            FileUtil.copy(sourceFS, sourcePaths, destFS, dest, deleteSource, overwrite, conf);
        } finally {
            DfsUtils.closeQuietly(sourceFS);
            DfsUtils.closeQuietly(destFS);
        }
    }

    public List<Path> listFiles(FileSystem sourceFS, Path source, String matchPattern)
            throws SmartFrogDeploymentException, IOException {
        Pattern responsePattern;
        try {
            responsePattern = Pattern.compile(matchPattern);
        } catch (PatternSyntaxException e) {
            throw new SmartFrogDeploymentException("Unable to compile " + matchPattern, e);
        }
        List<Path> sourceFiles =new ArrayList<Path>();
        FileStatus[] stats = sourceFS.listStatus(source);
        for (FileStatus file : stats) {
            String shortname = file.getPath().getName();
            Matcher matcher = responsePattern.matcher(shortname);
            if (matcher.matches()) {
                sourceFiles.add(file.getPath());
            }
        }
        return sourceFiles;
    }

}