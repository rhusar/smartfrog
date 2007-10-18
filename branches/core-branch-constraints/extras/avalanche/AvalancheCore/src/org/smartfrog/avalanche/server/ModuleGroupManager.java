/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.moduleGroup.ModuleGroupType;
/**
 * Provides an interface to Module Groups in Avalanche. A Module Group is a set of Module Ids along with the
 * version numbers. Each Module Group is identified by a unique ID, which is provided by user. 
 * <pre>
 * <code>
 * 		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		String moduleGroupId = "Basic Grid Setup" ;
		String desc = "Includes Tomcat, Ant and GT4" ;
		ModuleGroupManager mgm = factory.getModuleGroupManager();
		{
			ModuleGroupType moduleGroup = mgm.newModuleGroup(moduleGroupId);
			moduleGroup.setDescription(desc);
			
			Modules modules = moduleGroup.addNewModules();
			
			{
				Modules.Module mod = modules.addNewModule(); 
				mod.setId("Apache Tomcat") ;
				mod.setVersion("4.0.0") ;
			}
			{
				Modules.Module mod = modules.addNewModule(); 
				mod.setId("Apache Ant") ;
				mod.setVersion("5.0.2") ;
			}
			{
				Modules.Module mod = modules.addNewModule(); 
				mod.setId("GT4") ;
				mod.setVersion("4.0.1") ;
			}
			
			mgm.setModuleGroup(moduleGroup);
		}
		{
			ModuleGroupType moduleGroup = mgm.getModuleGroup(moduleGroupId);
			// validate all the data we added 
			assertNotNull(moduleGroup);
			assertEquals(moduleGroup.getId(), moduleGroupId);
			assertEquals(moduleGroup.getDescription(), desc);
			
			System.out.println(moduleGroup.xmlText());
			
		}
		
		{
			// delete the module 
			mgm.remove(moduleGroupId);
			ModuleGroupType moduleGroup = mgm.getModuleGroup(moduleGroupId);
			// validate its not there anymore 
			assertNull(moduleGroup);
		}
		factory.close();

 * </code>
 * </pre>
 * @author sanjaydahiya
 *
 */
public interface ModuleGroupManager {
	/**
	 * Returns Module Group associated with groupId, null if Module Group 
	 * doesnt exist. 
	 * @param groupId
	 * @return
	 * @throws DatabaseAccessException
	 */
    public ModuleGroupType getModuleGroup(String groupId)throws DatabaseAccessException ;
    /**
     * Returns list of All module Group IDs in the system in no particular order. 
     * @return
     * @throws DatabaseAccessException
     */
	public String []listModuleGroups() throws DatabaseAccessException ;
	/**
	 * Writes back the changes to a ModuleGroupType to database. 
	 * @param group
	 * @throws DatabaseAccessException
	 */
	public void setModuleGroup(ModuleGroupType group) throws DatabaseAccessException ; 
	/**
	 * Creates an empty Module Group for the given ID. 
	 * @param id
	 * @return
	 * @throws DatabaseAccessException
	 * @throws DuplicateEntryException if a Module Group already exist with the given name. 
	 */
	public ModuleGroupType newModuleGroup(String id) throws DatabaseAccessException, DuplicateEntryException ; 
	/**
	 * Removes a module group if exist mapping to id. 
	 * @param id
	 * @throws DatabaseAccessException
	 */
	public void remove(String id) throws DatabaseAccessException ; 
	/**
	 * Close database handles associated with this ModuleGroup Manager. 
	 *
	 */
	public void close() ; 
}
