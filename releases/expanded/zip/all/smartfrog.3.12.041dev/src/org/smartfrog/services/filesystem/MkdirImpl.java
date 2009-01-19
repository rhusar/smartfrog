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

import java.io.File;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.prim.TerminationRecord;

/**
 * Component to create directories; can clean them up too.
 * created 21-Jun-2004 16:52:40
 */

public class MkdirImpl extends FileUsingComponentImpl implements Mkdir {
    private boolean delete;

    /**
     * Constructor.
     * @throws RemoteException  In case of network/rmi error
     */
    public MkdirImpl() throws RemoteException {
    }

    /**
     * read in the directory settings and bind the file attributes.
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();

        String dir;

        File parentDir=null;
        String parent;
        parent= FileSystem.lookupAbsolutePath(this,
                ATTR_PARENT,
                null,
                null,
                false,
                null);
        if (parent!=null) {
            parentDir=new File(parent);
        }

        dir=FileSystem.lookupAbsolutePath(this,Mkdir.ATTR_DIR,null,parentDir,true,null);
        File directory=new File(dir);
        bind(directory);
        //get the delete flag
        //this is only done if the directory does not yet exist.
        //delete is implicitly false. 
        delete = sfResolve(ATTR_DELETE_ON_EXIT, false, false);
    }

    /**
     * we only create the directory at startup time, even though we bond at deploy time.
     * @throws SmartFrogException  failure in starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
            RemoteException {
        super.sfStart();
        File directory = getFile();
        boolean clean = sfResolve(ATTR_CLEAN_ON_START, false, false);
        if(directory.exists()) {
            if (clean) {
                FileSystem.recursiveDelete(directory);
            }
            //it already exists. that may be harmless, but it warns the component to not
            //delete the directory during termination.
            delete=false;
        }
        directory.mkdirs();
        if (!directory.exists() || !directory.isDirectory()) {
            //whatever it is, don't try and delete it.
            delete = false;
            //raise an error.
            throw new SmartFrogDeploymentException("Failed to create directory " +
                    directory.getAbsolutePath());
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "Mkdir "+getFile().getAbsolutePath(),
                sfCompleteNameSafe(),
                null);
    }


    /**
     * At terminate time, trigger a recursive delete of the directory if desired.
     *
     * @param status  TerminationRecord object
     */

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (delete) {
            FileSystem.recursiveDelete(getFile());
        }
    }

}
