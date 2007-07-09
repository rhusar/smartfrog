/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileDocument;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.modules.bdb.bindings.ActiveProfileBinding;

import java.util.ArrayList;

public class ActiveProfileManagerImpl implements ActiveProfileManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(ActiveProfileManagerImpl.class);
    private static ActiveProfileBinding binding = new ActiveProfileBinding();
    
    public ActiveProfileManagerImpl(Database db){
    		this.database = db;
    }
    
    public ActiveProfileType getProfile(String hostId) throws DatabaseAccessException{
    		ActiveProfileType profile = null ;
    		ActiveProfileDocument acdoc = null ; 
    		
    		try{
	    		DatabaseEntry key = new DatabaseEntry(hostId.getBytes());
	    		DatabaseEntry value = new DatabaseEntry();
	    		
	    		database.get(null, key, value, LockMode.DEFAULT);
	    		
	    		acdoc = (ActiveProfileDocument)binding.entryToObject(value);
	    		if( null != acdoc ){
	    			profile = acdoc.getActiveProfile();
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return profile ; 
    }
    
    public void removeProfile(String hostId) throws DatabaseAccessException{
		try{
			database.delete(null, new DatabaseEntry(hostId.getBytes()));
 		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
    }
    
    public String []listProfiles() throws DatabaseAccessException{
    		String[] profiles = new String[0];
    		try{
	   		ArrayList listHolder = new ArrayList();
	    		Cursor cursor = database.openCursor(null, null);
	    		DatabaseEntry key = new DatabaseEntry();
	    		DatabaseEntry value = new DatabaseEntry();
	    		for(OperationStatus stat=cursor.getFirst(key, value, LockMode.DEFAULT); 
	    			stat == OperationStatus.SUCCESS ; 
	    			stat=cursor.getNext(key, value, LockMode.DEFAULT)){
	    			
	    			listHolder.add(new String(key.getData()));
	    		}
	    		profiles = (String[]) listHolder.toArray(profiles);
	    		cursor.close(); 
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return profiles ;
    }
    
    public void addProfile(ActiveProfileType profile) throws DatabaseAccessException{
    	
    		try{
    		ActiveProfileDocument acdoc = ActiveProfileDocument.Factory.newInstance();
    		acdoc.setActiveProfile(profile);
    		
    		String profileId = profile.getHostId();
    		DatabaseEntry key = new DatabaseEntry(profileId.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		
    		binding.objectToEntry(acdoc, value);
    		Transaction t = database.getEnvironment().beginTransaction(null, null);
    		if( OperationStatus.KEYEXIST == database.putNoOverwrite(t, key, value)){
    			t.abort();
    			throw new DatabaseException("Active Profile already exists for host : " + profileId);
    		}
    		t.commit();
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    }
    
    /**
     * Overwrites existing value of active profile. 
     * @param profile
     * @throws DatabaseException
     */
    public void setProfile(ActiveProfileType profile) throws DatabaseAccessException{
    	
    		try{
	    		String profileId = profile.getHostId();
	    		
	    		ActiveProfileDocument acdoc = ActiveProfileDocument.Factory.newInstance();
	    		acdoc.setActiveProfile(profile);
	    		
	    		DatabaseEntry key = new DatabaseEntry(profileId.getBytes());
	    		DatabaseEntry value = new DatabaseEntry();
	    		binding.objectToEntry(acdoc, value);
	    		
	    		database.put(null, key, value);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    }
    
    public ActiveProfileType newProfile(String hostId) throws DatabaseAccessException, DuplicateEntryException{
    		ActiveProfileType profile = null; 
    		try{
	    		ActiveProfileDocument acdoc = ActiveProfileDocument.Factory.newInstance();
	    		profile = acdoc.addNewActiveProfile();
	    		profile.setHostId(hostId);
	    		
	    		DatabaseEntry key = new DatabaseEntry(hostId.getBytes());
	    		DatabaseEntry value = new DatabaseEntry();
	    		
	    		binding.objectToEntry(acdoc, value);
	    		if( OperationStatus.KEYEXIST == database.putNoOverwrite(null, key, value)){
	    			throw new DuplicateEntryException ("Active Profile already exists : " + hostId);
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return profile; 
    }
    
    public void close(){
    		try{
    			database.close();
    		}catch(Exception e){
    			log.error(e);
    		}
    }
}
