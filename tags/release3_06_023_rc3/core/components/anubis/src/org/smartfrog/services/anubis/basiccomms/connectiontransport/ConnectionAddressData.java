/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.anubis.basiccomms.connectiontransport;

import java.net.InetAddress;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

public class ConnectionAddressData
    extends PrimImpl implements Prim {

    static final String HOST = "host";
    static final String LOCALHOST = "localhost";
    static final String PORT = "port";

    public ConnectionAddressData() throws Exception {
        super();
    }

    protected int getPort() throws Exception {
        return sfResolve(PORT, (int) 0, false);
    }

    protected InetAddress getAddress() throws Exception {
        String hostName = sfResolve(HOST, LOCALHOST, false);
        if (hostName.equals(LOCALHOST)) {
           return InetAddress.getLocalHost();
       } else {
           return InetAddress.getByName(hostName);
       }
    }

    public ConnectionAddress getConnectionAddress() throws Exception {
        return new ConnectionAddress(getAddress(), getPort());
    }

}
