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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * This is a component which will delete a file that it is bound do at
 * termination time.
 * created 31-Mar-2005 14:51:14
 */

public class SelfDeletingFileImpl extends FileUsingComponentImpl
        implements FileUsingComponent {

    /**
     * Constructor.
     * @throws RemoteException  In case of network/rmi error
     */
    public SelfDeletingFileImpl() throws RemoteException {
    }

    /**
     *
     * Bind to the attributes defining filename; fail if not set
     * @throws SmartFrogException error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
            RemoteException {
        super.sfDeploy();
        bindWithDir(true, null);
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
        startTerminator();
    }

    /**
     * something that is called in the {@link #sfStart()} method to start the termination
     * process. It can be overridden, or called from an overridden sfStart method.
     */
    protected void startTerminator() {
        new ComponentHelper(this).sfSelfDetachAndOrTerminate(null,"SelfDeletingFile terminating",null,null);
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
}
