/* (C) Copyright 2009 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.cloudfarmer.server.manual;

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.rmi.RemoteException;

/**
 * Represents a physical node
 */

public class ManualHostImpl extends PrimImpl implements ManualHost  {


    /** a hostname is required */
    private String hostname ;

    /** Any description */
    private String description;

    public ManualHostImpl() throws RemoteException {
    }

    @Override
    public void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        hostname = sfResolve(ATTR_HOSTNAME,"",true);
        description = sfResolve(ATTR_DESCRIPTION,"",true);
    }

    /**
     * @return the hostname
     */
    @Override
    public String getHostname() {
        return hostname;
    }

    /**
     * @return The description
     */
    @Override
    public String getDescription() {
        return description;
    }
}
