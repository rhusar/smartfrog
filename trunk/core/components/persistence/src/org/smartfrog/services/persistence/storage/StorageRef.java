package org.smartfrog.services.persistence.storage;

import java.io.*;


public interface StorageRef extends Serializable {

	public Storage getStorage() throws StorageException;
	
	public StoragePollee getStoragePollee() throws StorageException;

}
