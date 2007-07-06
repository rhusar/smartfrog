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
package org.smartfrog.services.os.java;

import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.services.os.download.Download;

import java.rmi.RemoteException;

/**
 * Attributes of a library
 * created 04-Apr-2005 12:16:14
 */


public interface LibraryArtifact extends FileUsingComponent {
    /**
     * {@value}
     */
    String ATTR_LIBRARY = "library";
    /**
     * {@value}
     */
    String ATTR_PROJECT="project";

    /** {@value} */
    String ATTR_ARTIFACT = "artifact";
    /**
     * {@value}
     */
    String ATTR_EXTENSION = "extension";
    /**
     * {@value}
     */
    String ATTR_SHA1 = "sha1";
    /**
     * {@value}
     */
    String ATTR_MD5 = "md5";
    /**
     * {@value}
     */
    String ATTR_VERSION = "version";
    /**
     * {@value}
     */
    String ATTR_SYNCHRONOUS = "synchronousDownload";

    /**
     * Block size {@value}
     */
    String ATTR_BLOCKSIZE = Download.ATTR_BLOCKSIZE;

    /**
     * {@value}
     */
    String ATTR_DOWNLOAD_IF_ABSENT = "downloadIfAbsent";
    /**
     * {@value}
     */
    String ATTR_DOWNLOAD_ALWAYS = "downloadAlways";
    /**
     * {@value}
     */
    String ATTR_FAIL_IF_NOT_PRESENT = "failIfNotPresent";
    
    /**
     * optional post-name classifier
     */
    String ATTR_CLASSIFIER = "classifier";

    /**
     * Attribute to copy a component to. If it is a directory, 
     * then the file gets copied as is. If is not a dir (or doesnt exist)
     * it is taken as a filepath.
     */
    String ATTR_COPYTO = "copyTo";
    
    
    /**
     * Create a serialized artifact to work with 
     * @return a serialized representation of the artifact's state.
     * @throws RemoteException for network problems
     */
    public SerializedArtifact createSerializedArtifact() throws RemoteException;

    
}
