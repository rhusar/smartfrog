/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jul 29, 2005
 *
 */
package org.smartfrog.avalanche.server.modules.xindice;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileDocument;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.util.XbeanUtils;
import org.w3c.dom.Document;

/**
 * @author sanjay, Jul 29, 2005
 *
 * TODO 
 */
public class ActiveProfileManagerImpl implements ActiveProfileManager{

	protected Collection collection ;
    private static Log log = LogFactory.getLog(ActiveProfileManagerImpl.class);	  
    
	public ActiveProfileManagerImpl(Collection col) {
		super();
		this.collection = col; 
	}
	
	public String []listProfiles() throws DatabaseAccessException{
		String[] hosts = new String[0];
		
		try{
			hosts = collection.listDocuments();
		}catch(DBException e){
			log.error(e);
			throw new DatabaseAccessException(e);
		}
		return hosts;
	}
	
	
	public void removeProfile(String hostId) throws DatabaseAccessException {
		// TODO Auto-generated method stub
		throw new DatabaseAccessException("Not Implemented") ;
	}

	/**
	 * Returns active profile Object if exists null otherwise. 
	 * @param hostId non-null value
	 * @return
	 * @throws DatabaseAccessException
	 */
	public ActiveProfileType getProfile(String hostId) throws DatabaseAccessException{
		ActiveProfileType profile = null;
		try{
			if( null != hostId){
				Document doc = collection.getDocument(hostId);
				if( null != doc){
					ActiveProfileDocument hdoc = ActiveProfileDocument.Factory.parse(doc);
					profile = hdoc.getActiveProfile();
				}
			}
		}catch(DBException e){
			throw new DatabaseAccessException (e);
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
		return profile;
	}
	
	/**
	 * New profile created by this method doesnt exist in the database. use setActiveProfile()
	 * method to save in DB.
	 * @param hostId
	 * @return
	 * @throws DatabaseAccessException
	 */
	public ActiveProfileType newProfile(String hostId)throws DatabaseAccessException{
		ActiveProfileType profile = null ;
		try{
			if( null != hostId ){
				ActiveProfileDocument hdoc = ActiveProfileDocument.Factory.newInstance();
				profile = hdoc.addNewActiveProfile();
				profile.setHostId(hostId);
			}
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
		return profile;
	}	
	/**
	 * Save the pprofile in database.
	 * @param profile
	 * @return
	 * @throws DatabaseAccessException
	 */
	public void setProfile(ActiveProfileType profile) 
		throws DatabaseAccessException{
		try{
			if( null != profile){
				ActiveProfileDocument hdoc = ActiveProfileDocument.Factory.newInstance();
				hdoc.setActiveProfile(profile);
		
				if(!XbeanUtils.save(profile.getHostId(), hdoc, collection)){
					log.error("Error: failed to save document, XMLContent:" + profile);
				}
			}
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
	
	}
	/**
	 * close the collection and cleanup resources.
	 *
	 */
	public void close(){
		try{
			if( null != collection ){
				if( null != collection){
					this.collection.close();
					collection = null ; 
				}else{
					log.error("Error! ActiveProfileManager.close() collection is null");
				}
			}
		}catch(DBException e){
			log.error(e); 
			e.printStackTrace();
		}
	}	
}
