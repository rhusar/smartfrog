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

import java.net.DatagramPacket;

/**
 * This interface defines the methods that can be called from the SlpUdpClient classes. All classes that are to be
 * notified of incoming udp traffic must implement these.
 */
public interface SlpUdpCallback {
    /**
     * Called when a packet is received from the network.
     *
     * @param p The received packet.
     * @return A boolean saying if the listener should continue running.
     */
    public abstract boolean udpReceived(DatagramPacket p);

    /**
     * Called when a timeout occurs when waiting for data to arrive.
     *
     * @return A boolean saying if the listener should continue running.
     */
    public abstract boolean udpTimeout();

    /**
     * Called when an exception is caught by the UdpClient class.
     *
     * @param e The exception.
     * @return A boolean saying if the listener should continue running.
     */
    public abstract boolean udpError(Exception e);
}
