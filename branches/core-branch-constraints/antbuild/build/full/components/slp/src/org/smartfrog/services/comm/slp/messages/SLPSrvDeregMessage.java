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
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.io.IOException;
import java.util.Vector;

/**
 * Implements the Service Deregistration message. This is sent from a SA to a DA when a service is to be deregistered.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |           Service location header (function = SrvDeReg = 4)   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    Length of scope-list       |              scope-list       \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          URL Entry                            \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Length of tag-list       |              tag-list         \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPSrvDeregMessage extends SLPMessageHeader {
    private static final int FUNCTION = SLPMessageHeader.SLPMSG_SRVDEREG;
    private Vector scopes;
    private URLEntry urlEntry;
    private String taglist = ""; // currently not supported.

    // string representations...
    private String scopeStr;

    /** Creates an empty SLPSrvDeregMessage. Contents to be read from a stream. */
    public SLPSrvDeregMessage() {
        super(FUNCTION);
        scopes = null; //new Vector();
        urlEntry = null;
    }

    /**
     * Creates an SLPSrvDeregMessage.
     *
     * @param url    The service URL.
     * @param scopes The scopes the service is registered in.
     */
    public SLPSrvDeregMessage(ServiceURL url, Vector scopes) {
        super(FUNCTION);
        urlEntry = new URLEntry(url, new Vector());
        this.scopes = scopes;
        scopeStr = SLPUtil.vectorToString(scopes);

        // calculate length;
        length += 2 + scopeStr.length();
        length += urlEntry.getLength();
        length += 2; // tag list length
    }

    /** Returns the service URL */
    public ServiceURL getURL() {
        return urlEntry.getURL();
    }

    /** Returns the scope list */
    public Vector getScopes() {
        return scopes;
    }

    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(scopeStr.length());
            stream.writeString(scopeStr);
            // write url entry...
            urlEntry.toOutputStream(stream);
            stream.writeShort(0); // length of tag-list.
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        int len = 0;
        try {
            len = stream.readShort();
            scopeStr = stream.readString(len);
            urlEntry = new URLEntry();
            urlEntry.fromInputStream(stream);
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        scopes = SLPUtil.stringToVector(scopeStr);
    }

    public String toString() {
        String s = super.toString();
        s += "Scopes: " + scopeStr + "\n"
                + "Tag list: " + taglist + "\n"
                + "URL: " + urlEntry.getURL().toString() + "\n"
                + "*** End Of Message ***";

        return s;
    }
}

