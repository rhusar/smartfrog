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
package org.smartfrog.services.rpm.manager;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLivenessException;
import org.smartfrog.sfcore.prim.Liveness;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created 14-Apr-2008 17:11:58
 */

public class AbstractRpmManager extends PrimImpl implements RpmManager, Iterable<RpmFile> {

    private ArrayList<RpmFile> rpms = new ArrayList<RpmFile>(1);

    public AbstractRpmManager() throws RemoteException {
    }

    /**
     * Returns an iterator over the rpms
     *
     * @return an Iterator.
     */
    public Iterator<RpmFile> iterator() {
        return rpms.listIterator();
    }

    /**
     * Add another file to the list of RPMs that need managing
     *
     * @param rpm the RPM to manage
     * @throws SmartFrogException if unable to manage this file
     * @throws RemoteException    network problems
     */
    public void manage(RpmFile rpm) throws SmartFrogException, RemoteException {
        rpms.add(rpm);
        onNewFileAdded(rpm);
    }

    /**
     * Notification of a new artifact added, it is already on the rpms list at this point.
     *
     * @param rpm the newly added RPM
     */
    protected void onNewFileAdded(RpmFile rpm) {

    }

    /**
     * Liveness call in to check if this component is still alive.
     *
     * @param source source of call
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException {
        super.sfPing(source);
        for (RpmFile rpm : rpms) {
            ping(rpm);
        }
    }

    /**
     * Override point: ping the file
     *
     * @param rpm the file to ping
     * @throws SmartFrogLivenessException component is terminated
     * @throws RemoteException            for consistency with the {@link Liveness} interface
     */
    protected void ping(RpmFile rpm) throws SmartFrogLivenessException, RemoteException {

    }
}