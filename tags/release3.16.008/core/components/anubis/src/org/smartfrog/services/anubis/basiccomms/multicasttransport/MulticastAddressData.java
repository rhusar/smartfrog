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
package org.smartfrog.services.anubis.basiccomms.multicasttransport;

import java.net.InetAddress;

import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

public class MulticastAddressData
    extends PrimImpl implements Prim {

    static final String HOST = "host";
    static final String LOCALHOST = "localhost";
    static final String PORT = "port";
    static final String TTL = "timeToLive";

    public MulticastAddressData() throws Exception {
        super();
    }

    public MulticastAddress getMulticastAddress() throws Exception {

        String hostName = sfResolve(HOST, LOCALHOST, false);
        int port = sfResolve(PORT, 0, true);
        int ttl = sfResolve(TTL, 1, false);

        InetAddress host;
        if (hostName.equals(LOCALHOST)) {
            host = InetAddress.getLocalHost();
        }
        else {
            host = InetAddress.getByName(hostName);
        }

        return new MulticastAddress(host, port, ttl);
    }

}
