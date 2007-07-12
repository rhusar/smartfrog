/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server.modules.bdb;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.DefaultProfileManager;
import org.smartfrog.avalanche.server.HostGroupManager;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.ModuleGroupManager;
import org.smartfrog.avalanche.server.ModulesManager;
import org.smartfrog.avalanche.server.SettingsManager;
import org.smartfrog.avalanche.server.modules.ModuleCreationException;

import java.io.File;

public class BDBFactoryImpl extends AvalancheFactory{
	private static Log log = LogFactory.getLog(BDBFactoryImpl.class);
	
	private static DatabaseManager databaseManager ;
	
	private static ModulesManagerImpl modulesManager = null ;
	private static HostManager hostManager = null;
	private static HostGroupManager hostGroupManager = null;
	private static SettingsManager settingsManager = null;
	private static DefaultProfileManager defaultProfileManager = null ;
	private static ModuleGroupManager moduleGroupManager = null;
	private static ActiveProfileManager activeProfileManager = null ;
	
	public static final String MODULES = "modules";
	public static final String MODULEGROUPS = "moduleGroups";
	public static final String HOSTS = "hosts";
	public static final String HOSTGROUPS = "hostGroups";
	public static final String SETTINGS = "settings";
	public static final String ACTIVE_PROFILES = "activeProfiles";
	public static final String DEFAULT_PROFILES = "defaultProfiles" ;
	
	private static String avalancheHome ; 
	
	public void  init(String home) throws ModuleCreationException{
		avalancheHome = home ; 
		try{
			String dbPath = avalancheHome + File.separator + "data" + File.separator + "bdb" ;
			File dir = new File(dbPath);
			if( ! dir.exists() ){
				dir.mkdirs();
			}
			databaseManager = new DatabaseManager(dbPath);
		}catch(DatabaseException e){
			log.fatal("Failed to access database directory", e);
		}
	}
	/**
	 * Returns AvalancheRepository, if required database collection doesnt exist it creates it. 
	 * @return
	 * @throws ModuleCreationException
	 */
	public ModulesManager getModulesManager() throws ModuleCreationException{
		if( null == modulesManager ){
			try{
				Database db = databaseManager.getDatabase(MODULES);
				modulesManager = new ModulesManagerImpl(db);
			}catch(DatabaseException e){
				log.fatal(e);
				throw new ModuleCreationException(e);
			}
		}
		return modulesManager;
	}
	/**
	 * Home directory for database . 
	 * @return
	 */
	public String getAvalancheHome(){
		return avalancheHome ;
	}
	
	/**
	 * Gets the HostManager, initializes it if needed.
	 * @return
	 * @throws ModuleCreationException
	 */
	public HostManager getHostManager() throws ModuleCreationException{
		if( null == hostManager ){
			try{
				Database db = databaseManager.getDatabase(HOSTS);
				hostManager = new HostManagerImpl(db);
			}catch(DatabaseException e){
				log.fatal(e);
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
		if( null == hostGroupManager){
			try{
				Database db = databaseManager.getDatabase(HOSTGROUPS);
				hostGroupManager = new HostGroupManagerImpl(db);
			}catch(DatabaseException e){
				log.fatal(e);
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
	public SettingsManager getSettingsManager() throws ModuleCreationException{
		if( settingsManager == null ){
			try{
				Database db = databaseManager.getDatabase(SETTINGS);
				settingsManager = new SettingsManagerImpl(db);
				
			}catch(DatabaseException e){
				log.fatal(e);
				throw new ModuleCreationException(e);
			}
		}
		return settingsManager ;
	} 
		
	public ActiveProfileManager getActiveProfileManager()throws ModuleCreationException {
		if( null == activeProfileManager){
			try{
				Database db = databaseManager.getDatabase(ACTIVE_PROFILES);
				activeProfileManager = new ActiveProfileManagerImpl(db);
			}catch(DatabaseException e){
				log.fatal(e);
				throw new ModuleCreationException(e);
			}
		}
		return activeProfileManager;
	}
	
	public DefaultProfileManager getDefaultProfileManager() throws ModuleCreationException{
		if( null == defaultProfileManager ){
			try{
				Database db = databaseManager.getDatabase(DEFAULT_PROFILES);
				defaultProfileManager = new DefaultProfileManagerImpl(db) ;
			}catch(DatabaseException e){
				log.fatal(e);
				throw new ModuleCreationException(e);
			}
		}
		return defaultProfileManager;
	}
	public ModuleGroupManager getModuleGroupManager() throws ModuleCreationException{
		if( null == moduleGroupManager ){
			try{
				Database db = databaseManager.getDatabase(MODULEGROUPS);
				moduleGroupManager = new ModuleGroupManagerImpl(db);
			}catch(DatabaseException e){
				log.fatal(e);
				throw new ModuleCreationException(e);
			}
		}
		return moduleGroupManager;
	}
	
	
	public void close(){
		// If databases are closed externally it may give error in logs
		// check if there is a method to check that 
		try{
			if( modulesManager != null ){
				modulesManager.close();
				modulesManager = null;
			}
			if( hostManager != null ){
				hostManager.close();
				hostManager = null;
			}
			if( hostGroupManager != null){
				hostGroupManager.close();
				hostGroupManager = null;
			}
			if( settingsManager != null ){
				settingsManager.close();
				settingsManager = null;
			}
			if( activeProfileManager != null ){
				activeProfileManager.close();
				activeProfileManager = null;
			}
			if( moduleGroupManager != null ){
				moduleGroupManager.close();
				moduleGroupManager = null;
			}
			if( defaultProfileManager != null ){
				defaultProfileManager.close();
				defaultProfileManager = null;
			}
			
			if( null != databaseManager ){
				// this closes the database itself
				databaseManager.close();
				databaseManager = null; 
			}
		}catch(Exception e){
			// dont stop just log
			log.error(e);
		}
	}

}
