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
package org.smartfrog.services.filesystem;

import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;

/**
 * Remote interface for touching a file.
 * created 18-May-2004 11:30:01
 */


public interface TouchFileIntf extends FileUsingComponent {

    /**
     * its age in milliseconds since 1970-01-01
     */
    final static String ATTR_AGE = "timestamp";


    /**
     * extract the current values of the attributes, and then
     * touch the file
     * @throws java.rmi.RemoteException  In case of network/rmi error
     * @throws org.smartfrog.sfcore.common.SmartFrogException if there was a resolution problem
     */
    void touch() throws RemoteException, SmartFrogException;
}
