/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * created 18-May-2004 11:46:09
 */

public class TempFileImpl extends PrimImpl implements Prim, TempFile, FileIntf {

    /**
     * the temp file that was created
     */
    private File tempFile;

    /**
     * to delete flag
     */
    boolean delete=false;

    Logger log;

    public TempFileImpl() throws RemoteException {
    }

    /**
     * On startup we create the temp file and set the filename attribute to it
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  error while deploying
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        log=new ComponentHelper(this).getLogger();
        String prefix = sfResolve(ATTR_PREFIX, "", true);
        String suffix = sfResolve(ATTR_SUFFIX, (String) null, false);
        String dir = sfResolve(ATTR_DIRECTORY, (String) null, false);
        delete = sfResolve(ATTR_DELETE_ON_EXIT, delete, false);
        log.log(Level.FINE,"creating temp file in dir ["+dir+"] prefix="+prefix+" suffix="+suffix);
        try {
            if (dir == null) {
                tempFile = File.createTempFile(prefix, suffix);
            } else {
                tempFile = File.createTempFile(prefix, suffix, new File(dir));
            }
        } catch (IOException e) {
            throw SmartFrogException.forward(e);

        }
        sfReplaceAttribute(ATTR_FILENAME, tempFile.toString());
        sfReplaceAttribute(varAbsolutePath,tempFile.getAbsolutePath());
        sfContext().put(ATTR_FILENAME, tempFile.toString());

    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
    }

    /**
     * Provides hook for subclasses to implement usefull termination behavior.
     *
     * @param status termination status
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        try {
            //see if anyone changed the delete settings during our life
            delete = sfResolve(ATTR_DELETE_ON_EXIT,delete,false);
        } catch (Exception e) {
            //ignore this, as the delete flag will retain its previous value
        }
        if(delete) {
            if(!tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    /**
     * get the filename of this file
     *
     * @return
     */
    public String getFilename() {
        return tempFile.toString();
    }

    public String getAbsolutePath() throws RemoteException {
        return tempFile.getAbsolutePath();
    }

}
