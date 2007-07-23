/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.rmi.RemoteException;

public class IvyLocalCachePolicy extends AbstractPolicy implements LocalCachePolicy {

    /** @throws RemoteException  */
    public IvyLocalCachePolicy() throws RemoteException {
    }

    /** @see LocalCachePolicy#createLocalPath(SerializedArtifact) */
    public String createLocalPath(SerializedArtifact artifact)
            throws RemoteException, SmartFrogRuntimeException {
        SerializedArtifact.assertValid(artifact, true);
        String project = convertProjectToIvyFormat(artifact);
        String urlPath = new StringBuffer().append(project)
                .append("/")
                .append(artifact.artifact)
                .append("/")
                .append(artifact.version)
                .append("/")
                .append(createIvyArtifactFilename(artifact))
                .toString();
        return urlPath;
    }

    /**
     * In Ivy, this is currently a noop
     *
     * @param artifact
     * @return the project of an artifact
     */
    private String convertProjectToIvyFormat(SerializedArtifact artifact) {
        return artifact.project;
    }

    private String createIvyArtifactFilename(SerializedArtifact artifact) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(artifact.artifact);
        buffer.append(ARTIFACT_SEPARATOR);
        buffer.append(artifact.version);
        buffer.append('.');
        buffer.append(artifact.extension);
        String filename = buffer.toString();
        return filename;
    }

    /** @see LibraryCachePolicy#getDescription() */
    public String getDescription() throws RemoteException {
        return "Ivy local cache policy";
    }

}
