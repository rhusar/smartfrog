/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.org.smartfrog.avalanche.server;

import junit.framework.TestCase;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.ServerSetup;

public class ServerSetupTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.server.ServerSetup.startup()'
	 */
	public void testStartup() throws Exception{
		ServerSetup setup = new ServerSetup();
		setup.setAvalancheHome("/tmp/avalancheTest");
		setup.setXmppServer("15.76.99.8");
		setup.setXmppServerAdminUser("admin");
		setup.setXmppServerAdminPassword("admin");
		setup.setXmppServerPort(5223);
		
		setup.startup();
		System.out.println("Avalanche Server is started properly ... ");
		
		// add a host now and see if the user for the host is created properly
		AvalancheFactory factory = setup.getFactory();
		HostManager hm = factory.getHostManager();
		try{
			HostType host = hm.newHost("lx9622.india.hp.com");
		}catch(Exception e){
			System.out.println("Error !! Host already exists");
		}
		
		// create a thread for sending event on behalf of the new host
		
		System.in.read();
		try{
			hm.removeHost("lx9622.india.hp.com");
		}catch(Exception e){
			System.out.println("Error !! Failed deleting host");
		}
		setup.shutdown();
	}
}
