/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.avalanche.test.system.avalanche.server.engines.sf;

import junit.framework.TestCase;
import org.smartfrog.avalanche.core.host.ArgumentType;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.core.module.PlatformSelectorType;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.HostManager;
import org.smartfrog.avalanche.server.ServerSetup;
import org.smartfrog.avalanche.server.engines.sf.BootStrap;

import java.net.InetAddress;
import java.util.regex.Pattern;

public class BootStrapTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.server.engines.sf.BootStrap.ignite(String[])'
	 */
	public void testIgnite() throws Exception{

		ServerSetup setup = new ServerSetup();
		setup.setAvalancheHome("/Users/sanjaydahiya/dev/data/avalanche");
//		setup.setXmppServer("15.76.99.63");
		setup.setXmppServer("15.76.99.8");
		setup.setXmppServerAdminUser("admin");
		setup.setXmppServerAdminPassword("admin");
		setup.setXmppServerPort(5223);
		
		setup.startup();
		System.out.println("Avalanche Server is ready ... ");
		
		// add a host now and see if the user for the host is created properly
		AvalancheFactory factory = setup.getFactory();
		
		// instantiate AvalancheFactory
		String hostId = "grid1.india.hp.com" ;
		System.out.println("IP Address : " + InetAddress.getByName(hostId).getHostName());
		//hostId = InetAddress.getByName(hostId).getHostName() ;
		
		String user = "sanjay" ;
		String passwd = "iso*help" ;
		
		// add host and host properties 

		HostManager hm = factory.getHostManager();
		{
			HostType host = null ; 
			try{
				host = hm.newHost(hostId) ;
            }catch(Exception e){
				host = hm.getHost(hostId);
			}
			host.setUser(user) ;
			host.setPassword(passwd) ;
			
			PlatformSelectorType plaf = host.addNewPlatformSelector();
			plaf.setOs("linux");
			plaf.setArch("x86");
			plaf.setPlatform("intel");
			
			ArgumentType args = host.addNewArguments();
			ArgumentType.Argument arg = args.addNewArgument();
			arg.setName("JAVA_HOME");
			arg.setValue("/usr/java/j2sdk1.4.2_04") ;
			
			ArgumentType.Argument avHomeArg = args.addNewArgument();
			avHomeArg.setName("AVALANCHE_HOME");
			avHomeArg.setValue("/tmp") ;
			hm.setHost(host);
		}
		
		System.out.println("Igniting ... "); 
		// bootstap host. 
		BootStrap boot = new BootStrap(factory);
		boot.ignite(new String[]{hostId});
		
		System.out.println("Igniting successfully started... press key to exit "); 
		System.in.read();
		// delete factory 
        // delete the module
        HostType host = hm.getHost(hostId);
        assertNotNull(host);
        hm.removeHost(host);
        // validate its not there anymore
        assertNull(host);
        setup.shutdown();
		// close factory 
	}
}
