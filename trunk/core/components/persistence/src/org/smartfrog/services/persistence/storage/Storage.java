package org.smartfrog.services.persistence.storage;

import java.io.*;
import java.util.Vector;


/**
 * Defines the methods a simple and sequential storage 
 * space should have.
 * 
 */
public interface Storage extends Serializable{

	/**
	 * Creates a new entry in the storage space and associates it to a directory
	 * 
	 * @param entryname
	 * @throws StorageException
	 */
	public void createEntry (String entryname, String directory ) throws StorageException;
	
	
	public boolean hasEntry (String entryname) throws StorageException;
	
	/**
	 * Inserts a new record in the storage space for the specified entry
	 * The entry must have been created before, otherwise an exception is thrown.
	 * 
	 * @param obj Object value that should be written
	 * 
	 * @throws StorageException In case some failure happens
	 */
	public void addEntry (String entryname, Serializable value) throws StorageException;
	
	/**
	 * Deletes an indexed record of the given entry from the storage space
	 * 
	 * @param index Pointer to the internal object that should be deleted
	 * 
	 * @throws StorageException In case some failure happens
	 */
	public void deleteEntry (String entryname) throws StorageException;
	
	
	//public void doGarbageCollection (long version) throws StorageException;
	
	/**
	 * Recovers an entry from stable storage 
	 * 
	 * @param id index of the required entry
	 * @return
	 */
	public Serializable getEntry( String entryname ) throws StorageException;

	public void commit() throws StorageException;
	
	public void abort() throws StorageException;
	
	public void delete() throws StorageException;
	
	public void disableCommit();
	
	public void enableCommit();
	
	public Object[] getEntries (String directory) throws StorageException;

	//public long getLastVersion() throws StorageException;

	public StorageRef getStorageRef();
	
	public String getAgentUrl();
	
	public void close() throws StorageException;
	
}
