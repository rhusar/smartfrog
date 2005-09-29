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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

/** cache of files added to the filestore. When cleaned up, files are deleted. */
public class AddedFilestore {

    private File directory;

    private HashMap<URI, FileEntry> files = new HashMap<URI, FileEntry>();

    public AddedFilestore(File directory) {
        this.directory = directory;
        directory.mkdirs();
        assert directory.isDirectory();
    }

    /**
     * get
     *
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public FileEntry createNewFile(String prefix, String suffix)
            throws IOException {
        File tempfile = File.createTempFile(prefix, suffix, directory);
        FileEntry entry = new FileEntry(tempfile);
        synchronized (this) {
            files.put(entry.getUri(), entry);
        }

        return entry;
    }

    /**
     * Delete a file in the filestore, and remove it from the list of file
     * entries
     *
     * @param file
     * @return true if deletion went ahead
     */
    public boolean delete(File file) {
        URI uri = file.toURI();
        return delete(uri);
    }

    /**
     * Delete a file in the filestore, and remove it from the list of file
     * entries
     *
     * @param uri uri of entry
     * @return true if deletion went ahead
     */
    public synchronized boolean delete(URI uri) {
        FileEntry fileEntry = files.remove(uri);
        if (fileEntry == null) {
            return false;
        } else {
            return fileEntry.getFile().delete();
        }
    }
    
    


    public File getDirectory() {
        return directory;
    }

    /**
     * Delete all entries, reset the cache
     */
    public synchronized void deleteAllEntries() {
        for (FileEntry entry : getEntries()) {
            entry.getFile().delete();
        }
        files=new HashMap<URI, FileEntry>();
    }

    /**
     * get an entry
     *
     * @param file
     * @return file or null
     */
    public FileEntry lookup(File file) {
        return lookup(file.toURI());
    }

    /**
     * look up an entry
     *
     * @param uri of entry
     * @return file entry or null
     */
    public FileEntry lookup(URI uri) {
        return files.get(uri);
    }
    
    /**
     * Get all the entries
     *
     * @return a collection to iterate over
     */
    public Collection<FileEntry> getEntries() {
        return files.values();
    }

}
