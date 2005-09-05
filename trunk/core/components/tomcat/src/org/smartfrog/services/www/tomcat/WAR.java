package org.smartfrog.services.ia.tomcat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This methods defines the methods for starting/stopping, loading/unloading
 * WAR files.
 */
public interface WAR extends Remote {

    /**
     * Start an existing WAR file
     */
    public void startWAR(String warPath) throws RemoteException;
    
    /**
     * Stop an existing WAR file
     */
    public void stopWAR(String warPath) throws RemoteException;
    
    /**
     * Unload an existing WAR file
     */
    public void unloadWAR(String warPath) throws RemoteException;

    /**
     * Load a new WAR file
     */
    public void loadWAR(String warPath, String warLocation) throws RemoteException;
	
}
