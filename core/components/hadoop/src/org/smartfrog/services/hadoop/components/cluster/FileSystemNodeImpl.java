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

import org.smartfrog.services.filesystem.FileSystem;
import org.smartfrog.services.hadoop.conf.ManagedConfiguration;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.reference.Reference;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Vector;

/**
 *
 */
public class FileSystemNodeImpl extends PrimImpl implements FileSystemNode {

    private ManagedConfiguration configuration;
    protected static final Reference DATA_DIRECTORIES = new Reference(
            ATTR_DATA_DIRECTORIES);
    protected static final Reference NAME_DIRECTORIES = new Reference(
            ATTR_NAME_DIRECTORIES);

    public FileSystemNodeImpl() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        //get the filesystem name, validate it
        String filesystemName = sfResolve(FS_DEFAULT_NAME, "", true);
        try {
            URI uri=new URI(filesystemName);
            if(uri.getPort()==-1) {
                throw new SmartFrogLifecycleException("Undefined port on " + FS_DEFAULT_NAME + " value :" + filesystemName);
            }
        } catch (URISyntaxException e) {
            throw new SmartFrogLifecycleException("Bad "+ FS_DEFAULT_NAME+ " value :"+filesystemName,e);
        }


        configuration = new ManagedConfiguration(this);
        if(sfLog().isDebugEnabled()) {
            sfLog().debug(configuration.dumpQuietly());
        }
    }

    public ManagedConfiguration createConfiguration() {
        return new ManagedConfiguration(this);
    }

    /**
     * Run through the directories, create all that are there
     * @param dirs list of directories
     * @param createDirs create the directories?
     * @return the directories all converted to a list split by commas
     */
    protected String createDirectoryList(Vector<String> dirs,boolean createDirs) {
        StringBuilder path = new StringBuilder();
        for (String dir : dirs) {
            File directory = new File(dir);
            if (createDirs) {
                directory.mkdirs();
            }
            if (path.length() > 0) {
                path.append(',');
            }
            path.append(directory.getAbsolutePath());
        }
        String value = path.toString();
        return value;
    }

    /**
     * Go from a list of paths/fileIntfs to a comma separated list, create
     * directories on demand
     * @param sourceRef source reference
     * @param replaceAttribute attribute to replace
     * @return the directories
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    protected Vector<String> createDirectoryListAttribute(Reference sourceRef,
                                                String replaceAttribute)
            throws RemoteException, SmartFrogException {
        Vector<String> dirs;
        dirs= FileSystem.resolveFileList(this, sourceRef,null,true,null);
        String value = createDirectoryList(dirs,true);
        sfReplaceAttribute(replaceAttribute, value);
        return dirs;
    }
}
