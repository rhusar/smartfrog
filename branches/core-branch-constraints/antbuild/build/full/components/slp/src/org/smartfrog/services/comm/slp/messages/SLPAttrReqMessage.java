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
import org.smartfrog.services.comm.slp.ServiceType;
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

/**
 * A class representing the SLP Attribute request message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Service Location header (function = AttrRqst = 6)       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Length of PRList        |         PRList String         \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Length of URL         |              URL              \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Length of scope-list        |        scope-list string      \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Length of tag-list string   |        tag-list string        \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    Length of SLP SPI string   |       SLP SPI string          \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPAttrReqMessage extends SLPMessageHeader {
    /** List of previous responders */
    private String PRList;
    /** The Service to get attributes for */
    private String url;
    /** The scopes we search in */
    private Vector scopes;
    /** String representation of scope vector */
    private String scopeStr;
    /** tag list used to select which attributes to return */
    private String tagList;
    /** A Vector of the tags in the tag list. */
    private Vector tags;
    /** The SPI string for this message */
    private String SPI;

    private ServiceURL serviceUrl = null;
    private ServiceType serviceType = null;

    public SLPAttrReqMessage() {
        super(SLPMSG_ATTRREQ);
        PRList = "";
        url = "";
        scopes = null;//new Vector();
        scopeStr = "";
        tags = null;
        tagList = "";
        SPI = "";
    }

    public SLPAttrReqMessage(String u, Vector tags, Vector s, Locale lang) {
        super(SLPMSG_ATTRREQ, lang);
        PRList = "";
        url = u;
        this.tags = tags;
        scopes = s;
        SPI = "";

        // create scope string...
        scopeStr = SLPUtil.vectorToString(scopes);

        // create taglist string
        tagList = SLPUtil.vectorToString(tags);

        // calculate length...
        length += 5; // length fields
        length += PRList.length();
        length += url.toString().length();
        length += scopeStr.length();
        length += tagList.length();
        length += SPI.length();
    }

    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(PRList.length()); // length of PRList
            stream.writeString(PRList); // PRList
            stream.writeShort(url.length()); // length of URL
            stream.writeString(url); // url.
            stream.writeShort(scopeStr.length()); // length of scope list
            stream.writeString(scopeStr); // scope list
            stream.writeShort(tagList.length()); // length of tag-list
            stream.writeString(tagList);  // tag-list.
            stream.writeShort(SPI.length()); // length of SPI string
            stream.writeString(SPI); // SPI string.
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        try {
            int len = stream.readShort(); // lenght of PRList
            PRList = stream.readString(len); // PRList
            len = stream.readShort(); // length of URL
            url = stream.readString(len); // URL
            len = stream.readShort(); // length of scope list
            scopeStr = stream.readString(len); // scope list
            len = stream.readShort(); // length of lag-list
            tagList = stream.readString(len); // tag-list
            len = stream.readShort(); // length of SPI string
            SPI = stream.readString(len); // SPI string
            if (len != 0) {
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_UNKNOWN);
            }
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create scope vector...
        scopes = SLPUtil.stringToVector(scopeStr);

        // create tag vector
        tags = SLPUtil.parseTags(tagList);

        // create URL or ServiceType
        try {
            if (url.indexOf("//") != -1) {
                serviceUrl = new ServiceURL(url);
            } else {
                serviceType = new ServiceType(url);
            }
        } catch (Exception ex) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }

    public String getPRList() {
        return PRList;
    }

    public ServiceURL getURL() {
        return serviceUrl;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public Vector getScopes() {
        return scopes;
    }

    public Vector getTags() {
        return tags;
    }

    public String getSPI() {
        return SPI;
    }

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
                + "URL: " + url.toString() + "\n"
                + "Scope list: " + scopeStr + "\n"
                + "Tag list: " + tagList + "\n"
                + "*** End Of Message ***";

        return s;
    }
}

