/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */


package org.smartfrog.test.system.components.slp;

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;

/**
 * JUnit test class for test cases related to "SLP" component
 */
public class SLPTest
   
	extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/slp/";

    public SLPTest(String s) {
        super(s);
    }


public void testCaseTCP38() throws Throwable 
{
		Prim applicationtcp38 = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP38");
		assertNotNull(applicationtcp38);
		String actualSfClass = (String)applicationtcp38.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.services.comm.slp.SFSlpDAImpl", actualSfClass);
		//terminateApplication(applicationtcp38);
}




/* Testing without serviceType in Service Agant */

 public void testCaseTCN80() throws Throwable {
//		Prim applicationtcn68 = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN80");
	     deployExpectingException(FILES+"tcn80.sf",
                                 "tcn168",
                                 "ServiceLocationException",
                                 "sfDeploy",
                                 "ServiceLocationException",
                                 " SLP: No service type given");
    }


/* Testing without toAdvertise in Service Agant */
public void testCaseTCN81() throws Throwable {
//		Prim applicationtcn69 = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN81");
        deployExpectingException(FILES+"tcn81.sf",
                                 "tcn169",
                                 "ServiceLocationException",
                                 "sfDeploy",
                                 "ServiceLocationException",
                                 "No toAdvertise");
    }

/* Testing without serviceType in User Agant*/
public void testCaseTCN82() throws Throwable {
//		Prim applicationtcn70 = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN82");
		Prim applicationtcn70 = deployExpectingSuccess(FILES+"ServcieProvider.sf", "userAgentTCN70");
        deployExpectingException(FILES+"tcn82.sf",
                                 "tcn170",
                                 "SmartFrogLifecycleException",
                                 "sfDeploy",
                                 "SmartFrogException",
                                 " SLP: No service type given");
    }
	
/* Success in advertising- Test case */
public void testCaseTCP39() throws Throwable 
{
	//	Prim applicationtcn39D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP39");
		Prim applicationtcp39S = deployExpectingSuccess(FILES+"ServcieProvider.sf", "ServiceAgentTCP39");
		
		Prim adv = (Prim)applicationtcp39S.sfResolveHere("adv");
		
		String actualSfClass = (String)adv.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.services.comm.slp.SFSlpAdvertiserImpl", actualSfClass);
		
		Prim p = (Prim)applicationtcp39S.sfResolveHere("p");
		String actualPSfClass = (String)p.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.helloworld.PrinterImpl", actualPSfClass);
		
}

/* Success in locating-Test case */
public void testCaseTCP40() throws Throwable 
{
//		Prim applicationtcn40D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP39");
//		Prim applicationtcp40S = deployExpectingSuccess(FILES+"ServcieProvider.sf", "ServiceAgentTCP39");
		Prim applicationtcp40A = deployExpectingSuccess(FILES+"ServiceRequestor.sf", "UserAgentTCP40");
		assertNotNull(applicationtcp40A);

		Prim loc = (Prim)applicationtcp40A.sfResolveHere("loc");
		String actual_loc_SfClass = (String)loc.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.services.comm.slp.SFSlpLocatorImpl", actual_loc_SfClass);
		
		Prim g = (Prim)applicationtcp40A.sfResolveHere("g");
		String actualSfClass = (String)g.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.helloworld.GeneratorImpl", actualSfClass);
					
		Prim printer  = (Prim)g.sfResolveHere("printer");
		String actualprinterSfClass = (String)printer.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.helloworld.PrinterImpl", actualprinterSfClass);
			
}

	
}

