package org.smartfrog.services.persistence.storage;

import java.io.Serializable;

public class StorageException extends Exception implements Serializable {

	public StorageException(String string) {
		super(string);
	}

	public StorageException(String string, Throwable cause) {
		super(string, cause);
	}
	
}
