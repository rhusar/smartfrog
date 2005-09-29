
/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/

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
