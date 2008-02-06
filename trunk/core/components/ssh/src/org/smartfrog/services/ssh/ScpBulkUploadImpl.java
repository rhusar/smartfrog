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


package org.smartfrog.services.ssh;

import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.filesystem.files.FilesImpl;
import org.smartfrog.services.filesystem.files.Fileset;

import java.rmi.RemoteException;
import java.io.File;
import java.util.Vector;

/**
 * Component to do a bulk SCP upload. It works by overriding some
 *  of the functions to build a list of files. Also, it only supports
 *  file upload
 */
public class ScpBulkUploadImpl extends ScpComponentImpl implements
        ScpBulkUpload {

    private Fileset fileset;

    public ScpBulkUploadImpl() throws RemoteException {
    }

    /**
     * Read the file lists in, and resolve the local list Subclasses can
     * override this, as long as the localFiles and remoteFiles lists are full
     * at the end of the operation, and they have the same number of files.
     *
     * @throws SmartFrogResolutionException for resolution problems
     * @throws RemoteException network problems
     * @throws SmartFrogLifecycleException if there is a mismatch between the
     * count of local and remote files
     */
    protected void readFileLists() throws
            SmartFrogException,
            RemoteException {
        super.readFileLists();
        fileset = FilesImpl.resolveFileset(this);
        String remoteDir= sfResolve(ATTR_REMOTE_DIR,"",true);
        //this is the list of files to work with
        File[] files = fileset.listFiles();
        //now we build the local and remote lists from this
        Vector<File> local=new Vector<File>(files.length);
        Vector<String> remote=new Vector<String>(files.length);
        for(File file:files) {
            local.add(file);
            remote.add(remoteDir+ '/' + file.getName());
        }
        setLocalFiles(local);
        setRemoteFileList(remote);
    }

    /**
     * Force the transfer type to be an upload
     *
     * @throws SmartFrogResolutionException if failed to read any attribute or a
     * mandatory attribute is not defined.
     * @throws SmartFrogLifecycleException if the transfer type is unsupported
     * @throws RemoteException in case of network/rmi error
     */
    protected void readTransferType() throws
            SmartFrogResolutionException,
            RemoteException,
            SmartFrogLifecycleException {
        setGetFiles(false);
    }

    /**
     * Return a list of files that match the current pattern. This may be a
     * compute-intensive operation, so cache the result. Note that filesystem
     * race conditions do not guarantee all the files listed still exist...check
     * before acting
     *
     * @return a list of files that match the pattern, or an empty list for no
     *         match
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */

    public File[] listFiles() throws RemoteException, SmartFrogException {
        return fileset.listFiles();
    }

    /**
     * Get the base directory of these files (may be null)
     *
     * @return the base directory
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public File getBaseDir() throws RemoteException, SmartFrogException {
        return fileset.baseDir;
    }
}
