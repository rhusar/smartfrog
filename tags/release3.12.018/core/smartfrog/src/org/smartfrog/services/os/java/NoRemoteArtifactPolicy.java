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
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * a remote policy that always returns null when resolving an artifact, 
 * to indicate remote access is not allowed.
 */
public final class NoRemoteArtifactPolicy extends AbstractPolicy 
    implements RemoteCachePolicy{

    /**
     * Constructor
     * @throws RemoteException from the superclass
     */
    public NoRemoteArtifactPolicy() throws RemoteException {
        
    }
    
    
    /**
     * Always return null from the operation
     * @param artifact
     * @return null, always.
     */
    public String createRemotePath(SerializedArtifact artifact) {
        return null;
    }

    /**
     * Get the description
     * @return a description of this policy
     */
    public String getDescription() {
        return "No Remote Artifacts";
    }

}
