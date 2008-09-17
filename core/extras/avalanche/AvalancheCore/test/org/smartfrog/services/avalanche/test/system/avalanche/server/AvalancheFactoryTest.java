/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/
package org.smartfrog.services.avalanche.test.system.avalanche.server;

import junit.framework.TestCase;
import org.smartfrog.avalanche.core.activeHostProfile.ActiveProfileType;
import org.smartfrog.avalanche.core.activeHostProfile.ModuleStateType;
import org.smartfrog.avalanche.core.defaultHostProfile.DefaultProfileType;
import org.smartfrog.avalanche.core.host.ArgumentType;
import org.smartfrog.avalanche.core.host.HostType;
import org.smartfrog.avalanche.core.hostGroup.HostGroupType;
import org.smartfrog.avalanche.core.hostGroup.HostGroupType.Hosts;
import org.smartfrog.avalanche.core.module.*;
import org.smartfrog.avalanche.core.moduleGroup.ModuleGroupType;
import org.smartfrog.avalanche.core.moduleGroup.ModuleGroupType.Modules;
import org.smartfrog.avalanche.server.*;
import org.smartfrog.avalanche.settings.xdefault.SettingsType;

public class AvalancheFactoryTest extends TestCase {

	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getModulesManager()'
	 */
	public void testGetModulesManager() throws Exception{
		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
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
	}


	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getHostManager()'
	 */
	public void testGetHostManager() throws Exception{
		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		String hostId = "grid1.india.hp.com" ;
		String user = "testUser" ;
		String passwd = "testPwd" ;
		
		HostManager hm = factory.getHostManager();
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

        //now do a lookup
            host = hm.getHost(hostId) ;
			// validate all the data we added 
			assertNotNull(host);
			assertEquals(host.getId(), hostId);
			assertEquals(host.getUser(), user) ;
			assertEquals(host.getPassword(), passwd);
			
			System.out.println(host.xmlText());
			// delete the module
			hm.removeHost(host);
			host = hm.getHost(hostId) ;
			// validate its not there anymore 
			assertNull(host);
		factory.close();
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getHostGroupManager()'
	 */
	public void testGetHostGroupManager() throws Exception {
		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
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
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getSettingsManager()'
	 */
	public void testGetSettingsManager() throws Exception{

		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
		
		SettingsManager settings = factory.getSettingsManager();
		{
			SettingsType defaults  =  settings.getDefaultSettings();
			
			defaults.addArch("IA64") ;
			defaults.addArch("x86") ;
			
			{
			SettingsType.Action action = defaults.addNewAction();
			action.setName("INSTALL"); 
			}
			{
				SettingsType.Action action = defaults.addNewAction();
				action.setName("UNINSTALL"); 
			}
			
			defaults.addSystemProperty("JAVA_HOME");
			defaults.addSystemProperty("TOMCAT_HOME");
			defaults.addSystemProperty("AVALANCHE_HOME");
			
			settings.setDefaultSettings(defaults);
		}
		{
			
			SettingsType defaults  =  settings.getDefaultSettings();
			// validate all the data we added 
			assertNotNull(defaults);
			System.out.println(defaults.xmlText());
			
		}
		
		factory.close();
		
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getActiveProfileManager()'
	 */
	public void testGetActiveProfileManager() throws Exception{
		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
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
		
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getDefaultProfileManager()'
	 */
	public void testGetDefaultProfileManager() throws Exception {
		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
		factory.init("/tmp/avalancheTest");
	
		DefaultProfileManager manager = factory.getDefaultProfileManager();
		{
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux.*");
			selector.setPlatform(".*");
			selector.setArch(".*");
			try{
				DefaultProfileType profile = manager.newProfile(selector);
				ModuleGroupType mg = profile.addNewModuleGroup();
				Modules modules = mg.addNewModules();
				Modules.Module module = modules.addNewModule();
				module.setId("Apache Tomcat");
				module.setVersion("4.0.0");
				
				manager.setProfile(profile); 				
			}catch(Exception e){
				System.out.println(e);
			}

		}
		{
			// get the profile using a host's selector
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux redhat");
			selector.setPlatform("Intel");
			selector.setArch("x86");
			
			DefaultProfileType profile = manager.getProfile(selector);
			assertNotNull(profile); 
			
			System.out.println("Default profile : " + profile.xmlText());
			System.out.println("Default Profile modules : " + profile.getModuleGroup().xmlText());
		}
		// delete the profile 
		{
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux.*");
			selector.setPlatform(".*");
			selector.setArch(".*");
			
			manager.remove(selector);
		}
		{
			// get the profile using a host's selector
			PlatformSelectorType selector = PlatformSelectorType.Factory.newInstance();
			selector.setOs("Linux redhat");
			selector.setPlatform("Intel");
			selector.setArch("x86");
			
			DefaultProfileType profile = manager.getProfile(selector);
			assertNull(profile); 			
		}
		factory.close(); 
	}

	/*
	 * Test method for 'org.smartfrog.avalanche.server.AvalancheFactory.getModuleGroupManager()'
	 */
	public void testGetModuleGroupManager() throws Exception{
		AvalancheFactory factory = AvalancheFactory.getFactory(AvalancheFactory.BDB);
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
		
	}

}
