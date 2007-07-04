/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.avalanche.util;

import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.server.AvalancheFactory;
import org.smartfrog.avalanche.settings.xdefault.SettingsDocument;
import org.smartfrog.avalanche.util.XMLUtils;
import org.w3c.dom.Document;

import java.io.File;

/**
 *
 * This class loads initsettings.xml in the database. This file is present in
 * the development environment, so that it can be edited by hand if needed, but
 * it should not be there in the production environment. It can be integrated
 * with the build system by adding an Ant task in the build.xml file. Ant task
 * is -
 *
 * <java classname="tests.com.hp.grit.avalanche.server.SetupBDB"> <classpath
 * refid="avalanche-core.class.path"/> </java>
 *
 *
 * Note - AVALANCHE_HOME must be set in this file before running it otherwise it
 * will result in NullPointerException
 *
 * @author sanjaydahiya
 *
 */
public class SetupBDB {

	// **MUST** Set AVALANCHE_HOME on the server.
	//String avalancheHome = "/home/grid/avalanche-test"; // "/Users/sanjaydahiya/dev/data/avalanche"
	static String avalancheHome = null;
	//static String avalancheServerOS = null;

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
	Document doc = XMLUtils.load(settingsFile, false);
	SettingsDocument sdoc = SettingsDocument.Factory.parse(doc);

	AvalancheFactory factory =
		AvalancheFactory.getFactory(AvalancheFactory.BDB);
	//factory.init(avalancheHome,avalancheServerOS);
	factory.init(avalancheHome);
	factory.getSettingsManager().setDefaultSettings(sdoc.getSettings());

	//System.out.println(factory.getSettingsManager().getDefaultSettings());
	factory.close();
}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		avalancheHome = args[0];
		//avalancheServerOS = args[1];
		(new SetupBDB()).setup();
	}

}
