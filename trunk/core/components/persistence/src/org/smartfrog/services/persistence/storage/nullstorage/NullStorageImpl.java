/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP
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

package org.smartfrog.services.persistence.storage.nullstorage;

import java.io.Serializable;

import org.smartfrog.services.persistence.storage.Storage;
import org.smartfrog.services.persistence.storage.StorageException;
import org.smartfrog.services.persistence.storage.StorageRef;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;



/**
 * NullStorageImpl behaves as if there is no persistent copy of the storage. It is
 * used to make an RComponent behave as if it were a reqular SmartFrog Compound.
 * There are corresponding NullStorageRefImpl and NullStoragePolleeImpl classes
 * associated with this class.
 */
public class NullStorageImpl extends Storage {
	
	
	/**
	 * constructor
	 */
	public NullStorageImpl() {
		return;
	}
	

	/**
	 * This constructor would normally create a database, the Null storage does not 
	 * acually create anything.
	 * 
	 * @param configData
	 * @throws StorageException
	 */
    public NullStorageImpl(ComponentDescription configData) throws StorageException {
    	// do nothing
    	return;
    }
    
    
    /**
     * This constructor would normally open an existing database, the Null storage does
     * not open anyting.
     * 
     * @param dbname
     * @param configData
     * @throws StorageException
     */
    public NullStorageImpl(String dbname, ComponentDescription configData) throws
    		StorageException {
    	return;
    }

	
    /**
     * The null storage does not perform aborts - dummy method
     */
	public void abort() throws StorageException {
		// Do nothing
		return;
	}

	
    /**
     * The null storage does not add entries - dummy method
     */
	public void addEntry(String entryname, Serializable value) throws StorageException {
		// Do nothing
		return;
	}

    /**
     * The null storage does not replace entries - dummy method
     */
	public void replaceEntry(String entryname, Serializable value) throws StorageException {
		// Do nothing
		return;
	}

	
    /**
     * The null storage does not delete entries - dummy method
     */
	public void removeEntry(String entryname) throws StorageException {
		// Do nothing
		return;
	}

	
    /**
     * The null storage does not perform a close - dummy method
     */
	public void close() throws StorageException {
		// Do nothing
		return;
	}

	
    /**
     * The null storage does not perform commits - dummy method
     */
	public void commit() throws StorageException {
		// Do nothing
		return;
	}

	
    /**
     * The null storage does not perform commits - dummy method
     */
	public void disableCommit() {
		// Do nothing
		return;
	}

	
    /**
     * The null storage does not perform commits - dummy method
     */
	public void enableCommit() {
		// Do nothing
		return;
	}

	
    /**
     * The url for null storage is the string "null-storage"
     */
	public String getAgentUrl() {
		// Do nothing
		return "null-storage";
	}

	
    /**
     * The null storage does not have entries - dummy method
     * returns empty array of objects
     */
	public Object[] getEntries() throws StorageException {
		// Return empty array
		return new Object[0];
	}

	
    /**
     * The null storage does not have entries - dummy method
     * returns null
     */
	public Serializable getEntry(String entryname) throws StorageException {
		// return null
		return null;
	}

	
    /**
     * The null storage has a null implementation of a storage reference.
     * returns a new null storage reference
     */
	public StorageRef getStorageRef() throws StorageException {
		// TODO Auto-generated method stub
		return new NullStorageRefImpl();
	}

}
