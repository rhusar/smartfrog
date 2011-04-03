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

import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.rmi.RemoteException;

/**
 * This is a component which will delete a file that it is bound do at
 * startup.
 */

public class DeleteFileImpl extends FileUsingComponentImpl
        implements FileUsingComponent {

    /**
     * Constructor.
     * @throws RemoteException  In case of network/rmi error
     */
    public DeleteFileImpl() throws RemoteException {
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
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        File target = getFile();
        try {
            if (target != null && target.exists()) {
                if (!target.delete()) {
                    sfLog().warn("Failed to delete " + target);
                }
            }
        } catch (SecurityException e) {
            //failure is turned into a security problem; we catch it and make it meaningful
            throw new SmartFrogDeploymentException(
                    "Security blocked the deletion of the file " + target,
                    e,
                    this);
        }
        maybeStartTerminator("Deleted file");
    }

}
