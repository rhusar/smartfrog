package org.smartfrog.services.deployapi.components;

import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.URI;
import java.rmi.RemoteException;


/**
 * a filestore
 */
public interface LocalFilestore extends FileUsingComponent {


    /**
     * Create a new temp file
     * @param prefix prefix
     * @param suffix suffix
     * @return a file entry describing both the file and the URL
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     */
    FileEntry createNewFile(String prefix,String suffix)
            throws SmartFrogException, RemoteException;

    /**
     * Delete an entry
     * @param uri uri of entry
     * @return true if deletion worked
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     */
    boolean delete(URI uri)
            throws SmartFrogException, RemoteException;

    /**
     * look up a file from a URI
     * @param uri
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     * @throws java.rmi.RemoteException
     */
    FileEntry lookup(URI uri) throws SmartFrogException, RemoteException;
}
