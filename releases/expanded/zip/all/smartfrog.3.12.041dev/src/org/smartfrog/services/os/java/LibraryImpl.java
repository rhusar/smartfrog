/* (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingCompoundImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.utils.PlatformHelper;

import java.io.File;
import java.rmi.RemoteException;

/**
 * Implementation of libraries logic
 * created 04-Apr-2005 14:14:30
 */

public class LibraryImpl extends FileUsingCompoundImpl implements Library {

    /**
     * we are not a directory
     */
    public static final String ERROR_NOT_A_DIRECTORY = "Cache directory is not a directory: ";

    /**
     * cache directory
     */
    private File cacheDir;


    /**
     * Local cache policy
     */
    private LocalCachePolicy localPolicy;

    /**
     * Remote Cache Policy
     */
    private RemoteCachePolicy remotePolicy;

    public LibraryImpl() throws RemoteException {
    }

    /**
     * deployment: validate and create
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException any other error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        //this implicitly deploys all our children too
        super.sfDeploy();
        //bind our directory
        bindDirectory();
        //bind our policies
        localPolicy = (LocalCachePolicy) sfResolve(ATTR_LOCAL_CACHE_POLICY,
                localPolicy,
                true);
        remotePolicy = (RemoteCachePolicy) sfResolve(ATTR_REMOTE_CACHE_POLICY,
                remotePolicy,
                true);
    }

    /**
     * bind our cache directory information
     *
     * @throws RemoteException network problems
     * @throws SmartFrogException any other error
     */
    private void bindDirectory() throws RemoteException,
            SmartFrogRuntimeException {
        String cacheDirname = FileSystem.lookupAbsolutePath(this,
                ATTR_CACHE_DIR,
                null,
                null,
                true,
                null);
        cacheDir = new File(cacheDirname);
        cacheDir.mkdirs();
        if (!cacheDir.isDirectory()) {
            throw new SmartFrogResolutionException(ERROR_NOT_A_DIRECTORY + cacheDir, this);
        }
        //set up cache information
        bind(cacheDir);
    }


    /**
     * @see org.smartfrog.services.os.java.Library#determineArtifactPath(SerializedArtifact
     *      artifact)
     * @param artifact the artifact to look up
     * @return the absolute path of the local copy of the artifact
     * @throws RemoteException network problems
     * @throws SmartFrogException any other error
     */
    public String determineArtifactPath(SerializedArtifact artifact)
            throws RemoteException, SmartFrogException {
        return determineArtifactFile(artifact).getAbsolutePath();
    }


    /**
     * 
     * @param artifact the artifact to look up
     * @return the URL fragment to look up. Null means "no remote artifact supported"
     * @throws java.rmi.RemoteException network problems
     * @throws SmartFrogException any other error
     * @see org.smartfrog.services.os.java.Library#determineArtifactRelativeURLPath(org.smartfrog.services.os.java.SerializedArtifact)
     */
    public String determineArtifactRelativeURLPath(SerializedArtifact artifact)
            throws RemoteException, SmartFrogException {
        SerializedArtifact.assertValid(artifact, false);
        String path = remotePolicy.createRemotePath(artifact);
        return path;
    }

    /**
     * Get the filename of the artifact.
     * @param artifact the artifact to look up
     * @return the file instance of the local copy
     * @throws java.rmi.RemoteException network problems
     * @throws SmartFrogException any other error
     */
    public File determineArtifactFile(SerializedArtifact artifact)
            throws RemoteException, SmartFrogException {
        SerializedArtifact.assertValid(artifact, false);
        //get the path from our policy class
        String path = localPolicy.createLocalPath(artifact);
        PlatformHelper helper = PlatformHelper.getLocalPlatform();
        //convert this to platform specifics
        String localpath = helper.convertFilename(path);
        //create a file 
        File file = new File(cacheDir, localpath);
        return file;
    }


}
