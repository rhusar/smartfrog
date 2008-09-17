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
import org.smartfrog.avalanche.core.hostGroup.HostGroupDocument;
import org.smartfrog.avalanche.core.hostGroup.HostGroupType;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.HostGroupManager;
import org.smartfrog.avalanche.util.XbeanUtils;
import org.w3c.dom.Document;

/**
 * @author sanjay, Jun 26, 2005
 *
 * TODO 
 */
public class HostGroupManagerImpl implements HostGroupManager{

	protected Collection collection ;
	private static Log log = LogFactory.getLog(HostGroupManagerImpl.class);	
	
	public HostGroupManagerImpl(Collection col) {
		super();
		this.collection = col;
	} 
	
	public String []listHostGroups() throws DatabaseAccessException{
		String []hosts = null ;
		
		try{
			hosts = this.collection.listDocuments();
		}catch(DBException e){
			throw new DatabaseAccessException(e);
		}
		return hosts;
	}
	
	public HostGroupType getHostGroup(String hostGroupId) throws DatabaseAccessException{
		HostGroupType h = null;
		try{
			if( null != hostGroupId){
				Document doc = collection.getDocument(hostGroupId);
				if( null != doc){
					HostGroupDocument hdoc = HostGroupDocument.Factory.parse(doc);
					h = hdoc.getHostGroup();
				}
			}
		}catch(DBException e){
			throw new DatabaseAccessException (e);
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
		return h;
	}

	
	public void removeHostGroup(String hostGroupId) throws DatabaseAccessException {
		// TODO Auto-generated method stub
		throw new DatabaseAccessException("Not Implemented ") ;
	}

	public HostGroupType newHostGroup(String hostGroupId)throws DatabaseAccessException{
		HostGroupType hostGroup = null ;
		try{
			if( hostGroupId != null ){
				HostGroupDocument hdoc = HostGroupDocument.Factory.newInstance();
				hostGroup = hdoc.addNewHostGroup();
				hostGroup.setId(hostGroupId);
			}
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
		return hostGroup ;
	}
	
	public void setHostGroup(HostGroupType h) 
		throws DatabaseAccessException{
		try{
			if( null != h){
				HostGroupDocument hdoc = HostGroupDocument.Factory.newInstance();
				hdoc.setHostGroup(h);
				
				if(!XbeanUtils.save(h.getId(), hdoc, collection)){
					log.error("Error : Saving document, XML content : " + h);
				}
			}
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
	
	}	

	public void close(){
		try{
			if( null != collection){
				this.collection.close();
				collection = null;
			}else{
				log.error("Error: Null collection in HostGroupManager.close()");
			}
			
		}catch(DBException e){
			log.error(e);
			e.printStackTrace();
		}
	}
}
