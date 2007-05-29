/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.defaultHostProfile.DefaultProfileType;
import org.smartfrog.avalanche.core.module.PlatformSelectorType;

/**
 * Provides an interface to access Default Profiles in Avalanche Database, 
 * Default Profiles contain two parts 
 * 	Platform Selector - This supports regular expressions and is used to match against 
 * 						Platform details of Host. 
 * 	ModuleGroup 		 -	Contains Module Ids and version numbers, these modules are a part of default 
 * 						Profile and are deployed whenever the default profile is executed. 
 * 				
 * <pre>
 * 	<code>
 * 		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
	
		DefaultProfileManager manager = factory.getDefaultProfileManager();
		{
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux.*");
			selector.setPlatform(".*");
			selector.setArch(".*");
			try{
				DefaultProfileType profile = manager.newProfile(selector);
				ModuleGroupType mg = profile.addNewModuleGroup();
				Modules modules = mg.addNewModules();
				Modules.Module module = modules.addNewModule();
				module.setId("Apache Tomcat");
				module.setVersion("4.0.0");
				
				manager.setProfile(profile); 				
			}catch(Exception e){
				System.out.println(e);
			}

		}
		{
			// get the profile using a host's selector
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux redhat");
			selector.setPlatform("Intel");
			selector.setArch("x86");
			
			DefaultProfileType profile = manager.getProfile(selector);
			assertNotNull(profile); 
			
			System.out.println("Default profile : " + profile.xmlText());
			System.out.println("Default Profile modules : " + profile.getModuleGroup().xmlText());
		}
		// delete the profile 
		{
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux.*");
			selector.setPlatform(".*");
			selector.setArch(".*");
			
			manager.remove(selector);
		}
		{
			// get the profile using a host's selector
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux redhat");
			selector.setPlatform("Intel");
			selector.setArch("x86");
			
			DefaultProfileType profile = manager.getProfile(selector);
			assertNull(profile); 			
		}
		factory.close(); 

 * 	</code>
 * </pre>
 * @author sanjaydahiya
 *
 */
public interface DefaultProfileManager {
	/**
	 * Returns the first default profile that matches the host platform selector. 
	 * Follows Java regex conventions. 
	 * @param selector
	 * @return
	 * @throws DatabaseAccessException
	 */
	public DefaultProfileType getProfile(PlatformSelectorType selector) 
	throws DatabaseAccessException ;
	/**
	 * Returns List of all default profiles in the system. Strings in return array are 
	 * of the format - "os platform arch" seperated by String terminators. 
	 * @return
	 * @throws DatabaseAccessException
	 */
	public String []listProfiles() throws DatabaseAccessException ;
	/**
	 * Use this method to write back the changes to database. After getting a profile changes are not written bcak 
	 * until setProfile is invoked on the profile. 
	 * @param profile
	 * @throws DatabaseAccessException
	 */
 	public void setProfile(DefaultProfileType profile) throws DatabaseAccessException ;
 	/**
 	 * Creates a new Profile in the database for the platform selector, Add modules in the 
 	 * default profile and save it back in database. 
 	 * @param selector
 	 * @return
 	 * @throws DatabaseAccessException
 	 * @throws DuplicateEntryException Profile already exists in Database for this selector pattern. 
 	 */
 	public DefaultProfileType newProfile(PlatformSelectorType selector) 
 	throws DatabaseAccessException, DuplicateEntryException ;
 	/**
 	 * Deletes a default profile, the selector should be exactly same as passed while creating the 
 	 * profile. 
 	 * @param selector
 	 * @throws DatabaseAccessException
 	 */
 	public void remove(PlatformSelectorType selector) throws DatabaseAccessException ;
 	/**
 	 * Close database handles. 
 	 *
 	 */
	public void close() ;

}
