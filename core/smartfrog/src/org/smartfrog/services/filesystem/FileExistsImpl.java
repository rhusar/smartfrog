package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.io.File;

/**

 */
public class FileExistsImpl extends FileUsingComponentImpl implements FileExists {

    private long minSize = -1;
    private boolean canBeFile, canBeDir;

    public FileExistsImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        //set up all the filename bindings
        bindWithDir(true, "");
        minSize = sfResolve(ATTR_MIN_SIZE, minSize, true);
        canBeFile = sfResolve(ATTR_CAN_BE_FILE, true, true);
        canBeDir = sfResolve(ATTR_CAN_BE_DIR, true, true);
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException    for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        File f = getFile();
        boolean debug=sfLog().isDebugEnabled();
        if(!f.exists()) {
            if(debug) sfLog().debug("Does not exist: "+file);
            return false;
        }
        boolean isFile = f.isFile();
        if (isFile && !canBeFile) {
            if (debug) sfLog().debug("Is of type file: " + file);
        }
        boolean isDir = f.isDirectory();
        if (isDir && !canBeDir) {
            if (debug) sfLog().debug("Is a directory: " + file);
        }
        if(minSize >= 0 && f.length() < minSize) {
            if (debug) sfLog().debug("Too short " + file +" - size of "+f.length()+" is below the minSize of "+minSize);
            return false;
        }
        return true;
    }
}
