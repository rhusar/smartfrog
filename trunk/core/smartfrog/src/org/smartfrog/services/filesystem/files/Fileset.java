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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;

import java.io.Serializable;
import java.io.File;
import java.rmi.RemoteException;

/**
 * This is conceptually similar to an Ant FileSet, but architecturally very
 * different. It represents a set of files with a (possibly null) base directory.
 * It is marked as seriali
 *
 */
public class Fileset implements Serializable {

    /**
     * base directory, can be null
     */
    public File baseDir;
    /**
     * list of files
     */
    public File[] files=new File[0];

    /**
     * Filter -may be null.
     */
    public FilenamePatternFilter filter;

    /**
     * Simple constructor
     */
    public Fileset() {
    }

    /**
     * Create a fileset from a filter. This can calculate the fileset on demand
     * @param baseDir directory to work from
     * @param filter filter to use
     */
    public Fileset(File baseDir, FilenamePatternFilter filter) {
        this.baseDir = baseDir;
        this.filter = filter;
    }

    /**
     * create a static fileset
     * @param baseDir base directory, can be null
     * @param files list of files -should be an empty list if there are no files
     */
    public Fileset(File baseDir, File[] files) {
        this.baseDir = baseDir;
        this.files = files;
    }

    /**
     * Construct a filset from a files instance (Which may be remote)
     * @param files the component that knows about the files
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public Fileset(Files files) throws SmartFrogException, RemoteException {
        this.baseDir=files.getBaseDir();
        this.files=files.listFiles();
    }

    /**
     * Return a list of files that match the current pattern. This may be a
     * compute-intensive operation, so cache the result. Note that filesystem
     * race conditions do not guarantee all the files listed still exist...check
     * before acting
     *
     * @return a list of files that match the pattern, or an empty list for no
     *         match
     */

    public File[] listFiles() {

        return filter!=null?files=baseDir.listFiles(filter):files;
    }

    /**
     * Look for a fileset from the various attributes
     *
     * @param component component to work from
     * @param filesAttribute name of the files attribute
     * @param dirAttribute name of the dir attribute
     * @param patternAttribute name of the pattern attribute
     * @param caseAttribute name of the case attribute
     * @param hiddenAttribute name of the hidden attribute
     *
     * @return a fileset built from the attributes
     *
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public static Fileset createFileset(Prim component,
                                        Reference filesAttribute,
                                        Reference dirAttribute,
                                        Reference patternAttribute,
                                        Reference caseAttribute,
                                        Reference hiddenAttribute)
            throws
            SmartFrogException,
            RemoteException {
        Files files = null;
        files = (Files) component.sfResolve(filesAttribute, (Prim) null, false);
        if (files != null) {
            return new Fileset(files);
        } else {
            File baseDir = FileSystem.lookupAbsoluteFile(component,
                    dirAttribute,
                    null,
                    null,
                    true,
                    null);
            //no files, so resolve everything else
            String pattern = component.sfResolve(patternAttribute, "", true);
            boolean caseSensitive = component.sfResolve(caseAttribute, true, true);
            boolean includeHiddenFiles = component.sfResolve(
                    hiddenAttribute,
                    true,
                    true);
            FilenamePatternFilter filter = new FilenamePatternFilter(
                    pattern,
                    includeHiddenFiles,
                    caseSensitive);
            return new Fileset(baseDir, filter);
        }
    }

    /**
     * Returns a list of files in String format using the platform file separator.
     * @return
     */
    public String toString() {
          String fileSetString = java.util.Arrays.toString(listFiles());
          fileSetString = fileSetString.substring(1,fileSetString.length()-1);
          fileSetString = fileSetString.replace(", ",System.getProperty("path.separator"));
          return (fileSetString);
    }
}
