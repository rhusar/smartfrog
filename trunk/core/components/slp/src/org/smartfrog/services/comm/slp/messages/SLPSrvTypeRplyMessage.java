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
 * This class represents a service type reply message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Service location header (function = SrvTypeRply = 10)    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Error code              |    length of srvType-list     |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                        srvType-list                           \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPSrvTypeRplyMessage extends SLPMessageHeader {
    /** The SLP error code. */
    private int errorCode;
    /** A vector holding the service types in the reply */
    private Vector serviceTypes;
    /** A string representation of the service type vector */
    private String serviceTypesStr;

    /** Creates an empty SLPSrvTypeRplyMessage. */
    public SLPSrvTypeRplyMessage() {
        super(SLPMSG_SRVTYPE_REPLY);
        errorCode = 0;
        serviceTypes = null;
        serviceTypesStr = "";
    }

    /**
     * Creates an SLPSrvTypeRplyMessage.
     *
     * @param types The service types to include.
     * @param lang  The language locale.
     * @param mtu   The MTU for SLP messages.
     */
    public SLPSrvTypeRplyMessage(Vector types, Locale lang, int mtu) {
        super(SLPMSG_SRVTYPE_REPLY, lang);
        errorCode = 0;
        serviceTypes = new Vector();
        for (Iterator iter = types.iterator(); iter.hasNext();) {
//TODO: ADD LENGTH CHECK HERE IN CASE OF OVERFLOW !
            serviceTypes.add(iter.next());
        }

        // create string representation of vector.
        serviceTypesStr = SLPUtil.vectorToString(serviceTypes);

        // length
        length += 4; // error + length of service-type list.
        length += serviceTypesStr.length();
    }

    /**
     * Creates an SLPSrvTypeRplyMessage with the given error code.
     *
     * @param error The error code.
     * @param lang  The locale for this message.
     */
    public SLPSrvTypeRplyMessage(int error, Locale lang) {
        super(SLPMSG_SRVTYPE_REPLY, lang);
        errorCode = error;
        serviceTypes = null;
        serviceTypesStr = "";

        length += 4; // error + service-type list length.
    }

    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        super.toOutputStream(stream);
        try {
            stream.writeShort(errorCode); // error code
            stream.writeShort(serviceTypesStr.length()); // length of srvtype list
            stream.writeString(serviceTypesStr); // service type list.
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        super.fromInputStream(stream);
        try {
            errorCode = stream.readShort(); // error code
            int len = stream.readShort(); // length of srvtype list
            serviceTypesStr = stream.readString(len); // service type list.
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }

        // create service type vector.
        serviceTypes = SLPUtil.stringToVector(serviceTypesStr);
    }

    /** Returns the error code */
    public int getErrorCode() {
        return errorCode;
    }

    /** Returns the service types */
    public Vector getServiceTypes() {
        return serviceTypes;
    }

    /** Creates a String with the contents of the message */
    public String toString() {
        String s = super.toString();
        s += "\n"
                + "Error Code: " + errorCode + "\n"
                + "Service Types:\n";

        for (Iterator it = serviceTypes.iterator(); it.hasNext();) {
            s += "\t" + it.next().toString() + "\n";
        }

        s += "*** End Of Message ***";

        return s;
    }
}

