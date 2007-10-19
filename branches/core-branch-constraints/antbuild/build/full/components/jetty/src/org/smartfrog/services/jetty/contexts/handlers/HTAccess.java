/** (C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.jetty.contexts.handlers;

import org.mortbay.http.handler.HTAccessHandler;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;

import java.rmi.RemoteException;

/**
 * A HTAccess handler class for jetty server
 *
 * @author Ritu Sabharwal
 */


public class HTAccess extends HandlerImpl implements HTAccessIntf {
    protected Reference accessFileRef = new Reference(ACCESS_FILE);

    protected String accessFile = ".htaccess";

    protected HTAccessHandler hthandler = new HTAccessHandler();

    /**
     * Standard RMI constructor
     */
    public HTAccess() throws RemoteException {
        super();
    }

    /**
     * sfDeploy: adds the HTAccess Handler to ServetletHttpContext of jetty server
     *
     * @throws SmartFrogException In case of error while deploying
     * @throws RemoteException    In case of network/rmi error
     */
    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        accessFile = sfResolve(accessFileRef, accessFile, false);
        hthandler.setAccessFile(accessFile);
        addHandler(hthandler);
    }
}
