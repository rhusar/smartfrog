/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.rpm.local;

import org.smartfrog.services.rpm.manager.AbstractRpmManager;
import org.smartfrog.services.rpm.manager.RpmFile;
import org.smartfrog.services.rpm.manager.RpmManager;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;

/**
 * Created 14-Apr-2008 17:11:58
 */

public class ArtifactCheckingManagerImpl extends AbstractRpmManager implements RpmManager {

    boolean enabled;
    public static final String ATTR_ENABLED = "enabled";

    public ArtifactCheckingManagerImpl() throws RemoteException {
    }


    /**
     * Can be called to start components. Subclasses should override to provide functionality Do not block in this call,
     * but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        enabled = sfResolve(ATTR_ENABLED, true, true);
    }

    /**
     * Override point: ping the file
     *
     * @param rpm the file to ping
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    protected void ping(RpmFile rpm) throws SmartFrogLivenessException, RemoteException {
        if (enabled) {
            try {
                rpm.verifyAllManagedFilesExist();
            } catch (FileNotFoundException e) {
                throw new SmartFrogLivenessException(e);
            }
        }
    }
}
