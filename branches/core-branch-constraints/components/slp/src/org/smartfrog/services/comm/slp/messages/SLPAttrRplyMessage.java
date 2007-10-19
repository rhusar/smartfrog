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
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/**
 * This class represents the SLP Attribute reply message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Service Location Header (function = AttrRply = 7)       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         Error code            |     length of attr-list       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                          attr-list                            \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |# of AttrAuths |  Attribute Authentication Block (if present)  \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 * Note that authentication blocks are currently not supported by this implementation.
 */
public class SLPAttrRplyMessage extends SLPMessageHeader {
    /** SLP error code */
    private int errorCode;
    /** Found attributes */
    private Vector attributes;
    /** String representation of the attribute list */
    private String attributeStr;
    /** Authentication blocks */
    private Vector authBlocks;

    public SLPAttrRplyMessage() {
        super(SLPMSG_ATTRRPLY);
        errorCode = 0;
        attributes = null; //new Vector();
        attributeStr = "";
        authBlocks = new Vector();
    }

    public SLPAttrRplyMessage(Vector attrs, Locale lang, int mtu) {
        super(SLPMSG_ATTRRPLY, lang);
        errorCode = 0;
        attributes = attrs;
        attributeStr = SLPUtil.createAttributeString(attributes);
        authBlocks = new Vector();

        // calculate length
        length += 5;
        length += attributeStr.length();
        for (Iterator it = authBlocks.iterator(); it.hasNext();) {
            AuthBlock a = (AuthBlock) it.next();
            length += a.getLength();
        }
    }

    public SLPAttrRplyMessage(int error, Locale lang) {
        super(SLPMSG_ATTRRPLY, lang);
        errorCode = error;
        attributes = null;
        attributeStr = "";
        authBlocks = new Vector();

        // calculate length
        length += 5;
        for (Iterator it = authBlocks.iterator(); it.hasNext();) {
            AuthBlock a = (AuthBlock) it.next();
            length += a.getLength();
        }
    }

    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(errorCode); // error code
            stream.writeShort(attributeStr.length()); // length of attr. list
            stream.writeString(attributeStr); // attribute list.
            stream.writeByte(authBlocks.size()); // #auth blocks
            for (Iterator it = authBlocks.iterator(); it.hasNext();) {
                AuthBlock a = (AuthBlock) it.next();
                a.toOutputStream(stream);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        try {
            errorCode = stream.readShort(); // error code
            int len = stream.readShort(); // length. of attr. list
            attributeStr = stream.readString(len); // attr. list
            len = stream.readByte(); // # auth blocks
            for (int i = 0; i < len; i++) {
                AuthBlock a = new AuthBlock();
                a.fromInputStream(stream);
                authBlocks.add(a);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create attributes...
        attributes = SLPUtil.parseAttributes(attributeStr);
    }

    /** Returns the error code */
    public int getErrorCode() {
        return errorCode;
    }

    /** Returns the discovered attributes */
    public Vector getAttributes() {
        return attributes;
    }

    public String toString() {
        String s = super.toString();
        s += "Error code: " + errorCode + "\n"
                + "Attributes: " + attributeStr + "\n"
                + "***End Of Message";

        return s;
    }
}
