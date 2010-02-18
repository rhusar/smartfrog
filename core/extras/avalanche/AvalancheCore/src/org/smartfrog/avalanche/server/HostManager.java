/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.monitor.handlers.HostUpdateHandler;
/**
 * Provides an interface to Host data in Avalanche Server. Following example shows 
 * adding, getting and deleting Hosts in Avalanche Server using HostManager. 
 * 
 * <pre>
 * 	<code>
 * 		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		String hostId = "Apache Tomcat" ;
		String user = "testUser" ;
		String passwd = "testPwd" ;
		
		HostManager hm = factory.getHostManager();
		{
			HostType host = hm.newHost(hostId) ;
			host.setUser(user) ;
			host.setPassword(passwd) ;
			
			PlatformSelectorType plaf = host.addNewPlatformSelector();
			plaf.setOs("Linux");
			plaf.setArch("x86");
			plaf.setPlatform("intel");
			
			ArgumentType args = host.addNewArguments();
			ArgumentType.Argument arg = args.addNewArgument();
			arg.setName("JAVA_HOME");
			arg.setValue("/usr/local/java") ;
			
			hm.setHost(host);
		}
		{
			HostType host = hm.getHost(hostId) ;
			// validate all the data we added 
			assertNotNull(host);
			assertEquals(host.getId(), hostId);
			assertEquals(host.getUser(), user) ;
			assertEquals(host.getPassword(), passwd);
			
			System.out.println(host.xmlText());
			
		}
		
		{
			// delete the module 
			hm.removeHost(hostId);
			HostType host = hm.getHost(hostId) ;
			// validate its not there anymore 
			assertNull(host);
		}
		factory.close();
	</code>
 * </pre>
 * @author sanjaydahiya
 *
 */
public interface HostManager {
	   /**
	    * Returns Host data from database. 
	    * @param hostId
	    * @return null if no host exists for the host type. 
	    * @throws DatabaseAccessException
	    */
	   public HostType getHost(String hostId) throws DatabaseAccessException ;
	   /**
	    * Returns a list of all host ids in the system. 
	    * @return
	    * @throws DatabaseAccessException
	    */
	   public String []listHosts() throws DatabaseAccessException ;
	   /**
	    * Deletes host data for the host Id. It doesnt delete active profile associated with
	    * the host. 
	    * @param host
	    * @throws DatabaseAccessException
	    */
	   public void removeHost(HostType host) throws DatabaseAccessException;
	   /**
	    * 
	    * @param host
	    * @throws DatabaseAccessException
	    */
	   public void setHost(HostType host) throws DatabaseAccessException ; 
	   /**
	    * Create a new empty entry for for hostid. @see setHost(HostType) must be called 
	    * after changing values in the HostType returned. 
	    * @param hostId
	    * @return
	    * @throws DatabaseAccessException
	    * @throws DuplicateEntryException
	    */
	   public HostType newHost(String hostId) throws DatabaseAccessException, DuplicateEntryException ;
	   /**
	    * These handlers are invoked in the order of adding, when a new Host is added 
	    * or deleted from Avalanche server. 
	    * @param handler
	    */
	   public void addHandler(HostUpdateHandler handler) ;
	   /**
	    * Closes the database handles associated with this Host Manager. 
	    *
	    */
	   public void close() ;
}
