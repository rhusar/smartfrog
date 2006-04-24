/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.rmi.RemoteException;

/**
 * An extension of mkdir
 * created 24-Apr-2006 16:06:06
 */

public class TempDirImpl extends FileUsingComponentImpl implements TempFile {
    private boolean delete=false;

    public TempDirImpl() throws RemoteException {
    }

    /**
     * we only create the directory at startup time, even though we bond at deploy time
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *
     * @throws java.rmi.RemoteException
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        String prefix = sfResolve(TempFile.ATTR_PREFIX, "", true);
        if (prefix.length() == 0) {
            throw new SmartFrogException(TempFileImpl.ERROR_PREFIX_EMPTY, this);
        }
        String suffix = sfResolve(TempFile.ATTR_SUFFIX, (String) null, false);
        String dir;
        dir = FileSystem.lookupAbsolutePath(this, TempFile.ATTR_DIRECTORY, null, null, false, null);


        if (sfLog().isDebugEnabled()) {
            sfLog().debug("Creating temp file in dir [" + dir + "], prefix=" + prefix
                    + ", suffix=" + suffix );
        }

        //bind to the temp file
        bind(FileSystem.createTempDir(prefix, suffix, dir));
        sfReplaceAttribute(FileUsingComponent.ATTR_FILENAME, file.toString());

        //get the delete flag
        //this is only done after a successful creation of a temp dir; if it failed, then
        //delete is implicitly false. This stops us from trying to delete a directory that already existed.
        delete = sfResolve(ATTR_DELETE_ON_EXIT, false, false);
    }

    /**
     * At terminate time, trigger a recursive delete of the directory.
     * @param status
     */

    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (delete) {
            FileSystem.recursiveDelete(getFile());
        }
    }


    /**
     * get the filename of this file
     *
     * @return
     */
    public String getFilename() {
        return getFile().toString();
    }
}
