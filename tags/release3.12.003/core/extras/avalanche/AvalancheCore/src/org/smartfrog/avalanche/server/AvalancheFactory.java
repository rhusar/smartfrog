/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.server.modules.ModuleCreationException;
import org.smartfrog.avalanche.server.modules.bdb.BDBFactoryImpl;
import org.smartfrog.avalanche.server.modules.xindice.XindiceFactoryImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Main access point for Avalanche Database, its an abstract factory class 
 * use - AvalancheFactory.getFactory(AvalancheFactory.BDB) to get a concrete instance 
 * which uses Berkely DB. Xindice factory is now deprecated. 
 * AvalancheFactory.init() should be called before using it for the first time. 
 * @author sanjaydahiya
 *
 */
public abstract class AvalancheFactory {
	
	public static final int XINDICE = 1;
	public static final int BDB = 2;
	public static String AVALANCHE_SERVER_NAME = "AVALANCHE_SERVER_NAME" ;
	private static AvalancheFactory xindiceFactory = new XindiceFactoryImpl();
	private static AvalancheFactory bdbFactory = new BDBFactoryImpl();
	private Map attributes = new HashMap(); 
	
	protected AvalancheFactory(){
		
	}
	/**
	 * Main access point to Avalanche Database. 
	 * @param type type should be AvalancheFactory.BDB
	 * @return
	 */
	public static AvalancheFactory getFactory(int type){
		AvalancheFactory factory = null ;
		switch(type){
		case XINDICE :
			factory = xindiceFactory ;
			break;
		case BDB:
			factory = bdbFactory ;
		}
		return factory ; 
	}
	
	public String getAttribute(String attrName){
		return (String)attributes.get(attrName);
	}
	
	/**
	 * Allows for adding any system level configuration attributes.
	 * mendatory Attributes are - 
	 * AVALANCHE_SERVER_NAME - Name of the Avalanche webapp.   
	 * 	This is usuallly the URL after server on which avalanche is running.
	 *  e.g. in http://server/avalanche, AVALANCHE_SERVER_NAME is "avalanche" 
	 * @param name 
	 * @param value
	 */
	public void setAttribute(String name, String value){
		attributes.put(name, value);
	}
	/**
	 * Initializes Avalanche database, if database doesnt exist in given AVALANCHE_HOME
	 * it automatically creates all database related files there. 
	 * 
	 * @param home fully qualified path to AVALANCHE_HOME
	 * @throws ModuleCreationException
	 */
	 abstract public void  init(String home) throws ModuleCreationException ;
	 /**
	  * Returns a ModuleManager. 
	  * @return
	  * @throws ModuleCreationException
	  */
	 abstract public ModulesManager getModulesManager() throws ModuleCreationException ;
	 /**
	  * Returns AVALANCHE_HOME as passed in init().
	  * @return
	  */
 	 abstract public String getAvalancheHome();
 	 /**
 	  * Returns interface to access Host data in Avalanche database. 
 	  * @return
 	  * @throws ModuleCreationException
 	  */
  	 abstract public HostManager getHostManager() throws ModuleCreationException ;
  	 /**
 	  * Returns interface to access Host Group data in Avalanche database. 
  	  * @return
  	  * @throws ModuleCreationException
  	  */
  	 abstract public HostGroupManager getHostGroupManager()throws ModuleCreationException ;
  	 /**
  	  * Returns interface to access Settings in Avalanche database, default settings these map to initsettings.xml file.
  	  * @return
  	  * @throws ModuleCreationException
  	  */
 	 abstract public SettingsManager getSettingsManager() throws ModuleCreationException ;
 	 /**
 	  * Returns interface to access Host Active Profiles data in Avalanche database. 
 	  * @return
 	  * @throws ModuleCreationException
 	  */
 	 abstract public ActiveProfileManager getActiveProfileManager()throws ModuleCreationException ;
 	 /**
 	  * Returns interface to access Host Default Profiles data in Avalanche database. 
 	  * @return
 	  * @throws ModuleCreationException
 	  */
 	 abstract public DefaultProfileManager getDefaultProfileManager() throws ModuleCreationException ;
 	 /**
 	  * Returns interface to access Module Group data in Avalanche database. 
 	  * @return
 	  * @throws ModuleCreationException
 	  */
 	 abstract public ModuleGroupManager getModuleGroupManager() throws ModuleCreationException;
 	 /**
 	  * Closes all open databases. 
 	  *
 	  */
 	 abstract public void close();
}
