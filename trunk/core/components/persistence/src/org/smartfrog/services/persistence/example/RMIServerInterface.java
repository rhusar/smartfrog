package org.smartfrog.services.persistence.example;
import java.rmi.*;

public interface RMIServerInterface extends Remote {

	public void setToken(int token) throws RemoteException;
	
	public void killMe() throws RemoteException;
	
}
