/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on Jun 29, 2005
 *
 */
package org.smartfrog.avalanche.server.modules.xindice;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.smartfrog.avalanche.server.DatabaseAccessException;
import org.smartfrog.avalanche.server.SettingsManager;
import org.smartfrog.avalanche.settings.sfConfig.SfConfigsDocument;
import org.smartfrog.avalanche.settings.sfConfig.SfConfigsType;
import org.smartfrog.avalanche.settings.xdefault.SettingsDocument;
import org.smartfrog.avalanche.settings.xdefault.SettingsType;
import org.smartfrog.avalanche.util.XMLUtils;
import org.smartfrog.avalanche.util.XbeanUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

/**
 * @author sanjay, Jun 29, 2005
 *
 * TODO 
 */
public class SettingsManagerImpl implements SettingsManager{
	protected Collection collection ;
	public static String SFCONFIG = "sfConfig" ;
	
	private static Log log = LogFactory.getLog(SettingsManagerImpl.class);	
	private String avalancheHome ; 
	
	public SettingsManagerImpl(Collection col, String home) {
		super();
		this.collection = col;
		avalancheHome = home ; 
	}
	
	public SettingsType getDefaultSettings() throws DatabaseAccessException{
		SettingsType settings = null; 
		try{
			
			String settingsFile = avalancheHome + File.separator + "conf" + 
							File.separator + "initsettings.xml";   
			Document doc = XMLUtils.load(settingsFile, false);
			SettingsDocument sdoc = SettingsDocument.Factory.parse(doc);
			settings = sdoc.getSettings();
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
		return settings;
	}
	public void setDefaultSettings(SettingsType settings) throws DatabaseAccessException{
		try{
			SettingsDocument sdoc = SettingsDocument.Factory.newInstance();
			sdoc.setSettings(settings);
			
			Node n = sdoc.newDomNode() ;

			String settingsFile = avalancheHome + File.separator + "conf" + 
				File.separator + "initsettings.xml";   
			
			XMLUtils.docToStream(n, new java.io.FileOutputStream(settingsFile));	
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
	}  
	/**
	 * 
	 * 
	 * @return
	 * @throws DatabaseAccessException
	 */
	public SfConfigsType getSFConfigs() throws DatabaseAccessException {
		SfConfigsType configs = null; 
		try{
			Document doc = this.collection.getDocument(SFCONFIG);
			if( null == doc){
				SfConfigsDocument sdoc = SfConfigsDocument.Factory.newInstance();
				configs = sdoc.addNewSfConfigs();
			}else{
				SfConfigsDocument sdoc = SfConfigsDocument.Factory.parse(doc);
				configs = sdoc.getSfConfigs();
			}
		}catch(Exception e){
			throw new DatabaseAccessException(e);
		}
		return configs;
	}
	/**
	 * Save new configuration in the database.
	 * @param configs
	 * @return
	 * @throws DatabaseAccessException
	 */
	public void setSfConfigs(SfConfigsType configs) throws DatabaseAccessException{
		try{
			if( null != configs ){
				SfConfigsDocument sdoc = SfConfigsDocument.Factory.newInstance();
				sdoc.setSfConfigs(configs);
				if( !XbeanUtils.save(SFCONFIG, sdoc, this.collection) ){
					log.error("Error : document Save failed");
				}
				
			}else{
				log.debug("Error: Null sfConfigs to save");
			}
		}catch(DBException e){
			throw new DatabaseAccessException(e);
		}
	}
	
	/**
	 * Close and clean up database resources.
	 *
	 */
	public void close(){
		try{
			if( null != collection){
				this.collection.close();
			}else{
				log.error("Error: collection null in close : " + this.collection.getName());
			}
		}catch(DBException e){
			log.error(e);
			e.printStackTrace();
		}
	}
	
}
