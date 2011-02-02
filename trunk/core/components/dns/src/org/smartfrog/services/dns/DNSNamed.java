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

import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;




/**
 *  A remote interface to a "named" DNS server that allows to 
 * manage it.
 *
 * 
 */
public interface DNSNamed extends Prim {


    /**
     * Updates the internal configuration data reflecting a change in
     * zones/views/options...
     *
     * @param newData A hierarchy of data reflecting the current
     * configuration state.
     * @return The old configuration that is being replaced.
     * @exception RemoteException Cannot change the configuration.
     */
    public DNSData updateConfigData(DNSData newData) 
        throws RemoteException;

    /**
     * Stops the daemon, cleans up all the configuration
     * changes using dynamic updates, and patches the "fresh"
     * config files so that it can be
     * re-started in a known state.
     *
     * @exception DNSException Error while stopping/cleaning
     * the named daemon.
     * @exception RemoteException if an error occurs
     */
    public void cleanUp() throws DNSException, RemoteException;


    /**
     * Starts the named daemon unless it is already started.
     *
     * @exception DNSException Error while starting the named
     * daemon.
     * @exception RemoteException if an error occurs
     */
    public void start() throws DNSException, RemoteException;


    /**
     * Stops the named daemon assuming it is currently running.
     *
     * @exception DNSException Error while stopping the named
     * daemon.
     * @exception RemoteException if a network  error occurs
     */
    public void stop() throws DNSException, RemoteException;

    
    /**
     * Flushes all the caches. This allows forward views or zones
     * to ensure they will get the most up to date information.
     *
     * @exception DNSException Error while flushing caches in the named
     * daemon.
     * @exception RemoteException if a network  error occurs
     */
    public void flush() throws DNSException, RemoteException;

    /**
     * Returns the status of the named daemon.
     *
     * @return True if the named daemon is up, false otherwise.
     * @exception DNSException Error while trying to find the
     * status of the named daemon.
     * @exception RemoteException if an error occurs
     */
    public boolean status()  throws DNSException, RemoteException;

}
