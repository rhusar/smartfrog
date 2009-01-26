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
package org.smartfrog.services.xunit.listeners.xml;

import org.smartfrog.services.xunit.base.TestListener;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * An XML listener also lets you get the name of the file, as an operation on
 * the interface, <i>and</i> as an attribute that is added to a test suite when
 * run. That is, every test knows the name of the test. created Nov 23, 2004
 * 3:47:29 PM
 */


public interface FileListener extends TestListener {

    /**
     * the name of the file that is used to store the XML output of the test
     */
    String ATTR_FILE = "file";

    /**
     * get the filename of this
     *
     * @return the filename used
     * @throws RemoteException network trouble
     */
    String getFilename() throws RemoteException;
    
    /**
     * Open the listener. This can be a no-op, or it can open a file and throw
     * An exception on demand
     * @throws IOException for IO trouble
     * @throws RemoteException network trouble
     */
    void open() throws IOException, RemoteException;


    /**
     * close the file.
     * This call  must be harmless if the file is already closed.
     *
     * Note: some implementations may choose to delay saving the file until
     * this event. That is OK. just don't take too long.
     *
     * @throws IOException IO trouble
     * @throws RemoteException network trouble
     * @throws SmartFrogException for anything else.
     */
   void close() throws IOException, RemoteException, SmartFrogException;


    /**
     * test for the file being open
     *
     * @return true iff the file is not null
     * @throws RemoteException network trouble
     */
    boolean isOpen()  throws RemoteException;

}
