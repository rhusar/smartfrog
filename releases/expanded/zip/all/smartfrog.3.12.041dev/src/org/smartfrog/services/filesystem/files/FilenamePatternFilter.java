/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem.files;


import org.smartfrog.sfcore.common.SmartFrogDeploymentException;

import java.io.FilenameFilter;
import java.io.File;
import java.io.Serializable;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

/**
 * This is a
 * Created 04-Feb-2008 13:47:35
 *
 */

public class FilenamePatternFilter implements FilenameFilter, Serializable {


    private String pattern;
    private Pattern mask;

    private boolean caseSensitive=false;
    private boolean hiddenFiles = false;
    public static final String BAD_PATTERN = "Pattern syntax not understood by the Java Pattern class"
    +"\nConsult http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html";

    /**
     * Create a new filter. Consult {@link Pattern#compile(String)} for the syntax.
     * @param pattern pattern to parse
     * @param hiddenFiles flag to ask for hidden files too
     * @param caseSensitive clear this bit to do case-insensitive (unicode and ASCII) pattern matching
     * @throws SmartFrogDeploymentException if the pattern cannot be parsed.
     */
    public FilenamePatternFilter(String pattern, boolean hiddenFiles, boolean caseSensitive) throws SmartFrogDeploymentException {
        this.pattern = pattern;
        try {
            int flags=0;
            if(!caseSensitive) {
                flags|=Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE;
            }
            mask= Pattern.compile(pattern,flags);
        } catch (PatternSyntaxException e) {
            throw new SmartFrogDeploymentException(BAD_PATTERN,e);
        }
        this.hiddenFiles = hiddenFiles;
        this.caseSensitive = caseSensitive;
    }

    /**
     * Tests if a specified file should be included in a file list.
     *
     * @param dir  the directory in which the file was found.
     * @param name the name of the file.
     * @return <code>true</code> if and only if the name should be included in the file list; <code>false</code>
     *         otherwise.
     */
    public boolean accept(File dir, String name) {
        File targetFile=new File(dir,name);
        if(!hiddenFiles && targetFile.isHidden()) {
            //finish early on hidden files, as the cost of checking is less
            return false;
        }
        //try to match the pattern
        Matcher matcher = mask.matcher(name);
        return matcher.matches();
    }

    /**
     * Returns the pattern we are maching on
     * @return a string representation of the object.
     */
    public String toString() {
        return "matching on "+pattern + " caseSensitive="+caseSensitive+" hiddenFiles="+hiddenFiles;
    }
}
