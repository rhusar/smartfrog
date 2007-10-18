package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * This implements a naming policy for the filesystem cache
 *
 */
public interface LocalCachePolicy extends LibraryCachePolicy {
    
    /**
     * Form a local path, 
     * @param artifact
     * @return a relative path (with forward slashes in place of platform specific file separators)
     * to the the file (which may or may not exist)
     * 
     */
    public String createLocalPath(SerializedArtifact artifact) throws RemoteException,SmartFrogException;

}
