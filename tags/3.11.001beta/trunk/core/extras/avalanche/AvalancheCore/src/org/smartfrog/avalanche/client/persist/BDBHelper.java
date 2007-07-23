/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.client.persist;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Helper class to create BDB database in given location. 
 * The created database is enabled to store java objects directly. 
 * 
 * @author sanjaydahiya
 *
 */
public class BDBHelper {
	private Environment env ;
	private static StoredClassCatalog classCatalog ;
	private Database database ; 

	/**
	 * Creates or opens Berkeley DB in dbHome directory. 
	 * @param avalancheHome
	 * @throws DatabaseException
	 */
	public void init(String dbHome) throws DatabaseException{
	    // Environment open omitted for brevity
		EnvironmentConfig cfg = new EnvironmentConfig();
		
		cfg.setTransactional(true);
		cfg.setAllowCreate(true);
		
		env = new Environment(new File(dbHome), cfg); 

	    // Open the database that you will use to store your data
	    DatabaseConfig myDbConfig = new DatabaseConfig();
	    myDbConfig.setAllowCreate(true);
	    myDbConfig.setSortedDuplicates(true);
	    database = env.openDatabase(null, "avalancheData", myDbConfig);

	    // Open the database that you use to store your class information.
	    // The db used to store class information does not require duplicates
	    // support.
	    myDbConfig.setSortedDuplicates(false);
	    Database myClassDb = env.openDatabase(null, "classDb", myDbConfig); 

	    // Instantiate the class catalog
	    classCatalog = new StoredClassCatalog(myClassDb);
	}
	
	/**
	 * Store an object in database. The object is identified by key, usually a String. 
	 * Key and value both should be serializable objects. 
	 * @param key
	 * @param value
	 * @throws DatabaseException
	 */
	public void put(String key, Object value) throws DatabaseException{
	    // Create the binding
	    EntryBinding dataBinding = new SerialBinding(classCatalog, 
	                                                 value.getClass());

	    // Create the DatabaseEntry for the key
	    try{
	    		DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));

		    // Create the DatabaseEntry for the data. Use the EntryBinding object
		    // that was just created to populate the DatabaseEntry
		    DatabaseEntry theData = new DatabaseEntry();
		    dataBinding.objectToEntry(value, theData);
	
	    		database.put(null, theKey, theData);		
	    }catch(UnsupportedEncodingException e){
    		// shouldnt 
	    		e.printStackTrace();
	    }
	}
	/**
	 * Retrieves an object from database identified by key. 
	 * @param key
	 * @return null if object doesnt exist in DB. 
	 * @throws DatabaseException
	 */
	public Object get(String key, Class valueType)throws DatabaseException{
		Object value = null ; 
	    // Create the binding
	    EntryBinding dataBinding = new SerialBinding(classCatalog, valueType);

	    try{
		    // Create DatabaseEntry objects for the key and data
		    DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
		    DatabaseEntry theData = new DatabaseEntry();
	
		    // Do the get as normal
		    database.get(null, theKey, theData, LockMode.DEFAULT);
	
		    // Recreate the MyData object from the retrieved DatabaseEntry using
		    // the EntryBinding created above
		    value = dataBinding.entryToObject(theData);
	    }catch(UnsupportedEncodingException e){
	    		e.printStackTrace();
	    }
	    return value; 
	}
	/**
	 * Delete the object from DB. 
	 * @param key
	 * @throws DatabaseException
	 */
	public void delete(String key)throws DatabaseException{
	    try{
		    // Create DatabaseEntry objects for the key and data
		    DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
		    database.delete(null, theKey);
	
	    }catch(UnsupportedEncodingException e){
	    		e.printStackTrace();
	    }
	}
	/**
	 * Close all database handlers. 
	 * @throws DatabaseException
	 */
	public void close() throws DatabaseException{
		classCatalog.close();
		database.close();
		env.close();
		
	}

}
