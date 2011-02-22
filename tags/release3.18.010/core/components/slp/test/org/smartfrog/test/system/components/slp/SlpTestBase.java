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

import org.smartfrog.test.SmartFrogTestBase;
import org.smartfrog.test.DeployingTestBase;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.services.comm.slp.SFSlpDA;

/**
 *
 * Created 18-Jan-2008 12:54:43
 *
 */

public class SlpTestBase extends DeployingTestBase {
    protected static final String FILES = "org/smartfrog/test/system/components/slp/";
    protected static final int SLEEP_DELAY = 10000;

    public SlpTestBase(String name) {
        super(name);
    }

    /**
     * deploy the DA into the directoryAgent member variable, and under the name DirectoryAgent.
     * The DA will be terminated at the end of every test run.
     * @return the deployed application.
     * @throws Throwable
     */
    protected Prim deployDirectoryAgent() throws Throwable {
        application = deployExpectingSuccess(FILES + "DirectoryAgent.sf","DirectoryAgent");
        assertInstanceOf(getDirectoryAgent(), SFSlpDA.class);
        return application;
    }

    public Prim getDirectoryAgent() {
        return application;
    }
}
