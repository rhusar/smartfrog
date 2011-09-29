package org.smartfrog.services.persistence.test.testcases;

import java.rmi.RemoteException;
import java.util.Vector;

import org.smartfrog.services.persistence.rebind.Rebind;
import org.smartfrog.services.persistence.rebind.locator.Locator;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.security.SFGeneralSecurityException;
import org.smartfrog.sfcore.security.SFSecurity;


public class HelloNonNativeSFClientImpl {
    
    private Hello helloServer;
    private Vector hosts = new Vector();
    
    

    /**
     * @param args
     * @throws SmartFrogResolutionException 
     * @throws RemoteException 
     * 
     */
    public static void main(String[] args) throws SmartFrogException, RemoteException   {

        try {
            SFSecurity.initSecurity();
        } catch (SFGeneralSecurityException e1) {
            System.out.println("Security failed");
            e1.printStackTrace();
        }
        
        
        if( args.length == 0 ) {
            System.out.println("Needs a list of host names as arguments");
            return;
        }
        Vector<String> hosts = new Vector<String>();
        for( int i = 0; i < args.length; i++ ) {
            hosts.add(args[i]);
        }
        HelloNonNativeSFClientImpl testClient = new HelloNonNativeSFClientImpl(hosts);
        testClient.loop();
    }
	public void ext(String host) throws SmartFrogException, RemoteException   {

        try {
            SFSecurity.initSecurity();
        } catch (SFGeneralSecurityException e1) {
            System.out.println("Security failed");
            e1.printStackTrace();
        }
        
        
       /* if( args.length == 0 ) {
            System.out.println("Needs a list of host names as arguments");
            return;
        }
       /* Vector<String> hosts = new Vector<String>();
        for( int i = 0; i < args.length; i++ ) {
            hosts.add(args[i]);
        }*/
		hosts.add(host);
        HelloNonNativeSFClientImpl testClient = new HelloNonNativeSFClientImpl(hosts);
        testClient.loop();
    }
    
    public HelloNonNativeSFClientImpl(Vector<String> hosts) throws SmartFrogResolutionException  {
        helloServer = (Hello)Locator.multiHostSfResolve(hosts, "hello");
    }

    public void loop() throws RemoteException, SmartFrogException {
        int i = 0;
        while( true ) {
            
            i++;
            System.out.println("Calling test: me " + i);
            helloServer.hello("me " + i);
            ((Rebind)helloServer).setSessionState("[state recorded as " + i + "]");
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
