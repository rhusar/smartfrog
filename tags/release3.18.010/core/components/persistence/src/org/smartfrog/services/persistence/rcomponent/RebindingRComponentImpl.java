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

package org.smartfrog.services.persistence.rcomponent;

import java.io.ObjectStreamException;
import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.services.persistence.framework.interfaceguard.InterfaceGuard;
import org.smartfrog.services.persistence.rebind.Binder;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.RebindingStub;
import org.smartfrog.services.persistence.rebind.RemoteRebind;
import org.smartfrog.services.persistence.rebind.binders.SFReferenceTimedAlternateHostsBinderImpl;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.prim.Prim;

/**
 * RebindingRComponentImpl extends RComponentImpl with reflection mechanisms to generate a 
 * dynamic proxy in place of a regular RMI stub for this class. The dynamic proxy is an instance 
 * of RebindingStub, a class that understands the activate/deactivate behaviour of the 
 * persistence framework and implements transparent failover for the client side. The 
 * RebindingRComponentImpl can be used to develop fault tolerant applications.
 */
public class RebindingRComponentImpl extends RComponentImpl implements RComponent, Rebind, Prim {
    
	private static final String REBIND_TIMEOUT_ATTR = "sfRebindTimeout";
	private static final String REBIND_DELAY_ATTR = "sfRebindDelay";
	private static final String FAILFAST_TIMEOUT_ATTR = "sfFailFastTimeout";
	private static final long TIMEOUT_DEFAULT = (10 * 60 * 1000); // ten minutes
	private static final long DELAY_DEFAULT = (2 * 1000); // two seconds
	private static final long FAILFAST_TIMEOUT_DEFAULT = 0; // unlimited
    private Binder binder;
    private long failFastTimeout;
    private Object rebindingStub;
    private Object rebindingStubMonitor = new Object();
    protected InterfaceGuard interfaceGuard;

    public RebindingRComponentImpl() throws RemoteException {
        super();
    }
    
    public void sfDeployWith(Prim parent, Context context) throws SmartFrogDeploymentException, RemoteException {
        /**
         * Interface manager is done this way because it is set in the super also, but after 
         * this (in sfDeployWith()) so this only sets it once.
         */
        setInterfaceManager(context);

        try {
            
            /**
             * binder must be set up before deplyWith as that is where the object is
             * exported.
             */
            Vector<String> hosts = interfaceManager.getRecoveryHosts();
            String name = (String)context.get("sfProcessComponentName");
            binder = new SFReferenceTimedAlternateHostsBinderImpl(hosts, name, TIMEOUT_DEFAULT, DELAY_DEFAULT);
      
            /**
             * Get the interface guard - again do it before we get exported.
             * As we are not fully constructed this has to be done through the 
             * process compound and not this component's interface.
             */
            interfaceGuard = interfaceManager.getInterfaceGuard();
            setFailFastTimeout(FAILFAST_TIMEOUT_DEFAULT);
            
        } catch(NullPointerException ex) {
            throw new SmartFrogDeploymentException("Failed to set up interface manager - missing attribute", ex);
        } catch(ClassCastException ex) {
            throw new SmartFrogDeploymentException("Failed to get interface manager - found attribute of wrong type", ex);            
        }
        
        super.sfDeployWith(parent, context);
        
    }
    
    public void sfDeploy() throws RemoteException, SmartFrogException {
    	super.sfDeploy();
    	/**
    	 * Modify binder timeouts and generate new stub
    	 * This is tricky because we can not try to get the attributes
    	 * in the deployWith phase, but we need a binder before the end of
    	 * the deployWith because the object gets exported there. The original
    	 * is setup with default timeouts and the timeouts are adjusted here.
    	 * We need to create a new stub if one already exists as it have made a
    	 * copy of the binder with the wrong timeouts. Do this in a synchronized 
    	 * block on the rebindingStubMonitor
    	 */
    	((SFReferenceTimedAlternateHostsBinderImpl)binder).setTimeouts(
    			sfResolve(REBIND_TIMEOUT_ATTR, TIMEOUT_DEFAULT, false), 
    			sfResolve(REBIND_DELAY_ATTR, DELAY_DEFAULT, false) );
    	
    	setFailFastTimeout( sfResolve(FAILFAST_TIMEOUT_ATTR, FAILFAST_TIMEOUT_DEFAULT, false) );
    }
    
    /**
     * This method can be used as part of the sand boxing behaviour of
     * recoverable components. The method returns true if it is safe
     * for clients to invoke the interfaces of this component, otherwise
     * it returns false. Open implies the component has not terminated and
     * the persistence framework interface guard is set to open. 
     *
     * @return true if open, false if closed
     */
    protected boolean sfIsInterfaceOpen() {
        if( sfIsTerminated ) {
            return false;
        } else {
            return interfaceGuard.isOpen();
        }
    }
    
    /**
     * This method can be used as part of the sand boxing behaviour of
     * recoverable components. The method returns true if it is not safe
     * for clients to invoke the interfaces of this component, 
     * it returns false if it is safe. Closed implies either the component 
     * has terminated or the persistence framework interface guard is set to closed. 
     *
     * @return true if closed, false if open
     */
    protected boolean sfIsInterfaceClosed() {
        if( sfIsTerminated ) {
            return true;
        } else{
            return interfaceGuard.isClosed();
        }
    }
    
    /**
     * This components binder
     */
    public Binder getBinder() {
        return binder;
    }
    
    /**
     * set the fail fast timeout
     * @throws RemoteException 
     */
    public void setFailFastTimeout(long timeout) throws RemoteException {
		failFastTimeout = timeout;
	}

	/**
     * This components fail fast timeout
	 * @throws RemoteException 
     */
    public long getFailFastTimeout() throws RemoteException {
    	return failFastTimeout;
    }

    /**
     * Get the session state
     */
    public Object getSessionState() throws RemoteException {
        if( sfIsInterfaceOpen() ) {
            return null;
        } else {
            throw new RemoteException("Interface is closed");
        }
    }

    /**
     * Set the session state
     */
    public void setSessionState(Object obj) throws RemoteException {
        if( sfIsInterfaceOpen() ) {
            return;
        } else {
            throw new RemoteException("Interface is closed");
        }
    }
    
    /**
     * Close session - has no affect on local reference
     */
    public void closeSession() throws RemoteException {
    	// does nothing here
    }
    
    /**
     * If not a stub it must be the same object.
     * Otherwise equal if binders are equal.
     */
    public boolean equals(Object obj) {
        
        /**
         * if not a Rebind object return false
         */
        if( !(obj instanceof Rebind) ) {
            return false;
        }
        
        /**
         * if its a remoteRebind object compare with my stub
         */
        if (obj instanceof RemoteRebind) {
            try {
                return obj.equals(this);
            } catch( Exception e ) {
                return false;
            }
        } 
        /**
         * Otherwise use super.equals
         */
        else {
            return super.equals(obj);
        }
    }
    
    /**
     * Replaces the component by its dynamic proxy with recovery properties during
     * serialization.
     *
     * @return A new proxy object linked to this component
     * @throws ObjectStreamException in case an error occurs
     * @throws RuntimeException 
     * @throws RemoteException 
     * @throws IllegalArgumentException 
     */
    public Object writeReplace() throws ObjectStreamException, StorageException, IllegalArgumentException, RemoteException, RuntimeException {
    	return RebindingStub.getProxy( this );
    }

}
