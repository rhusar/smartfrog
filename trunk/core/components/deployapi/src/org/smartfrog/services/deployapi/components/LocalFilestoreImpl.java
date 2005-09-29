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

package org.smartfrog.services.deployapi.components;

import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;

/**

 */
public class LocalFilestoreImpl extends FileUsingComponentImpl implements LocalFilestore {

    private AddedFilestore filestore;
    public static final String ERROR_NOT_A_DIRECTORY = "Not a directory :";

    public LocalFilestoreImpl() throws RemoteException {
    }

    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy()
            throws SmartFrogException, RemoteException {
        super.sfDeploy();
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        bind(true,null);
        filestore=new AddedFilestore(getFile());
        if(!getFile().isDirectory()) {
            throw new SmartFrogException(ERROR_NOT_A_DIRECTORY +getFile());
        }
    }

    /**
     * Provides hook for subclasses to implement useful termination behavior.
     * Deregisters component from local process compound (if ever registered)
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        filestore.deleteAllEntries();
    }

    /**
     * Get the filestore
     * @return filestore
     */
    public AddedFilestore getFilestore() {
        return filestore;
    }

    /**
     * Create a new temp file
     * @param prefix prefix
     * @param suffix suffix
     * @return a file entry describing both the file and the URL
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public FileEntry createNewFile(String prefix,String suffix)
            throws SmartFrogException, RemoteException {
        try {
            FileEntry newFile = filestore.createNewFile(prefix, suffix);
            return newFile;
        } catch (IOException e) {
            throw new SmartFrogException(e);
        }
    }

    /**
     * Delete an entry
     * @param uri uri of entry
     * @return true if deletion worked
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public boolean delete(URI uri)
            throws SmartFrogException, RemoteException {
        return filestore.delete(uri);
    }

    /**
     * look up a file from a URI
     * @param uri
     * @return
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public FileEntry lookup(URI uri) throws SmartFrogException, RemoteException {
        return filestore.lookup(uri);
    }
}
