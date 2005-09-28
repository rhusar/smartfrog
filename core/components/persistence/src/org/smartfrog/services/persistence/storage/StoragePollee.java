package org.smartfrog.services.persistence.storage;

import java.io.Serializable;

public interface StoragePollee {

	public Serializable pollEntry( String entryname) throws StorageException;

	public void close() throws StorageException;

}
