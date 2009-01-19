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

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.rmi.RemoteException;

/**
 * An implementation of the files class. Created 04-Feb-2008 14:13:14
 */

public class FilesImpl extends PrimImpl implements Files {

    private Fileset fileset = new Fileset();
    /**
     * Error text {@value}
     */
    public static final String ERROR_NOT_STARTED = "The component must be started before it can list the files";
    private static final Reference FILES_ATTRIBUTE = new Reference(ATTR_FILES);
    private static final Reference DIR_ATTRIBUTE = new Reference(ATTR_DIR);
    private static final Reference PATTERN_ATTRIBUTE = new Reference(ATTR_PATTERN);
    private static final Reference CASE_ATTRIBUTE = new Reference(ATTR_CASESENSITIVE);
    private static final Reference HIDDEN_ATTRIBUTE = new Reference(ATTR_INCLUDEHIDDENFILES);
    public static final String ERROR_FILE_COUNT_MISMATCH = "File count mismatch: expected ";
    public static final String ERROR_NOT_LIVE = "Cannot list files until we are started";


    public FilesImpl() throws RemoteException {
    }

    /**
     * start up by building our list of files
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        fileset = resolveFileset(this);
        checkAndUpdateFileCount(this, fileset);
        //CreateRuntime Attributes
        sfReplaceAttribute(ATTR_FILE_SET, fileset);
        sfReplaceAttribute(ATTR_FILE_SET_STRING, fileset.toString());
        sfReplaceAttribute(ATTR_FILELIST, fileset.toVector());
    }

    /**
     * Return a list of files that match the current pattern. This may be a compute-intensive operation, so cache the
     * result. Note that filesystem race conditions do not guarantee all the files listed still exist...check before
     * acting
     *
     * @return a list of files that match the pattern, or an empty list for no match
     * @throws SmartFrogLifecycleException if it could  not do this
     */

    public File[] listFiles() throws SmartFrogLifecycleException {
        if(!sfIsStarted()) {
            throw new SmartFrogLifecycleException(ERROR_NOT_LIVE);
        }
        return fileset.listFiles();
    }

    /**
     * Get the base directory of these files (may be null)
     *
     * @return the base directory
     * @throws RemoteException    when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public File getBaseDir() throws RemoteException, SmartFrogException {
        return fileset.getBaseDir();
    }


    /**
     * Build a fileset from a ReferenceResolverHelper component with the Files attributes.
     *
     * @param component or  component description to work with
     * @return a newly filled in fileset
     * @throws SmartFrogException if there were problems resolving attributes
     * @throws RemoteException    for networking problems
     */
    public static Fileset resolveFileset(Object component)
            throws SmartFrogException, RemoteException {
        return Fileset.createFileset(component,
                FILES_ATTRIBUTE,
                DIR_ATTRIBUTE,
                PATTERN_ATTRIBUTE,
                CASE_ATTRIBUTE,
                HIDDEN_ATTRIBUTE);
    }

    /**
     * check and update file count attributes
     * @param component component to work on
     * @param fileset the fileset to check
     * @throws SmartFrogRuntimeException for resolution problems
     * @throws RemoteException network problems
     */
    public static void checkAndUpdateFileCount(Prim component, Fileset fileset)
            throws SmartFrogRuntimeException, RemoteException {
        File[] files = fileset.listFiles();
        int length = files.length;
        //deal with the file count
        int filecount = component.sfResolve(ATTR_FILECOUNT, -1, false);
        int minFilecount = component.sfResolve(ATTR_MINFILECOUNT, -1, false);
        int maxFilecount = component.sfResolve(ATTR_MAXFILECOUNT, -1, false);

        if (filecount >= 0 && length != filecount) {
            throw exceptionBadFileCount(component, fileset, length, filecount, " exactly ");
        }
        if (minFilecount >= 0 && length < minFilecount) {
            throw exceptionBadFileCount(component, fileset, length, minFilecount, " a minimum of ");
        }
        if (maxFilecount >= 0 && length > maxFilecount) {
            throw exceptionBadFileCount(component, fileset, length, maxFilecount, " a maximum of ");
        }

        if (filecount < 0) {
            component.sfReplaceAttribute(ATTR_FILECOUNT, length);
        }
    }

    /**
     * Report the wrong file count in a fileset
     * @param component owning component
     * @param fileset fileset
     * @param length actual length
     * @param filecount expected length
     * @param prefix text to include in the error message, such as " exactly ". Include spaces at the front
     * and end if non empty
     * @return an exception to throw
     */
    private static SmartFrogDeploymentException exceptionBadFileCount(Prim component,
                                                                   Fileset fileset,
                                                                   int length,
                                                                   int filecount, String prefix) {

        StringBuilder builder=new StringBuilder();
        builder.append(ERROR_FILE_COUNT_MISMATCH)
                .append(prefix)
                .append(filecount)
                .append(" but found ")
                .append(length)
                .append(" files in the list [")
                .append(fileset == null ? "(null)" : fileset)
                .append(']' + " from ")
                .append(fileset.getSource());
        builder.append('\n');
        File dir = fileset.getBaseDir();
        if(!dir.exists()) {
            builder.append("Base directory ")
                    .append(dir)
                    .append(" does not exist");
        } else {
            File[] files = dir.listFiles();
            builder.append("Base directory ")
                    .append(dir)
                    .append(" contains ")
                    .append(files.length)
                    .append(" files");
        }

        return new SmartFrogDeploymentException(
                builder.toString(),
                component);
    }
}
