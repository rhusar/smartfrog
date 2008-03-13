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
import org.smartfrog.sfcore.common.SmartFrogDeploymentException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.utils.ComponentHelper;

import java.rmi.RemoteException;

/**
 * component to make assertions about messages recieved Created 13-Feb-2008 11:06:58
 */

public class HistoryPacketHandlerAssertionImpl extends PrimImpl {
    public static final String ATTR_MESSAGE = "textRegexp";
    public static final String ATTR_HISTORY = "history";
    public static final String ATTR_SENDER = "sender";

    public HistoryPacketHandlerAssertionImpl() throws RemoteException {
    }

    /**
     * Check for a message, fail if it was not found. Otherwise, trigger workflow termination
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException    In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        ComponentHelper helper = new ComponentHelper(this);
        Prim history = sfResolve(ATTR_HISTORY, (Prim) null, true);
        HistoryPacketHandler historyHandler = (HistoryPacketHandler) history;
        String message = sfResolve(ATTR_MESSAGE, "", true);
        String sender = sfResolve(ATTR_SENDER, "", true);
        WireMessage found = historyHandler.hasMessage(sender, message);
        if (found == null) {
            historyHandler.dump();
            String xml = historyHandler.toXML();
            throw new SmartFrogDeploymentException("Failed to find a message with message body '"
                    + message
                    + "'\n in:\n<history>\n " + xml + "\n</history>");
        }
        helper.sfSelfDetachAndOrTerminate(TerminationRecord.NORMAL, "received message: " + found, sfCompleteName(),
                null);
    }
}
