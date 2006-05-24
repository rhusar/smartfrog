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
import org.smartfrog.sfcore.common.SmartFrogLogException;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.logging.Log;
import org.smartfrog.sfcore.logging.LogToPrim;
import org.smartfrog.sfcore.logging.LogRegistration;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
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
    public static boolean subscribeListener(Object testlog, TestListener listener)
            throws
            SmartFrogException, RemoteException {
        TestListenerLog tll = extractTestListenerLog(testlog);
        if (tll!=null) {
            //this log listens for test events, so we can bond to it
            tll.addLogListener(listener);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a test listener log from the log passed in. If it is the right
     * type, it is cast, but if it is a ref to a LogToPrim logger,
     * that log is asked for its ultimate destination.
     * @param testlog
     * @return
     * @throws RemoteException
     */
    private static TestListenerLog extractTestListenerLog(Object testlog) throws RemoteException, SmartFrogLogException {
        TestListenerLog testlistener = null;
        Object target=testlog;
        if (target instanceof LogToPrim) {
            //this log listens for test events, so we can bond to it
            LogToPrim ltp=(LogToPrim) target;
            target=ltp.getLogTo();
        } else if (target instanceof TestListenerLog) {
            //this log listens for test events, so we can bond to it
            testlistener = (TestListenerLog) testlog;
        } else if (target instanceof LogRegistration) {
            LogRegistration logreg=(LogRegistration) target;
            Log[] logs = logreg.listRegisteredLogs();
            for(int i=0;i<logs.length;i++) {
                testlistener =extractTestListenerLog(logs[i]);
                if(testlistener !=null) {
                    break;
                }
            }

        }
        //at this point testlistener is either set or null
        return testlistener;
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
        unsubscribeListener(testlog, listener);
    }

    public static void unsubscribeListener(Object testlog, TestListener listener) throws SmartFrogException,
            RemoteException {
        TestListenerLog tll = extractTestListenerLog(testlog);
        if (tll != null) {
            tll.removeLogListener(listener);
        }
    }
}
