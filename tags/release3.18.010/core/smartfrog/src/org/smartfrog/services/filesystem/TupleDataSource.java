/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface is implemented by components that act as a source of string tuples of
 * intederminate width.
 */
public interface TupleDataSource extends Remote {
    /**
     * Get the next line
     * @return the next line, all broken up, or null for no new lines.
     * @throws RemoteException network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    String[] getNextTuple() throws RemoteException, SmartFrogException;

    /**
     * Go back to the start of the file
     * @throws RemoteException network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    void start() throws RemoteException, SmartFrogException;

    /**
     * Close the reader. harmless if we are already closed
     * @throws RemoteException network problems
     * @throws SmartFrogException parsing/file IO problems
     */
    void close() throws RemoteException, SmartFrogException;
}
