/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
/**
 * This class is used to access Active Profile Data in Avalanche Database. Active Profiles are XML documents 
 * see ActiveProfile.xsd for schema of this document. Each Host in Avalanche maps to an Active Profile, 
 * this profile is updated by events from client nodes. At any point the Active Profile data can 
 * be checked to see the last known state of any module on any node. 
 * Active Profile contain current(last known) state of modules on the nodes.
 * Following example shows usage of ActiveProfileManager
 * <pre>
 * <code>
 * 		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		String hostId = "grid1.india.hp.com" ;
		
		ActiveProfileManager apm = factory.getActiveProfileManager();
		{
			ActiveProfileType activeProfile = apm.newProfile(hostId);
			ModuleStateType state = activeProfile.addNewModuleState();
			state.setId("Apache Tomcat") ;
			state.setInstanceName("GT4Runner");
			state.setLastAction("START");
			state.setLastUpdated("102019");
			state.setLogFile("logs/Apache Tomcat.log");
			state.setMsg("Starting Tomcat failed, port in use ");
			state.setState("FAILED");
			state.setVersion("4.0.0");
			
			apm.setProfile(activeProfile);
		}
		{
			ActiveProfileType profile = apm.getProfile(hostId) ;
			// validate all the data we added 
			assertNotNull(profile);
			assertEquals(profile.getHostId(), hostId);
			
			System.out.println(profile.xmlText());
			
		}
		
		{
			// delete the module 
			apm.removeProfile(hostId);
			ActiveProfileType profile = apm.getProfile(hostId) ;
			// validate its not there anymore 
			assertNull(profile);
		}
		factory.close();

 * </code>
 * </pre>
 * 
 * @author sanjaydahiya
 *
 */
public interface ActiveProfileManager {
	/**
	 * Returns Active Host Profile for the given host Id. If host profile doesn't exist it returns null. 
	 * @param hostId
	 * @return null or Active Profile of the given host id. 
	 * @throws DatabaseAccessException
	 */
	public ActiveProfileType getProfile(String hostId) throws DatabaseAccessException ;
	/**
	 * Lists All active profiles in the system. 
	 * @return list fo host ids, for whom active profiles exist in the system. 
	 * @throws DatabaseAccessException
	 */
	public String []listProfiles() throws DatabaseAccessException ;
	/**
	 * Writes back modifcation to Active profile, Profile contains a host Id, which is used to write back. 
	 * @param profile
	 * @throws DatabaseAccessException
	 */
	public void setProfile(ActiveProfileType profile) throws DatabaseAccessException ;
	/**
	 * Create a new Active Host Profile in Avalanche Database for given host id.  
	 * @param hostId
	 * @return
	 * @throws DatabaseAccessException
	 * @throws DuplicateEntryException
	 */
 	public ActiveProfileType newProfile(String hostId) throws DatabaseAccessException, DuplicateEntryException ;
 	/**
 	 * Delete host profile from Avalanche database. 
 	 * @param hostId
 	 * @throws DatabaseAccessException
 	 */
    public void removeProfile(String hostId) throws DatabaseAccessException ;
    /**
     * Close associated database handles. 
     *
     */
	public void close() ;
}
