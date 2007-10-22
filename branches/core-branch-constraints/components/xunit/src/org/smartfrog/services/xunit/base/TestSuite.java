/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xunit.base;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.workflow.conditional.Conditional;
import java.rmi.RemoteException;


/**
 * A test suite is anything that is testable. It extends Conditional as all
 * test suites are inherently conditional.
 * 15-Apr-2004 11:51:51
 */


public interface TestSuite extends TestResultAttributes, Conditional {


    /**
     * Non-marked up text description
     * {@value}
     */
    public static final String ATTR_DESCRIPTION = "description";

    /**
     * A list of URLs to link to
     * {@value}
     */
    public static final String ATTR_LINKS = "links";

    /**
     * {@value}
     */
    public static final String ATTR_NAME = "name";

    /**
     * list in name, value pairs [[name,value],[n2,v2]]
     * {@value}
     */
    public static final String ATTR_SYSPROPS="properties";


    /**
     * A list of text tags to describe the test suite
     * {@value}
     */
    public static final String ATTR_TAGS = "tags";

    /**
     * The name of another test suite that must have succeeded
     * before this test runs. If this component failed to deploy or
     * succeed, then this test suite is skipped.
     */
    public static final String ATTR_MUST_SUCCEED = "mustSucceed";

    /**
     * bind to the configuration. A null parameter means 'stop binding'
     *
     * @param configuration configuration to bind to
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems
     */
    void bind(RunnerConfiguration configuration) throws RemoteException,
            SmartFrogException;

    /**
     * run the tests
     * @return true if they worked
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems
     * @throws InterruptedException if the thread got interrupted while the tests were running
     */
    boolean runTests() throws RemoteException, SmartFrogException,InterruptedException;

}
