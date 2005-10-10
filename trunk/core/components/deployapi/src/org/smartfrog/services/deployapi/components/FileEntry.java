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

import nu.xom.Element;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

/** What is stored in our table of files */
public class FileEntry implements Serializable {
    private File file;
    private URI uri;
    private String mimetype;
    private Element metadata;
    

    private FileEntry() {
    }

    public FileEntry(File file) {
        this.file = file;
        this.uri = file.toURI();
    }

    public File getFile() {
        return file;
    }

    public URI getUri() {
        return uri;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public Element getMetadata() {
        return metadata;
    }

    public void setMetadata(Element metadata) {
        this.metadata = metadata;
    }

    /** @return a string representation of the object. */
    public String toString() {
        return file.getAbsolutePath();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FileEntry fileEntry = (FileEntry) o;

        if (!uri.equals(fileEntry.uri)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return uri.hashCode();
    }
}
