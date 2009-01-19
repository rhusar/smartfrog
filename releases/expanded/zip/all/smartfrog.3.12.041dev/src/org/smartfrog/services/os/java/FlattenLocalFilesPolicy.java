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
 * policy for flattening local files
 *
 */
public class FlattenLocalFilesPolicy 
    extends AbstractPolicy 
    implements LocalCachePolicy {

    
    /**
     * @throws RemoteException
     */
    public FlattenLocalFilesPolicy() throws RemoteException {
    }

    /**
     * This is the maven filename and nothing else; no directory tree.  
     * @see LocalCachePolicy#createLocalPath(SerializedArtifact)
     */
    public String createLocalPath(SerializedArtifact artifact) throws SmartFrogRuntimeException {
        String filename=LibraryHelper.createMavenArtifactName(artifact);
        return filename;
    }

    /**
     * @see org.smartfrog.services.os.java.LibraryCachePolicy#getDescription()
     */
    public String getDescription() throws RemoteException {
        return "FlattenLocalFilesPolicy ";
    }
    
}
