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

import org.smartfrog.services.persistence.rebind.BindException;
import org.smartfrog.services.persistence.rebind.Binder;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.RemoteRebind;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * SFReferenceBinderImpl is a Binder that uses the SmartFrog
 * reference resolution to locate the server object on a specific
 * host. The binder will allow up to 30 attempts to rebind.
 */
public class SFReferenceBinderImpl implements Binder {
    
    protected String host;
    protected String name;
    private int count;
    private int timeout = 30;  // 30 attempts default
    private long delay = 1000; // 1 second default

    /**
     * Constructor
     * 
     * @param host the host name
     * @param name the name for location
     */
    public SFReferenceBinderImpl(String host, String name) {
        this.host = host;
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
     * Get the stub by doing an sfResolve of the reference on the 
     * target host.
     *
     * {@inheritDoc}
     */
    public Object getStub() throws BindException {
        
        Prim reg = null;
        Rebind stub = null;
        
         try {
            /**
             * Get the remote register - if this works increment
             * the count.
             */
            count++;
            reg = (Prim)SFProcess.getRootLocator().getRootProcessCompound( InetAddress.getByName(host) );
            
            /**
             * Get the component - if this works clear the count.
             */
            stub = (Rebind)reg.sfResolve(name);
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
     * SmartFrog reference Binders are equal if their references are equal and
     * their target hosts are equal.
     *
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if( (obj instanceof SFReferenceBinderImpl) ) {
            return false;
        } else {
            SFReferenceBinderImpl binder = (SFReferenceBinderImpl)obj;
            return ( host.equals(binder.host) &&
                     name.equals(binder.name) );
        }
    }

    /**
     * Dead if we get through to the remote process compound 
     * but the class is not there on 30 consecutive occasions.
     *
     * {@inheritDoc}
     */
    public boolean isDead() throws BindException {
        return (count >= timeout);
    }

    /**
     * Retry after 1 seconds
     *
     * {@inheritDoc}
     */
    public void retryDelay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exc2) {}
    }

}
