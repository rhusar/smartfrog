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
 * This class implements all the methods that are called locally to the RebindingStub.
 * These calls (such as "equal") shall not be forwarded to the Rebind object
 */
public class RemoteRebindImpl implements RemoteRebind {

    protected Binder binder;
    private RebindingStub rebindingStub;
    private Object sessionState;

    /**
     * Constructor
     * 
     * @param binder the binder
     * @param rebindingStub the rebinding stub 
     */
    public RemoteRebindImpl(Binder binder, RebindingStub rebindingStub) {
        this.binder = binder;
        this.rebindingStub = rebindingStub;
        this.sessionState = null;
    }
    
    /**
     * {@inheritDoc}
     */
    public Binder getBinder() {
        return binder;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setFailFastTimeout(long timeout) {
    	rebindingStub.setFailFastTimeout(timeout);
    }
    
    /**
     * {@inheritDoc}
     */
    public long getFailFastTimeout() {
    	return rebindingStub.getFailFastTimeout();
    }
    
    /**
     * set the session state. Session state is used to communicate 
     * information that should be preserved across rebinds.
     *
     * {@inheritDoc}
     */
    public void setSessionState(Object obj) {
        sessionState = obj;
    }
    
    /**
     * Get the session state. Session state is used to communicate
     * information that should be recreated at the server after a rebind.
     *
     * {@inheritDoc}
     */
    public Object getSessionState() {
        return sessionState;
    }
    
    /**
     * Close session sets the binder to the dead state. This calls to 
     * the remote object to fail and prevents any attempts to rebind.
     *
     * {@inheritDoc}
     */
    public void closeSession() {
    	rebindingStub.close();
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getDirectObject() throws RemoteException {
        return rebindingStub.getDirectObject();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDead() {
        try {
            return binder.isDead();
        } catch (BindException exc) {}
        return false;
    }

    /**
     * Equality makes the following assumption: 
     * 1) the objects have to implement Rebind
     * 2) binders for all incarnations of a single object instance are equal
     * 3) binders for any incarnation of different object instances are not equal
     * 4) there can be only one incarnation of an object instance in the system at a time.
     * 
     * So we compare binders with binders.
     */
    public boolean equals(Object obj) {
        
        if( !(obj instanceof Rebind) ) {
            return false;
        }
        try {
            return binder.equals( ((Rebind)obj).getBinder() );
        } catch (Exception e) {
            return false;
        }
        
        /**
         * it has to be one of our stubs
         *
        if( !(obj instanceof RemoteRebind) ) {
            return false;
        }
        
        /**
         * compare binders
         *
        try {
            return binder.equals( ((RemoteRebind)obj).getBinder() );
        } catch (Exception e) {
            return false;
        }
        */
    }

}
