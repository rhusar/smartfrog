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
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;


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
    protected File file;

    /**
     *  Constructor .
     *
     *@exception  RemoteException In case of network/rmi error
     */
    public FileUsingComponentImpl() throws RemoteException {
    }

    /**
     * get the absolute path of this file
     *
     * @return path of the file
     */
    public String getAbsolutePath()  {
        if (file == null) {
            return null;
        } else {
            return file.getAbsolutePath();
        }
    }

    /**
     * get the URI of this file
     *
     * @return URI of the file
     */
    public URI getURI()  {
        if (file == null) {
            return null;
        } else {
            return file.toURI();
        }
    }

    /**
     * get the file we are using
     * @return the file
     */
    public File getFile() {
        return file;
    }


    /**
     * Bind the class to the filename; indicate in the operation whether
     * the filename is mandatory or not
     * @param mandatory flag to indicate mandatoryness
     * @param defval a default value to use if not mandatory (can be null)
     * @throws RemoteException In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime error
     */
    protected void bind(boolean mandatory, String defval) throws RemoteException, SmartFrogRuntimeException {
        String filename = bind(this, mandatory, defval);
        if (filename != null) {
            file = new File(filename);
        }
    }

    /**
     * Bind the class to the filename; indicate in the operation whether the
     * filename is mandatory or not.
     * This is the variation that also uses the {@link #ATTR_DIR attribute}
     * to select a parent directory.
     *
     * @param mandatory flag to indicate mandatoryness
     * @param defval    a default value to use if not mandatory (can be null)
     *
     * @throws RemoteException           In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime error
     */
    protected void bindWithDir(boolean mandatory, String defval)
            throws RemoteException, SmartFrogRuntimeException {
        String filename = bindWithDir(this, mandatory, defval);
        if (filename != null) {
            file = new File(filename);
        }
    }

    /**
     * creates the file object instance, to the absolute path,
     * then sets the attribute {@link FileIntf#ATTR_ABSOLUTE_PATH}
     * to the absolute path, and {@link FileUsingComponent#ATTR_URI}
     * to the URI. From here on, {@link #getFile()} is valid.
     * @param absolutePath absolute pat of file
     * @throws SmartFrogRuntimeException runtime error
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
        bind(this,newfile);
    }


    /**
     * Bind the class to the filename; indicate in the operation whether the
     * filename is mandatory or not.
     * This variation also looks up parent directory in the {@link #ATTR_DIR}
     * attribute.
     *
     * @param component the component to read attributes from
     * @param mandatory flag to indicate mandatoryness
     * @param defval    a default value to use if not mandatory (can be null)
     * @return the absolutePath value
     * @throws RemoteException  In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime error
     */
    public static String bindWithDir(Prim component,boolean mandatory, String defval)
            throws RemoteException, SmartFrogRuntimeException {
        File parentDir = null;
        String dir = FileSystem.lookupAbsolutePath(component,
                ATTR_DIR,
                null,
                null,
                false,
                null);
        if (dir != null) {
            parentDir = new File(dir);
        }
        return bind(component, parentDir, defval, mandatory);
    }

    /**
     * Bind the class to the filename; indicate in the operation whether the
     * filename is mandatory or not
     *
     * @param component the component to read attributes from
     * @param mandatory flag to indicate mandatoryness
     * @param defval    a default value to use if not mandatory (can be null)
     *
     * @return the absolutePath value
     *
     * @throws RemoteException           In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime error
     */
    public static String bind(Prim component, boolean mandatory, String defval)
            throws RemoteException, SmartFrogRuntimeException {
        return bind(component, null, defval, mandatory);
    }

    /**
     * Bind the class to the filename; indicate in the operation whether the
     * filename is mandatory or not
     *
     * @param component the component to read attributes from
     * @param parentDir the parent directory to use (can be null)
     * @param mandatory flag to indicate mandatoryness
     * @param defval    a default value to use if not mandatory (can be null)
     *
     * @return the absolutePath value
     *
     * @throws RemoteException           In case of network/rmi error
     * @throws SmartFrogRuntimeException runtime error
     */
    public static String bind(Prim component,
                              File parentDir,
                              String defval,
                              boolean mandatory)
            throws RemoteException, SmartFrogRuntimeException {
        String absolutePath = FileSystem.lookupAbsolutePath(component,
                ATTR_FILENAME,
                defval,
                parentDir,
                mandatory,
                null);
        if (absolutePath != null && absolutePath.length()>0) {
            File newfile = new File(absolutePath);
            bind(component, newfile);
        } else {
            if(mandatory) {
                throw new SmartFrogDeploymentException("No filename supplied",component);
            }
        }
        return absolutePath;
    }


    /**
     * Bind to a new file. sets the {@link #ATTR_ABSOLUTE_PATH} and {@link #ATTR_URI}
     * attributes. This is a static function for use by any component that
     * wants to set the relevant deploy-time attributes.
     *
     * @param component component to configure.
     * @param newfile file to bind to to
     * @throws SmartFrogRuntimeException  runtime error
     * @throws RemoteException  In case of network/rmi error
     */
    public static void bind(Prim component, File newfile) throws SmartFrogRuntimeException,
            RemoteException {
        component.sfReplaceAttribute(ATTR_ABSOLUTE_PATH, newfile.getAbsolutePath());
        String uri;
        uri = newfile.toURI().toString();
        component.sfReplaceAttribute(FileUsingComponent.ATTR_URI, uri);
    }



    /**
     * Returns the name of the file we are bound to.
     *
     * @return string form for this component
     */
    public String toString() {
        return file != null ? file.getAbsolutePath() : "uninitialized component";
    }

    /**
     * Helper method for all components that support delete-on-clearup;
     * This should be called from the {@link Prim#sfTerminatedWith(org.smartfrog.sfcore.prim.TerminationRecord, org.smartfrog.sfcore.prim.Prim)}
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
        } catch (RemoteException e) {
            sfLog().ignore(e);
        } catch (SmartFrogResolutionException e) {
            sfLog().ignore(e);
        }
    }

}
