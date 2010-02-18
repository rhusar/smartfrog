/** (C) Copyright 1998-2006 Hewlett-Packard Development Company, LP

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

import java.io.IOException;
import java.rmi.RemoteException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Writer;
import java.io.OutputStream;

/**
 * created 18-May-2004 11:46:09
 */

public class TempFileImpl extends FileUsingComponentImpl implements TempFile {

    public static final String ERROR_PREFIX_EMPTY = ATTR_PREFIX+ " can not be an empty string";

    private boolean createOnDeploy;

    /**
     * create a temporary file instance; do no real work (yet)
     * @throws RemoteException In case of network/rmi error
     */
    public TempFileImpl() throws RemoteException {
    }

    /**
     * On startup we create the temp file and set the filename attribute to it
     *
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        createOnDeploy= sfResolve(ATTR_CREATE_ON_DEPLOY, createOnDeploy, true);
        if(createOnDeploy) {
            createTempFile();
        }
    }

    /**
     * Create the temporary file
     * @throws SmartFrogException error while deploying
     * @throws RemoteException In case of network/rmi error
     */
    private void createTempFile() throws RemoteException, SmartFrogException {
        String prefix = sfResolve(ATTR_PREFIX, "", true);
        if(prefix.length()==0) {
            throw new SmartFrogException(ERROR_PREFIX_EMPTY,this);
        }
        String suffix = sfResolve(ATTR_SUFFIX, (String) null, false);
        String dir;
        dir= FileSystem.lookupAbsolutePath(this,ATTR_DIRECTORY,null,null,false,null);

        String text = sfResolve(ATTR_TEXT, (String) null, false);

        if (sfLog().isDebugEnabled()){
            sfLog().debug("Creating temp file in dir ["+dir+"], prefix="+prefix
                           +", suffix="+suffix+", text="+text);
        }

        bind(FileSystem.createTempFile(prefix, suffix, dir));

        //Create File content if needed
        if (text!=null) {
            String encoding = null;
            encoding = sfResolve(ATTR_TEXT_ENCODING, encoding, true);
            Writer wout = null;
            try {
                OutputStream fout;
                fout = new FileOutputStream(getFile());
                wout = new OutputStreamWriter(fout, encoding);
                wout.write(text);
                wout.flush();
                wout.close();
            } catch (IOException ioe) {
                FileSystem.close(wout);
                throw SmartFrogException.forward("When trying to write to "+ getFile(),ioe);
            }
        }
        sfReplaceAttribute(FileUsingComponent.ATTR_FILENAME, file.toString());
    }


    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        if (!createOnDeploy) {
            //create the temp file now
            createTempFile();
        }

        //maybe terminate
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,
                "Created temp file " + file,
                null,
                null);
    }

    /**
     * delete the file if needed
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        deleteFileIfNeeded();
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
