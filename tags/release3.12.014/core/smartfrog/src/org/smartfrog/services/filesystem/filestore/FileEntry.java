package org.smartfrog.services.filesystem.filestore;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**

 */
public interface FileEntry extends Remote {
    /**
     * Get File
     * @return File
     * @throws RemoteException  In case of network/rmi error
     */
    File getFile() throws RemoteException;

    /**
     * Get URI
     * @return URI
     * @throws RemoteException  In case of network/rmi error
     */
    URI getUri() throws RemoteException;

    /**
     * Get MIME type
     * @return String
     * @throws RemoteException In case of network/rmi error
     */
    String getMimetype() throws RemoteException;


    /**
     * Set MIME type
     * @param mimetype String
     * @throws RemoteException In case of network/rmi error
     */
    void setMimetype(String mimetype) throws RemoteException;

    /**
     * Get metadata
     * @return Object
     * @throws RemoteException In case of network/rmi error
     */
    Object getMetadata() throws RemoteException;

    /**
     * Set metadata
     * @param metadata Object
     * @throws RemoteException In case of network/rmi error
     */
    void setMetadata(Object metadata) throws RemoteException;

    /**
     * Append data to the file. After the write the buffer is flushed and the
     * file is unlocked.
     * <p/>
     * To do an atomic update, write everything in one go. To do a less-effient
     * but potentially less costly-over-the-wire update, write in a few large
     * blocks.
     * <p/>
     * There is no equivalent operation to get the content back.
     *
     * @param content byte array of content
     * @throws RemoteException if something went wrong over the wire
     * @throws IOException     if something went wrong saving the content
     */
    void append(byte[] content) throws RemoteException, IOException;

    /**
     * Test for a file existing.
     *
     * @return boolean
     * @throws RemoteException In case of network/rmi error
     */
    boolean exists() throws RemoteException;


    /**
     * Look up a piece of metadata
     *
     * @param key String to lookup
     * @return the object stored under there
     * @throws RemoteException In case of network/rmi error
     */
    Object lookupMetadata(String key) throws RemoteException;
}
