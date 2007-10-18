/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package tests.org.smartfrog.avalanche.server;

import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.server.ActiveProfileManager;
import org.smartfrog.avalanche.server.AvalancheFactory;

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
public class ActiveHostProfiles {

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
public void setup() throws Exception{
	/*Document doc = XMLUtils.load(settingsFile, false);
	SettingsDocument sdoc = SettingsDocument.Factory.parse(doc);
	
	AvalancheFactory factory = 
		AvalancheFactory.getFactory(AvalancheFactory.BDB);
	factory.init(avalancheHome);
	factory.getSettingsManager().setDefaultSettings(sdoc.getSettings());
	
	//System.out.println(factory.getSettingsManager().getDefaultSettings());
	factory.close();
	*/
	System.out.println("In ActiveHostProfiles");
	AvalancheFactory factory = 
		AvalancheFactory.getFactory(AvalancheFactory.BDB);
	factory.init(avalancheHome);
	
	ActiveProfileManager manager = factory.getActiveProfileManager();
	String profiles[] = manager.listProfiles();
	System.out.println("Profiles length : " + profiles.length);
	for (int i=0; i<profiles.length; i++) {
		System.out.println("Profile : " + profiles[0]);
		ActiveProfileType profile = manager.getProfile(profiles[0]);
		System.out.println("Host State : " + profile.getHostState());
		System.out.println("Host ID : " + profile.getHostId());
		System.out.println("\n");
	}
	
	/*SFAdapter adapter = new SFAdapter(factory);
	System.out.println("State : " + adapter.isActive("15.76.96.133"));*/
}
		
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		(new ActiveHostProfiles()).setup();
	}

}
