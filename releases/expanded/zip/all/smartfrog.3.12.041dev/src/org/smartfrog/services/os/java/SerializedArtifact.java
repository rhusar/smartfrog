/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */

package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

import java.io.Serializable;

/**
 * Wire representation of an artifact. This is one way only: there is no way to create a 
 * {@link LibraryArtifact} from this, but we can create one with
 * {@link LibraryArtifact#createSerializedArtifact()}
 */

public class SerializedArtifact implements Serializable {

    public static final String ERROR_NO_LIBRARY_VERSION = "No library version: ";

    public static final String ERROR_INVALID_LIBRARY = "Invalid library: ";

    public static final String ERROR_NULL_ARTIFACT = "No library";

    /**
     * Project name
     */
    public String project;

    /**
     * Artifact. Required
     */
    public String artifact;

    /**
     * optional extension
     */
    public String extension;

    /**
     * optional classifier
     */
    public String classifier;

    /**
     * Version. Required by some policies
     */
    public String version;

    /**
     * SHA1 checksum
     */
    public String sha1;

    /**
     * MD5 checksum
     */
    public String md5;

    /**
     * Test for being valid. Looks at project and artifact only
     *
     * @return true if we are valid
     */
    public boolean isValid() {
        return project != null && project.length() > 0
                && artifact != null && artifact.length() > 0;
    }

    /**
     * Stringify, for debugging
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return "Serialized Library " +
                "project:" + project
                + " artifact:" + artifact
                + " version:" + version
                + " classifer:" + classifier
                + " extension:" + extension;
    }

    /**
     * validity logic
     *
     * @param library       library to check
     * @param versionNeeded is the version attribute needed
     *
     * @throws SmartFrogRuntimeException if the library is null or invalid, or,
     *                                   if versionNeeded set, if there is no
     *                                   version
     */
    public static void assertValid(SerializedArtifact library,
                                   boolean versionNeeded)
            throws SmartFrogRuntimeException {
        if (library == null) {
            throw new SmartFrogRuntimeException(ERROR_NULL_ARTIFACT);
        }
        if (!library.isValid()) {
            throw new SmartFrogRuntimeException(ERROR_INVALID_LIBRARY + library);
        }
        if (versionNeeded && library.version == null) {
            throw new SmartFrogRuntimeException(ERROR_NO_LIBRARY_VERSION + library);
        }
    }


}
