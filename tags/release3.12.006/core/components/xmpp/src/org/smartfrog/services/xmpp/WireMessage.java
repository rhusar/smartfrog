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
package org.smartfrog.services.xmpp;

import org.jivesoftware.smack.packet.Message;

import java.io.Serializable;

/**
 *
 * Created 14-Aug-2007 17:36:40
 *
 */

public class WireMessage implements Serializable {

    private String subject;
    private String thread;
    private String body;
    private String packetID;
    private String to;
    private String type;


    public WireMessage() {
    }


    public WireMessage(Message m) {
        subject=m.getSubject();

        body=m.getBody();
        packetID=m.getPacketID();
        thread=m.getThread();
        to = m.getTo();
        type = m.getType().toString();
    }
}
