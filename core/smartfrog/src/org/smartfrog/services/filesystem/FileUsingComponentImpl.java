/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;

/**
 * This is a class of limited usefulness. It exists to be subclassed.
 * Why is it not abstract? For easier testing
 * created 30-Mar-2005 16:50:04
 */

public class FileUsingComponentImpl extends PrimImpl implements FileUsingComponent, FileIntf {
    /**
     * the file we are bonded to
     */
    private File file;

    public FileUsingComponentImpl() throws RemoteException {
    }

    /**
     * get the absolute path of this file
     *
     * @return
     */
    public String getAbsolutePath()  {
        return file.getAbsolutePath();
    }

    /**
     * get the URI of this file
     *
     * @return
     */
    public URI getURI()  {
        return file.toURI();
    }

    /**
     * get the file we are using
     * @return
     */
    public File getFile() {
        return file;
    }


    /**
     * Bind the class to the filename; indicate in the operation whether to
     * dema
     * @param mandatory flag to indicate mandatoryness
     * @param defval a default value to use if not mandatory (can be null)
     * @throws RemoteException
     * @throws SmartFrogRuntimeException
     */
    protected void bind(boolean mandatory,String defval) throws RemoteException, SmartFrogRuntimeException {
        String absolutePath=FileImpl.lookupAbsolutePath(this,ATTR_FILE, defval,null, mandatory,null);
        if(absolutePath!=null) {
            setAbsolutePath(absolutePath);
        }
    }

    /**
     * creates the file object instance, to the absolute path,
     * then sets the attribute {@value FileIntf#varAbsolutePath}
     * to the absolute path, and {@value FileUsingComponent#ATTR_URI}
     * to the URI. From here on, {@link #getFile()} is valid.
     * @param absolutePath
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    protected void setAbsolutePath(String absolutePath)
            throws SmartFrogRuntimeException, RemoteException {
        File newfile=new File(absolutePath);
        bind(newfile);

    }

    /**
     * Bind to a new file.
     * sets the
     * @param newfile
     * @throws SmartFrogRuntimeException
     * @throws RemoteException
     */
    public void bind(File newfile) throws SmartFrogRuntimeException,
            RemoteException {
        file=newfile;
        sfReplaceAttribute(ATTR_ABSOLUTE_PATH, file.getAbsolutePath());
        String uri;
        uri = file.toURI().toString();
        sfReplaceAttribute(FileUsingComponent.ATTR_URI, uri);
    }

    /**
     * Returns the name of the file we are bound to.
     *
     * @return string form for this component
     */
    public String toString() {
        return file!=null?file.getAbsolutePath() : "uninitialized file";
    }

    /**
     * Helper method for all components that support delete-on-clearup;
     * This should be called from the {@link Prim#sfTerminatedWith(TerminationRecord, Prim)}
     * implementation -after calling the superclass.
     * Will delete the file if {@link FileIntf#ATTR_DELETE_ON_EXIT } is set to
     * true, and there is a file to delete. If the file cannot be deleted immediately,
     * it will be set for a deletion on termination.
     * This is not (currently) supported for directories.
     */
    protected void deleteFileIfNeeded() {
        try {
            boolean delete;
            //see if anyone changed the delete settings during our life
            delete = sfResolve(ATTR_DELETE_ON_EXIT,false,false);
            if (delete && getFile() != null && getFile().exists()) {
                if (!getFile().delete()) {
                    getFile().deleteOnExit();
                }
            }
        } catch (Exception e) {
            //ignore this, as the delete flag will retain its previous value
        }
    }
}
