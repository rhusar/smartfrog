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
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * An extension of mkdir
 * created 24-Apr-2006 16:06:06
 */

public class TempDirImpl extends FileUsingComponentImpl implements TempFile {
    private boolean delete=false;
    private boolean createOnDeploy;

    /**
     * Constructor.
     * @throws RemoteException  In case of network/rmi error
     */
    public TempDirImpl() throws RemoteException {
    }


    /**
     * Called after instantiation for deployment purposes. Heart monitor is
     * started and if there is a parent the deployed component is added to the
     * heartbeat. Subclasses can override to provide additional deployment
     * behavior.
     *
     * @throws SmartFrogException
     *                                  error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        createOnDeploy = sfResolve(ATTR_CREATE_ON_DEPLOY, createOnDeploy, true);
        if (createOnDeploy) {
            readAttributesAndCreateDir();
        }
    }

    /**
     * start the component
     *
     * @throws SmartFrogException error wile starting
     *
     * @throws RemoteException  In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        if (!createOnDeploy) {
            readAttributesAndCreateDir();
        }
        //maybe terminate
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "Created temp dir " + file,
                null,
                null);
    }

    /**
     * create the temporary directory
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogException error while reading attributes
     */
    private void readAttributesAndCreateDir() throws RemoteException, SmartFrogException {
        final String prefix = sfResolve(TempFile.ATTR_PREFIX, "", true);
        if (prefix.length() == 0) {
            throw new SmartFrogException(TempFileImpl.ERROR_PREFIX_EMPTY, this);
        }
        final String suffix = sfResolve(TempFile.ATTR_SUFFIX, (String) null, false);
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
     * @param status TerminationRecord object
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
     * @return String filename
     */
    public String getFilename() {
        return getFile().toString();
    }
}
