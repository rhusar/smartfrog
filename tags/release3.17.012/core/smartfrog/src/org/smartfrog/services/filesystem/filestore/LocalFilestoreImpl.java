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

package org.smartfrog.services.filesystem.filestore;

import org.smartfrog.services.filesystem.FileUsingComponentImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;

/**
 * A local filestore that uses {@link AddedFilestore} to do the work underneath.
 */
public class LocalFilestoreImpl extends FileUsingComponentImpl
        implements LocalFilestore {

    /**
     * Filestore underneath
     */
    private AddedFilestore filestore;

    /**
     * Error string if our repository is not a directory. {@value}
     */
    public static final String ERROR_NOT_A_DIRECTORY = "Not a directory :";

    /**
     * Constructor.
     *
     * @throws RemoteException In case of network/rmi error
     */
    public LocalFilestoreImpl() throws RemoteException {
    }


    /**
     * start the filestore
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        bind(true, null);
        filestore = new AddedFilestore(getFile());
        if (!getFile().isDirectory()) {
            throw new SmartFrogException(ERROR_NOT_A_DIRECTORY + getFile());
        }
    }

    /**
     * delete the filestore on termination
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        filestore.deleteAllEntries();
    }

    /**
     * Get the filestore
     *
     * @return filestore
     */
    public AddedFilestore getFilestore() {
        return filestore;
    }

    /**
     * Create a new temp file
     *
     * @param prefix prefix
     * @param suffix suffix
     * @return a file entry describing both the file and the URL
     * @throws SmartFrogException error while creating file
     * @throws RemoteException    In case of network/rmi error
     */
    public FileEntry createNewFile(String prefix, String suffix)
            throws SmartFrogException, RemoteException {
        try {
            FileEntry newFile = filestore.createNewFile(prefix, suffix);
            return newFile;
        } catch (IOException e) {
            throw new SmartFrogException(e);
        }
    }

    /**
     * Create a new temp file
     *
     * @param prefix   prefix
     * @param suffix   suffix
     * @param content  the actual content of the file content
     * @param metadata any metadata
     * @return a file entry describing both the file and the URL
     * @throws SmartFrogException error while creating file
     * @throws RemoteException In case of network/rmi error
     */

    public FileEntry uploadNewFile(String prefix,
                                   String suffix,
                                   byte[] content,
                                   Object metadata)
            throws SmartFrogException, RemoteException {
        FileEntry entry = createNewFile(prefix, suffix);
        entry.setMetadata(metadata);
        try {
            entry.append(content);
            return entry;
        } catch (IOException e) {
            throw SmartFrogException.forward("When writing to " + entry.getUri(), e);
        }
    }

    /**
     * Delete an entry
     *
     * @param uri uri of entry
     * @return true if deletion worked
     * @throws SmartFrogException error while deleting
     * @throws RemoteException    In case of network/rmi error
     */
    public boolean delete(URI uri)
            throws SmartFrogException, RemoteException {
        return filestore.delete(uri);
    }

    /**
     * look up a file from a URI
     *
     * @param uri URI to look up
     * @return FileEntry entry in the filestore, or null for no match
     * @throws SmartFrogException error while lookup
     * @throws RemoteException    In case of network/rmi error
     */
    public FileEntry lookup(URI uri)
            throws SmartFrogException, RemoteException {
        return filestore.lookup(uri);
    }
}
