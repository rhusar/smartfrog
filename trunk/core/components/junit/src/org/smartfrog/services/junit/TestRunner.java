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
    String FORK_ATTRIBUTE = "fork";
    /**
     * name of the listener
     */
    String LISTENER_ATTRIBUTE = "listener";

    /**
     * name of the keepgoing attr
     */
    String KEEPGOING_ATTRIBUTE = "keepgoing";

    TestListener getListener() throws RemoteException;

    void setListener(TestListener listener) throws RemoteException;

    boolean isKeepGoing() throws RemoteException;

    void setKeepGoing(boolean keepGoing) throws RemoteException;

    boolean isFork() throws RemoteException;

    void setFork(boolean fork) throws RemoteException;
}
