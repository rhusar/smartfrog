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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.processcompound.SFProcess;


/**
 * SFReferenceAlternateHostsBinderImpl is a Binder that uses the SmartFrog
 * reference resolution to locate the server object on a list of alternative
 * hosts. The binder iterates through the hosts in sequence until it finds
 * the server object or reaches a given timeout that defaults to ten minutes.
 */
public class SFReferenceAlternateHostsBinderImpl implements Binder {
    
	protected Vector<String> hosts;
    protected String name;
    private long startTime;
    private long timeout = (10 * 60 * 1000); // ten minutes
    private long delay = (2 * 1000); // 2 seconds

    /**
     * Constructor using default timeout and retry delay
     * 
     * @param hosts
     * @param name
     */
    public SFReferenceAlternateHostsBinderImpl(Vector<String> hosts, String name) {
        if( hosts == null || name == null ) {
            throw new NullPointerException("Missing hosts or name");
        }
    	this.hosts = hosts;
        this.name = name;
        startTime = -1;
    }
    
    /**
     * Constructor setting non-default timeout and retry delay
     * 
	 * @param hosts list of hosts
	 * @param name name to locate
	 * @param timeout the timeout
	 * @param delay the delay
	 */
	public SFReferenceAlternateHostsBinderImpl(Vector<String> hosts, String name, long timeout, long delay) {
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
     * Get the stub by trying an sfResolve of the reference on  
     * each alternative host in sequence and returning the first 
     * successful attempt.
     *
     * {@inheritDoc}
     */
    public Object getStub() throws BindException {

		if (startTime == -1) {
			startTime = System.currentTimeMillis();
		}

		Iterator<String> iter = hosts.iterator();
		while (iter.hasNext()) {
			try {
				Object stub = getStub(iter.next());
				startTime = -1;
				return stub;
			} catch (Exception e) {
				// drop the exception - just failed to bind
			}
		}
		throw new BindException("Failed to bind to any host ");
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
        if( (obj instanceof SFReferenceAlternateHostsBinderImpl) ) {
            return false;
        } else {
            SFReferenceAlternateHostsBinderImpl binder = (SFReferenceAlternateHostsBinderImpl)obj;
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
     * Dead if we fail to bind in the configured timeout (default 1 minute)
     *
     * {@inheritDoc}
     */
    public boolean isDead() throws BindException {
        
        if( startTime == -1 ) {
            return false;
        } else {
            return ( (System.currentTimeMillis() - startTime) > timeout );
        }
    }
    

    /**
     * Pause for configured timeout (default 2 seconds)
     *
     * {@inheritDoc}
     */
    public void retryDelay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exc2) {}
    }

}
