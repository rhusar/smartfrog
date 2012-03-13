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
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.services.persistence.rebind.BindException;
import org.smartfrog.services.persistence.rebind.Binder;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.RemoteRebind;
import org.smartfrog.services.persistence.rebind.locator.TimedLocator;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * SFReferenceTimedAlternateHostsBinderImpl is a Binder that uses the SmartFrog
 * reference resolution to locate the server object on a list of alternative
 * hosts. The binder tries all hosts in parallel until it finds
 * the server object or reaches a given timeout that defaults to ten minutes.
 */
public class SFReferenceTimedAlternateHostsBinderImpl implements Binder {
    
	protected Vector<String> hosts;
    protected String name;
    private long startTime;
    private long timeout = (10 * 60 * 1000); // ten minutes default
    private long delay = (2 * 1000); // 2 second default 

    /**
     * Constructor using default timeout and delay
     * 
     * @param hosts
     * @param name
     */
    public SFReferenceTimedAlternateHostsBinderImpl(Vector<String> hosts, String name) {
        if( hosts == null || name == null ) {
            throw new NullPointerException("Missing hosts or name");
        }
    	this.hosts = hosts;
        this.name = name;
        startTime = -1;
    }

    /**
     * Constructor setting non-default timeout and delay.
     * 
	 * @param delay
	 * @param hosts
	 * @param name
	 * @param timeout
	 */
	public SFReferenceTimedAlternateHostsBinderImpl(Vector<String> hosts, String name, long timeout, long delay) {
		this(hosts, name);
		this.delay = delay;
		this.timeout = timeout;
	}
	
	/**
	 * Change the timeouts - note that this is not 
	 * synchronized. The changes are not atomic with respect 
	 * to checks.
	 * 
	 * @param timeout
	 * @param delay
	 */
	public void setTimeouts(long timeout, long delay) {
		this.timeout = timeout;
		this.delay = delay;
	}


	/**
     * Get the stub by doing an sfResolve of the reference on the 
     * all the hosts in parallel and returning the first suceessful
     * response.
     *
     * {@inheritDoc}
     */
    public Object getStub() throws BindException {

		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}
        
        Object stub = null;
        try {
            stub = TimedLocator.multiHostSfResolve(hosts, name);
            if (stub instanceof RemoteRebind) {
                stub = (Rebind) ((RemoteRebind) stub).getDirectObject();
            }
        } catch (Exception e) {
            throw new BindException("Failed to bind to any host ");
        }
        startTime = -1;
        return stub;
	}
    
    /**
	 * Attempts to bind to the given host name
	 * 
	 * @param host
	 * @return
	 * @throws Exception
	 */
    private Object getStub(String host) throws Exception {
            	
		Prim reg = null;
		Rebind stub = null;

		/**
		 * Get the remote register - if this works increment the count.
		 */
		reg = (Prim) SFProcess.getRootLocator().getRootProcessCompound(
				InetAddress.getByName(host));

		/**
		 * Get the component - if this works clear the count.
		 */
		stub = (Rebind) reg.sfResolve(name);
		if (stub instanceof RemoteRebind) {
			stub = (Rebind) ((RemoteRebind) stub).getDirectObject();
		}
		return stub;

	}
    
    /**
	 * SmartFrog reference Binders are equal if their references are equal and
	 * their host lists overlap.
	 *
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if( (obj instanceof SFReferenceTimedAlternateHostsBinderImpl) ) {
            return false;
        } else {
            SFReferenceTimedAlternateHostsBinderImpl binder = (SFReferenceTimedAlternateHostsBinderImpl)obj;
            return ( overlappingHostLists(hosts, binder.hosts) &&
                     name.equals(binder.name) );
        }
    }
    
    /**
     * compares two vectors for common members in any order.
     * 
     * @param list1
     * @param list2
     * @return true - if any common members, false if disjoint.
     */
    private boolean overlappingHostLists(Vector<String> list1, Vector<String> list2) {
    	Iterator<String> iter = list1.iterator();
    	while( iter.hasNext() ) {
    		if( list2.contains( iter.next() ) ) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Dead if we fail to bind before within the configured timeout (default 10 minutes) 
     *
     * {@inheritDoc}
     */
    public boolean isDead() throws BindException {
        
        if( startTime == -1 ) {
            return false;
        } else {
            return ( (System.currentTimeMillis() - startTime) > timeout ); // ten minutes
        }
    }
    

    /**
     * pause for the configured timeout (default 2 seconds)
     *
     * {@inheritDoc}
     */
    public void retryDelay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exc2) {}
    }
    
}
