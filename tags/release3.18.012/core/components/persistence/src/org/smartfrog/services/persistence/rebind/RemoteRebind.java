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

package org.smartfrog.services.persistence.rebind;

import java.rmi.RemoteException;

/**
 * This interface extends Rebind with methods that are only available locally to 
 * the RebindingStub. It may be also used to identify whether an object is a 
 * RebindingStub as it will be an instance of RemoteRebind. The object implementing
 * Rebind will not be an instance of RemoteRebind - this interface is added in to 
 * the dynamic proxy by reflection when it is constructed.
 */
public interface RemoteRebind extends Rebind {

	/**
	 * Determine if the remote server object is dead. Actually determines
	 * if this stub is unable to contact the server.
	 * 
	 * @return true if this stub cannot contact the server, false otherwise
	 * @throws RemoteException
	 */
    public boolean isDead() throws RemoteException;
    
    /**
     * Get the real RMI stub for the remote server object.
     *  
     * @return the RMI stub
     * @throws RemoteException
     */
    public Object getDirectObject() throws RemoteException;
    
}
