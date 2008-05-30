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


package org.smartfrog.services.xunit.testng;

import org.smartfrog.services.xunit.base.AbstractTestSuite;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Implementation of a TestNG tset suite runner, hosting TestNG under SmartFrog
 */
public class TestNGTestSuiteImpl extends AbstractTestSuite implements TestSuite {

    public TestNGTestSuiteImpl() throws RemoteException {
    }



    /**
     * run the tests
     *
     * @return true if they worked
     *
     * @throws RemoteException for network problems
     * @throws SmartFrogException for other problems
     * @throws InterruptedException if the thread got interrupted while the
     * tests were running
     */
    public boolean runTests()
            throws RemoteException, SmartFrogException, InterruptedException {
        return false;
    }
}
