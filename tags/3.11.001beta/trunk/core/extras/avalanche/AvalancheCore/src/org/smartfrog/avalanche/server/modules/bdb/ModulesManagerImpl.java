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
import org.smartfrog.avalanche.core.module.ModuleDocument;
import org.smartfrog.avalanche.core.module.ModuleType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.DuplicateEntryException;
import org.smartfrog.avalanche.server.ModulesManager;
import org.smartfrog.avalanche.server.modules.bdb.bindings.ModuleBinding;

import java.util.ArrayList;

/**
 * 
 * @author sanjaydahiya
 *
 */
public class ModulesManagerImpl implements ModulesManager{
	private Database database ; 
    private static Log log = LogFactory.getLog(ModulesManagerImpl.class);
    
    private static ModuleBinding moduleBinding = new ModuleBinding();
    
	public ModulesManagerImpl(Database db){
		database = db;
	}
	
	public ModuleType getModule(String moduleId) throws DatabaseAccessException{
		ModuleType module = null;
		try{
			DatabaseEntry key = new DatabaseEntry(moduleId.getBytes());
			DatabaseEntry value = new DatabaseEntry();
			
			this.database.get(null, key, value, LockMode.DEFAULT);
			if( null != value ){
				ModuleDocument mdoc = (ModuleDocument)moduleBinding.entryToObject(value);
				if( null != mdoc ){
					System.out.println("GetModule  " + mdoc.xmlText());
					module = mdoc.getModule();
				}
			}
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
		return module ; 
	}
	
	public String[] listModules() throws DatabaseAccessException{
		String []list = new String[0] ;
		
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
			list = (String[]) listHolder.toArray(list);
			cursor.close() ; 
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
		return list;
	}
	
	public void addModule(ModuleType m) throws DatabaseAccessException, DuplicateEntryException{
		String mid = m.getId();
		DatabaseEntry key = new DatabaseEntry(mid.getBytes());
		DatabaseEntry value = new DatabaseEntry();
		
		ModuleDocument mdoc = ModuleDocument.Factory.newInstance();
		mdoc.setModule(m);
		
		moduleBinding.objectToEntry(mdoc, value);
		try{
			if( OperationStatus.KEYEXIST == database.putNoOverwrite(null, key, value)) {
				throw new DuplicateEntryException("Module already exists : " + mid);
			}
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
	}
	
	public void removeModule(String moduleId) throws DatabaseAccessException{
		DatabaseEntry key = new DatabaseEntry(moduleId.getBytes());
		try{
			database.delete(null, key);
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
	}
	
	public ModuleType newModule(String moduleId) throws DatabaseAccessException, DuplicateEntryException{
		ModuleType module = null ; 
		
		DatabaseEntry key = new DatabaseEntry(moduleId.getBytes());
		ModuleDocument mdoc = ModuleDocument.Factory.newInstance();
		module = mdoc.addNewModule();
		module.setId(moduleId);
		
		DatabaseEntry value  = new DatabaseEntry();
		moduleBinding.objectToEntry(mdoc, value);
		try{
			if( OperationStatus.KEYEXIST == database.putNoOverwrite(null, key, value)){
				throw new DuplicateEntryException("Module already exists :" + moduleId) ;
			}
		}catch(DatabaseException e){
			throw new DatabaseAccessException(e);
		}
		return module ;
	}
	
	public void setModule(ModuleType module) throws DatabaseAccessException{
		String moduleId = module.getId();
		
		DatabaseEntry key = new DatabaseEntry(moduleId.getBytes());
		DatabaseEntry value = new DatabaseEntry();
		
		ModuleDocument mdoc = ModuleDocument.Factory.newInstance();
		mdoc.setModule(module);
		System.out.println("Setting in DB : " + mdoc.xmlText()) ;
		moduleBinding.objectToEntry(mdoc, value);
		try{
			if( OperationStatus.SUCCESS != database.put(null, key, value) ){
				log.error("DB put failed for module " + moduleId );
			}
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
