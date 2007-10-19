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
package org.smartfrog.test.system.autoloader;

import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.services.autoloader.Autoloader;

import java.rmi.RemoteException;

/**
 *
 * Created 18-Jul-2007 16:44:13
 *
 */

public class AutoloaderTest extends DeployingTestBase {


    public static final String FILES="/org/smartfrog/test/system/autoloader/";
    private static final String TEXT2 = "this is another example of an autoloaded component";
    private static final String TEXT1 = "this is an example of an autoloaded component";

    public AutoloaderTest(String name) {
        super(name);
    }

    public void testTest1Loads() throws Throwable {
        deploy("autoloadTest1");
    }

    public void testTest2Loads() throws Throwable {
        deploy("autoloadTest2");
    }

    public void testAutoloader() throws Throwable {
        deploy("testAutoloader");
        Prim example = (Prim) application.sfResolve("example");
        String text=example.sfResolve("text","",true);
        assertTrue(text.indexOf(TEXT1)>=0);
    }

    public void testAutoloaderTest2() throws Throwable {
        deploy("testAutoloaderTest2");
        Prim example = (Prim) application.sfResolve("example");
        String text = example.sfResolve("text", "", true);
        assertTrue(text.indexOf(TEXT2) >= 0);
    }

    public void testMissingReference() throws Throwable {
        Prim autoloader = deployAutoloader("testMissingReference");
        try {
            Object o = autoloader.sfResolve("missingReference", "", true);
            fail("Should not have resolved " + o);
        } catch (SmartFrogResolutionException e) {
            assertFaultCauseAndTextContains(e, null, Autoloader.ERROR_UNRESOLVED_AUTOLOAD_REFERENCE, null);
        }
    }

    private Prim deployAutoloader(String s) throws Throwable {
        deploy(s);
        Prim autoloader = (Prim) application.sfResolve("autoloader");
        return autoloader;
    }

    private void deploy(String s) throws Throwable {
        application = deployExpectingSuccess(FILES + s +".sf", s);
    }

    public void testBadName() throws Throwable {
        Prim autoloader = deployAutoloader("testMissingReference");
        try {
            Object o= autoloader.sfResolve("illegal-reference ", "", true);
            fail("Should not have resolved "+o);
        } catch (SmartFrogResolutionException e) {
            assertFaultCauseAndTextContains(e,null, Autoloader.ERROR_REFERENCE_NAME_DOES_NOT_MATCH_THE_PATTERN,null);
        }
    }

    public void testValidLoadsLoads() throws Throwable {
        Prim autoloader = deployAutoloader("testValidLoads");
        Prim example = (Prim) autoloader.sfResolve("autoloadTest2");
    }

    public void testValidLoadsBlocks() throws Throwable {
        Prim autoloader =deployAutoloader("testValidLoads");
        try {
            Prim example = (Prim) autoloader.sfResolve("autoloadTest1");
            fail("Should not have resolved " + example);
        } catch (SmartFrogResolutionException e) {
            assertFaultCauseAndTextContains(e, null, Autoloader.ERROR_REFERENCE_NAME_IS_NOT_IN_THE_LIST_OF_ALLOWED_LOADS, null);

        }
    }


}
