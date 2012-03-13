package org.smartfrog.services.persistence.test.testcases;

import java.io.ObjectStreamException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rebind.Binder;
import org.smartfrog.services.persistence.rebind.FailFast;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.RebindingStub;
import org.smartfrog.services.persistence.rebind.RemoteRebind;
import org.smartfrog.services.persistence.rebind.binders.SFReferenceBinderImpl;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.compound.CompoundImpl;


public class HelloSFRebindImpl extends CompoundImpl implements Compound, Rebind, Hello {
    
    Binder binder;
    long failFastTimeout = 0;

    public HelloSFRebindImpl() throws RemoteException {
        super();
    }
    
    public void hello(String client) throws RemoteException {
        System.out.println("Hello World from: " + client);
    }
    
    @FailFast
    public void goodbye(String client) throws RemoteException {
    	hello(client);
    }
    
    public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        String host = getHost();
        String name = (String)sfResolve("sfProcessComponentName");
        binder = new SFReferenceBinderImpl(host, name);
    }
    
    /**
     * Get the local host name
     * @return
     * @throws SmartFrogException
     */
    public String getHost() throws SmartFrogException {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new SmartFrogException("Failed to get local host name", e);
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
        return null;
    }

    /**
     * Set the session state
     */
    public void setSessionState(Object obj) throws RemoteException {
        System.out.println("New session state: " + obj);
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
