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
import org.smartfrog.sfcore.common.SmartFrogException;
import java.rmi.Remote;

/**
 * A remote interface to interact with the DNS manager.
 */
public interface DNSManager extends Remote {

    /**
     * Registers a DNS update in the name server. 
     *
     * @param modif A DNS update to be added to the name server.
     * @exception DNSModifierException if an error occurs 
     * while registering the update.
     * @exception RemoteException if an error occurs
     */
    public void register(DNSModifier modif)
        throws DNSModifierException, RemoteException;

    /**
     * Unregisters a DNS update in the name server. 
     *
     * @param modif A DNS update to be added to the name server.
     * @exception DNSModifierException if an error occurs 
     * while unregistering the update.
     * @exception RemoteException if an error occurs
     */
    public void unregister(DNSModifier modif) 
        throws DNSModifierException, RemoteException;

    /**
     * Checks that all the bindings that are currently registered
     * can be looked up in the name server.
     *
     * @return True if all the NORMAL bindings are OK, false otherwise.
     * @exception DNSBindingException If I cannot check that  bindings are
     * OK.
     * @exception RemoteException if an error occurs
     */
    public boolean sanityCheck() 
        throws DNSModifierException, RemoteException;
    
}
