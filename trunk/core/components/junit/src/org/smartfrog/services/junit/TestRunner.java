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

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the public testrunner interface
 * created 15-Apr-2004 11:51:44
 */


public interface TestRunner extends Remote {
    /**
     * name of the fork attribute
     */
    String ATTR_FORK = "fork";
    /**
     * name of the listener
     */
    String ATTR_LISTENER = "listener";

    /**
     * name of the keepgoing attr
     */
    String ATTR_KEEPGOING = "keepgoing";

    /**
     * should deploy fail if there is an error?
     */
    String ATTR_FAILONERROR = "failOnError";

    /**
     * read only -after start
     */
    String ATTR_ERRORS  = "errors";

    /**
     * read only -after start
     */
    String ATTR_FAILURES = "failures";

    String ATTR_THREAD_PRIORITY="threadPriority";


    TestListener getListener() throws RemoteException;

    void setListener(TestListener listener) throws RemoteException;

    /**
     * run the test
     *
     * @throws RemoteException
     */
    boolean runTests() throws RemoteException, SmartFrogException;

}
