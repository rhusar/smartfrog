package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;

import java.io.ObjectStreamException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.smartfrog.services.persistence.rcomponent.RebindingRComponentImpl;
import org.smartfrog.services.persistence.rebind.FailFast;
import org.smartfrog.services.persistence.rebind.Finalizing;
import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.persistence.rebind.Binder;
import org.smartfrog.services.persistence.rebind.binders.SFReferenceBinderImpl;


public class HelloSFAlternateRebindImpl extends RebindingRComponentImpl implements Hello, Rebind, Prim {
    
    int tries = 0;
	Binder binder;
    
    public HelloSFAlternateRebindImpl() throws RemoteException {
        super();
    }
    
    /**
     * Set the session state
     */
    public void setSessionState(Object obj) throws RemoteException {
        if( sfIsInterfaceOpen() ) {
            if( tries < 2 ) {
                tries++;
                System.out.println("Refusing to set session state - test refusal");
                throw new RemoteException("Interface is closed");
            } else {
                System.out.println("New session state: " + obj);
            }
        } else {
            System.out.println("Refused attempt to set session state - inteface closed");
            throw new RemoteException("Interface is closed");
        }
    }

	public String getHost() throws SmartFrogException {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new SmartFrogException("Failed to get local host name", e);
        }
    }

    @FailFast
    @Finalizing
    public void goodbye(String client) throws RemoteException, SmartFrogException {
        System.out.println("Sleeping");
    	try { Thread.sleep(1500); } 
    	catch (InterruptedException e1) {}
		hello(client);
    }

    public void hello(String client) throws RemoteException, SmartFrogException {
    	
        /**
         * Guard the interface - admit if open - even though we will
         * be un-exported.
         */
        if( sfIsInterfaceClosed() ) {
            System.out.println("*** Throwing remote exception - interface closed at start ****");
            throw new RemoteException("Interface is closed");
        }
        
        /**
         * Catch runtime exceptions and then any method specific 
         * exceptions and either rethrow if the interface is open
         * or replace with a RemoteException if the interface is closed. 
         * 
         * Note: if a call succeeds we allow it through even if the interface 
         * is closed. It clearly succeeded in what it was doing and had no
         * effect on the recovery storage as that was cut off too.
         */
        try {
            
            //System.out.println("Blocking 1 sec: " + client);
            //try {
            //    Thread.sleep(1000);
            //} catch (InterruptedException e) {
                // do nothing
            //}
            String lastClient = sfResolve("lastClient", "", true);
            System.out.println("Hello world - Last client was: " + lastClient + ", this client is: " + client);
            sfReplaceAttribute("lastClient", client);
            if( sfIsInterfaceClosed() ) {
                throw new NullPointerException();
            }
            
        } catch(RuntimeException rte) {
            if( sfIsInterfaceOpen() ) {
                System.out.println("*** passing on internal exception - interface open at end ****");
                throw rte;
            } else {
                System.out.println("*** throwing remote exception - interface closed at end ****");
                throw new RemoteException("Interface closed before completion");
            }
        } catch (SmartFrogResolutionException e) {
            if( sfIsInterfaceOpen() ) {
                System.out.println("*** passing on internal exception - interface open at end ****");
                throw e;
            } else {
                System.out.println("*** throwing remote exception - interface closed at end ****");
                throw new RemoteException("Interface closed before completion");
            }
        } catch (SmartFrogRuntimeException e) {
            if( sfIsInterfaceOpen() ) {
                System.out.println("*** passing on internal exception - interface open at end ****");
                throw e;
            } else {
                System.out.println("*** throwing remote exception - interface closed at end ****");
                throw new RemoteException("Interface closed before completion");
            }
        }
    }
}
