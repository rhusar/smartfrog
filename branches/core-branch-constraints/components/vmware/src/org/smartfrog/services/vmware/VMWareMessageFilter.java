/**
(C) Copyright 1998-2007 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org
*/

package org.smartfrog.services.vmware;

import org.smartfrog.services.xmpp.MessageFilter;
import org.smartfrog.services.xmpp.XMPPEventExtension;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Message;

public class VMWareMessageFilter extends MessageFilter {
    public boolean accept(Packet packet) {
        // is it a xmpp message?
        if (packet instanceof Message)
        {
            // try to get the extension
            XMPPEventExtension ext = (XMPPEventExtension) packet.getExtension(XMPPEventExtension.rootElement, XMPPEventExtension.namespace);
            if (ext != null)
            {
                if (ext.getPropertyBag().get("vmpath") != null)
                    return true;
            }
        }

        return false;
    }
}
