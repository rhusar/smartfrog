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

import org.smartfrog.services.xunit.serial.Statistics;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the public testrunner interface
 * created 15-Apr-2004 11:51:44
 */


public interface TestRunner extends Remote , TestResultAttributes
 {

    /**
     * should deploy fail if there is an error?
     */
    String ATTR_FAILONERROR = "failOnError";

     /**
      * name of the keepgoing attr
      */
     String ATTR_KEEPGOING = "keepGoing";

     /**
      * name of the listener
      */
     String ATTR_LISTENER = "listener";

     /**
     * boolean: run tests on startup
     */
    String ATTR_RUN_TESTS_ON_STARTUP="runTests";

     /**
      * Name of a single test to run
      */
    String ATTR_SINGLE_TEST="singleTest";

     /**
      * the test log
      */
     String ATTR_TESTLOG = "testLog";

     /**
      * thread priority; 1 to 9
      */
     String ATTR_THREAD_PRIORITY = "threadPriority";


     /**
      * time in seconds that a single test can take
      */
    String ATTR_TIMEOUT_SECONDS="timeout";


     /**
      * Get the listener factory
      * @return the factory
      * @throws RemoteException
      */


    TestListenerFactory getListenerFactory() throws RemoteException;

    void setListenerFactory(TestListenerFactory listener) throws RemoteException;

    /**
     * run the test
     * @return true if the tests were started
     * @throws RemoteException
     */
    boolean startTests() throws RemoteException, SmartFrogException;

    /**
     * Get test execution statistics
     * @return stats
     * @throws RemoteException
     */
    Statistics getStatistics() throws RemoteException;

     /**
      * test for being finished
      * @return true if we have finished
      * @throws RemoteException
      */
    boolean isFinished() throws RemoteException;
}
