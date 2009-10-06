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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.core.hostGroup.HostGroupDocument;
import org.smartfrog.avalanche.core.hostGroup.HostGroupType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.HostGroupManager;
import org.smartfrog.avalanche.server.modules.bdb.bindings.HostGroupBinding;

import java.util.ArrayList;

public class HostGroupManagerImpl implements HostGroupManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(HostGroupManagerImpl.class);
    private static HostGroupBinding binding = new HostGroupBinding();
    
    public HostGroupManagerImpl(Database db){
    		this.database = db ;
    }
    
    public HostGroupType getHostGroup(String groupId) throws DatabaseAccessException{
    		HostGroupType hostGroup = null ; 
    		DatabaseEntry key = new DatabaseEntry(groupId.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		try{
    			database.get(null, key, value, LockMode.DEFAULT);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		
    		HostGroupDocument hgdoc = (HostGroupDocument)binding.entryToObject(value);
    		if( null != hgdoc ){
    			hostGroup = hgdoc.getHostGroup();
    		}
    		return hostGroup ; 
    }
    
    public void removeHostGroup(String hostGroupId) throws DatabaseAccessException{
		try{
			database.delete(null, new DatabaseEntry(hostGroupId.getBytes()));
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
    }

    public String []listHostGroups() throws DatabaseAccessException{
    		String []hostGroups = new String[0];
    		
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
	    		hostGroups = (String[]) listHolder.toArray(hostGroups);
	    		cursor.close();
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return hostGroups; 
    }
    
    public HostGroupType newHostGroup(String groupId) throws DatabaseAccessException, DuplicateEntryException{
    		HostGroupDocument hgdoc = HostGroupDocument.Factory.newInstance();
    		HostGroupType group = hgdoc.addNewHostGroup();
    		group.setId(groupId);
    		
    		DatabaseEntry key = new DatabaseEntry(groupId.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		
    		binding.objectToEntry(hgdoc, value);
    		try{
	    		if( OperationStatus.KEYEXIST == database.putNoOverwrite(null, key, value)){
	    			throw new DuplicateEntryException("Host Group already exist : "+ groupId);
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return group;
    }
    
    public void setHostGroup(HostGroupType group)throws DatabaseAccessException{
    		HostGroupDocument hgdoc = HostGroupDocument.Factory.newInstance();
    		hgdoc.setHostGroup(group);
    		
    		String id = group.getId();
    		DatabaseEntry key = new DatabaseEntry(id.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		binding.objectToEntry(hgdoc, value);
    		try{
    			database.put(null, key, value);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    }
    
    public void close(){
    		try{
    			database.close();
    		}catch(DatabaseException e){
    			log.error(e);
    		}
    }
}
