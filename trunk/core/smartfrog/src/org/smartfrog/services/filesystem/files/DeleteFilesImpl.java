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

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Delete a set of files
 */
public class DeleteFilesImpl extends FilesCompoundImpl implements Files {

    public static final String ATTR_CONTINUE_AFTER_DELETE_FAILURES = "continueAfterDeleteFailures";

    public DeleteFilesImpl() throws RemoteException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        //now delete the files
        Set<File> files = getFileset();
        execute(files, sfResolve(ATTR_CONTINUE_AFTER_DELETE_FAILURES, true, true));
    }

    private void execute(Set<File> files, boolean continueAfterFailures)
            throws SmartFrogException, RemoteException {
        for (File file : files) {
            executeSingleFile(file, continueAfterFailures);
        }
    }

    private void executeSingleFile(final File file, final boolean continueAfterFailures)
            throws SmartFrogException, RemoteException {
        if (!file.delete()) {
            String message = "Failed to delete " + file;
            if (continueAfterFailures) {
                sfLog().info(message);
            } else {
                throw new SmartFrogDeploymentException(message);
            }
        }
    }
}
