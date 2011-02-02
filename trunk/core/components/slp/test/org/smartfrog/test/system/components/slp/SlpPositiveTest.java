/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.comm.slp.SFSlpAdvertiser;
import org.smartfrog.services.comm.slp.SFSlpLocator;
import org.smartfrog.examples.helloworld.Printer;

/**
 *
 * Created 18-Jan-2008 12:52:26
 *
 */

public class SlpPositiveTest extends SlpTestBase{

    public SlpPositiveTest(String name) {
        super(name);
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
            terminateApplication(application);
        }

    }
    // change the port and test the success
    public void testCaseTCP41() throws Throwable {
        //	Prim applicationtcn41D = deployExpectingSuccess("org/smartfrog/services/comm/slp/sf/SFSlpDA.sf", "DirectoryAgentTCP41");
        Prim userAgent = null;
        Prim serviceAgent= null;
        try {
            serviceAgent = deployExpectingSuccess(FILES + "tcp41_SA.sf", "tcp41_ServiceAgent");
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
            terminateApplication(serviceAgent);
        }

    }
}
