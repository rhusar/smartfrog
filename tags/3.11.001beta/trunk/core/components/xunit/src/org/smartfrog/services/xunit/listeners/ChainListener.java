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
package org.smartfrog.services.xunit.listeners;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestListenerFactory;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.LogEntry;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is a bulk listener for things. 
 * created 03-Jun-2005 16:49:03
 */

public class ChainListener implements TestListener,Iterable<TestListener> {

    private List<TestListener> listeners =new ArrayList<TestListener>();


    public ChainListener() {
    }

    public ChainListener(List<TestListenerFactory> factories, TestSuite suite,
                         String hostname,
                         String processname, String suitename,
                         long timestamp) throws SmartFrogException, RemoteException {
        createAndAddListeners(factories,suite,hostname, processname, suitename,timestamp);
    }

    /**
     * Run through every factory and create a listener from each one, adding each
     * returned instance to the listener factory.
     * @param factories
     * @param suite     the test suite that is about to run. May be null,
     *                  especially during testing.
     * @param hostname  name of host
     * @param processname process of the tests
     * @param suitename name of test suite
     * @param timestamp start timestamp (UTC)
     * @throws SmartFrogException on smartfrog trouble
     * @throws RemoteException on network trouble
     */
    public void createAndAddListeners(List<TestListenerFactory> factories,
                                      TestSuite suite,
                                      String hostname,
                                      String processname,
                                      String suitename,
                                      long timestamp) throws SmartFrogException, RemoteException {
        //reset the list of listeners
        listeners =new ArrayList<TestListener>(factories.size());
        //run through the factories
        for(TestListenerFactory factory:factories) {
            TestListener listener = factory.listen(suite, hostname, processname, suitename, timestamp);
            addListener(listener);
        }
    }

    public synchronized void addListener(TestListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TestListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<TestListener> iterator() {
        return listeners.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void endSuite() throws RemoteException, SmartFrogException {
        Iterator i=iterator();
        while (i.hasNext()) {
            TestListener testListener = (TestListener) i.next();
            testListener.endSuite();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addError(TestInfo test) throws RemoteException,
            SmartFrogException {

        Iterator i = iterator();
        while (i.hasNext()) {
            TestListener testListener = (TestListener) i.next();
            testListener.addError(test);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addFailure(TestInfo test) throws RemoteException,
            SmartFrogException {
        Iterator i = iterator();
        while (i.hasNext()) {
            TestListener testListener = (TestListener) i.next();
            testListener.addFailure(test);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void endTest(TestInfo test) throws RemoteException,
            SmartFrogException {
        Iterator i = iterator();
        while (i.hasNext()) {
            TestListener testListener = (TestListener) i.next();
            testListener.endTest(test);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startTest(TestInfo test) throws RemoteException,
            SmartFrogException {
        Iterator i = iterator();
        while (i.hasNext()) {
            TestListener testListener = (TestListener) i.next();
            testListener.startTest(test);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void log(LogEntry event) throws RemoteException {
        Iterator i = iterator();
        while (i.hasNext()) {
            TestListener testListener = (TestListener) i.next();
            testListener.log(event);
        }

    }

}
