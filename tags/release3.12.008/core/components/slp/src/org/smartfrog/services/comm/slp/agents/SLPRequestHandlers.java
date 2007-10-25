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

import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.messages.SLPAttrReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPAttrRplyMessage;
import org.smartfrog.services.comm.slp.messages.SLPMessageHeader;
import org.smartfrog.services.comm.slp.messages.SLPSrvReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvRplyMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvTypeReqMessage;
import org.smartfrog.services.comm.slp.messages.SLPSrvTypeRplyMessage;

import java.util.Vector;

/** This class implements the handling of requests that are common for the SA and the DA. */
public class SLPRequestHandlers {
    /**
     * Searches the database for entries matching the request and creates a reply if needed. The method only handles
     * normal service requests. Special requests (service:directory-agent and service:service-agent) are handled
     * directly by the DA and SA classes.
     *
     * @param db    The SLP Database to search in.
     * @param msg   The received request.
     * @param error An error code - If an error occured while parsing the message
     * @param isUDP Set to true if the request was received by UDP.
     * @return A SLPMessageHeader containing the reply to the message or null if no reply is to be sent.
     */
    protected static SLPMessageHeader
    handleServiceRequest(SLPDatabase db, SLPSrvReqMessage msg, int error, int mtu, boolean isUDP) {
        SLPMessageHeader reply = null;
        if (error == 0) {
            // search database
            Vector matches = db.findEntries(msg.getServiceType(),
                    msg.getSearchFilter(),
                    msg.getLanguage());
            if (matches == null) {
                // service exists, but not in the given locale
                error = ServiceLocationException.LANGUAGE_NOT_SUPPORTED;
            } else {
                // Either no service matching the request was found, or we have
                // one or more matching services.
                // Create a reply if we found a service or the message was not
                // multicast.
                if (matches.size() != 0 || (msg.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {
                    int maxlen = mtu;
                    if (!isUDP) maxlen = Integer.MAX_VALUE;
                    reply = new SLPSrvRplyMessage(matches, msg.getLanguage(), maxlen);
                    reply.setXID(msg.getXID());
                }
            }
        }//error

        // if we have an error, create a reply if message was not multicast.
        if (error != 0) {
            if ((msg.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {
                reply = new SLPSrvRplyMessage(error, msg.getLanguage());
                reply.setXID(msg.getXID());
            }
        }

        return reply;
    }

    protected static SLPMessageHeader
    handleServiceTypeRequest(SLPDatabase db, SLPSrvTypeReqMessage msg, int error, int mtu, boolean isUDP) {
        SLPMessageHeader reply = null;

        if (error == 0) {
            Vector stypes = db.findServiceTypes(msg.getNamingAuthority());

            // create reply if needed.
            if (stypes.size() != 0 || (msg.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {
                int maxlen = mtu;
                if (!isUDP) maxlen = Integer.MAX_VALUE;
                reply = new SLPSrvTypeRplyMessage(stypes, msg.getLanguage(), maxlen);
                reply.setXID(msg.getXID());
            }
        } else {
            // create reply if unicast...
            if ((msg.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {
                reply = new SLPSrvTypeRplyMessage(error, msg.getLanguage());
                reply.setXID(msg.getXID());
            }
        }

        return reply;
    }

    protected static SLPMessageHeader
    handleAttributeRequest(SLPDatabase db, SLPAttrReqMessage msg, int error, int mtu, boolean isUDP) {
        SLPMessageHeader reply = null;
        if (error == 0) {
            Vector attributes;
            if (msg.getURL() != null) {
                attributes = db.findServiceAttributes(msg.getURL(),
                        msg.getLanguage(),
                        msg.getTags());
            } else {
                attributes = db.findServiceAttributes(msg.getServiceType(),
                        msg.getLanguage(),
                        msg.getTags());
            }
            // create reply if needed.
            if (attributes.size() != 0 || (msg.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {
                int maxlen = mtu;
                if (!isUDP) maxlen = Integer.MAX_VALUE;
                reply = new SLPAttrRplyMessage(attributes, msg.getLanguage(), maxlen);
                reply.setXID(msg.getXID());
            }
        } else {
            // create reply if unicast...
            if ((msg.getFlags() & SLPMessageHeader.FLAG_MCAST) == 0) {
                reply = new SLPAttrRplyMessage(error, msg.getLanguage());
                reply.setXID(msg.getXID());
            }
        }

        return reply;
    }
}
