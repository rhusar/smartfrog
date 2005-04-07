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

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.compound.CompoundImpl;

import java.io.File;
import java.rmi.RemoteException;

/**
 * Implementation of libraries logic
 * created 04-Apr-2005 14:14:30
 */

public class LibraryImpl extends CompoundImpl implements Library {

    /**
     * we are not a directory
     */
    public static final String ERROR_NOT_A_DIRECTORY = "Cache directory is not a directory: ";

    /**
     * cache directory
     */
    private File cacheDir;

    public LibraryImpl() throws RemoteException {
    }

    /**
     * deployment: validate and create
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        //deploy children
        super.sfDeploy();
        //bind our directory
        bindDirectory();
    }

    /**
     * befire we deploy our children, but after prim is configured, we
     * want to set up our directories
     * @throws SmartFrogResolutionException
     * @throws RemoteException
     * @throws SmartFrogLifecycleException
     */
    protected void sfDeployCompoundChildren()
            throws SmartFrogResolutionException, RemoteException,
            SmartFrogLifecycleException {
/*
        try {
            bindDirectory();
        } catch (SmartFrogRuntimeException e) {
            throw (SmartFrogLifecycleException)SmartFrogLifecycleException.forward(e);
        }
*/
        super.sfDeployCompoundChildren();
    }

    /**
     * bind our cache directory information
     * @throws RemoteException
     * @throws SmartFrogException
     */
    /**
     *
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    private void bindDirectory() throws RemoteException,
            SmartFrogRuntimeException {
        String cacheDirname=FileSystem.lookupAbsolutePath(this,ATTR_CACHE_DIR,null,null,true,null);
        cacheDir = new File(cacheDirname);
        cacheDir.mkdirs();
        if(!cacheDir.isDirectory()) {
            throw new SmartFrogResolutionException(ERROR_NOT_A_DIRECTORY+cacheDir,this);
        }
        //set up cache information
        FileUsingComponentImpl.bind(this,cacheDir);
    }


}
