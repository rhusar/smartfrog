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
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems

     */
    public String createLocalPath(SerializedArtifact artifact) throws RemoteException,SmartFrogException;

}
