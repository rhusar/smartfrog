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


import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.agents.SLPMessageCallbacks;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SLPTcpClient {
    private Socket socket;
    private OutputStream ostream;
    private InputStream istream;
    private SLPMessageCallbacks agent;

    public SLPTcpClient(SLPMessageCallbacks a) {
        agent = a;
        socket = new Socket(); // connect when needed.
    }

    public void sendSlpMessage(SLPMessageHeader msg, String toAddress, int toPort,
                               ServiceLocationEnumeration res) throws IOException, ServiceLocationException {
        SLPOutputStream sos = new SLPOutputStream(new ByteArrayOutputStream());
        // write message to stream
        socket.connect(new InetSocketAddress(toAddress, toPort), 1000);
        istream = socket.getInputStream();
        ostream = socket.getOutputStream();
        // send message...
        msg.toOutputStream(sos);
        ostream.write(sos.getByteArray());
        // get reply...
        SLPInputStream sis = new SLPInputStream(istream);
        // read function and version...
        int version = sis.readByte();
        int function = sis.readByte();
        //System.out.println("got reply: function = " + function + ", version = " + version);
        agent.handleReplyMessage(function, sis, res);
        socket.close();
    }
}
