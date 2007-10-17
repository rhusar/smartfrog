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


import org.smartfrog.services.filesystem.FileSystem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Hashtable;

/** What is stored in our table of files */
public class FileEntryImpl implements FileEntry {
    private File file;
    private URI uri;
    private String mimetype;
    private Object metadata;

    //extra data
    private Hashtable data;

    /**
     * Constructor.
     * @param file  file
     */
    public FileEntryImpl(File file) {
        this.file = file;
        this.uri = file.toURI();
    }

    /**
     * Get File
     * @return File
     */
    public File getFile() {
        return file;
    }

    /**
     * Get URI
     * @return URI
     *
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Returns extra data
     * @return  Hashtable
     */
    private Hashtable data() {
        if (data == null) {
            data = new Hashtable();
        }
        return data;
    }

    /**
     * Look up a piece of metadata
     *
     * @param key String name
     * @return the object stored under there
     */
    public Object lookupMetadata(String key) {
        return data().get(key);
    }

    /**
     * Add metadata to the system
     *
     * @param key String name
     * @param metadata value
     */
    public void addMetadata(String key, Object metadata) {
        data().put(key, metadata);
    }

    /**
     * Get MIME type
     * @return String
     *
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Set MIME type
     * @param mimetype String
     *
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     * Get metadata
     * @return Object
     *
     */
    public Object getMetadata() {
        return metadata;
    }

     /**
     * Set metadata
     * @param metadata Object
     *
     */
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    /** @return a string representation of the object. */
    public String toString() {
        return file.getAbsolutePath();
    }

    /**
     * Compares the Object
     * @param o Object
     * @return boolean
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FileEntryImpl fileEntry = (FileEntryImpl) o;

        if (!uri.equals(fileEntry.uri)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hashcode
     * @return int
     */
    public int hashCode() {
        return uri.hashCode();
    }

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
    public void append(byte[] content) throws RemoteException, IOException {

        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file, true));
            out.write(content);
        } finally {
            FileSystem.close(out);
        }

    }

    /**
     * Test for a file existing.
     *
     * @return boolean
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */
    public boolean exists() throws RemoteException {
        return file.exists();
    }
}
