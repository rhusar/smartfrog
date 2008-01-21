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
package org.smartfrog.services.xunit.listeners;

import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.serial.TestInfo;
import org.smartfrog.services.xunit.serial.LogEntry;

import java.rmi.RemoteException;

/**
 * Base class that provides no-op entry points for the various callbacks.
 * It does not implement a factory; that should be done by subclasses
 * created 17-Jan-2007 15:55:50
 * */

public abstract class AbstractListenerImpl extends PrimImpl implements TestListener {


    /**
     * Constructor for children to extend
     * @throws RemoteException network problems
     */
    protected AbstractListenerImpl() throws RemoteException {
    }


    /**
     * end this test suite.
     * <p/>
     * After calling this, caller should discard all references; they may no longer be valid. <i>No
     * further methods may be called</i>
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void endSuite() throws RemoteException, SmartFrogException {

    }

    /**
     * An error occurred.
     *
     * @param test test that errored
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void addError(TestInfo test) throws RemoteException, SmartFrogException {

    }

    /**
     * A failure occurred.
     *
     * @param test test that failed
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void addFailure(TestInfo test) throws RemoteException, SmartFrogException {

    }

    /**
     * A test ended.
     *
     * @param test test that ended
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void endTest(TestInfo test) throws RemoteException, SmartFrogException {

    }

    /**
     * A test started.
     *
     * @param test test that started
     * @throws RemoteException    network problems
     * @throws SmartFrogException other problems
     */
    public void startTest(TestInfo test) throws RemoteException, SmartFrogException {

    }

    /**
     * Log an event
     *
     * @param event what happened
     * @throws RemoteException network problems
     */
    public void log(LogEntry event) throws RemoteException {

    }
}
