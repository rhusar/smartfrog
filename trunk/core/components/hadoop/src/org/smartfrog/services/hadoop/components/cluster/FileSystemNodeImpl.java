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


package org.smartfrog.services.hadoop.components.cluster;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.services.filesystem.FileSystem;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * This is a base class for any component that is bonded to a Hadoop filesystem
 */
public class FileSystemNodeImpl extends HadoopServiceImpl implements FileSystemNode {


    public static final Reference DATA_DIRECTORIES = new Reference(
            ATTR_DATA_DIRECTORIES);
    public static final Reference NAME_DIRECTORIES = new Reference(
            ATTR_NAME_DIRECTORIES);
    public static final Reference TEST_MODE_DELETE_DIRECTORIES = new Reference(ATTR_TEST_MODE_DELETE_DIRECTORIES);
    protected boolean testModeDeleteDirectories = false;

    protected List<File> directoriesToDelete = new ArrayList<File>();

    public FileSystemNodeImpl() throws RemoteException {
    }

    /**
     * Can be called to start components.
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    @Override
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        //get the filesystem name, validate it
        String filesystemName = sfResolve(FS_DEFAULT_NAME, "", true);
        try {
            URI uri = new URI(filesystemName);
            if (uri.getPort() == -1) {
                throw new SmartFrogLifecycleException(
                        "Undefined port on " + FS_DEFAULT_NAME + " value :" + filesystemName);
            }
        } catch (URISyntaxException e) {
            throw new SmartFrogLifecycleException("Bad " + FS_DEFAULT_NAME + " value :" + filesystemName, e);
        }

        testModeDeleteDirectories = sfResolve(TEST_MODE_DELETE_DIRECTORIES,false,true);
        dumpConfiguration();
    }

    /**
     * List a directory to delete
     * @param directory directory to delete
     */
    protected void addDirectoryToDelete(File directory) {
        directoriesToDelete.add(directory);
    }

    /**
     * Add a list of directories to delete
     * @param dirList the directory list
     */
    protected void addDirectoriesToDelete(List<String> dirList) {
        for (String dir : dirList) {
            addDirectoryToDelete(new File(dir));
        }
    }


    /**
     * during cleanup: delete the directories if {@link #testModeDeleteDirectories} is true
     */
    @Override
    protected void postTerminationCleanup() {
        if (testModeDeleteDirectories) {
            for (File dir:directoriesToDelete) {
                FileSystem.recursiveDelete(dir);
            }
        }
    }

}
