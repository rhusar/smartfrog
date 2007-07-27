/*
 Service Location Protocol - SmartFrog components.
 Copyright (C) 2004 Glenn Hisdal <ghisdal(a)c2i.net>
 
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
 
 This library was originally developed by Glenn Hisdal at the 
 European Organisation for Nuclear Research (CERN) in Spring 2004. 
 The work was part of a master thesis project for the Norwegian 
 University of Science and Technology (NTNU).
 
 For more information: http://home.c2i.net/ghisdal/slp.html 
 */

package org.smartfrog.services.comm.slp.network;

import org.smartfrog.services.comm.slp.ServiceLocationException;

import java.net.InetAddress;
import java.net.MulticastSocket;

public class SlpMulticastClient extends SlpUdpClient {
    private int port;
    private InetAddress address;
    private InetAddress iface;

    public SlpMulticastClient(String addr, InetAddress iface,
                              int port, int MTU, SlpUdpCallback cb) throws ServiceLocationException {
        super(MTU);
        // update variables
        try {
            this.address = InetAddress.getByName(addr);
            this.port = port;
            this.iface = iface;
            this.callback = cb;

            // create socket
            //MulticastSocket s = new MulticastSocket(new InetSocketAddress(address, port));
            MulticastSocket s = new MulticastSocket(port);
            // set timeToLive
            s.setTimeToLive(255);
            // set correct interface and join multicast group.
            //s.setInterface(iface); // Is this needed ?
            s.joinGroup(address);

            // set timeout to 0. (no timeout)
            s.setSoTimeout(0);

            socket = s;
        } catch (Exception ex) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_INIT_FAILED,
                    "Failed to initilize multicast network to " + addr, ex);
        }

        //start listenerthread...
        Thread t = new Thread(this);
        t.start();
    }
}

