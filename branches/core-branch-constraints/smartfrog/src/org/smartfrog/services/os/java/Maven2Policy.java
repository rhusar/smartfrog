package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.rmi.RemoteException;

public class Maven2Policy extends AbstractPolicy implements LocalCachePolicy,
        RemoteCachePolicy {

    /**
     * @throws RemoteException
     */
    public Maven2Policy() throws RemoteException {
    }

    /**
     * @see LocalCachePolicy#createLocalPath(SerializedArtifact)
     */
    public String createLocalPath(SerializedArtifact artifact)
            throws RemoteException, SmartFrogRuntimeException {
        return createRemotePath(artifact);
    }

    /**
     * @see RemoteCachePolicy#createRemotePath(SerializedArtifact)
     */
    public String createRemotePath(SerializedArtifact library)
            throws SmartFrogRuntimeException,
            RemoteException {
        SerializedArtifact.assertValid(library, true);
        String filename = createMavenArtifactName(library);
        String patchedProject = LibraryHelper.patchProject(library.project);
        String urlPath = new StringBuffer().append(patchedProject)
                .append("/")
                .append(library.artifact)
                .append("/")
                .append(library.version)
                .append("/")
                .append(filename)
                .toString();
        return urlPath;
    }

    /**
     * @see org.smartfrog.services.os.java.LibraryCachePolicy#getDescription()
     */
    public String getDescription() throws RemoteException {
        return "Maven2 policy";
    }

}
