/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.hadoop.components.dfs;

import org.apache.hadoop.fs.Path;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;

import java.rmi.RemoteException;

/**
 *
 */
public class DfsPathOperationImpl extends DfsOperationImpl implements DfsPathOperation {

    private String pathName;
    private Path path;
    private boolean idempotent;

    public DfsPathOperationImpl() throws RemoteException {
    }

    /**
     * start up, bind to the cluster
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        idempotent = sfResolve(ATTR_IDEMPOTENT, true, true);
        pathName = sfResolve(ATTR_PATH, "", true);
        try {
            path = new Path(pathName);
        } catch (IllegalArgumentException e) {
            throw new SmartFrogLifecycleException("Failed to create path " + e.getMessage(), e, this);
        }
    }

    /**
     * Get the path name
     *
     * @return the path as a string
     */
    public String getPathName() {
        return pathName;
    }

    public Path getPath() {
        return path;
    }

    public boolean isIdempotent() {
        return idempotent;
    }
}
