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
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/**
 * This class represents a DAAdvert message as defined in RFC2608. It offers the ability to write a DAAdvert message to
 * an output stream and read data into a message from a input stream.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |        Service Location header (function = DAAdvert = 8)      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Error Code           | DA Stateless Boot Timestamp   |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |DA Stateless Boot Time, contd. |      Length of URL            |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                              URL                              \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    Length of scope-list       |            scope-list         \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Length of attr-list         |            attr-list          \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Length of SLP SPI List      |      SLP SPI LIST string      \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | # Auth Blocks |          Authentication blocks (if any)       \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * Note that Authentication blocks are not currently supported in this implementation.
 */
public class SLPDAAdvMessage extends SLPMessageHeader {
    /** The function type for this message */
    private static final int FUNCTION = SLPMessageHeader.SLPMSG_DAADV;
    /** Used to indicate an error */
    private int errorCode;
    /** DA stateless boot timestamp */
    private int timestamp;
    /** The url for the DA */
    private ServiceURL url;
    /** Scopes supported by the DA */
    private Vector scopes;
    /** Attributes for the DA. */
    private Vector attributes;

    private Vector authBlocks;

    private String urlStr;
    private String attributeStr;
    private String scopeStr;

    /** Creates a new SLPDAAdvMessage. Used when the contents of the message are to be read from a stream. */
    public SLPDAAdvMessage() {
        super(FUNCTION);
        errorCode = 0;
        timestamp = 0;
        url = null;
        scopes = null;
        attributes = null;
        authBlocks = new Vector();
    }

    /**
     * Creates a new SLPDAAdvMessage with the specified error code and locale.
     *
     * @param errorCode The error code to put in the message.
     * @param lang      The locale for the message
     */
    public SLPDAAdvMessage(int errorCode, int ts, Locale lang) {
        super(FUNCTION, lang);
        this.errorCode = errorCode;
        timestamp = ts;
        url = null;
        scopes = null;
        attributes = null;
        authBlocks = new Vector();
    }

    /**
     * Creates a new SLPDAAdvMessage with the specified contents.
     *
     * @param url        The service url to put in the message
     * @param scopes     The scopes to put in the message
     * @param attributes The attribute list to add
     * @param lang       The locale for the message.
     */
    public SLPDAAdvMessage(ServiceURL url, Vector scopes, Vector attributes, int ts, Locale lang) {
        super(FUNCTION, lang);
        errorCode = 0;
        timestamp = ts;
        this.url = url;
        this.scopes = scopes;
        this.attributes = attributes;
        authBlocks = new Vector();

        // strings...
        urlStr = url.toString();
        scopeStr = SLPUtil.vectorToString(scopes);
        // create a String representation of the attributes...
        attributeStr = SLPUtil.createAttributeString(attributes);

        // calculate length...
        length += 6; // error + timestamp
        length += 2 + urlStr.length(); // url
        length += 2 + scopeStr.length(); // scope list
        length += 2 + attributeStr.length(); // attribute list
        length += 2; // SPI length (allways 0)
        length += 1; // # auth blocks.
        for (Iterator iter = authBlocks.iterator(); iter.hasNext();) {
            length += ((AuthBlock) iter.next()).getLength();
        }
    }

    /** Returns the error code. */
    public int getErrorCode() {
        return errorCode;
    }

    /** Returns the timestamp. */
    public int getTimestamp() {
        return timestamp;
    }

    /** Returns the service url. */
    public ServiceURL getURL() {
        return url;
    }

    /** Returns the scopes */
    public Vector getScopes() {
        return scopes;
    }

    /** Returns the attributes. */
    public Vector getAttributes() {
        return attributes;
    }

    /**
     * Writes the message to the given output stream.
     *
     * @param stream The stream to write to.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(errorCode); // error code
            stream.writeInt(timestamp); // timestamp
            stream.writeShort(urlStr.length()); // url length
            stream.writeString(urlStr); // url
            stream.writeShort(scopeStr.length()); // scope length
            stream.writeString(scopeStr); // scope list
            stream.writeShort(attributeStr.length()); // attribute length
            stream.writeString(attributeStr); // attribute list
            stream.writeShort(0); // spi length
            stream.writeByte(authBlocks.size()); // # auth blocks
            for (Iterator iter = authBlocks.iterator(); iter.hasNext();) {
                ((AuthBlock) iter.next()).toOutputStream(stream);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * Reads data from the given input stream.
     *
     * @param stream The stream to read from.
     */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        int scopeLen = 0;
        String scopeList = "";
        int attrLen = 0;
        String attrList = "";
        try {
            errorCode = stream.readShort(); // error
            timestamp = stream.readInt(); // timestamp
            int urlLen = stream.readShort(); // url length
            url = new ServiceURL(stream.readString(urlLen)); // url
            scopeLen = stream.readShort(); // scope list length
            scopeList = stream.readString(scopeLen); // scope list
            attrLen = stream.readShort(); // attr list length
            attrList = stream.readString(attrLen); // attr list.

            // SPI and Auth block are not implemented.
            // SPI is ignored. If an authentication block is included,
            // we can not handle the message, and throw an exception
            int len = stream.readShort();
            stream.readString(len); // SPI list (ignored)
            len = stream.readByte();
            for (int i = 0; i < len; i++) {
                AuthBlock b = new AuthBlock();
                b.fromInputStream(stream);
                authBlocks.add(b);
            }
            if (len != 0) {
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED); // does not support auth blocks
            }
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create scope vector
        if (scopeLen != 0) {
            scopes = SLPUtil.stringToVector(scopeList);
        }
        // create attr. list
        if (attrLen != 0) {
            attributes = SLPUtil.parseAttributes(attrList);
        }
    }

    /** Writes the contents of the message to stdout. */
    public String toString() {
        String scopeStr = (scopes != null ? scopes.toString() : "");
        String attrStr = (attributes != null ? attributes.toString() : "");
        String theString;
        theString =
                super.toString() + "\n" +
                        "Error Code: " + errorCode + "\n" +
                        "Timestamp: " + timestamp + "\n" +
                        "URL: " + url.toString() + "\n" +
                        "Scopes: " + scopeStr + "\n" +
                        "Attributes: " + attrStr + "\n" +
                        "*** End Of Message ***";

        return theString;
    }
}
