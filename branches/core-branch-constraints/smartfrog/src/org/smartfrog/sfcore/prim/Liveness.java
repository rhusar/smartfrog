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

package org.smartfrog.sfcore.prim;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogLivenessException;


/**
 * Defines the basic liveness interface to which all deployed components
 * respond.
 *
 */
public interface Liveness extends Remote {
    /**
     * Check liveness of component. Any exception while executing this call
     * should mean that the component has failed. Since multiple sources can
     * call on a component, the source of the liveness message is also passed
     * in
     *
     * @param source Source of call. Optional; can be null.
     *
     * @throws SmartFrogLivenessException liveness failure
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfPing(Object source) throws SmartFrogLivenessException, RemoteException;
}
