package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.io.File;

/**

 */
public class FileExistsImpl extends FileUsingComponentImpl implements FileExists {

    private long minSize=-1;

    public FileExistsImpl() throws RemoteException {
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
        //set up all the filename bindings
        bind(true,null);
        minSize=sfResolve(ATTR_MIN_SIZE,minSize,true);
    }

    /**
     * Evaluate the condition.
     *
     * @return true if it is successful, false if not
     * @throws RemoteException for network problems
     * @throws SmartFrogException for any other problem
     */
    public boolean evaluate() throws RemoteException, SmartFrogException {
        return getFile().exists() &&
            (minSize < 0 || getFile().length() >= minSize);
    }
}
