package org.smartfrog.services.persistence.storage;

import java.rmi.*;


public interface StorageAgent extends Remote {

	static final String ServiceName = "WoodFrogStorageAgent";

	public Object getComponentStub(StorageRef storef) throws RemoteException, StorageException;
	
	public boolean isDead(StorageRef storef) throws RemoteException,StorageException;
	
}
