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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class SlpUnicastClient extends SlpUdpClient {
    private int port;
    private InetAddress address;

    public SlpUnicastClient(InetAddress address, int port,
                            int MTU, SlpUdpCallback cb) throws ServiceLocationException {

        this(address, port, false, MTU, cb);
    }

    public SlpUnicastClient(InetAddress address, int port, boolean allowReuse,
                            int MTU, SlpUdpCallback cb) throws ServiceLocationException {
        super(MTU);

        // update variables
        this.address = address;
        this.port = port;
        this.callback = cb;

        try {
            if (allowReuse) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(address, port));
            } else {
                socket = new DatagramSocket(port, address);
            }

            // set timeout to 0. (no timeout)
            socket.setSoTimeout(0);
        } catch (SocketException e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_INIT_FAILED,
                    "Could not create a socket on " +
                            address.getHostAddress() + " port " + port);
        }

        //start listenerthread...
        Thread t = new Thread(this);
        t.start();
    }

    public synchronized void send(DatagramPacket pk) throws ServiceLocationException {
        try {
            socket.send(pk);
        } catch (Exception e) {
            throw new ServiceLocationException(ServiceLocationException.NETWORK_ERROR,
                    "Could not send SLP message.");
        }
    }
}
