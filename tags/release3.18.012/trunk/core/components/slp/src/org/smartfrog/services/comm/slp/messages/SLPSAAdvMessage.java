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
 * This class represents an SLP SAAdvert message. It contains the methods required to read a message from an input
 * stream and write it to an output stream.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Service location header (function = SAAdvert = 11)    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |     Length of URL             |             URL               \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Length of scope-list     |           scope-list          \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Length of attr-list      |           attr-list           \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | # Auth blocks |      Authentication blocks (if any)           \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * Note that Authentication blocks are currently not supported in this implementation.
 */
public class SLPSAAdvMessage extends SLPMessageHeader {
    /** The service url for the SA */
    private ServiceURL url;
    /** The scopes supported by the SA */
    private Vector scopes;
    /** A String representation of the scope vector */
    private String scopeStr;
    /** The attributes of the SA */
    private Vector attributes;
    /** A string representation of the attribute vector */
    private String attributeStr;
    /** The authblocks used to sign this message */
    private Vector authBlocks;

    /** Creates a new, empty SLPSAAdvMessage. Used when the contents are to be read from an input stream. */
    public SLPSAAdvMessage() {
        super(SLPMSG_SAADV);
        url = null;
        scopes = null; //new Vector();
        scopeStr = "";
        attributes = new Vector();
        attributeStr = "";
        authBlocks = new Vector();
    }

    /**
     * Creates a new SLPSAAdvMessage.
     *
     * @param u    The service url
     * @param s    The scope vector.
     * @param a    The attribute vector.
     * @param lang The locale for the message.
     */
    public SLPSAAdvMessage(ServiceURL u, Vector s, Vector a, Locale lang) {
        super(SLPMSG_SAADV, lang);
        url = u;
        scopes = s;
        attributes = a;
        authBlocks = new Vector();

        // create strings
        scopeStr = SLPUtil.vectorToString(scopes);
        attributeStr = SLPUtil.createAttributeString(attributes);

        // calculate length
        length += 7; // length-fields
        length += url.toString().length(); // url
        length += scopeStr.length(); // scope-list
        length += attributeStr.length(); // attribute-list
        for (Iterator it = authBlocks.iterator(); it.hasNext();) {
            AuthBlock b = (AuthBlock) it.next();
            length += b.getLength();
        }
    }

    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(url.toString().length()); // length of url
            stream.writeString(url.toString()); // url
            stream.writeShort(scopeStr.length()); // length of scope list
            stream.writeString(scopeStr); // scope list
            stream.writeShort(attributeStr.length()); // length of attibute list
            stream.writeString(attributeStr); // attribute list
            stream.writeByte(authBlocks.size()); // #auth blocks
            for (Iterator it = authBlocks.iterator(); it.hasNext();) {
                AuthBlock b = (AuthBlock) it.next();
                b.toOutputStream(stream);
            }
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        try {
            int len = stream.readShort(); // length of url
            url = new ServiceURL(stream.readString(len)); // url
            len = stream.readShort(); // length of scope list
            scopeStr = stream.readString(len); // scope list
            len = stream.readShort(); // length of attribute list
            attributeStr = stream.readString(len); // attribute list
            len = stream.readByte(); // num auth blocks.
            for (int i = 0; i < len; i++) {
                AuthBlock b = new AuthBlock();
                b.fromInputStream(stream);
                authBlocks.add(b);
            }
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create scope vector
        scopes = SLPUtil.stringToVector(scopeStr);

        // create attribute vector...
        attributes = SLPUtil.parseAttributes(attributeStr);
    }

    /** Returns the service url */
    public ServiceURL getURL() {
        return url;
    }

    /** Returns the scope vector */
    public Vector getScopes() {
        return scopes;
    }

    /** Returns the attribute vector */
    public Vector getAttributes() {
        return attributes;
    }

    /** returns the authentication blocks */
    public Vector getAuthBlocks() {
        return authBlocks;
    }

    public String toString() {
        String s = super.toString();
        s += "Scope list: " + scopeStr + "\n"
                + "Attributes: " + attributeStr + "\n"
                + "URL: " + url.toString() + "\n"
                + "*** End Of Message ***";

        return s;
    }
}
