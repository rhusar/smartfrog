package org.smartfrog.services.jetty.listeners;

import java.rmi.RemoteException;
import java.rmi.Remote;

import org.smartfrog.sfcore.common.SmartFrogException;
/**
 * An interface for listeners for jetty server 
 * @author Ritu Sabharwal
 */

public interface Listener extends Remote {
    String LISTENER_PORT = "listenerPort";
    String SERVER_HOST = "serverHost";

    /**
	 * Add the listener to the http server
	 * @exception  RemoteException In case of network/rmi error 
	 */
    public void addlistener(int listenerPort, String serverHost) throws
            SmartFrogException, RemoteException;
}
