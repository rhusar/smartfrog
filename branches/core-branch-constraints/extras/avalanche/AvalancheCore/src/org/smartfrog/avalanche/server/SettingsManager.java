/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.settings.sfConfig.SfConfigsType;
import org.smartfrog.avalanche.settings.xdefault.SettingsType;
/**
 * Provides access to system settings in Avalanche Server. 
 * Following example shows usage of settings manager
 * <pre>
 * 	<code>
 * 		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		SettingsManager settings = factory.getSettingsManager();
		{
			SettingsType defaults  =  settings.getDefaultSettings();
			
			defaults.addArch("IA64") ;
			defaults.addArch("x86") ;
			
			{
			SettingsType.Action action = defaults.addNewAction();
			action.setName("INSTALL"); 
			}
			{
				SettingsType.Action action = defaults.addNewAction();
				action.setName("UNINSTALL"); 
			}
			
			defaults.addSystemProperty("JAVA_HOME");
			defaults.addSystemProperty("TOMCAT_HOME");
			defaults.addSystemProperty("AVALANCHE_HOME");
			
			settings.setDefaultSettings(defaults);
		}
		{
			
			SettingsType defaults  =  settings.getDefaultSettings();
			// validate all the data we added 
			assertNotNull(defaults);
			System.out.println(defaults.xmlText());
			
		}
		
		factory.close();

 * 	</code>
 * </pre>
 * @author sanjaydahiya
 *
 */
public interface SettingsManager {
	/**
	 * Returns defalt settings as given in conf/initsettings.xml in Avalanche server. 
	 * @return
	 * @throws DatabaseAccessException
	 */
	   public SettingsType getDefaultSettings() throws DatabaseAccessException ; 
 	   public void setDefaultSettings(SettingsType settings) throws DatabaseAccessException ;
 	   /**
 	    * Returns smartfrog action mapping from Avalanche Server. 
 	    * @return
 	    * @throws DatabaseAccessException
 	    */
 	   public SfConfigsType getSFConfigs() throws DatabaseAccessException; 
 	   /**
 	    * Write back changes to Smartfrog action mappings. 
 	    * @param sfc
 	    * @throws DatabaseAccessException
 	    */
 	   public void setSfConfigs(SfConfigsType sfc) throws DatabaseAccessException ;
	   public void close() ; 
}
