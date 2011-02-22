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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface defines the methods that must be implemented by a rebinding 
 * component.
 */
public interface Rebind extends Remote, Serializable {
    
	/**
	 * Returns the binder object for this Rebind component.
	 * 
	 * @return binder object
	 * @throws RemoteException
	 */
    public Binder getBinder() throws RemoteException;
    
    /**
     * An upcall used by clients to deliver the session state they
     * have built up. The state may be null. If the session state
     * is specific to a particular client the client identifier must
     * be held in the state.
     * 
     * @param obj session state
     * @throws RemoteException
     */
    public void setSessionState(Object obj) throws RemoteException;
    
    /**
     * Get the session state of this component.
     * 
     * @return the session state (may be null)
     * @throws RemoteException
     */
    public Object getSessionState() throws RemoteException;
 
    /**
     * close a session with this component.
     * 
     * @throws RemoteException
     */
    public void closeSession() throws RemoteException;
    
    /**
     * Set the failfast timeout for this component
     * 
     * @param timeout the timeout
     * @throws RemoteException
     */
    public void setFailFastTimeout(long timeout) throws RemoteException;
    
    /**
     * Get the failfast timeout for this component
     * @return the timeout
     * @throws RemoteException
     */
    public long getFailFastTimeout() throws RemoteException;

}
