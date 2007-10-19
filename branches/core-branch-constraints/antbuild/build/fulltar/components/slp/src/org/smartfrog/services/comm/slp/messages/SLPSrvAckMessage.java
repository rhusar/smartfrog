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

import java.io.IOException;
import java.util.Locale;

/**
 * This class represents a SrvAck message. It offers the ability to write the message to an output stream, and read data
 * back from an input stream.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |          Service location header (function = SrvAck = 5)      |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |       Error Code              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPSrvAckMessage extends SLPMessageHeader {
    /** The message type */
    private static int FUNCTION = SLPMessageHeader.SLPMSG_SRVACK;
    /** An integer set to the appropriate error code. 0 when no error is set. */
    private int errorCode;

    /** Creates an empty SLPSrvAckMessage. Use this when the data is to be read from an input stream. */
    public SLPSrvAckMessage() {
        super(FUNCTION);
        errorCode = 0;
        length += 2;
    }

    /**
     * Creates an SLPSrvAckMessage with the given error code and locale.
     *
     * @param errorCode The error code to set.
     * @param lang      The locale for this message.
     */
    public SLPSrvAckMessage(int errorCode, Locale lang) {
        super(FUNCTION, lang);
        this.errorCode = errorCode;
        length += 2;
    }

    /** Returns the error code */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Writes the message to an output stream.
     *
     * @param stream The stream to write to.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        // write to stream
        super.toOutputStream(stream);
        try {
            stream.writeShort(errorCode);
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
            errorCode = stream.readShort();
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }

    public String toString() {
        String theString;
        theString =
                super.toString() + "\n" +
                        "Error code: " + errorCode + "\n" +
                        "*** End Of Message ***";

        return theString;
    }
}
