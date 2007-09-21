/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jun 25, 2005
 *
 */
package org.smartfrog.avalanche.server.modules.xindice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.smartfrog.avalanche.core.module.ModuleDocument;
import org.smartfrog.avalanche.core.module.ModuleType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.ModulesManager;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.util.XbeanUtils;
import org.w3c.dom.Document;

/**
 * @author sanjay, Jun 25, 2005
 *
 */
public class ModulesManagerImpl implements ModulesManager{

	protected Collection modulesCol ;
    private static Log log = LogFactory.getLog(ModulesManagerImpl.class);	  
	
	
	public ModulesManagerImpl(Collection col) throws ModuleCreationException{
		super();
		if( null == col){
			throw new ModuleCreationException("Error : Null collection for Module creation");
		}
		modulesCol = col;
	}
	
	/**
	 * returns module if it exists in database, else returns null.
	 * @param moduleId if null returns null.
	 * @return
	 * @throws DatabaseAccessException
	 */
	public ModuleType getModule(String moduleId) throws DatabaseAccessException{
		ModuleType m = null;
		try{
			if( null != moduleId ){
				Document doc = modulesCol.getDocument(moduleId);
				if( null != doc){
					ModuleDocument mdoc = ModuleDocument.Factory.parse(doc);
					m = mdoc.getModule();
				}
			}
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException (e);
		}catch(Exception e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return m;
		
	}
	/**
	 * Lists all modules
	 * @return
	 * @throws DatabaseAccessException
	 */
	public String [] listModules() throws DatabaseAccessException{
		String []modules = null;
		try{
			modules = modulesCol.listDocuments();
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return modules ;
	}

	/**
	 * Remove a module from database.
	 * @param moduleId
	 * @throws DatabaseAccessException
	 */
	public void removeModule(String moduleId) throws DatabaseAccessException{
		try{
			log.debug("Removing module " + moduleId);
			modulesCol.remove(moduleId);
			modulesCol.flushSymbolTable();
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
	}
	/**
	 * New module is not created in database, use setModule() method to save changes back in database. 
	 * @param moduleId
	 * @return
	 * @throws DatabaseAccessException
	 */
	public ModuleType newModule(String moduleId) throws DatabaseAccessException{
		ModuleType m = null ;
		try{
			ModuleDocument mdoc = ModuleDocument.Factory.newInstance();
			m = mdoc.addNewModule();
			m.setId(moduleId);

			log.debug("New module : " + moduleId);
		}catch(Exception e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return m ;
	}
	/**
	 * Save a module in database
	 * @param m
	 * @return Saved Module if successful otherwise null. 
	 * @throws DatabaseAccessException
	 */
	public void setModule(ModuleType m) 
		throws DatabaseAccessException{
		try{
			if( null != m ){
				ModuleDocument mdoc = ModuleDocument.Factory.newInstance();
				mdoc.setModule(m);
	
				if( ! XbeanUtils.save(m.getId(), mdoc, this.modulesCol) ){
					log.debug("Error ! Saving document failed, XML content: " + m);
				}
			}
		}catch(Exception e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
	}
	
	/**
	 * Close and cleanup.
	 *
	 */
	public void close(){
		try{
			if( null != modulesCol){
				this.modulesCol.close();
				log.debug("Closing Avalanche Repository");
			}else{
				log.debug("Error! Closing Avalanche Repository already null");
			}
		}catch(DBException e){
			log.error(e);
			e.printStackTrace();
		}
	}
}
