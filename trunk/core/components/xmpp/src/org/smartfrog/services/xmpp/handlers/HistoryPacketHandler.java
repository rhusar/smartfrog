/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xmpp.handlers;

import org.smartfrog.services.xmpp.WireMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created 13-Feb-2008 12:02:22
 */


public interface HistoryPacketHandler extends Remote {
    String ATTR_LIMIT = "limit";
    String ATTR_DUMP_ON_TERMINATE = "dumpOnTerminate";

    /**
     * Test for the handler having received a message
     *
     * @param sender sender
     * @param regexp regexp to assert for
     * @return the message in a wire format, or null
     * @throws RemoteException for networking problems
     */

    WireMessage hasMessage(String sender, String regexp) throws RemoteException;

    /**
     * Dump all packets at info level
     *
     * @throws RemoteException for networking problems
     */
    void dump() throws RemoteException;

    /**
     * Dump all packets at info level
     *
     * @return an concatenation of all packets' XML forms
     * @throws RemoteException for networking problems
     */
    String toXML() throws RemoteException;
}
