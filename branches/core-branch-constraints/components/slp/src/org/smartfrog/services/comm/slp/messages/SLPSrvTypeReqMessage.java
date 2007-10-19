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

package org.smartfrog.services.comm.slp.messages;

import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Vector;


/**
 * This class represents a service type request message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Service location header (function = SrvTypeRqst = 9)    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     Length of PRList          |           PRList string       \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  length og naming authority   |    Naming authority string    \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    Length of scope-list       |         scope-list            \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPSrvTypeReqMessage extends SLPMessageHeader {
    /** List of previous responders */
    private String PRList;
    /** Naming authority for the service type */
    private String namingAuth;
    /** scopes to search in */
    private Vector scopes;
    /** String representsation of scope vector */
    private String scopeStr;

    /** Creates an empty SLPSrvTypeReqMessage. */
    public SLPSrvTypeReqMessage() {
        super(SLPMSG_SRVTYPE);
        PRList = "";
        namingAuth = "";
        scopes = null; //new Vector();
        scopeStr = "";
    }

    /**
     * Creates an SLPSrvTypeReqMessage.
     *
     * @param na   The naming authority to use.
     * @param s    The scope list
     * @param lang The locale to use.
     */
    public SLPSrvTypeReqMessage(String na, Vector s, Locale lang) {
        super(SLPMSG_SRVTYPE, lang);
        PRList = "";
        namingAuth = na;
        scopes = s;
        scopeStr = SLPUtil.vectorToString(scopes);

        // calculate length
        length += 6; // constant length fields.
        length += PRList.length();
        if (na != null) length += na.length();
        length += scopeStr.length();
    }

    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(PRList.length()); // length of PRList
            stream.writeString(PRList); // PRList
            if (namingAuth == null) {
                stream.writeShort(0xFFFF);
            } else {
                stream.writeShort(namingAuth.length());
                stream.writeString(namingAuth);
            }
            stream.writeShort(scopeStr.length());
            stream.writeString(scopeStr);
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        int len = 0;
        try {
            len = stream.readShort();
            PRList = stream.readString(len);
            len = stream.readShort();
            if (len == 0xFFFF) {
                namingAuth = null;
            } else {
                namingAuth = stream.readString(len);
            }
            len = stream.readShort();
            scopeStr = stream.readString(len);
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create scope vector
        scopes = SLPUtil.stringToVector(scopeStr);
    }

    /** Returns the previous responders list */
    public String getPRList() {
        return PRList;
    }

    /** Returns the naming authority */
    public String getNamingAuthority() {
        return namingAuth;
    }

    /** Returns the scope list */
    public Vector getScopes() {
        return scopes;
    }

    /** Adds an address to the list of previous responders */
    public void addResponder(String resp) {
        length -= PRList.length();
        if (PRList.equals("")) {
            PRList = resp;
        } else {
            PRList = PRList + "," + resp;
        }

        length += PRList.length();
    }

    public String toString() {
        String s = super.toString();
        s += "PRList: " + PRList + "\n"
                + "Naming Auth: " + namingAuth + "\n"
                + "Scopes: " + scopeStr + "\n"
                + "*** End Of Message ***";

        return s;
    }
}
