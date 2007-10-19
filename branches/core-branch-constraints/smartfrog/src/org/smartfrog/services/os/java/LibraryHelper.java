/**
 * 
 */
package org.smartfrog.services.os.java;

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


    /**
     * Create an ivy filename from an artifact
     * @param artifact the artifact to convert
     * @return a filename of the form name-version.extension
     */
    public static String createIvyArtifactFilename(SerializedArtifact artifact) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(artifact.artifact);
        buffer.append(AbstractPolicy.ARTIFACT_SEPARATOR);
        buffer.append(artifact.version);
        buffer.append('.');
        buffer.append(artifact.extension);
        String filename = buffer.toString();
        return filename;
    }
}
