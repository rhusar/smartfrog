/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
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
