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

 */package org.smartfrog.services.xunit.listeners;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.services.xunit.base.TestSuite;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is what index components
 * implement to get told of tests to index 
 */
public interface TestIndex extends Remote {


    /**
     * notify indexer that a test suite has started
     * @param suite test suite
     * @param hostname host starting the tests
     * @param processname process of the tests
     * @param suitename name of the suite
     * @param timestamp when they started
     * @param listener who is listening to it
     * @param filename the file being created
     * @throws SmartFrogException SmartFrog trouble
     * @throws RemoteException In case of network/rmi error
     */
    void testSuiteStarted(
           TestSuite suite,
           String hostname,
           String processname,
           String suitename,
           long timestamp,
           TestListener listener,
           File filename)
            throws RemoteException, SmartFrogException;

}
