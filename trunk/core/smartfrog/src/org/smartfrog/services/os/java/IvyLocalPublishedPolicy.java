/*
 * (C) Copyright 2008 Hewlett-Packard Development Company, LP
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

import java.rmi.RemoteException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

/**
 *
 * @author slo
 */
public class IvyLocalPublishedPolicy extends AbstractPolicy implements LocalCachePolicy {

    /**
     *  @throws RemoteException as the parent does
     */
    public IvyLocalPublishedPolicy() throws RemoteException {
    }

    /**
     * Create the local path of the artifact. 
     * the path here is something like 
     *  org.smarfrog/sf-www/3.13.017/jars/sf-www.jar
     * -a different extension (e.g. war) leads to it being placed in the directory
     * org.smarfrog/sf-www/3.13.017/wars/sf-www-testwar.war
     * -(the latter has  testwar as the classifier)
     * @throws  RemoteException for network problems
     * @throws SmartFrogRuntimeException for SmartFrog problems
     */
    public String createLocalPath(SerializedArtifact artifact)
            throws RemoteException, SmartFrogRuntimeException {
        SerializedArtifact.assertValid(artifact, true);
        String project = LibraryHelper.convertProjectToIvyFormat(artifact);
        
        String urlPath = new StringBuffer().append(project)
                .append('/')
                .append(artifact.artifact)
                .append('/')
                .append(artifact.version)
                .append('/')
                .append(artifact.extension)
                .append("s/")
                .append(LibraryHelper.createIvyArtifactPublishedFilename(artifact))
                .toString();
        return urlPath;
    }

    /**
     * @see LibraryCachePolicy#getDescription() 
     * @throws  RemoteException for network problems
     */
    public String getDescription() throws RemoteException {
        return "Ivy local published policy";
    }

}
