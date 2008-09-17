/*
 * (C) Copyright 2005-2008 Hewlett-Packard Development Company, LP
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For more information: www.smartfrog.org
 */
package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.rmi.RemoteException;

/**
 * This is the maven2 system policy. It has both local and remote policies.
 */
public class Maven2Policy extends AbstractPolicy implements LocalCachePolicy,
        RemoteCachePolicy {

    /**
     * @throws RemoteException
     */
    public Maven2Policy() throws RemoteException {
    }

    /**
     * @param artifact artifact to work on
     * @see LocalCachePolicy#createLocalPath(SerializedArtifact)
     * @throws SmartFrogRuntimeException on a validity error
     * @throws RemoteException on network errors
     */
    public String createLocalPath(SerializedArtifact artifact)
            throws RemoteException, SmartFrogRuntimeException {
        return createRemotePath(artifact);
    }

    /**
     * @param library library to build a path for
     * @see RemoteCachePolicy#createRemotePath(SerializedArtifact)
     * @throws SmartFrogRuntimeException on a validity error
     * @throws RemoteException on network errors
     */
    public String createRemotePath(SerializedArtifact library)
            throws SmartFrogRuntimeException,
            RemoteException {
        SerializedArtifact.assertValid(library, true);
        String filename = LibraryHelper.createMavenArtifactName(library);
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
     * @throws RemoteException on network errors
     */
    public String getDescription() throws RemoteException {
        return "Maven2 policy";
    }

}
