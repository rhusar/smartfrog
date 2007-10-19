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

package org.smartfrog.services.comm.slp.agents;

import org.smartfrog.services.comm.slp.ServiceLocationEnumeration;
import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.util.SLPInputStream;

/** Defines the methods used to handle incoming requests. All agents implement these. */
public interface SLPMessageCallbacks {
    /**
     * Called whenever request that is not a reply is received. These are: SrvReq, SrvTypeReq, SrvReg, SrvDeReg.
     *
     * @param function The type of message received.
     * @param sis      The SLPInputStream to read the message from.
     * @param isUDP    Set to 'true' if message was received on the UDP listener.
     * @return A reply to the message, or null if no reply is to be sent.
     */
    abstract SLPMessageHeader handleNonReplyMessage(int function, SLPInputStream sis, boolean isUDP)
            throws ServiceLocationException;

    /**
     * Called whenever a reply to a request is received. Replies can be: SrvRply, SrvTypeRply, SrvAck.
     *
     * @param function The type of message received.
     * @param sis      The SLPInputStream to read the message from.
     * @param results  Any results returned by the reply is put into this.
     * @return true if message was complete. fals if message was truncated.
     */
    abstract boolean handleReplyMessage(int function, SLPInputStream sis,
                                        ServiceLocationEnumeration results)
            throws ServiceLocationException;

}

