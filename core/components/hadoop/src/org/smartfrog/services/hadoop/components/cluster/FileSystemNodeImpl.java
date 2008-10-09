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

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

/**
 * This is a base class for any component that is bonded to a Hadoop filesystem
 */
public class FileSystemNodeImpl extends HadoopServiceImpl implements FileSystemNode {


    public static final Reference DATA_DIRECTORIES = new Reference(
            ATTR_DATA_DIRECTORIES);
    public static final Reference NAME_DIRECTORIES = new Reference(
            ATTR_NAME_DIRECTORIES);

    public FileSystemNodeImpl() throws RemoteException {
    }

    /**
     * Can be called to start components.
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
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

        dumpConfiguration();
    }

}
