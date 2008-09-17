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
import org.smartfrog.services.comm.slp.util.SLPDefaults;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/**
 * This class represents an SLP Message header as defined in RFC2608. This is also the base class for all SLP message
 * classes.
 * <pre>
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Version     |  Function-ID  |            Length             |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Length, contd.|O|F|R|       Reserved          |Next Ext Offset|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |  Next Extension Offset, contd.|              XID              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |      Language Tag Length      |      Language Tag             \
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </pre>
 */
public class SLPMessageHeader {
    /**********************************/
    /******* Message types ************/
    /**********************************/

    /** Service request message. */
    public static final int SLPMSG_SRVREQ = 1;
    /** Service reply message. */
    public static final int SLPMSG_SRVRPLY = 2;
    /** Service registration message. */
    public static final int SLPMSG_SRVREG = 3;
    /** Service deregistration message. */
    public static final int SLPMSG_SRVDEREG = 4;
    /** Service ack message. */
    public static final int SLPMSG_SRVACK = 5;
    /** Attribute request */
    public static final int SLPMSG_ATTRREQ = 6;
    /** Attribute reply */
    public static final int SLPMSG_ATTRRPLY = 7;
    /** DA Advert message. */
    public static final int SLPMSG_DAADV = 8;
    /** Service type request */
    public static final int SLPMSG_SRVTYPE = 9;
    /** Service type reply */
    public static final int SLPMSG_SRVTYPE_REPLY = 10;
    /** Service Agent advert */
    public static final int SLPMSG_SAADV = 11;

    /***********************************************/

    /** The length of the SLP header (excluding the language tag) */
    protected static final int HEAD_LEN = 14;
    /** Overflow flag. Set when a message does not fit in a single datagram */
    public static final int FLAG_OVERFLOW = 0x80;
    /** Fresh flag. Set when registering a new service */
    public static final int FLAG_FRESH = 0x40;
    /** Mcast flag. Set when a message is multicast. */
    public static final int FLAG_MCAST = 0x20;

    /** Random number generator. Used for generation the XID of a message. */
    private static Random random = new Random();
    /** SLP Version */
    private static final int version = 2;
    /** SLP message type */
    private int function;
    /** Length of SLP message */
    protected int length;
    /** SLP message flags (overflow/fresh/mcast) */
    protected int flags;
    /** Unique id identifying the message */
    private int XID;
    /** The language for the service */
    private Locale language;

    /**
     * Create a new SLPMessageHeader with the given function and language
     *
     * @param function The message type we have.
     * @param lang     The language for the service.
     */
    public SLPMessageHeader(int function, Locale lang) {
        this.function = function;
        language = lang;
        XID = nextXID();
        flags = 0x0;
        length = HEAD_LEN + language.getLanguage().length();
    }

    /**
     * Create a new SLPMessageHeader with the given function.
     *
     * @param function The type of message we have.
     */
    public SLPMessageHeader(int function) {
        this.function = function;
        language = new Locale(SLPDefaults.DEF_CONFIG_LOCALE);
        XID = Math.abs(random.nextInt() % 65534 + 1);
        flags = 0x0;
        length = HEAD_LEN + language.getLanguage().length();
    }

    /** Returns the slp version used */
    public int getVersion() {
        return version;
    }

    /** Returns the length of the message */
    public int getLength() {
        return length;
    }

    /** Returns the flags set in this message. */
    public int getFlags() {
        return flags;
    }

    /** Returns the XID of the message */
    public int /*short*/ getXID() {
        return XID;
    }

    /** Returns the language of the service in question */
    public Locale getLanguage() {
        return language;
    }

    /**
     * Adds a flag to the message
     *
     * @param f The flag to add.
     */
    public void setFlags(int f) {
        flags |= f;
    }

    /** Sets the flags variable to 0 */
    public void clearFlags() {
        flags = 0;
    }

    /**
     * Sets the XID. Used when creating a reply message
     *
     * @param id The xid to use.
     */
    public void setXID(int id) {
        XID = id;
    }

    /** Returns the next XID to use. */
    public int nextXID() {
        return Math.abs(random.nextInt() % 65534 + 1);
    }

    /**
     * Writes the SLPMessageHeader to an output stream.
     *
     * @param stream The SLPOutputStream to write to.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        // write to stream
        try {
            stream.writeByte(version); // SLP version
            stream.writeByte(function); // Function ID
            stream.writeInt3B(length); // Message length
            stream.writeByte(flags); // Flags
            stream.writeByte(0); // reserved
            stream.writeByte(0); // next ext offset
            stream.writeShort(0); // next ext offset contd.
            stream.writeShort(XID); // XID for this message
            stream.writeShort(language.getLanguage().length()); // lang. tag length
            stream.writeString(language.getLanguage()); // language tag
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * Reads the data for a SLPMessageHeader from an input stream.
     *
     * @param stream The stream to read from.
     */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        // read from stream
        try {
            stream.readByte(); // length part 1
            length = stream.readShort(); // find length of message
            flags = stream.readByte(); // flags
            stream.readByte(); // reserved.
            stream.readByte(); // next ext offset
            stream.readShort(); // next ext offset contd.
            XID = stream.readShort(); // XID
            int langLen = stream.readShort(); // lang length.
            language = new Locale(stream.readString(langLen));
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }

    /** Writes the contents of the message to stdout. Useful when debugging :-) */
    public String toString() {
        String theString;
        theString =
                "*** SLP Message ***" + "\n" +
                        "Version: " + version + "\n" +
                        "Function: " + function + "\n" +
                        "Length: " + length + "\n" +
                        "Flags: " + flags + "\n" +
                        "XID: " + XID + "\n" +
                        "Language: " + language.toString();

        return theString;
    }
}




