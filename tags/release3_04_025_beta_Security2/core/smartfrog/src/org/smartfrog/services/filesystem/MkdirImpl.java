/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.File;
import java.rmi.RemoteException;

/**
 * created 21-Jun-2004 16:52:40
 */

public class MkdirImpl extends FileUsingComponentImpl implements Mkdir {
    public MkdirImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        String dir;

        File parentDir=null;
        String parent;
        parent= FileSystem.lookupAbsolutePath(this,
                ATTR_PARENT,
                (String) null,
                (File) null,
                false,
                null);
        if (parent!=null) {
            parentDir=new File(parent);
        }

        dir=FileSystem.lookupAbsolutePath(this,Mkdir.ATTR_DIR,(String)null,parentDir,true,null);
        File directory=new File(dir);
        bind(directory);
    }

    /**
     * we only create the directory at startup time, even though we bond at deploy time
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        File directory=getFile();
        directory.mkdirs();
        if (!directory.exists() || !directory.isDirectory()) {
            throw new SmartFrogDeploymentException("Failed to create directory " +
                    directory.getAbsolutePath());
        }

    }
}
