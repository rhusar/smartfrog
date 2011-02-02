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

package org.smartfrog.services.persistence.framework.interfaceguard;

/**
 * This interface checks for the status of the Interface Guard.
 * The interface guard is part of the sand boxing part of the 
 * framework. It is not safe for remote clients to interact 
 * with the recoverable components when the interface guard 
 * indicates that interfaces should be closed. 
 */
public interface InterfaceGuard {

	/**
	 * Is the interface guard set to open? By definition: <code>(not isOpen()) = isClosed()</code>
	 * @return true if the guard is open
	 */
    public boolean isOpen();

    /**
     * Is the interface guard closed? by definition: <code>(not isClosed()) = isOpen()</code>
     * @return tru if the interface guard is closed
     */
    public boolean isClosed();

}