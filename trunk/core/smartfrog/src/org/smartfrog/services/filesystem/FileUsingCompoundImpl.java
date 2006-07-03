/**
 * (C) Copyright 2005 Hewlett-Packard Development Company, LP This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA For
 * more information: www.smartfrog.org
 */
package org.smartfrog.services.filesystem;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.compound.CompoundImpl;

/**
 * This is a compound object that has the {@link FileUsingComponent} interfaces
 * and attributes.
 *
 * This is essentially just a cut and paste of {@link FileUsingComponentImpl};
 * it has been reparented to a compound component. Please keep the two
 * files synchronised.
 */
public class FileUsingCompoundImpl extends CompoundImpl implements
        FileUsingComponent,FileIntf {

    /**
     * the file we are bonded to
     */
    private File file;

    /**
     * Constructor.
     * @throws RemoteException In case of network/rmi error
     */
    public FileUsingCompoundImpl() throws RemoteException {
    }

    /**
     * get the absolute path of this file
     *
     * @return path
     */
    public String getAbsolutePath()  {
        return file.getAbsolutePath();
    }

    /**
     * get the URI of this file
     *
     * @return URI
     */
    public URI getURI()  {
        return file.toURI();
    }

    /**
     * get the file we are using
     * @return File
     */
    public File getFile() {
        return file;
    }


    /**
     * Bind the class to the filename; indicate in the operation whether to
     * dema
     * @param mandatory flag to indicate mandatoryness
     * @param defval a default value to use if not mandatory (can be null)
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime error
     */
    protected void bind(boolean mandatory,String defval) throws RemoteException, SmartFrogRuntimeException {
        String absolutePath=FileSystem.lookupAbsolutePath(this,ATTR_FILENAME, defval,null, mandatory,null);
        if(absolutePath!=null) {
            setAbsolutePath(absolutePath);
        }
    }

    /**
     * creates the file object instance, to the absolute path,
     * then sets the attribute {@value FileIntf#ATTR_ABSOLUTE_PATH}
     * to the absolute path, and {@value FileUsingComponent#ATTR_URI}
     * to the URI. From here on, {@link #getFile()} is valid.
     * @param absolutePath
     * @throws SmartFrogRuntimeException  runtime error
     * @throws RemoteException In case of network/rmi error
     */
    protected void setAbsolutePath(String absolutePath)
            throws SmartFrogRuntimeException, RemoteException {
        File newfile=new File(absolutePath);
        bind(newfile);

    }

    /**
     * Bind to a new file. sets the {@link #ATTR_ABSOLUTE_PATH} and {@link #ATTR_URI}
     * attributes. It also saves the file to the {@link #file} attribute.
     *
     * @param newfile file to bind to to
     * @throws SmartFrogRuntimeException runtime error
     * @throws RemoteException In case of network/rmi error
     */
    public void bind(File newfile) throws SmartFrogRuntimeException,
            RemoteException {
        file=newfile;
        FileUsingComponentImpl.bind(this,newfile);
    }





}
