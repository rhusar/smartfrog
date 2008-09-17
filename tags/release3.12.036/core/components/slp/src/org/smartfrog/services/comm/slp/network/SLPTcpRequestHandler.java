/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.comm.slp.network;

import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.agents.SLPMessageCallbacks;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/** created 21-Jun-2007 14:30:26 */
class SLPTcpRequestHandler extends Thread {
    private Socket socket;
    private InputStream istream;
    private OutputStream ostream;
    private SLPMessageCallbacks agent;

    SLPTcpRequestHandler(Socket s, SLPMessageCallbacks a) {
        socket = s;
        agent = a;
    }

    public void run() {
        try {
            istream = socket.getInputStream();
            ostream = socket.getOutputStream();
            SLPInputStream sis = new SLPInputStream(istream);
            int version = sis.readByte();
            int function = sis.readByte();
            //System.out.println("TCP request: v="+version+", f="+function);
            SLPMessageHeader msgReply = agent.handleNonReplyMessage(function, sis, false);
            SLPOutputStream sos = new SLPOutputStream(new ByteArrayOutputStream());
            msgReply.toOutputStream(sos);
            ostream.write(sos.getByteArray());
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (ServiceLocationException e) {
            //e.printStackTrace();
        }
    }
}
