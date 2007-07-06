/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb;

import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.core.defaultHostProfile.DefaultProfileDocument;
import org.smartfrog.avalanche.core.defaultHostProfile.DefaultProfileType;
import org.smartfrog.avalanche.core.module.PlatformSelectorType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DefaultProfileManager;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.modules.bdb.bindings.DefaultProfileBinding;

import java.util.ArrayList;

public class DefaultProfileManagerImpl implements DefaultProfileManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(DefaultProfileManagerImpl.class);
    private static DefaultProfileBinding binding = new DefaultProfileBinding();
    
    public DefaultProfileManagerImpl(Database db ){
    		this.database = db ; 
    }

	public void close() {
   		try{
			database.close();
		}catch(Exception e){
			log.error(e);
		}
	}

	/**
	 * First profile that matches the selector using Java regular expression is 
	 * returned. 
	 */
	public DefaultProfileType getProfile(PlatformSelectorType selector) throws DatabaseAccessException {
		String []profiles = listProfiles(); 
		DefaultProfileType profile = null ; 
		
		for( int i=0;i<profiles.length; i++ ){
			TupleInput ti = new TupleInput(profiles[i].getBytes());
			String os = ti.readString();
			String plaf = ti.readString();
			String arch = ti.readString();
			
			// TODO: do we need case insensitive matching here ?? 
			if( 	selector.getOs().matches(os) && 
					selector.getArch().matches(arch) &&
					selector.getPlatform().matches(plaf)){
				// get the profile from here
		   		try{
			    		DatabaseEntry key = new DatabaseEntry(profiles[i].getBytes());
			    		DatabaseEntry value = new DatabaseEntry();
			    		
			    		database.get(null, key, value, LockMode.DEFAULT);
			    		
			    		DefaultProfileDocument defdoc = (DefaultProfileDocument)binding.entryToObject(value);
			    		profile = defdoc.getDefaultProfile();
		   		}catch(DatabaseException e){
		   			log.error(e);
		   			throw new DatabaseAccessException(e);
		   		}
				break;
			}
			
		}
		return profile;
	}

	/**
	 * 
	 */
	public String[] listProfiles() throws DatabaseAccessException {
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

	public DefaultProfileType newProfile(PlatformSelectorType selector) 
						throws DatabaseAccessException, DuplicateEntryException {
		String os = selector.getOs() ;
		String plaf = selector.getPlatform();
		String arch = selector.getArch() ;
		
		// these are all regular expressions, create a key based on these, 
		// efficient lookup is not a criterion here. 
		// so just use the three values to create a key directly. 
		
		DefaultProfileType profile = null; 
		try{
	    		DefaultProfileDocument defdoc = DefaultProfileDocument.Factory.newInstance();
	    		profile = defdoc.addNewDefaultProfile();
	    		
	    		PlatformSelectorType psel = profile.addNewPlatformSelector();
	    		psel.setArch(arch);
	    		psel.setOs(os);
	    		psel.setPlatform(plaf);
	    		
	    		TupleOutput out = new TupleOutput(); 
	    		out.writeString(os);
	    		out.writeString(plaf);
	    		out.writeString(arch);
	    		
	    		DatabaseEntry key = new DatabaseEntry(out.toByteArray());
	    		DatabaseEntry value = new DatabaseEntry();
	    		
	    		binding.objectToEntry(defdoc, value);
	    		if( OperationStatus.KEYEXIST == database.putNoOverwrite(null, key, value)){
	    			throw new DuplicateEntryException ("Default Profile already exists for host type : ");
	    		}
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
		return profile; 		
	}

	public void setProfile(DefaultProfileType profile) throws DatabaseAccessException {
		PlatformSelectorType selector = profile.getPlatformSelector();
		String os = selector.getOs() ;
		String plaf = selector.getPlatform();
		String arch = selector.getArch() ;
		
		try{
	    		DefaultProfileDocument defdoc = DefaultProfileDocument.Factory.newInstance();
	    		defdoc.setDefaultProfile(profile);
	    		
	    		TupleOutput out = new TupleOutput(); 
	    		out.writeString(os);
	    		out.writeString(plaf);
	    		out.writeString(arch);
	    		
	    		DatabaseEntry key = new DatabaseEntry(out.toByteArray());
	    		DatabaseEntry value = new DatabaseEntry();
	    		
	    		binding.objectToEntry(defdoc, value);
	    		database.put(null, key, value) ;
	    		
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}		
	}

	/**
	 * Deletes the exact matching Default profile, its not a regex match. 
	 */
	public void remove(PlatformSelectorType selector) throws DatabaseAccessException {
		try{
	    		TupleOutput out = new TupleOutput(); 
	    		out.writeString(selector.getOs());
	    		out.writeString(selector.getPlatform());
	    		out.writeString(selector.getArch());
	    		
	    		DatabaseEntry key = new DatabaseEntry(out.toByteArray());			
			database.delete(null, key );
 		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}		
	}
}
