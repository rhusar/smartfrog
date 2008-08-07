/*
 * (C) Copyright 2005-2008 Hewlett-Packard Development Company, LP
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For more information: www.smartfrog.org
 */
package org.smartfrog.services.os.java;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;

/**
 * @author slo
 *
 */
public final class LibraryHelper {




    private LibraryHelper() {

    }

    /**
     * get a string value of a digest as a hex list, two characters per byte.
     * There would seem to be a more efficient implementation of this involving
     * a 256 byte memory buffer.
     *
     * @param digest checksum
     *
     * @return hex equivalent
     */
    public static String digestToString(byte[] digest) {
        int length = digest.length;
        StringBuffer buffer = new StringBuffer(length * 2);
        for (int i = 0; i < length; i++) {
            String ff = Integer.toHexString(digest[i] & 0xff);
            if (ff.length() < 2) {
                buffer.append('0');
            }
            buffer.append(ff);
        }
        return buffer.toString();
    }

    /**
     * Convert a dotted project name into a forward slashed project name. This
     * is done in preparation for Maven2 repositories, which will have more
     * depth to their classes. NB: only public for testing. This is not a public
     * API.
     *
     * @param projectName the project name
     *
     * @return a string whch may or may not match the old string.
     */
    public static String patchProject(String projectName) {
        //break out early if no match; create no new object
        if (projectName.indexOf('.') < 0) {
            return projectName;
        }
        //create a new buffer, patch it
        int len = projectName.length();
        StringBuffer patched = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            char c = projectName.charAt(i);
            if (c == '.') {
                c = '/';
            }
            patched.append(c);
        }
        return patched.toString();
    }

    /** what artifacts are separated by {@value} */
    public static final String ARTIFACT_SEPARATOR = "-";

    
    /**
     * test that a string is not empty
     *
     * @param s
     * @return true iff the string is non null, and not ""
     */
    public static boolean nonEmpty(String s) {
        return s != null && s.length() > 0;
    }
    /**
     * Create an artifact filename. This claims to be maven1, but really it is also maven2, as if there is a classifier,
     * we use that as well. There just near zero chance of that finding a match against the classic M1 repository.
     *
     * @param library
     * @return the filename of an artifact using maven separation rules
     * @throws SmartFrogRuntimeException on validity errors
     */
    public static String createMavenArtifactName(SerializedArtifact library) throws SmartFrogRuntimeException {
        SerializedArtifact.assertValid(library, false);
        StringBuffer buffer = new StringBuffer();
        buffer.append(library.artifact);
        if (nonEmpty(library.version)) {
            buffer.append(ARTIFACT_SEPARATOR);
            buffer.append(library.version);
        }
        if (nonEmpty(library.classifier)) {
            buffer.append(ARTIFACT_SEPARATOR);
            buffer.append(library.classifier);
        }
        if (nonEmpty(library.extension)) {
            buffer.append('.');
            buffer.append(library.extension);
        }
        return buffer.toString();
    }
    
    /**
     * Create an ivy artifact
     * @param artifact the artifact
     * @param versioned a flag to set to being true if the artifact should be versioned
     * @return the filename
     */
    public static String createIvyArtifactFilename(SerializedArtifact artifact,
                                boolean versioned) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(artifact.artifact);
        if (nonEmpty(artifact.classifier)) {
            buffer.append(AbstractPolicy.ARTIFACT_SEPARATOR);
            buffer.append(artifact.classifier);
        }
        if (versioned) {
            buffer.append(AbstractPolicy.ARTIFACT_SEPARATOR);
            buffer.append(artifact.version);
        }
        buffer.append('.');
        buffer.append(artifact.extension);
        String filename = buffer.toString();
        return filename;
    }

    /**
     * Create an ivy filename from an artifact. This is the layout in the cache
     * @param artifact the artifact to convert
     * @return a filename of the form name-version.extension
     */
    public static String createIvyArtifactCacheFilename(SerializedArtifact artifact) {
        return createIvyArtifactFilename(artifact,true);
    }
    
    /**
     * Create an ivy published filename from an artifact. 
     * This does not include the version marker, as that is expected to be encoded
     * in a parent directory.
     * @param artifact the artifact to convert
     * @return a filename of the form name-version.extension
     */
    public static String createIvyArtifactPublishedFilename(SerializedArtifact artifact) {
        return createIvyArtifactFilename(artifact,false);
    }    
    
    /**
     * Conver the project to the ivy format. 
     * This currently returns the project, as is.
     *
     * @param artifact the artifact to convert
     * @return the project of an artifact
     */
    public static String convertProjectToIvyFormat(SerializedArtifact artifact) {
        return artifact.project;
    }

}
