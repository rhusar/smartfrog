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

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * The SlpUdpClient class and subclasses are responsible for listening for incoming UDP messages and sending datagrams
 * from the SLP agents.
 */
public abstract class SlpUdpClient implements Runnable {
    protected DatagramSocket socket;
    protected boolean running = true;
    protected byte[] data;
    protected int MTU;
    protected DatagramPacket packet = null;
    protected SlpUdpCallback callback = null;

    /**
     * Creates a new SlpUdpClient.
     *
     * @param mtu The MTU for SLP messages.
     */
    public SlpUdpClient(int mtu) {
        MTU = mtu;
        data = new byte[MTU];
        socket = null; // create socket in subclass !
    }

    /** Stops the listener thread, and closes the socket. */
    public synchronized void close() {
        running = false;
        try {
            socket.close();
        } catch (Exception e) {
        }
    }

    /**
     * Sends a datagrampacket.
     *
     * @param p The packet to send.
     * @throws ServiceLocationException if there is an error.
     */
    public synchronized void send(DatagramPacket p) throws ServiceLocationException {
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
    }

    /**
     * Sends a datagrampacket and regisers a callback. This is to be used when one wants to receive replies after
     * sending a message through a shared SlpUdpClient.
     *
     * @param p  The packet to send.
     * @param id The XID of the message we want the replies for.
     * @param cb The SlpUdpCallback that will handle the replies.
     */
    public synchronized boolean send(DatagramPacket p, int id, SlpUdpCallback cb) throws ServiceLocationException {
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
    }

    /** Removes the callback registered with a shared SlpUdpClient. */
    public synchronized void removeCallback(int id) throws ServiceLocationException {
        throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
    }

    /** Runs the listener thread. Listens for incoming messages, and calls the appropriate callback methods. */
    public void run() {
        while (running) {
            try {
                packet = new DatagramPacket(data, MTU);
                socket.receive(packet);
                if (running) running = callback.udpReceived(packet);

            } catch (InterruptedIOException e) {
                if (running) running = callback.udpTimeout();
            }
            catch (Exception e) {
                if (running) running = callback.udpError(e);
            }
        }

        // try to close socket.
        try {
            socket.close();
        } catch (Exception e) {
        } // ignore error
    }

    /** Returns the port this object is listening to. */
    public int getPort() {
        return socket.getLocalPort();
    }
}



