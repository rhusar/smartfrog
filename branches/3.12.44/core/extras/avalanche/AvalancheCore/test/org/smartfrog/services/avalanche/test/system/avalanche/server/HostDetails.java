/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.avalanche.test.system.avalanche.server;

import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.server.HostManager;

import java.io.File;

/**
 * 
 * This class loads initsettings.xml in the database. This file is present in
 * the development environment, so that it can be edited by hand if needed, but
 * it should not be there in the production environment. It can be integrated
 * with the build system by adding an Ant task in the build.xml file. Ant task
 * is -
 * 
 * <java classname="org.smartfrog.avalanche.util.SetupBDB"> <classpath
 * refid="avalanche-core.class.path"/> </java>
 * 
 * 
 * Note - AVALANCHE_HOME must be set in this file before running it otherwise it
 * will result in NullPointerException
 * 
 * @author sanjaydahiya
 * 
 */
public class HostDetails {

	// **MUST** Set AVALANCHE_HOME on the server.
	String avalancheHome = "/home/grid/avl-test"; // "/Users/sanjaydahiya/dev/data/avalanche"
														// ;

	// settings file to load data from, it can be changed to any location.
	// ** Make Sure This File is Present**
	String settingsFile = avalancheHome + File.separator + "conf"
			+ File.separator + "initsettings.xml";

	/**
	 * Loads initsettings.xml in Avalanche Server database. This file is present
	 * in the development environment but need not be there in the production
	 * environment. The database which goes along with the distribution contains
	 * this data preloaded. This can also be made a part of the buld system by
	 * adding it in build.xml file.
	 * 
	 * @throws Exception
	 */
public void setup() throws Exception {
	System.out.println("In HostDetails");
	AvalancheFactory factory = 
		AvalancheFactory.getFactory(AvalancheFactory.BDB);
	factory.init(avalancheHome);
	
	HostManager manager = factory.getHostManager();
	String[] hosts = manager.listHosts();
	System.out.println("Num Hosts : " + hosts.length);
	for (int i=0; i<hosts.length; i++) {
		System.out.println("HostID : " + hosts[i]);
	}
	
	
	
	/*SFAdapter adapter = new SFAdapter(factory);
	System.out.println("State : " + adapter.isActive("15.76.96.133"));*/
}
		
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		(new HostDetails()).setup();
	}

}
