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
package org.smartfrog.services.junit;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the interface that all testlisteners must implement.
 * It is very similar to the {@link junit.framework.TestListener} interface,
 * except it is designed for remoting.
 * <p/>
 * Instead of passing real tests around, we have a TestInfo class that contains
 * summary data about the test.
 * If a fault occurred, that is inserted into the testInfo that we add
 * created 14-Apr-2004 16:56:58
 */


public interface TestListener extends Remote {

    /**
     * end this test suite. After calling this, caller should discard
     * all references; they may no longer be valid.
     * <i>No further methods may be called</i>
     *
     */
    void endSuite()
            throws RemoteException, SmartFrogException;

    /**
     * An error occurred.
     */
    void addError(TestInfo test) throws RemoteException, SmartFrogException;

    /**
     * A failure occurred.
     */
    void addFailure(TestInfo test)
            throws RemoteException, SmartFrogException;

    /**
     * A test ended.
     */
    void endTest(TestInfo test) throws RemoteException, SmartFrogException;

    /**
     * A test started.
     */
    void startTest(TestInfo test) throws RemoteException, SmartFrogException;

}
