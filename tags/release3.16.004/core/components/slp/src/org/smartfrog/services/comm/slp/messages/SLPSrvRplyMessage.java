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

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

/**
 * This class represents a SrvRply message.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |        Service location header (function = SrvRply = 2)       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Error code               |       URL Entry count         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      URL Entry 1             ...           URL Entry N        \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPSrvRplyMessage extends SLPMessageHeader {
    /**
     The message type.
     */
    //private static final int FUNCTION = 2;
    /** Indicates what the type of error */
    private int errorCode = 0;
    /** A vector of URLs included in this reply. */
    private Vector URLs;

    /** Creates an empty SLPSrvRplyMessage. */
    public SLPSrvRplyMessage() {
        super(SLPMSG_SRVRPLY);
        URLs = new Vector();
        errorCode = 0;
    }

    /**
     * Creates a SLPSrvRplyMessage with the given contents. If the message is too large to fit inside a datagram, the
     * overflow flag is set, and the remaining URLs left out.
     *
     * @param urls The URLs to include in the message.
     * @param lang The locale for this message
     * @param MTU  The maximum size of the message.
     */
    public SLPSrvRplyMessage(Vector urls, Locale lang, int MTU) {
        super(SLPMSG_SRVRPLY, lang);
        URLs = new Vector();
        errorCode = 0;
        // calculate length
        length += 4; // error code + url count

        for (Iterator iter = urls.iterator(); iter.hasNext();) {
            URLEntry ue = new URLEntry((ServiceURL) iter.next(), new Vector());
            if (length + ue.getLength() <= MTU) {
                // add url entry
                URLs.add(ue);
                length += ue.getLength();
            } else {
                // overflow
                setFlags(FLAG_OVERFLOW);
                break;
            }
        }
    }

    /**
     * Creates a SLPSrvRplyMessage with the given error code.
     *
     * @param error The error code to use.
     * @param lang  The locale for this message.
     */
    public SLPSrvRplyMessage(int error, Locale lang) {
        super(SLPMSG_SRVRPLY, lang);
        URLs = null;
        errorCode = error;
        length += 4; // num urls + error code
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
            stream.writeShort(errorCode); // error
            if (errorCode != 0) {
                stream.writeShort(0); // num urls
            } else {
                stream.writeShort(URLs.size()); // number of urls
                // write url entries
                for (Iterator iter = URLs.iterator(); iter.hasNext();) {
                    ((URLEntry) iter.next()).toOutputStream(stream);
                }
            }
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
            errorCode = stream.readShort(); // error code
            int numUrls = stream.readShort(); // number of url entries
            // read url entries
            for (int i = 0; i < numUrls; i++) {
                URLEntry ue = new URLEntry();
                ue.fromInputStream(stream);
                URLs.add(ue);
            }
        } catch (EOFException eof) {
            // if the message overflows, we may get here when the buffer is empty
            // if we get here, and the overflow flag is not set, something is wrong...
            if ((flags & SLPMessageHeader.FLAG_OVERFLOW) == 0) {
                throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
            }
        } catch (IOException ioe) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }

    /** Returns the error code */
    public int getErrorCode() {
        return errorCode;
    }

    /** Returns the URLs in this message */
    public Vector getURLs() {

        Vector toReturn = new Vector();
        for (Iterator iter = URLs.iterator(); iter.hasNext();) {
            toReturn.add(((URLEntry) iter.next()).getURL());
        }
        return toReturn;

        //return URLs;
    }

    /** Writes the contents of the message to stdout. */
    public String toString() {
        String theString;
        theString =
                super.toString() + "\n" +
                        "Error code: " + errorCode + "\n" +
                        "# URLs: " + URLs.size() + "\n" +
                        "URLS:\n";

        Iterator iter = URLs.iterator();
        while (iter.hasNext()) {
            ServiceURL u = ((URLEntry) iter.next()).getURL();
            theString += "\t" + u.getLifetime() + " - " + u.toString() + "\n";
        }

        theString += "*** End Of Message ***";
        return theString;
    }
}
