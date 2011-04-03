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

import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * This component aggregates files. Created in 2008, in 2010 it was tweaked
 * to be a workflow component too. 
 */

public class FilesCompoundImpl extends CompoundImpl implements Files {

    private Set<File> fileset = new HashSet<File>();
    private File[] fileArray = new File[]{};
    private Vector<String> fileVector;
    private String filePath;

    public FilesCompoundImpl() throws RemoteException {
    }

    /**
     * Starts the compound. This sends a synchronous sfStart to all managed components in the compound context. Any
     * failure will cause the compound to terminate
     *
     * @throws SmartFrogException failed to start compound
     * @throws RemoteException    In case of Remote/network error
     */
    @Override
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        //start all our children
        super.sfStart();

        //now run through our children and, for all that implement File or Files, add them to our list.
        aggregateChildFiles();
        int filesize = fileset.size();

        fileVector = new Vector<String>(filesize);
        fileArray = new File[filesize];
        int index = 0;
        StringBuilder aggregatePath = new StringBuilder();
        for (File file : fileset) {
            fileArray[index++] = file;
            String path = file.getAbsolutePath();
            fileVector.add(path);
            aggregatePath.append(path);
            aggregatePath.append(File.pathSeparatorChar);
        }
        filePath = aggregatePath.toString();
        sfReplaceAttribute(Files.ATTR_FILE_SET_STRING, filePath);
        sfReplaceAttribute(Files.ATTR_FILELIST, fileVector);
        int count = checkAndUpdateFileCount();
        ComponentHelper helper = new ComponentHelper(this);
        TerminationRecord record = TerminationRecord.normal("File count " + count, sfCompleteName());
        helper.sfSelfDetachAndOrTerminate(record);
    }

    /**
     * go through our children and aggregate their files
     * @throws RemoteException  In case of Remote/network error
     * @throws SmartFrogException if thrown by a child
     */
    private void aggregateChildFiles() throws RemoteException, SmartFrogException {
        for (Prim child : sfChildList()) {
            if (child instanceof FileIntf) {
                addFiles((FileIntf) child);
            } else if (child instanceof Files) {
                addFiles((Files) child);
            } else {
                String path = child.sfResolve(FileIntf.ATTR_ABSOLUTE_PATH, (String) null, false);
                if (path != null) {
                    addFile(path);
                }
            }
        }
    }

    private void addFile(String path) {
        if (path != null) {
            addFile(new File(path));
        }
    }

    private void addFile(File path) {
        fileset.add(path);
    }

    private synchronized void addFiles(Files files) throws SmartFrogException, RemoteException {
        File[] filelist = files.listFiles();
        fileset.addAll(Arrays.asList(filelist));
    }

    private void addFiles(FileIntf fileIntf) throws RemoteException {
        addFile(fileIntf.getAbsolutePath());
    }

    /**
     * Return a list of files that match the current pattern. This may be a compute-intensive operation, so cache the
     * result. Note that filesystem race conditions do not guarantee all the files listed still exist...check before
     * acting
     *
     * @return a list of files that match the pattern, or an empty list for no match
     * @throws RemoteException    when the network plays up
     * @throws SmartFrogException if something else went wrong
     */

    public File[] listFiles() throws RemoteException, SmartFrogException {
        return fileArray;
    }

    /**
     * Get the base directory of these files -will always be null for this class
     *
     * @return the base directory
     * @throws RemoteException    when the network plays up
     * @throws SmartFrogException if something else went wrong
     */
    public File getBaseDir() throws RemoteException, SmartFrogException {
        return null;
    }

    /**
     * check and update file count attributes. Only run this after the filelist has been updated
     *
     * @return the file count
     * @throws SmartFrogRuntimeException for resolution problems
     * @throws RemoteException           network problems
     */
    public int checkAndUpdateFileCount()
            throws SmartFrogRuntimeException, RemoteException {
        int length = fileset.size();
        //deal with the file count
        Prim component = this;
        int filecount = sfResolve(ATTR_FILECOUNT, -1, false);
        int minFilecount = sfResolve(ATTR_MINFILECOUNT, -1, false);
        int maxFilecount = sfResolve(ATTR_MAXFILECOUNT, -1, false);

        if ((filecount >= 0 && length != filecount)
            || (minFilecount >= 0 && length < minFilecount)
            || (maxFilecount >= 0 && length > maxFilecount)) {
            throw new SmartFrogDeploymentException(
                    FilesImpl.ERROR_FILE_COUNT_MISMATCH + filecount + " but found " + length + " files "
                    + "in the list [ " + filePath + "]", this);
        }

        if (filecount < 0) {
            component.sfReplaceAttribute(ATTR_FILECOUNT, length);
        }
        return length;
    }

    /**
     * Get the fileset
     * @return the fileset, this is only valid after the component is started.
     */
    public Set<File> getFileset() {
        return fileset;
    }

    /**
     * Get the list of filenames as strings
     * @return a list of filenames or null if the component is not yet deployed.
     */
    public List<String> getFileList() {
        return fileVector;
    }

    /**
     * Get the string path of the files with the platform separator between them .
     * @return a path like "/bin/file1:/home/user/file2" on Unix, "C:\autoexec.bat;c:\file2.txt" on Windows
     */
    public String getFilePath() {
        return filePath;
    }


}
