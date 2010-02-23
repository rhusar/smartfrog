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


package org.smartfrog.test.system.dependencies;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.EVENTLOG;
import static org.smartfrog.services.dependencies.statemodel.state.Constants.TRANSITION;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.test.DeployingTestBase;

public class DependenciesTest extends DeployingTestBase {

    private static final String FILES = "org/smartfrog/test/system/dependencies/";
    private static Log log= LogFactory.getLog(DependenciesTest.class);

    private static final String VM = "VM";
    private static final String OS = "OS";
    private static final String App = "App";
    private static final String TSTART = "tstart";
    private static final String TSTOP = "tstop";


    public DependenciesTest(String name) {
        super(name);
    }

    /**
     * test case - test simple three components up and down
     * @throws Throwable on failure
     */
    public void testThreeUpDown() throws Throwable {
        log.debug("testThreeUpDown");
        application = deployExpectingSuccess(FILES + "testThreeUpDown.sf", "testThreeUpDown");
        WaitForTerminated.wait(getClass(), application, "model");

        Prim p = (Prim) application.sfResolve(EVENTLOG);  
        assertTrue(p.sfResolve(TRANSITION+"0").toString().equals(VM+":"+TSTART));
        assertTrue(p.sfResolve(TRANSITION + "1").toString().equals(OS + ":" + TSTART));
        assertTrue(p.sfResolve(TRANSITION + "2").toString().equals(App + ":" + TSTART));
        assertTrue(p.sfResolve(TRANSITION + "3").toString().equals(App + ":" + TSTOP));
        assertTrue(p.sfResolve(TRANSITION + "4").toString().equals(OS + ":" + TSTOP));
        assertTrue(p.sfResolve(TRANSITION + "5").toString().equals(VM + ":" + TSTOP));
        log.debug("testThreeUpDown: SUCCESS");

    }
    

}