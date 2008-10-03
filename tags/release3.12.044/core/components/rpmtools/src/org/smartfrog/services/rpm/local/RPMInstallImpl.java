/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.rpm.local.AbstractLocalRPMToolImpl;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * @author sandya
 */
public class RPMInstallImpl extends AbstractLocalRPMToolImpl implements LocalRPMTool {
    public static final String ERROR_UNABLE_TO_INSTALL = "Unable to Install RPM package ";

    /**
     * @throws RemoteException failure in super constructor
     */
    public RPMInstallImpl() throws RemoteException {
        super();
    }

    /**
     * Install the package on startup.
     *
     * @throws SmartFrogException SmartFrog problems
     * @throws RemoteException network problems
     */
    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        try {
            getRpmUtils().InstallPackage(getRpmPackage(), getOptions());
        } catch (IOException e) {
            sfLog().error(ERROR_UNABLE_TO_INSTALL + getRpmPackage(), e);
            throw new SmartFrogException(ERROR_UNABLE_TO_INSTALL +
                    getRpmPackage(), e);
        }
    }

}