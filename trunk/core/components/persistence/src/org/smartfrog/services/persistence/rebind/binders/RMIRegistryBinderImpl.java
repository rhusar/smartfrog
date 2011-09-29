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

package org.smartfrog.services.persistence.rebind.binders;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.smartfrog.services.persistence.rebind.BindException;
import org.smartfrog.services.persistence.rebind.Binder;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.RemoteRebind;

/**
 * RMIRegistryBinderImpl is a Binder that uses the RMI register to 
 * to locate the server object by name.
 */
public class RMIRegistryBinderImpl implements Binder {
    
    protected String host;
    protected String name;
    private int count;
    private int timeout = 30;  // 30 attempts default
    private long delay = 1000; // 1 second default

    /**
     * Constructor
     * 
     * @param name the RMI name used to locate the remote object
     * @throws UnknownHostException failed to get the local host
     */
    public RMIRegistryBinderImpl(String name) throws UnknownHostException {
        this.host = InetAddress.getLocalHost().getCanonicalHostName();
        this.name = name;
        count = 0;
    }
    
	/**
	 * Change the timeouts - note that this is not 
	 * synchronized. The changes are not atomic with respect 
	 * to checks.
	 * 
	 * @param count
	 * @param delay
	 */
	public void setTimeouts(int count, long delay) {
		this.timeout = count;
		this.delay = delay;
	}


    /**
     * binds the given name to the given Rebind object in the local registry
     * 
     * @param rebind
     * @throws RemoteException
     * @throws AlreadyBoundException
     */
    public void bind(Rebind rebind) throws RemoteException, AlreadyBoundException {
        Registry reg = LocateRegistry.getRegistry();
        reg.rebind(name, rebind);
    }
    
    /**
     * Get the stub by looking it up in the RMI registry on the target host.
     * 
     * {@inheritDoc}
     */
    public Object getStub() throws BindException {
        
        Registry reg = null;
        Rebind stub = null;
        
         try {
            /**
             * Get the remote register - if this works increment
             * the count.
             */
            count++;
            reg = LocateRegistry.getRegistry(host);
            
            /**
             * Get the component - if this works clear the count.
             */
            stub = (Rebind)reg.lookup(name);
            if( stub instanceof RemoteRebind ) {
                stub = (Rebind)((RemoteRebind)stub).getDirectObject();
            }
            count = 0;
            return stub;
            
        } catch (Exception e) {
            throw new BindException("Failed to access host", e);
        }    
    }
    
    /**
     * RMI Binders are equal if they reference the same name on the same host.
     *
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if( (obj instanceof RMIRegistryBinderImpl) ) {
            return false;
        } else {
            RMIRegistryBinderImpl binder = (RMIRegistryBinderImpl)obj;
            return ( host.equals(binder.host) &&
                     name.equals(binder.name) );
        }
    }

    /**
     * Dead if we have reached the configured number of retries.
     * (default 30)
     *
     * {@inheritDoc}
     */
    public boolean isDead() throws BindException {
        return (count >= timeout);
    }

    /**
     * Wait for the configured timeout (default 1 second)
     *
     * {@inheritDoc}
     */
    public void retryDelay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exc2) {}
    }
    
}
