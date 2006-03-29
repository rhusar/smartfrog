package org.smartfrog.services.filesystem.filestore;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**

 */
public interface FileEntry extends Remote {
    File getFile() throws RemoteException;

    URI getUri() throws RemoteException;

    String getMimetype() throws RemoteException;

    void setMimetype(String mimetype) throws RemoteException;

    Object getMetadata() throws RemoteException;

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
     * @return
     * @throws RemoteException
     */
    boolean exists() throws RemoteException;


    /**
     * Look up a piece of metadata
     *
     * @param key
     * @return the object stored under there
     */
    Object lookupMetadata(String key) throws RemoteException;
}
