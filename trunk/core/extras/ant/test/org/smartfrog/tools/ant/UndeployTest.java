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

package org.smartfrog.tools.ant;

import junit.framework.TestCase;

/**
 * Junit test cause
 * 
 * @author root
 */
public class UndeployTest extends TaskTestBase {
    protected static final String NO_APP = "Missing application name";

    public UndeployTest(String test) {
        super(test);
    }

    /**
     * implementation point: return the name of a test build file
     *
     * @return the path (from the test files base dir) to the build file
     */
    protected String getBuildFile() {
        return "undeploy.xml";
    }


    /**
     * shut down anything running
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        executeTarget("teardown");
    }

    /**
     *
     testNoParams:

     BUILD FAILED
     Missing application name
     */
    public void testNoParams() {
        expectBuildExceptionContaining("testNoParams", "no parameters",
                NO_APP);
    }


    /**
     * failonerror is not deemed to affect the no apps configuration,
     * as that is a fundamental configuration error
     */
    public void testNoFailure() {
        expectBuildExceptionContaining("testNoFailure", "no parameters",
                NO_APP);
    }

    public void testEmptyApplication() {
        expectBuildExceptionContaining("testEmptyApplication", "null app string",
                NO_APP);
    }

/*
    in the log, the started daemon will exit.
[sf-startdaemon] SmartFrog [rootProcess] dead
[sf-undeploy] SmartFrog daemon terminated

*/
    public void testBadHost() {
        expectBuildExceptionContaining("testBadHost", "unknown host",
                "failed to terminate");
        assertLogContaining("Unable to locate IP address of the host: no-such-hostname");
        assertLogContaining("java.net.UnknownHostException: no-such-hostname");

    }

    public void testStopDaemon() {
        executeTarget("testStopDaemon");
        assertLogContaining("SmartFrog [rootProcess] dead");
        assertLogContaining("SmartFrog daemon terminated");

    }



}