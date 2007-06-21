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
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;
import org.smartfrog.services.comm.slp.util.SLPUtil;

import java.io.IOException;
import java.util.Locale;
import java.util.Vector;

/**
 * This class represents a SrvReq message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |        Service location header (function = SrvRqst = 1)       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      length of PRList         |             PRList            \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      length of service-type   |            service-type       \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      length of scope-list     |            scope-list         \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | length of predicate string    |   Service request predicate   \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   length of SLP SPI string    |        SLP SPI string         \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPSrvReqMessage extends SLPMessageHeader {
    /** The message type. */
    private static final int FUNCTION = 1;
    /** The list of previous responders to this request */
    private String PRList;
    /** The service type to look for. */
    private ServiceType type;
    /** The scopes in which to search for the service. */
    private Vector scopes;
    /**
     * A search filter to use when finding matches. This may be an empty string, in which case all services of the
     * correct type in a supported scope will be found.
     */
    private String searchFilter;
    /** String representation of the service type. */
    private String typeStr;
    /** String representation of the scope list. */
    private String scopeStr;

    private String spiStr;

    /** Creates an empty SLPSrvReqMessage. */
    public SLPSrvReqMessage() {
        super(FUNCTION);
        type = null;
        scopes = null;//new Vector();
        searchFilter = "";
        PRList = "";
        spiStr = "";
    }

    /**
     * Creates a SLPSrvReqMessage with the given contents.
     *
     * @param type   The service type
     * @param scopes The scopes to search in.
     * @param filter The search filter to use.
     * @param lang   The locale for this message.
     */
    public SLPSrvReqMessage(ServiceType type, Vector scopes, String filter, Locale lang) {
        super(FUNCTION, lang);
        this.type = type;
        this.scopes = scopes;
        searchFilter = filter;
        PRList = "";
        spiStr = "";
        // create string representations
        typeStr = type.toString();
        scopeStr = SLPUtil.vectorToString(scopes);
        // calculate length
        length += PRList.length() + 2; // PRList
        length += typeStr.length() + 2; // service type
        length += scopeStr.length() + 2; // scope list
        length += searchFilter.length() + 2; // filter.
        length += spiStr.length() + 2; // spi string
    }

    /** Adds a responder to the list of previous responders. */
    public void addResponder(String responder) {
        length -= PRList.length();
        if (PRList.equals("")) {
            PRList = responder;
        } else {
            PRList = PRList + "," + responder;
        }
        length += PRList.length();
    }

    /** Clears the list of previous responders. */
    public void clearResponders() {
        length -= PRList.length();
        PRList = "";
    }

    /** Returns the service type */
    public ServiceType getServiceType() {
        return type;
    }

    /** Returns the scope list */
    public Vector getScopes() {
        return scopes;
    }

    /** Returns the search filter. */
    public String getSearchFilter() {
        return searchFilter;
    }

    /** Returns the list of previous responders. */
    public String getPRList() {
        return PRList;
    }

    /**
     * Writes the message to an ouput stream.
     *
     * @param stream The stream to write to.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
//        System.out.println("Scopes: " + scopes.toString());
//        System.out.println("PRList: " + PRList);

        // write to stream
        super.toOutputStream(stream);
        try {
            stream.writeShort(PRList.length());
            stream.writeString(PRList); // PRList
            stream.writeShort(typeStr.length());
            stream.writeString(typeStr); // service type
            stream.writeShort(scopeStr.length());
            stream.writeString(scopeStr); // scope list
            stream.writeShort(searchFilter.length());
            stream.writeString(searchFilter); // search filter
            stream.writeShort(spiStr.length()); // spi length.
            stream.writeString(spiStr); // spi 
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * Reads data from an input stream.
     *
     * @param stream The stream to read from.
     */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        try {
            int prlen = stream.readShort();
            PRList = stream.readString(prlen);
            int stlen = stream.readShort();
            if (stlen == 0) {
                // Service type field can NOT be empty !
                throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
            }
            type = new ServiceType(stream.readString(stlen));
            int scopelen = stream.readShort();
            scopeStr = stream.readString(scopelen);
            int predLen = stream.readShort();
            searchFilter = stream.readString(predLen);

            int spiLength = stream.readShort();
            spiStr = stream.readString(spiLength);
            if (spiLength != 0) {
                // No support for SPI/Authentication blocks at this time...
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_UNKNOWN);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create scope vector...
        scopes = SLPUtil.stringToVector(scopeStr);
    }

    /** Writes the contents of the message to stdout. */
    public String toString() {
        String theString;
        theString =
                super.toString() + "\n" +
                        "PRList: " + PRList + "\n" +
                        "Service Type: " + type.toString() + "\n" +
                        "Scope list: " + scopes.toString() + "\n" +
                        "Search Filter: " + searchFilter + "\n" +
                        "*** End Of Message ***";

        return theString;
    }
}
