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
 * This class represents a Service registration message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Service location header (function = SrvReg = 3)       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          URL Entry                            \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Length of service type string |           service type        \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     Length of scope-list      |          scope-list           \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     Length of attr-list       |          attr-list            \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |# of AttrAuths |  Attribute Authentication blocks (if any)     \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * Note that authentication blocks are not currently supported by this implementation.
 */
public class SLPSrvRegMessage extends SLPMessageHeader {
    /** The message type. */
    private static final int FUNCTION = SLPMessageHeader.SLPMSG_SRVREG;
    /** The service url for the service to register. */
    //private ServiceURL url;
    private URLEntry urlEntry;
    /** The scopes to register this service in. */
    private Vector scopes;
    /** The attributes for the service. */
    private Vector attributes;
    private Vector attrAuths;
    /**
     String representation of ServiceURL
     */
    //private String urlStr;
    /** String representation of scope list. */
    private String scopeStr;
    /** String representation of attribute list (with added escape sequences for reserved chars) */
    private String attributeStr;
    /** String representation of the service type. */
    private String serviceType;

    /** Creates an empty SLPSrvRegMessage. */
    public SLPSrvRegMessage() {
        super(FUNCTION);
        urlEntry = null;
        scopes = null;//new Vector();
        attributes = new Vector();
        attrAuths = new Vector();
        length += 7;
    }

    /**
     * Creates a SLPSrvRegMessage with the given content.
     *
     * @param url    The service url to add.
     * @param scopes The scopes to add
     * @param lang   The locale for this message.
     */
    public SLPSrvRegMessage(ServiceURL url, Vector scopes, Vector attributes, Locale lang) {
        super(FUNCTION, lang);
        urlEntry = new URLEntry(url, new Vector());
        this.scopes = scopes;
        this.attributes = attributes;
        attrAuths = new Vector();

        // create String representations...
        scopeStr = SLPUtil.vectorToString(scopes);
        serviceType = url.getServiceType().toString();

        // create a String representation of the attributes...
        attributeStr = SLPUtil.createAttributeString(attributes);

        // calculate length
        length += urlEntry.getLength() + 2 + serviceType.length() +
                2 + scopeStr.length() + 2 + attributeStr.length() + 1;

    }

    /** Returns the service url */
    public ServiceURL getURL() {
        return urlEntry.getURL();
    }

    /** Returns the scope list */
    public Vector getScopes() {
        return scopes;
    }

    /** Returns the attributes */
    public Vector getAttributes() {
        return attributes;
    }

    /**
     * Writes the message to the given output stream.
     *
     * @param stream The stream to write to.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        // write message to stream
        super.toOutputStream(stream);
        try {
            urlEntry.toOutputStream(stream);
            stream.writeShort(serviceType.length()); // length of service type
            stream.writeString(serviceType); // service type
            stream.writeShort(scopeStr.length()); // length of scope list
            stream.writeString(scopeStr); // scope list
            stream.writeShort(attributeStr.length()); // length of attribute string
            stream.writeString(attributeStr); // attributes.
            stream.writeByte(attrAuths.size()); // #attr auths.
            for (Iterator iter = attrAuths.iterator(); iter.hasNext();) {
                ((AuthBlock) iter.next()).toOutputStream(stream);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * Read data from an input stream.
     *
     * @param stream The stream to read from.
     */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        int len;
        try {
            urlEntry = new URLEntry();
            urlEntry.fromInputStream(stream);
            len = stream.readShort(); // service type length.
            serviceType = stream.readString(len);
            len = stream.readShort(); // scope-list length
            scopeStr = stream.readString(len); // scope list
            len = stream.readShort(); // attr-list lenght
            attributeStr = stream.readString(len);
            int numAuths = stream.readByte(); // # auth blocks
            for (int i = 0; i < numAuths; i++) {
                AuthBlock b = new AuthBlock();
                b.fromInputStream(stream);
                attrAuths.add(b);
            }
            if (numAuths != 0) {
                // auth blocks currently not supported...
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create scope vector.
        if (!scopeStr.equals("")) {
            scopes = SLPUtil.stringToVector(scopeStr);
        }

        // create attribute vector...
        if (!attributeStr.equals("")) {
            attributes = SLPUtil.parseAttributes(attributeStr);
        }
    }

    /** Writes the contents of the message to stdout. */
    public String toString() {
        String theString;
        theString =
                super.toString() + "\n" +
                        "URL: " + urlEntry.getURL().toString() + "\n" +
                        "service type: " + serviceType + "\n" +
                        "scope list: " + scopeStr + "\n" +
                        "attr list: " + attributeStr + "\n" +
                        "*** End Of Message ***";

        return theString;
    }
}

