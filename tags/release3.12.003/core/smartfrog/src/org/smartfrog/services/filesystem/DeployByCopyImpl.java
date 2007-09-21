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


import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/** a component that deletes a file after copying it */
public class DeployByCopyImpl extends CopyFileImpl implements CopyFile {

    private boolean shouldDelete;

    public DeployByCopyImpl() throws RemoteException {
    }

    public synchronized void sfStart()
            throws SmartFrogException, RemoteException {
        super.sfStart();
        //if we got here, then we should delete when terminating
        shouldDelete = true;
    }

    /**
     * When terminating, delete the destination file if there is one. If
     * deletion fails (i.e. maybe in use), we mark it for delete on exit, then
     * keep going.
     *
     * @param status  TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord status) {
        super.sfTerminateWith(status);
        if (shouldDelete && getToFile() != null) {
            if (getToFile().exists() && !getToFile().delete()) {
                getToFile().deleteOnExit();
            } else {
                shouldDelete = false;
            }
        }
    }
}
