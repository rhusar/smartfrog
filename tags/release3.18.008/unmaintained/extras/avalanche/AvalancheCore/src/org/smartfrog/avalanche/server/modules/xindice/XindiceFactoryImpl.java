/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
/*
 * Created on May 7, 2005
 *
 */
package org.smartfrog.avalanche.server.modules.xindice;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xindice.core.Collection;
import org.apache.xindice.core.DBException;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.DefaultProfileManager;
import org.smartfrog.avalanche.server.HostGroupManager;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.ModuleGroupManager;
import org.smartfrog.avalanche.server.ModulesManager;
import org.smartfrog.avalanche.server.RepositoryConfig;
import org.smartfrog.avalanche.server.SettingsManager;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.util.xindice.XindiceHelper;

/**
 * @author sanjay, May 7, 2005
 *
 * TODO 
 */

public class XindiceFactoryImpl extends AvalancheFactory{
	private static Log log = LogFactory.getLog(XindiceFactoryImpl.class);
	private static XindiceHelper helper = null ;
	
	private static ModulesManagerImpl repository = null ;
	private static HostManagerImpl hostManager = null;
	private static HostGroupManagerImpl hostGroupManager = null;
	private static SettingsManagerImpl settings = null;
	private static ActiveProfileManagerImpl activeProfileManager = null ;
	
	public static String MODULES_COLNAME = "modules";
	public static String HOSTS_COLNAME = "hosts";
	public static String HOSTGROUPS_COLNAME = "hostGroups";
	public static String SETTINGS_COLNAME = "settings";
	public static String ACTIVE_PROFILES_COLNAME = "activeProfiles";
	
	private static String avalancheHome ; 
	
	
	public XindiceFactoryImpl(){
		
	}
	public void  init(String home) throws ModuleCreationException{
		avalancheHome = home ; 
		String fs = java.io.File.separator ;
		String dbPath = avalancheHome + fs + "data" + fs + "xindice" ;

		if( null == home){
			throw new ModuleCreationException("Error: Null path for AvalancheFactory");
		}
		if( null == helper){
			RepositoryConfig cfg = new RepositoryConfig();
			cfg.setAttribute(XindiceHelper.DB_PATH, dbPath);
			
			helper = new XindiceHelper();
			helper.init(cfg);
		}
	}
	/**
	 * Returns AvalancheRepository, if required database collection doesnt exist it creates it. 
	 * @param path
	 * @return
	 * @throws ModuleCreationException
	 */
	public ModulesManager getModulesManager() throws ModuleCreationException{
		
		if( null == helper ){
			throw new ModuleCreationException("Xindice Database not Initialized");
		}
		
		if( repository == null ){
			try{
				Collection col = helper.getCollection(MODULES_COLNAME);
				if ( null == col){
					// create a new collection if it doesnt exist when running first time.. 
					col = helper.createCollection("/", MODULES_COLNAME);
				}
				repository = new ModulesManagerImpl(col);
				
			}catch(DBException e){
				log.error(e);
				throw new ModuleCreationException(e);
			}
		}
		return repository ;
	}
	public String getAvalancheHome(){
		return avalancheHome ;
	}
	
	/**
	 * Gets the HostManager, initializes it if needed.
	 * @return
	 * @throws ModuleCreationException
	 */
	public HostManager getHostManager() throws ModuleCreationException{
		if( null == helper ){
			throw new ModuleCreationException("Xindice Database not Initialized");
		}
		if( null == hostManager){
			try{
				Collection col = helper.getCollection(HOSTS_COLNAME);
				if ( null == col){
					// create a new collection, happens when running first time.
					col = helper.createCollection("/", HOSTS_COLNAME);
					
				}
				hostManager = new HostManagerImpl(col);
	
			}catch(DBException e){
				log.error(e);
				throw new ModuleCreationException(e);
			}
		}
		return hostManager;
	}	
	/**
	 * 
	 * @return
	 * @throws ModuleCreationException
	 */
	public HostGroupManager getHostGroupManager()throws ModuleCreationException{
		if( null == helper ){
			throw new ModuleCreationException("Xindice Database not Initialized");
		}
		if( hostGroupManager == null){
			try{
				Collection col = helper.getCollection(HOSTGROUPS_COLNAME);
				if ( null == col){
					// create a new repository if it doesnt exist. 
					col = helper.createCollection("/", HOSTGROUPS_COLNAME);
					
				}
				hostGroupManager = new HostGroupManagerImpl(col);
				
			}catch(DBException e){
				log.error(e);
				throw new ModuleCreationException(e);
			}
		}
		return hostGroupManager;
	}
	/**
	 * 
	 * @return
	 * @throws ModuleCreationException
	 */
	public SettingsManagerImpl getSettings() throws ModuleCreationException{
		if( null == helper ){
			throw new ModuleCreationException("Xindice Database not Initialized");
		}
		
		if( settings == null ){
			try{
				Collection col = helper.getCollection(SETTINGS_COLNAME);
				if ( null == col){
					// create a new repository if it doesnt exist. 
					col = helper.createCollection("/", SETTINGS_COLNAME);
					
				}
				settings = new SettingsManagerImpl(col, avalancheHome); 
			}catch(DBException e){
				log.error(e);
				throw new ModuleCreationException(e);
			}catch(Exception e){
				log.error(e);
				throw new ModuleCreationException(e);
			}
		}
		return settings;
	} 
	/**
	 * 
	 * @return
	 * @throws ModuleCreationException
	 */
	public ActiveProfileManagerImpl getActiveHostProfileManager() throws ModuleCreationException{

		if( null == helper ){
			throw new ModuleCreationException("Xindice Database not Initialized");
		}
		
		if( activeProfileManager == null ){
			try{
				Collection col = helper.getCollection(ACTIVE_PROFILES_COLNAME);
				if ( null == col){
					// create a new repository if it doesnt exist. 
					col = helper.createCollection("/", ACTIVE_PROFILES_COLNAME);
					
				}
				activeProfileManager = new ActiveProfileManagerImpl(col); 
			}catch(DBException e){
				log.error(e);
				throw new ModuleCreationException(e);
			}catch(Exception e){
				log.error(e);
				throw new ModuleCreationException(e);
			}
		}
		return activeProfileManager;
	}
	
	
	public ActiveProfileManager getActiveProfileManager() throws ModuleCreationException {
		// TODO Auto-generated method stub
		return null;
	}
	public DefaultProfileManager getDefaultProfileManager() throws ModuleCreationException {
		// TODO Auto-generated method stub
		return null;
	}
	public ModuleGroupManager getModuleGroupManager() throws ModuleCreationException {
		// TODO Auto-generated method stub
		return null;
	}
	public SettingsManager getSettingsManager() throws ModuleCreationException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Cleanup all resources. 
	 *
	 */	
	public void close(){
			try{
				if( repository != null ){
					repository.close();
					repository = null;
				}
				if( hostManager != null ){
					hostManager.close();
					hostManager = null;
				}
				if( hostGroupManager != null){
					hostGroupManager.close();
					hostGroupManager = null;
				}
				if( settings != null ){
					settings.close();
					settings = null;
				}
				if( activeProfileManager != null ){
					activeProfileManager.close();
					activeProfileManager = null;
				}
				if( null != helper ){
					// this closes the database itself
					helper.close();
					helper = null; 
				}
			}catch(Exception e){
				// dont stop just log
				log.error(e);
			}
	}
}
