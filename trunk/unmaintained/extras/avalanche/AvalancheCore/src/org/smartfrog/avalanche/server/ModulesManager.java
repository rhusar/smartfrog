/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.module.ModuleType;
/**
 * Provides an interface to modules in the system. Each Module is identified by a unique module Id. 
 * The module Id is provided by the user while creating the module. 
 * Following example shows usage of ModuleManager
 * <pre>
 * 	<code>
 * 		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		String moduleId = "Apache Tomcat" ;
		String desc = "Web Server from Apache" ;
		String vendor = "Apache" ;
		
		ModulesManager m = factory.getModulesManager();
		{
			ModuleType module = m.newModule(moduleId) ;
			module.setDescription(desc) ;
			module.setVendor(vendor) ;
			
			VersionType version = module.addNewVersion();
			version.setNumber("1.0");
			
			DistributionType distro = version.addNewDistribution();
			distro.setId("All_Linux");
			PlatformSelectorType plaf = distro.addNewPlatformSelector();
			plaf.setOs("Lin*");
			plaf.setArch("Intel");
			plaf.setPlatform("*");
			
			ActionType action = distro.addNewAction();
			action.setConfiguration("org/smartfrog/testing.sf");
			action.setName("INSTALL");
			
			m.setModule(module);
		}
		{
			ModuleType module = m.getModule(moduleId) ;
			// validate all the data we added 
			assertNotNull(module);
			assertEquals(module.getId(), moduleId);
			assertEquals(module.getVendor(), vendor) ;
			assertEquals(module.getDescription(), desc);
			
		}
		
		{
			// delete the module 
			m.removeModule(moduleId);
			ModuleType module = m.getModule(moduleId) ;
			// validate its not there anymore 
			assertNull(module);
		}
		factory.close();

 * 	</code>
 * </pre>
 * 
 * @author sanjaydahiya
 *
 */
public interface ModulesManager {
	/**
	 * Returns a Module associated with moduleId, null otherwise. 
	 * @param moduleId
	 * @return
	 * @throws DatabaseAccessException
	 */
	public ModuleType getModule(String moduleId) throws DatabaseAccessException; 
	/**
	 * List all module Ids present in the system. 
	 * @return
	 * @throws DatabaseAccessException
	 */
	public String[] listModules() throws DatabaseAccessException ;
	
// 	public void addModule(ModuleType m) throws DatabaseAccessException; 
	/**
	 * Deletes a module if it exist in the system.
	 */
	public void removeModule(String moduleId) throws DatabaseAccessException; 
	/**
	 * Create a new empty module for the modele Id. 
	 * @param moduleId
	 * @return
	 * @throws DatabaseAccessException
	 * @throws DuplicateEntryException id Module Id already exists in the system. 
	 */
	public ModuleType newModule(String moduleId) throws DatabaseAccessException, DuplicateEntryException ;
	/**
	 * Write back changes to a ModuleType to the database. Note: changes made to ModuleType are not written 
	 * back to database until setModule() is called on the ModuleType. 
	 * @param module
	 * @throws DatabaseAccessException
	 */
	public void setModule(ModuleType module) throws DatabaseAccessException ; 
	/**
	 * Close all database handles associated with the ModuleManager. 
	 *
	 */
	public void close() ;
}
