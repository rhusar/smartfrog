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
 * Provides cross-platform stuff
 * Future versions may do more; this design is there to permit subclasses to do the work.
 * created 27-May-2004 11:41:31
 */

public class PlatformHelper {

    private String fileSeparator;
    private char fileSeparatorChar;
    private String pathSeparator;
    private char pathSeparatorChar;

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


    private static PlatformHelper dosPlatform=new PlatformHelper("\\",'\\',";",';');
    private static PlatformHelper unixPlatform = new PlatformHelper("/", '/', ":", ':');

    /**
     * create a platform helper which is bound to this system
     * @return
     */
    public static PlatformHelper getLocalPlatform() {
        return localPlatform;
    }

    /**
     * get the DOS settings
     * @return
     */
    public static PlatformHelper getDosPlatform() {
        return dosPlatform;
    }

    /**
     * get the unix settings
     * @return
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

    public String getFileSeparator() {
        return fileSeparator;
    }

    public char getFileSeparatorChar() {
        return fileSeparatorChar;
    }

    public String getPathSeparator() {
        return pathSeparator;
    }

    public char getPathSeparatorChar() {
        return pathSeparatorChar;
    }
}
