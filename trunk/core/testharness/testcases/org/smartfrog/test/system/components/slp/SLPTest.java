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
import org.smartfrog.sfcore.common.SmartFrogException;

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
		terminateApplication(applicationtcp38);
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
		Prim applicationtcn70 = deployExpectingSuccess(FILES+"ServcieProvider.sf", "serviceAgentTCN70");
        deployExpectingException(FILES+"tcn82.sf",
                                 "tcn170",
                                 "SmartFrogLifecycleException",
                                 "sfDeploy",
                                 "SmartFrogException",
                                 " SLP: No service type given");
		terminateApplication(applicationtcn70);
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
		terminateApplication(applicationtcp39S);
		
}

/* Success in locating-Test case */
public void testCaseTCP40() throws Throwable 
{
		Prim applicationtcp40D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP40");
		Prim applicationtcp40S = deployExpectingSuccess(FILES+"ServcieProvider.sf", "ServiceAgentTCP40");
		Prim applicationtcp40A = deployExpectingSuccess(FILES+"ServiceRequestor.sf", "UserAgentTCP40");

		

		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
		
			e.printStackTrace();
		}
		assertNotNull(applicationtcp40A);
		Prim loc = (Prim)applicationtcp40A.sfResolveHere("loc");
		String actual_loc_SfClass = (String)loc.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.services.comm.slp.SFSlpLocatorImpl", actual_loc_SfClass);

		Prim g = (Prim)applicationtcp40A.sfResolve("g");
		String actualSfClass = (String)g.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.helloworld.GeneratorImpl", actualSfClass);
/* 
		Prim printer  = (Prim)g.sfResolveHere("printer");
		String actualprinterSfClass = (String)printer.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.helloworld.PrinterImpl", actualprinterSfClass);
		*/
			terminateApplication(applicationtcp40S);
			terminateApplication(applicationtcp40D);
			terminateApplication(applicationtcp40A);

}

// Expecting failer in locating-Test case  - there is no service adv. 
public void testCaseTCN83() throws Throwable 
{
		deployExpectingException(FILES+"ServiceRequestor.sf",
                                 "UserAgentTCN83",
                                 "SmartFrogLifecycleException",
                                 "sfDeploy",
                                 "SmartFrogResolutionException",
                                 "SLP: No service found");
		
		
		

			
}

// Testing service life time - Expecting failure  because of life time time out.
public void testCaseTCN84() throws Throwable 
{
		Prim applicationtcn84D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN84");
		Prim applicationtcp84S = deployExpectingSuccess(FILES+"tcn84_SA.sf", "ServiceAgentTCN84");
			try {
			Thread.sleep(100000);
		}
		catch (InterruptedException e) {
		
			e.printStackTrace();
		}
	

	deployExpectingException(FILES+"tcn84_UA.sf",
                                 "UserAgentTCP84",
                                 "SmartFrogLifecycleException",
                                 "sfDeploy",
                                 "SmartFrogResolutionException",
                                 "SLP: No service found");

			terminateApplication(applicationtcn84D);
			terminateApplication(applicationtcp84S);
					
					
}


// change the port and test the success
public void testCaseTCP41() throws Throwable 
{
	//	Prim applicationtcn41D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP41");
		Prim applicationtcp41S = deployExpectingSuccess(FILES+"tcp41_SA.sf", "ServiceAgentTCP41");
		Prim applicationtcp41A = deployExpectingSuccess(FILES+"tcp41_UA.sf", "UserAgentTCP41");
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
		assertNotNull(applicationtcp41A);

		Prim loc = (Prim)applicationtcp41A.sfResolveHere("loc");
		String actual_loc_SfClass = (String)loc.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.services.comm.slp.SFSlpLocatorImpl", actual_loc_SfClass);
		
		Prim g = (Prim)applicationtcp41A.sfResolve("g");
		String actualSfClass = (String)g.sfResolveHere("sfClass");
		assertEquals("org.smartfrog.examples.helloworld.GeneratorImpl", actualSfClass);
	//		terminateApplication(applicationtcn41D);
			terminateApplication(applicationtcp41S);
			terminateApplication(applicationtcp41A);
	
		
}

}


