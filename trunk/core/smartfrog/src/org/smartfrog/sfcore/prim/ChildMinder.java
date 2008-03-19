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
import java.util.Enumeration;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;


/**
 * Defines the interface an object needs to implement if it wants to manage
 * children. Primitives expect their parent to implement this interface so
 * they can register for liveness and termination messages
 *
 */
public interface ChildMinder extends Remote {
    /**
     * Add a child.
     *
     * @param child child to add
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public void sfAddChild(Liveness child) throws RemoteException;

    /**
     * Remove a child.
     *
     * @param child child to add
     *
     * @return Status of child removal
     * @throws SmartFrogRuntimeException if failed to remove the child
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfRemoveChild(Liveness child) throws SmartFrogRuntimeException, RemoteException;

    /**
     * Request whether implementor contains a given child.
     *
     * @param child child to check for
     *
     * @return true is child is present else false
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public boolean sfContainsChild(Liveness child) throws RemoteException;

    /**
     * Gets an enumeration over the children of the implementor.
     *
     * @return enumeration over children
     *
     * @throws RemoteException In case of Remote/nework error
     */
    public Enumeration<Liveness> sfChildren() throws RemoteException;
}
