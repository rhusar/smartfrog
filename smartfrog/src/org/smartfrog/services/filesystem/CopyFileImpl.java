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

import java.io.IOException;
import java.io.File;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.utils.ComponentHelper;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.logging.Log;

/**
 *   Implemetation for CopyFile component.
 */
public class CopyFileImpl extends CompoundImpl implements CopyFile, Compound {


    private File fromFile=null;
    private File toFile=null;
    private boolean copyOnDeploy=true;

    /**
     * Constructor
     * @throws RemoteException  In case of network/rmi error
     */
    public CopyFileImpl() throws RemoteException {
    }

    /**
     * Get From file
     * @return File
     */
    public File getFromFile() {
        return fromFile;
    }

    /**
     * Get to file
     * @return File
     */
    public File getToFile() {
        return toFile;
    }


    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *                                  failure while starting
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        copyOnDeploy=sfResolve(ATTR_COPY_ON_DEPLOY,copyOnDeploy,true);
        if (copyOnDeploy) {
            copyFile();
        }
    }



    /**
     * Copies file using attributes "source" and "destination" from description
     * @throws SmartFrogException failure while copying
     * @throws RemoteException  In case of network/rmi error
     * @throws SmartFrogResolutionException failure while resolving
     */
    private void copyFile() throws SmartFrogException, RemoteException,
        SmartFrogResolutionException {
        String from= FileSystem.lookupAbsolutePath(this, ATTR_FROM,null,null,true,null);
        String to =
                FileSystem.lookupAbsolutePath(this,
                        ATTR_TO,
                        null,
                        null,
                        true,
                        null);
        fromFile=new File(from);
        toFile=new File(to);
        //if the destination is a directory, we copy the file in there
        //using its name as the key
        if(toFile.isDirectory()) {
            toFile=new File(toFile,fromFile.getName());
        }
        //check that the sorce file exists and fail if it is not there
        if (!fromFile.exists()) {
            throw new SmartFrogDeploymentException("Unable to copy "
                +fromFile.getAbsolutePath() +
                " to " +
                toFile.getAbsolutePath()
            +" as the source file does not exist");
        }
        if(fromFile.isDirectory()) {
            throw new SmartFrogDeploymentException("Unable to copy the directory"
                + fromFile.getAbsolutePath() 
                + " as directory copy is not implemented");
        }
        Log log=sfLog();
        if(log.isInfoEnabled()) {
            log.info("Copying "+fromFile.getAbsolutePath()+" to "+toFile.getAbsolutePath());
        }
        boolean overwrite=sfResolve(ATTR_OVERWRITE,true,true);
        if(!overwrite && toFile.exists()) {
            log.info("Skipping copy as the destination file exists and 'overwrite'==false");
        } else {
            try {
                FileSystem.fCopy( fromFile, toFile);
            } catch (IOException ex) {
                throw SmartFrogException.forward("Failed when copying "
                        +fromFile.getAbsolutePath() +
                        " to " +
                        toFile.getAbsolutePath()
                        ,ex);
            }
        }
    }

    /**
     * we only create the directory at startup time, even though we bond at deploy time
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        Throwable thrown = null;
        try {
            if (!copyOnDeploy) {
                copyFile();
            }
        } catch (SmartFrogException e) {
            thrown=e;
        } catch (RemoteException e) {
            thrown = e;
        }
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null, "Copy",null,thrown);
    }

}
