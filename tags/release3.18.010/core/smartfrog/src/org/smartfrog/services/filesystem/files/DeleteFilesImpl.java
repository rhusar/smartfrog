/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Delete a set of files
 */
public final class DeleteFilesImpl extends FilesCompoundImpl implements Files {


    public DeleteFilesImpl() throws RemoteException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //now delete the files
        Set<File> files = getFileset();
        int count = execute(files);
        TerminationRecord tr = TerminationRecord.normal("Deleted " + count + " files", sfCompleteName);
        new ComponentHelper(this).targetForWorkflowTermination(tr);
    }

    /**
     * run through the set of files
     * @param files the files to work with
     * @return the number of files processed
     * @throws SmartFrogException SmartFrog problems (or wrapped issues)
     * @throws RemoteException Networking problems.
     */
    private int execute(final Set<File> files)
            throws SmartFrogException, RemoteException {
        int count = 0;
        for (File file : files) {
            if (executeSingleFile(file)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Process a single file
     * @param file filename
     * @return true if it was deleted
     * @throws SmartFrogException SmartFrog problems (or wrapped issues)
     * @throws RemoteException Networking problems.
     */
    private boolean executeSingleFile(final File file)
            throws SmartFrogException, RemoteException {
        if (!file.delete()) {
            String message = "Failed to delete " + file;
            sfLog().info(message);
            return false;
        }
        return true;
    }
}
