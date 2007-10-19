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

import org.smartfrog.services.xunit.base.TestListenerFactory;
import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Dump all listened to events into the void.
 * <p>
 * The factory returns the same instance of the class every time, the null listener itself,
 * which discards all events
 */

public class NullListenerImpl extends AbstractListenerImpl implements TestListenerFactory {


    public NullListenerImpl() throws RemoteException {
    }


    /**
     * Start listening to a test suite
     *
     * @param suite       the test suite that is about to run. May be null, especially during testing.
     * @param hostname    name of host
     * @param processname name of the process
     * @param suitename   name of test suite
     * @param timestamp   start timestamp (UTC)
     * @return a listener to talk to
     * @throws RemoteException    network problems
     * @throws SmartFrogException code problems
     */
    public TestListener listen(TestSuite suite, String hostname, String processname, String suitename, long timestamp) throws RemoteException, SmartFrogException {
        return this;
    }
}
