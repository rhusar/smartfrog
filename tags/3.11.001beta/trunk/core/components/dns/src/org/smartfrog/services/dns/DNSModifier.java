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

package org.smartfrog.services.dns;

import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;
import java.net.InetAddress;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * A remote interface representing an active modification to DNS.
 *
 */
public interface DNSModifier extends Prim {

    /**
     * Gets the component description that encapsulates the configuration 
     * changes submitted to the DNS server.
     *
     * @return The component description that encapsulates the configuration 
     * changes submitted to the DNS server.
     *
     * @exception RemoteException if an error occurs
     */
    public DNSComponent getUpdate() throws RemoteException;

    /**
     * Gets whether this update contains only bindings and no structural
     * changes.
     *
     * @return True if this update contains only bindings and no structural
     * changes.
     * @exception RemoteException if an error occurs
     */
    public boolean isOnlyBindings()  throws RemoteException;

    /**
     * Gets the manager that performed the DNS update.
     *
     * @return The manager that performed the DNS update.
     * @exception RemoteException if an error occurs
     */
    public DNSManager getDNSManager() throws RemoteException;


}
