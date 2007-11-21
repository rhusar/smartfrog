/** (C) Copyright 2004-2007 Hewlett-Packard Development Company, LP

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
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.comm.slp.SFSlpLocatorImpl;
import org.smartfrog.services.comm.slp.SFSlpDA;
import org.smartfrog.services.comm.slp.SFSlpAdvertiser;
import org.smartfrog.services.comm.slp.SFSlpLocator;
import org.smartfrog.examples.helloworld.PrinterImpl;
import org.smartfrog.examples.helloworld.Printer;
import org.smartfrog.examples.helloworld.GeneratorImpl;

/**
 *  JUnit test class for test cases related to "SLP" component
 * */
public class SLPTest extends SmartFrogTestBase {

    private static final String FILES = "org/smartfrog/test/system/components/slp/";
    private static final int SLEEP_DELAY = 10000;
    private Prim directoryAgent;

    public SLPTest(String s) {
        super(s);
    }


    protected void tearDown() throws Exception {
        terminateApplication(directoryAgent);
        super.tearDown();
    }

    /**
     * deploy the DA into the directoryAgent member variable, and under the name DirectoryAgent.
     * The DA will be terminated at the end of every test run.
     * @return the deployed application.
     * @throws Throwable
     */
    protected Prim deployDirectoryAgent() throws Throwable {
        directoryAgent = deployExpectingSuccess(FILES + "DirectoryAgent.sf","DirectoryAgent");
        assertInstanceOf(getDirectoryAgent(), SFSlpDA.class);
        return directoryAgent;
    }


    public Prim getDirectoryAgent() {
        return directoryAgent;
    }

    public void testCaseTCP38() throws Throwable {
        deployDirectoryAgent();
    }

/* Testing without serviceType in Service Agant */

    public void testCaseTCN80() throws Throwable {
//		application = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN80");
        deployExpectingException(FILES + "tcn80.sf",
                "tcn80",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_LINKRESOLUTION,
                "HERE serviceType");
        //Unresolved Reference: HERE serviceType
    }


    /* Testing without toAdvertise in Service Agant */
    public void testCaseTCN81() throws Throwable {
//		application = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN81");
        deployExpectingException(FILES + "tcn81.sf",
                "tcn81",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_SMARTFROG,
                "Could not find 'toAdvertise' attribute");

    }

    /* Testing without serviceType in User Agant*/
    public void testCaseTCN82() throws Throwable {
//		Prim applicationtcn70 = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCN82");
        application = deployExpectingSuccess(FILES + "ServiceProvider.sf", "serviceAgentTCN70");
        deployExpectingException(FILES + "tcn82.sf",
                "tcn170",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                SFSlpLocatorImpl.EXCEPTION_NO_SERVICE_TYPE);
    }

    /* Success in advertising- Test case */
    public void testCaseTCP39() throws Throwable {
        //	Prim applicationtcn39D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP39");
        application = deployExpectingSuccess(FILES + "ServiceProvider.sf", "ServiceAgentTCP39");

        Prim adv = (Prim) application.sfResolveHere("adv");
        assertInstanceOf(adv, SFSlpAdvertiser.class);
        Prim printer = (Prim) application.sfResolveHere("p");
        assertInstanceOf(printer, Printer.class);
        String actualPSfClass = (String) printer.sfResolveHere("sfClass");
        assertEquals("org.smartfrog.examples.helloworld.PrinterImpl", actualPSfClass);
    }

    /* Success in locating-Test case */
    public void testCaseTCP40() throws Throwable {
        Prim serviceProvider = null;
        Prim serviceRequestor = null;
        try {
            deployDirectoryAgent();
            serviceProvider = deployExpectingSuccess(FILES + "ServiceProvider.sf",
                    "testCaseTCP40ServiceProvider");
            serviceRequestor = deployExpectingSuccess(FILES + "ServiceRequestor.sf",
                    "testCaseTCP40ServiceRequestor");
            Thread.sleep(SLEEP_DELAY);
            assertNotNull(serviceRequestor);
            Prim loc = (Prim) serviceRequestor.sfResolveHere("loc");
            assertInstanceOf(loc, SFSlpLocator.class);

            Prim g = (Prim) serviceRequestor.sfResolve("g");
            String actualSfClass = (String) g.sfResolveHere("sfClass");
            assertEquals("org.smartfrog.examples.helloworld.GeneratorImpl", actualSfClass);

        } finally {
            terminateApplication(serviceProvider);
            terminateApplication(serviceRequestor);
        }

    }

    // Expecting failer in locating-Test case  - there is no service adv.
    public void testCaseTCN83() throws Throwable {
        deployExpectingException(FILES + "ServiceRequestor.sf",
                "UserAgentTCN83",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                SFSlpLocatorImpl.EXCEPTION_NO_SLP_SERVICE);
    }

    // Testing service life time - Expecting failure  because of life time time out.
    public void testCaseTCN84() throws Throwable {
        deployDirectoryAgent();
        application = deployExpectingSuccess(FILES + "tcn84_SA.sf", "ServiceAgentTCN84");
        Thread.sleep(SLEEP_DELAY);
        deployExpectingException(FILES + "tcn84_UA.sf",
                "UserAgentTCP84",
                EXCEPTION_LIFECYCLE,
                "sfDeploy",
                EXCEPTION_RESOLUTION,
                SFSlpLocatorImpl.EXCEPTION_NO_SLP_SERVICE);
    }


    // change the port and test the success
    public void testCaseTCP41() throws Throwable {
        //	Prim applicationtcn41D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP41");
        Prim userAgent=null;
        try {
            application = deployExpectingSuccess(FILES + "tcp41_SA.sf", "tcp41_ServiceAgent");
            userAgent = deployExpectingSuccess(FILES + "tcp41_UA.sf", "tcp41_UserAgent");
            Thread.sleep(SLEEP_DELAY);
            assertNotNull(userAgent);

            Prim loc = (Prim) userAgent.sfResolveHere("loc");
            String actual_loc_SfClass = (String) loc.sfResolveHere("sfClass");
            assertEquals("org.smartfrog.services.comm.slp.SFSlpLocatorImpl", actual_loc_SfClass);

            Prim g = (Prim) userAgent.sfResolve("g");
            String actualSfClass = (String) g.sfResolveHere("sfClass");
            assertEquals("org.smartfrog.examples.helloworld.GeneratorImpl", actualSfClass);
        } finally {
            //		terminateApplication(applicationtcn41D);
            terminateApplication(userAgent);
        }


    }

}


