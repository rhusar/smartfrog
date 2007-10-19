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

package org.smartfrog.sfcore.processcompound;

import java.net.InetAddress;
import java.rmi.RemoteException;

import org.smartfrog.sfcore.common.SmartFrogException;


/**
 * Interface which root locators must implement. A root locator implementation
 * maintains the resource which defines a process as being the root or owner
 * process of a host. All other processcompounds in other processes on the
 * same host must be slaves to the root process compound. A root locator
 * should not allow multiple process compounds to identify themself as a root
 * in a single process.
 *
 */
public interface RootLocator {
    /**
     * Set given process compound as the root process compound for this host.
     *
     * @param c process compound to set as root
     *
     * @throws RemoteException if there is any network/rmi error
     * @throws SmartFrogException if failed to set root
     *
     * @see #getRootProcessCompound
     */
    public void setRootProcessCompound(ProcessCompound c)
        throws SmartFrogException, RemoteException;


    /**
     * Unbinds root process compound from local registry.
     *
     *
     * @throws RemoteException if there is any network/rmi error
     * @throws SmartFrogException if failed to unbind
     *
     */
    public void unbindRootProcessCompound()
        throws SmartFrogException, RemoteException;


    /**
     * Gets the root process compound for a given host. If
     * the passed host is null the root process compound for the local host is
     * looked up.

     *
     * @param host host to get root compound for
     *
     * @return Root ProcessCompound
     *
     * @throws Exception if failed to get process compound
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress host)
        throws Exception;

    /**
     * Gets the root process compound for a given host on a specified port. If
     * the passed host is null the root process compound for the local host is
     * looked up. Checks if the local process compound is equal to the
     * requested one, and returns the local object instead of the stub to
     * avoid all calls going through RMI
     *
     * @param hostAddress host to look up root process compound
     * @param portNum port to locate registry for root process conmpound if not
     *        default
     *
     * @return the root process compound on given host
     *
     * @throws Exception if error locating root process compound on host
     *
     * @see #setRootProcessCompound
     */
    public ProcessCompound getRootProcessCompound(InetAddress hostAddress,
        int portNum) throws Exception;
}
