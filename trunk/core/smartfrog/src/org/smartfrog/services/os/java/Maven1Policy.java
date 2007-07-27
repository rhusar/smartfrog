/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */

package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.rmi.RemoteException;

/**
 * This is our maven1 naming policy. It is essentially
 * 
 * project/jars/project[-version][.extension];
 *
 */
public class Maven1Policy extends AbstractPolicy implements RemoteCachePolicy, LocalCachePolicy {


    /**
     * @throws RemoteException
     */
    public Maven1Policy() throws RemoteException {
    }


    /**
     * directory separator for Maven file systems. {@value}
     */
    public static final String MAVEN1_JAR_SUBDIR = "/jars/";

    /**
     * @see RemoteCachePolicy#createRemotePath(SerializedArtifact)
     */
    public String createRemotePath(SerializedArtifact artifact)
            throws SmartFrogRuntimeException {
        return createMavenURL(artifact);
    }


    /**
     * @see LocalCachePolicy#createLocalPath(SerializedArtifact)
     */
    public String createLocalPath(SerializedArtifact artifact)
            throws SmartFrogRuntimeException {
        // the policy here is the same as with remote names
        return createRemotePath(artifact);
    }


    /**
     * method to create a maven URL
     *
     * @param library
     *
     * @return url  /project/jars/+artifact name
     */
    public String createMavenURL(SerializedArtifact library)
            throws SmartFrogRuntimeException {
        SerializedArtifact.assertValid(library, false);
        String artifactName = createMavenArtifactName(library);
        String urlPath = library.project + MAVEN1_JAR_SUBDIR + artifactName;
        return urlPath;
    }

    /**
     * @see org.smartfrog.services.os.java.LibraryCachePolicy#getDescription()
     */
    public String getDescription() throws RemoteException {
        return "Maven1 policy";
    }

}
