package org.smartfrog.services.filesystem.filestore;

import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.URI;
import java.rmi.RemoteException;


/** A filestore. */
public interface LocalFilestore extends FileUsingComponent {


    /**
     * Create a new temp file
     *
     * @param prefix prefix
     * @param suffix suffix
     * @return a file entry describing both the file and the URL
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in creating file
     *
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    FileEntry createNewFile(String prefix, String suffix)
            throws SmartFrogException, RemoteException;

    /**
     * Delete an entry
     *
     * @param uri uri of entry
     * @return true if deletion worked
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in deleting
     *
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */
    boolean delete(URI uri)
            throws SmartFrogException, RemoteException;

    /**
     * look up a file from a URI
     *
     * @param uri  uri
     * @return FileEntry
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in lookup
     *
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */
    FileEntry lookup(URI uri) throws SmartFrogException, RemoteException;

    /**
     * Create a new temp file
     *
     * @param prefix   prefix
     * @param suffix   suffix
     * @param content  the actual content of the file content
     * @param metadata any metadata
     * @return a file entry describing both the file and the URL
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in creating file
     *
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */

    FileEntry uploadNewFile(String prefix,
                            String suffix,
                            byte[] content,
                            Object metadata)
            throws SmartFrogException, RemoteException;

}
