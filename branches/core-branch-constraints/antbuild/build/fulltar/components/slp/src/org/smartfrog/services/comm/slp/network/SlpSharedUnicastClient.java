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
import org.smartfrog.services.comm.slp.util.SLPInputStream;

import java.io.ByteArrayInputStream;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SlpSharedUnicastClient extends SlpUnicastClient {
    private static final int XID_POSITION = 10;
    private Map callbackMap;

    public SlpSharedUnicastClient(InetAddress address, int port, int MTU) throws ServiceLocationException {
        super(address, port, MTU, null);
        callbackMap = Collections.synchronizedMap(new TreeMap());
    }

    public synchronized boolean send(DatagramPacket p, int id, SlpUdpCallback cb) throws ServiceLocationException {
        if (callbackMap.containsKey(Integer.toString(id))) {
            return false; // XID is in use !
        }

        // register XID and send message
        callbackMap.put(Integer.toString(id), cb);
        send(p);
        return true;
    }

    public synchronized void removeCallback(int id) {
        callbackMap.remove(Integer.toString(id));
    }

    public void run() {
        while (running) {
            try {
                packet = new DatagramPacket(data, MTU);
                socket.receive(packet);
                if (running) {
                    // need to find the correct callback object.
                    ByteArrayInputStream stream = new ByteArrayInputStream(packet.getData());
                    SLPInputStream sis = new SLPInputStream(stream);
                    //System.out.println("Getting XID");
                    int xid;
                    if ((sis.skip(XID_POSITION)) == XID_POSITION) {
                        xid = sis.readShort();
                        //System.out.println("XID = " + xid);
                        callback = (SlpUdpCallback) callbackMap.get(Integer.toString(xid));
                        if (callback != null) {
                            if (!callback.udpReceived(packet)) running = false;
                            callback = null;
                        }
                        /*
						else {
                            System.out.println("SLP: No callback for XID = " + xid);
                        }
						*/
                    }
                }
            } catch (InterruptedIOException e) {
                // should never get a timeout.
                // we try to continue.
            }
            catch (Exception e) {
                // If we get here, something strange has happened.
                // Notify callback objects, and kill thread.
                synchronized (callbackMap) {
                    Iterator iter = callbackMap.values().iterator();
                    while (iter.hasNext()) {
                        ((SlpUdpCallback) iter.next()).udpError(e);
                    }
                    callbackMap.clear();
                }
                callbackMap = null;
                running = false;
            }
        }

        // try to close socket.
        try {
            socket.close();
        } catch (Exception e) {
        } // ignore error
    }
}
