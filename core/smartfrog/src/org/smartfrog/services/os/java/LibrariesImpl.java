/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.services.filesystem.FileImpl;

import java.rmi.RemoteException;
import java.io.File;

/**
 * Implementation of libraries logic
 * created 04-Apr-2005 14:14:30
 */

public class LibrariesImpl extends FileUsingComponentImpl implements Libraries {
    public static final String ERROR_NOT_A_DIRECTORY = "Cache directory is not a directory: ";

    public LibrariesImpl() throws RemoteException {
    }

    /**
     * deployment: validate and create
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();

        String cacheDirname=FileImpl.lookupAbsolutePath(this,ATTR_CACHE_DIR,null,null,true,null);
        File cacheDir=new File(cacheDirname);
        cacheDir.mkdirs();
        if(!cacheDir.isDirectory()) {
            throw new SmartFrogException(ERROR_NOT_A_DIRECTORY+cacheDir,this);
        }
        //set up cache information
        bind(cacheDir);
    }

    
}
