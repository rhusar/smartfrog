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
package org.smartfrog.services.xunit.log;

import org.smartfrog.services.xunit.base.LogListener;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.logging.LogRemote;

import java.rmi.RemoteException;

/**
 * Remote interface to something that listens for smartfrog events and yet which can be
 * pointed at a listener or two.
 * created 18-May-2006 15:32:57
 */

public interface TestListenerLog extends LogRemote {

    /**
     * Add a listener to log events
     * @param listener the listeer (must not be null)
     * @throws SmartFrogException implementation-specific errors.
     * In {@link TestListenerLogImpl#addLogListener(LogListener)}, a SmartFrogException is thrown if the
     * listener is already listed as listening to the log
     * @throws RemoteException network problems
     */
    public void addLogListener(LogListener listener) throws SmartFrogException, RemoteException;

    /**
     * Remove a log listener. Harmless if the log is not active
     * @param listener listener
     * @throws SmartFrogException implementation-specific errors
     * @throws RemoteException network problems
     */
    public void removeLogListener(LogListener listener) throws SmartFrogException, RemoteException;

    /**
     * Remove all listeners
     * @throws SmartFrogException implementation-specific errors
     * @throws RemoteException network problems
     */
    void clearListeners() throws RemoteException, SmartFrogException;
}
