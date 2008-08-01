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

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.Arrays;

/**
 * This is conceptually similar to an Ant FileSet, but architecturally very
 * different. It represents a set of files with a (possibly null) base directory.
 * It is marked as serialisable... the list of files will get sent over.
 *
 */
public class Fileset implements Serializable {


    /**
     * base directory, can be null
     */
    private File baseDir;
    /**
     * list of files
     */
    private File[] files= EMPTY_FILES;

    /**
     * Filter -may be null.
     */
    private FilenamePatternFilter filter;
    /**
     * An empty set of files
     */
    private static final File[] EMPTY_FILES = {};

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
        setFiles(files);
    }

    /**
     * Construct a fileset from a files instance (Which may be remote)
     * @param files the component that knows about the files
     * @throws RemoteException when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public Fileset(Files files) throws SmartFrogException, RemoteException {
        baseDir=files.getBaseDir();
        setFiles(files.listFiles());
    }

    /**
     * Set the files attribute and declare us as built.
     * @param files
     */
    private void setFiles(File[] files) {
        this.files = files;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public FilenamePatternFilter getFilter() {
        return filter;
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
        //return any cached value
        if (files != null) {
            return files;
        }

        //no value? Maybe we should build it
        if (filter != null) {
            recalculate();
            return files;
        } else {
            //no way to bind a file list, so say there is nothing
            return EMPTY_FILES;
        }
    }

    /**
     * Recalculate the fileset and update our cache.
     * Has no effect if the component is caching someone else's value
     */
    public void recalculate() {
        if (filter != null) {
            setFiles(baseDir.listFiles(filter));
        }
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
    public static Fileset createFileset(Object component,
                                        String filesAttribute,
                                        String dirAttribute,
                                        String patternAttribute,
                                        String caseAttribute,
                                        String hiddenAttribute)
            throws  SmartFrogException, RemoteException {

           return createFileset(component,
                                Reference.fromString(filesAttribute),
                                Reference.fromString(dirAttribute),
                                Reference.fromString(patternAttribute),
                                Reference.fromString(caseAttribute),
                                Reference.fromString(hiddenAttribute));
       }

    /**
     * Look for a fileset from the various attributes
     *
     * @param component or component description to work from
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
    public static Fileset createFileset(Object component,
                                        Reference filesAttribute,
                                        Reference dirAttribute,
                                        Reference patternAttribute,
                                        Reference caseAttribute,
                                        Reference hiddenAttribute)
            throws SmartFrogException, RemoteException {

        Files files = null;
        ComponentDescription cd=null;
        Prim prim=null;
        if (component instanceof Prim) {
            prim = (Prim) component;
            files = (Files) prim.sfResolve(filesAttribute, (Prim) null, false);
        } else if (component instanceof ComponentDescription) {
            cd = (ComponentDescription) component;
            files = (Files) cd.sfResolve(filesAttribute, (Prim) null, false);
        } else {
            throw  new SmartFrogResolutionException("Wrong object type. It does not implement Resolve() interfaces: "
                            +component.getClass().getName());
        }


        if (files != null) {
            return new Fileset(files);
        } else {
            File baseDir = FileSystem.lookupAbsoluteFile(component, dirAttribute, null, null, true, null);
            //no files, so resolve everything else
            String pattern;
            boolean caseSensitive;
            boolean includeHiddenFiles;
            if (component instanceof Prim) {
                pattern = prim.sfResolve(patternAttribute, "", true);
                caseSensitive = prim.sfResolve(caseAttribute, true, true);
                includeHiddenFiles = prim.sfResolve(hiddenAttribute, true, true);
            }  else  {
                pattern = cd.sfResolve(patternAttribute, "", true);
                caseSensitive = cd.sfResolve(caseAttribute, true, true);
                includeHiddenFiles = cd.sfResolve(hiddenAttribute, true, true);
            }

            FilenamePatternFilter filter = new FilenamePatternFilter(pattern, includeHiddenFiles, caseSensitive);
            return new Fileset(baseDir, filter);
        }
    }

    

    /**
     * Returns a list of files in String format using the platform file separator.
     * @return String list of files separated by the platform's path separator.
     */
    public String toString() {
          String fileSetString = Arrays.toString(listFiles());
          fileSetString = fileSetString.substring(1,fileSetString.length()-1);
          fileSetString = fileSetString.replace(", ",System.getProperty("path.separator"));
          return (fileSetString);
    }


    public Vector<File> toVector() {
        return new Vector<File>(Arrays.asList(listFiles()));
    }
}
