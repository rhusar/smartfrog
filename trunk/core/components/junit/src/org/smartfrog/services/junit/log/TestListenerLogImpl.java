/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.junit.log;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.services.junit.LogListener;
import org.smartfrog.services.junit.TestListener;
import org.smartfrog.services.junit.data.LogEntry;

import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * created 18-May-2006 15:37:08
 */

public class TestListenerLogImpl extends AbstractTestLog implements TestListenerLog {


    List/*<LogListener>*/ listeners=new ArrayList(1);
    public static final String ERROR_DUPLICATE_ADD = "Duplicate listener registration";

    public TestListenerLogImpl() throws RemoteException {
    }

    /**
     * log an event by passing it to each entry in turn.
     * A remote exception in one propagates to all
     *
     * @param entry
     */
    public void log(LogEntry entry) throws RemoteException {
        Iterator elements = listeners.iterator();
        while (elements.hasNext()) {
            LogListener listener = (LogListener) elements.next();
            listener.log(entry);
        }
    }

    /**
     * Add a listener to log events
     *
     * @param listener
     */
    public void addLogListener(LogListener listener) throws SmartFrogException {
        if(listeners.indexOf(listener)>=0) {
            throw new SmartFrogException(ERROR_DUPLICATE_ADD);
        }
        listeners.add(listener);
    }

    /**
     * Remove a log listener. Harmless if the log is not active
     *
     * @param listener
     */
    public void removeLogListener(LogListener listener)  {
        listeners.remove(listener);
    }

    /**
     * Remove all listeners
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Subscribe a listener to the logger of a test suite
     * @param testSuite the component running the tests
     * @param listener the listener
     * @return true if we subscribed; if the log was the right type.
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public static boolean subscribeListener(PrimImpl testSuite, TestListener listener)
            throws SmartFrogException, RemoteException {
        //first, we grab our log
        Log testlog = testSuite.sfLog();
        return subscribeListener(testlog, listener);
    }

    /**
     *
     * Subscribe a listener to the logger of a test suite
     * @param testlog the log
     * @param listener the listener
     * @return true if we subscribed; if the log was the right type.
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public static boolean subscribeListener(Log testlog, TestListener listener)
            throws
            SmartFrogException, RemoteException {
        if (testlog instanceof TestListenerLog) {
            //this log listens for test events, so we can bond to it
            TestListenerLog tll = (TestListenerLog) testlog;
            tll.addLogListener(listener);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unsubscribe a listener from a log of a component. Harmless if the
     * log is of the wrong type, or the listener is not registered
     * @param testSuite
     * @param listener
     * @throws SmartFrogException
     * @throws RemoteException
     */
    public static void unsubscribeListener(PrimImpl testSuite,
                                       TestListener listener) throws
            SmartFrogException, RemoteException {
        //first, we grab our log
        Log testlog = testSuite.sfLog();
        if (testlog instanceof TestListenerLog) {
            //this log listens for test events, so we can bond to it
            TestListenerLog tll = (TestListenerLog) testlog;
            tll.removeLogListener(listener);
        }
    }
}
