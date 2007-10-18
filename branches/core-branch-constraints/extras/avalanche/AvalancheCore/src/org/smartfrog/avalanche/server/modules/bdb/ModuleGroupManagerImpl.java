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
import org.smartfrog.avalanche.core.moduleGroup.ModuleGroupDocument;
import org.smartfrog.avalanche.core.moduleGroup.ModuleGroupType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.ModuleGroupManager;
import org.smartfrog.avalanche.server.modules.bdb.bindings.ModuleGroupBinding;

import java.util.ArrayList;

public class ModuleGroupManagerImpl implements ModuleGroupManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(ModuleGroupManagerImpl.class);
    private static ModuleGroupBinding binding = new ModuleGroupBinding();
    
    public ModuleGroupManagerImpl(Database db){
    		this.database  = db ; 
    }
    
    public ModuleGroupType getModuleGroup(String groupId)throws DatabaseAccessException{
    		ModuleGroupType group = null ; 
    		
    		DatabaseEntry key = new DatabaseEntry(groupId.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		try{
    			database.get(null, key, value, LockMode.DEFAULT);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e) ;
    		}
    		ModuleGroupDocument mgdoc = (ModuleGroupDocument)binding.entryToObject(value);
    		if( null != mgdoc ){
    			group = mgdoc.getModuleGroup();
    		}
    		return group;
    }
    
    public String []listModuleGroups() throws DatabaseAccessException{
    		String[]groups = new String[0];
    		
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
	    		groups = (String[]) listHolder.toArray(groups);
	    		cursor.close() ;
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return groups; 
    }
    public void setModuleGroup(ModuleGroupType group) throws DatabaseAccessException{
    		ModuleGroupDocument mgdoc = ModuleGroupDocument.Factory.newInstance();
    		mgdoc.setModuleGroup(group);
    		
    		DatabaseEntry key = new DatabaseEntry(group.getId().getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		binding.objectToEntry(mgdoc, value );
    		try{
    			database.put(null, key, value);
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		
    }
    public ModuleGroupType newModuleGroup(String id) throws DatabaseAccessException, DuplicateEntryException{
    		ModuleGroupType group = null ;
    		
    		ModuleGroupDocument mgdoc = ModuleGroupDocument.Factory.newInstance();
    		group = mgdoc.addNewModuleGroup();
    		group.setId(id);
    		
    		DatabaseEntry key = new DatabaseEntry(id.getBytes());
    		DatabaseEntry value = new DatabaseEntry();
    		binding.objectToEntry(mgdoc, value);
    		
    		try{
	    		if( OperationStatus.KEYEXIST == database.put(null, key, value)){
	    			throw new DuplicateEntryException("Module group already exists :" + id);
	    		}
    		}catch(DatabaseException e){
    			throw new DatabaseAccessException(e);
    		}
    		return group ; 
    }
    public void remove(String id) throws DatabaseAccessException{
    		try{
    			database.delete(null, new DatabaseEntry(id.getBytes()));
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
