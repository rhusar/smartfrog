/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jun 26, 2005
 *
 */
package org.smartfrog.avalanche.server.modules.xindice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.smartfrog.avalanche.core.host.HostDocument;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.server.monitor.handlers.HostUpdateHandler;
import org.smartfrog.avalanche.util.XbeanUtils;
import org.w3c.dom.Document;

/**
 * @author sanjay, Jun 26, 2005
 *
 */
public class HostManagerImpl implements HostManager{

	protected Collection collection ; 
    private static Log log = LogFactory.getLog(HostManagerImpl.class);	  
	
	public HostManagerImpl(Collection col) throws ModuleCreationException{
		if( null == col ){
			throw new ModuleCreationException ("Null collection for HostManager");
		}
		this.collection = col;
	}
	/**
	 * Creates and returns a list of all hostsIds present in the database.
	 * @return
	 * @throws DatabaseAccessException
	 */
	public String [] listHosts() throws DatabaseAccessException{
		String []hosts = null ;
		
		try{
			hosts = this.collection.listDocuments();
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return hosts;
	}
	
	/**
	 * This method does not save the new Host in database, user setHost() method to save
	 * the new Host. 
	 * @param hostId should be non-null otherwise this method fails to create a new HostType
	 * @return
	 * @throws DatabaseAccessException
	 */
	public HostType newHost(String hostId)throws DatabaseAccessException{
		HostType host = null ;
		try{
			if( null != hostId ){
				HostDocument hdoc = HostDocument.Factory.newInstance();
				host = hdoc.addNewHost();
				host.setId(hostId);
			}
		}catch(Exception e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return host ;
	}
	/**
	 * Save the HostType in database. 
	 * @param h ignored if null without error.
	 * @return
	 * @throws DatabaseAccessException
	 */
	public void setHost(HostType h) 
		throws DatabaseAccessException{
		try{
			if( null != h){

				HostDocument hdoc = HostDocument.Factory.newInstance();
				hdoc.setHost(h);
		
				XbeanUtils.save(h.getId(), hdoc, this.collection);
			}else{
				log.debug("Null HostType to save in database");
			}
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
	
	}	
	/**
	 * gets the host from database, if hostId doesnot exist in DB it returns null.
	 * @param hostId if null this methods returns null.
	 * @return
	 * @throws DatabaseAccessException
	 */

    public HostType getHost(String hostId) throws DatabaseAccessException{
		HostType h = null;
		try{
			if( null != hostId){
				Document doc = collection.getDocument(hostId);
				if( null != doc){
					HostDocument hdoc = HostDocument.Factory.parse(doc);
					h = hdoc.getHost();
				}
			}
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException (e);
		}catch(Exception e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return h;
		
	}

    public void removeHost (HostType h) throws DatabaseAccessException{
		try{ 
			if( null != h.getId()){
				this.collection.remove(h.getId());
				log.debug("Removed Host :" + h.getId());
				this.collection.flushSymbolTable();
			}
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
	}
	

	public void addHandler(HostUpdateHandler handler) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Close the collection associated with HostManager. This HostManager will not be usable after this.
	 *
	 */
	public void close(){
		try{
			if( this.collection == null){
				log.error("Inconsistant HostManager : Null collection");
			}else{
				log.debug("closing collection : " + this.collection.getName());
				this.collection.close();
			}
		}catch(DBException e){
			log.error(e);
			e.printStackTrace();
		}
	}
}
