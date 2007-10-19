/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.utils;

import java.io.File;

/**
 * Repository of file and path values for the local and different platforms.
 * Future versions may do more; this design is there to permit subclasses to do the work.
 * created 27-May-2004 11:41:31
 */

public class PlatformHelper {

    private String fileSeparator;
    private char fileSeparatorChar;
    private String pathSeparator;
    private char pathSeparatorChar;

    /**
     * construct a platform helper bound to a set of settings
     * @param fileSeparator file separator
     * @param fileSeparatorChar file separator character
     * @param pathSeparator  path separator
     * @param pathSeparatorChar path separator character
     */
    protected PlatformHelper(String fileSeparator, char fileSeparatorChar, String pathSeparator, char pathSeparatorChar) {
        this.fileSeparator = fileSeparator;
        this.fileSeparatorChar = fileSeparatorChar;
        this.pathSeparator = pathSeparator;
        this.pathSeparatorChar = pathSeparatorChar;
    }

    /**
     * cache of the local one
     */
    private static PlatformHelper localPlatform= new PlatformHelper(File.separator, File.separatorChar,
            File.pathSeparator, File.pathSeparatorChar);


    /**
     * Windows
     */
    private static PlatformHelper dosPlatform=new PlatformHelper("\\",'\\',";",';');

    /**
     * Unix
     */
    private static PlatformHelper unixPlatform = new PlatformHelper("/", '/', ":", ':');

    /**
     * create a platform helper which is bound to this system
     * @return the local platform helper
     */
    public static PlatformHelper getLocalPlatform() {
        return localPlatform;
    }

    /**
     * get the DOS settings
     * @return the DOS platform helper
     */
    public static PlatformHelper getDosPlatform() {
        return dosPlatform;
    }

    /**
     * get the unix settings
     * @return the unix platform helper
     */
    public static PlatformHelper getUnixPlatform() {
        return unixPlatform;
    }

    /**
     * convert the filename by happily converting all forward or back slashes
     * into the appropriate one for the string. Always creates a new string, even
     * if if the original is unchanged.
     * @param filename filename -can be null
     * @return a converted filename or null if the input was null
     */
    public String convertFilename(String filename) {
        if(filename==null) {
            return null;
        }
        int len = filename.length();
        StringBuffer buffer=new StringBuffer(len);
        for(int i=0;i<len;i++) {
            char c=filename.charAt(i);
            if(c=='/') {
                c= fileSeparatorChar;
            } else if (c=='\\') {
                c = fileSeparatorChar;
            }
            buffer.append(c);
        }
        return new String(buffer);
    }

    /**
     * get the file separator
     * @return current file separator
     */
    public String getFileSeparator() {
        return fileSeparator;
    }

    /**
     * get the file separator character
     * @return the file separator
     */
    public char getFileSeparatorChar() {
        return fileSeparatorChar;
    }

    /**
     * get the path separator character
     * @return the path separator
     */
    public String getPathSeparator() {
        return pathSeparator;
    }

    /**
     * get the path separator character
     * @return the path separator
     */
    public char getPathSeparatorChar() {
        return pathSeparatorChar;
    }


    /**
     * equality test
     * @param that the object to test against
     * @return true iff there is  match
     */
    public boolean equals(Object that) {
        if ( this == that ) {
            return true;
        }
        if ( !(that instanceof PlatformHelper) ) {
            return false;
        }

        final PlatformHelper platformHelper = (PlatformHelper) that;

        if ( fileSeparatorChar != platformHelper.fileSeparatorChar ) {
            return false;
        }
        if ( pathSeparatorChar != platformHelper.pathSeparatorChar ) {
            return false;
        }
        if ( !fileSeparator.equals(platformHelper.fileSeparator) ) {
            return false;
        }
        if ( !pathSeparator.equals(platformHelper.pathSeparator) ) {
            return false;
        }

        return true;
    }

    /**
     * hash code is based on the file and path separators
     * @return a hash code
     */
    public int hashCode() {
        int result;
        result = fileSeparator.hashCode();
        result = 29 * result + (int) fileSeparatorChar;
        result = 29 * result + pathSeparator.hashCode();
        result = 29 * result + (int) pathSeparatorChar;
        return result;
    }
}
