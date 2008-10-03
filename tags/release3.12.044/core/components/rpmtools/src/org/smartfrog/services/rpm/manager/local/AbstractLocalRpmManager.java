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
package org.smartfrog.services.rpm.manager.local;

import org.smartfrog.services.rpm.manager.AbstractRpmManager;
import org.smartfrog.services.rpm.manager.RpmManager;
import org.smartfrog.services.rpm.manager.RpmFile;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;

import java.rmi.RemoteException;
import java.io.FileNotFoundException;

/**
 * <p/>
 * Created: 17-Apr-2008
 */
public abstract class AbstractLocalRpmManager extends AbstractRpmManager implements RpmManager {

    protected AbstractLocalRpmManager() throws RemoteException {
    }

    /**
     * Check the files in the RPM exist, throw a LivenessException if not
     * @param rpm rpm to check
     * @throws SmartFrogLivenessException missing file.
     */
    protected void verifyFiles(RpmFile rpm) throws SmartFrogLivenessException {
        try {
            rpm.verifyAllManagedFilesExist();
        } catch (FileNotFoundException e) {
            throw new SmartFrogLivenessException(e);
        }
    }

    /**
     * Override point: probe the file
     *
     * @param rpm the file to probe
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for network problems
     */
    @Override
    protected void probe(RpmFile rpm) throws SmartFrogLivenessException, RemoteException {
        verifyFiles(rpm);
    }
}
