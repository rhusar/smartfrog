/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.hostGroup.HostGroupType;

/**
 * Provides interface to Host Groups in Avalanche Database. Each host group is 
 * identified by an Id and can contain one ore more host Ids. Following example shows 
 * How to use a host group. 
 * <pre>
 *  <code>
 *  		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		String hostGroupId = "Cluster 1" ;
		
		HostGroupManager hgm = factory.getHostGroupManager();
		{
			HostGroupType hostGroup = hgm.newHostGroup(hostGroupId) ;
			Hosts hosts = hostGroup.addNewHosts();

			Hosts.Host host1 = hosts.addNewHost() ;
			host1.setId("grid1.india.hp.com") ;

			Hosts.Host host2 = hosts.addNewHost() ;
			host2.setId("grid2.india.hp.com") ;
			
			Hosts.Host host3 = hosts.addNewHost() ;
			host3.setId("grid3.india.hp.com") ;
			
			hgm.setHostGroup(hostGroup);
		}
		{
			HostGroupType hostGroup = hgm.getHostGroup(hostGroupId) ;
			// validate all the data we added 
			assertNotNull(hostGroup);
			assertEquals(hostGroup.getId(), hostGroupId);
			
			System.out.println(hostGroup.xmlText());
			
		}
		
		{
			// delete the module 
			hgm.removeHostGroup(hostGroupId);
			HostGroupType host = hgm.getHostGroup(hostGroupId) ;
			// validate its not there anymore 
			assertNull(host);
		}
		factory.close();

 *  </code>
 * </pre>
 *  
 * @author sanjaydahiya
 *
 */
public interface HostGroupManager {
	   /**
	    * Returns a host group by id. 
	    * @param groupId
	    * @return null if no HostGroup exists for the id. 
	    * @throws DatabaseAccessException
	    */
	   public HostGroupType getHostGroup(String groupId) throws DatabaseAccessException ; 
	   /**
	    * Returns a list of all HostGroup Ids. 
	    * @return
	    * @throws DatabaseAccessException
	    */
	   public String []listHostGroups() throws DatabaseAccessException ;
	   /**
	    * Create a new Host group. 
	    * @param groupId
	    * @return
	    * @throws DatabaseAccessException
	    * @throws DuplicateEntryException
	    */
	   public HostGroupType newHostGroup(String groupId) throws DatabaseAccessException, DuplicateEntryException ;
	   /**
	    * Store changes to an existing HostGroup back to database. 
	    * @param group
	    * @throws DatabaseAccessException
	    */
	   public void setHostGroup(HostGroupType group)throws DatabaseAccessException ;
	   /**
	    * Delete a host group. 
	    * @param hostGroupId
	    * @throws DatabaseAccessException
	    */
	   public void removeHostGroup(String hostGroupId) throws DatabaseAccessException ;
	   /**
	    * Close database handles. 
	    *
	    */
	   public void close() ;

}
