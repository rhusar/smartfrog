/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.listeners;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.TestSuite;
import org.smartfrog.services.junit.data.TestInfo;
import org.smartfrog.services.junit.data.Statistics;
import org.smartfrog.services.junit.data.LogEntry;

import java.rmi.RemoteException;

/**
 * This listener does nothing but collect statistics and set external properties.
 * All events are otherwise lost.
 */

public class StatisticsTestListenerComponent extends PrimImpl implements StatisticsTestListener, TestListener {

    public StatisticsTestListenerComponent() throws RemoteException {
    }

    /**
     * bind to a caller
     *
     * @param suite     the test suite that is about to run. May be null,
     *                  especially during testing.
     * @param hostname  name of host
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @return a listener to talk to
     */
    public TestListener listen(TestSuite suite, String hostname, String suitename, long timestamp)
            throws RemoteException, SmartFrogException {
        return this;
    }

    private Statistics stats=new Statistics();


    /**
     * end this test suite. After calling this, caller should discard
     * all references; they may no longer be valid.
     * <i>No further methods may be called</i>
     */
    public void endSuite() throws RemoteException, SmartFrogException {

    }

    /**
     * An error occurred.
     */
    public synchronized void addError(TestInfo test) throws RemoteException, SmartFrogException {
        stats.incErrors();
    }

    /**
     * A failure occurred.
     */
    public synchronized void addFailure(TestInfo test) throws RemoteException, SmartFrogException {
        stats.incFailures();
    }

    /**
     * A test ended.
     */
    public synchronized void endTest(TestInfo test) throws RemoteException, SmartFrogException {
        stats.incTestsRun();
    }

    /**
     * A test started.
     */
    public synchronized void startTest(TestInfo test) throws RemoteException, SmartFrogException {
        stats.incTestsStarted();
    }


    public int getTestsStarted() {
        return stats.getTestsStarted();
    }

    public int getTestsRun() {
        return stats.getTestsRun();
    }

    public int getFailures() {
        return stats.getFailures();
    }

    public int getErrors() {
        return stats.getErrors();
    }

    public int getUnsuccessfulTests() {
        return stats.getUnsuccessfulTests();
    }

    public Statistics getStatistics() {
        return stats;
    }

    public void log(LogEntry event) throws RemoteException {
        //discard
    }
}
