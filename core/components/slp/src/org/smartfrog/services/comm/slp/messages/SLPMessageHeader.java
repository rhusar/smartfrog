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
import org.smartfrog.services.comm.slp.util.SLPDefaults;

import java.util.Locale;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import java.util.Iterator;

/**
This class represents an SLP Message header as defined in RFC2608.
This is also the base class for all SLP message classes.
 
  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |   Version     |  Function-ID  |            Length             |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 | Length, contd.|O|F|R|       Reserved          |Next Ext Offset|
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |  Next Extension Offset, contd.|              XID              |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |      Language Tag Length      |      Language Tag             \
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
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
    
    /** 
        Random number generator.
        Used for generation the XID of a message.
    */
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
        Create a new SLPMessageHeader with the given function and language
        @param function The message type we have.
        @param lang The language for the service.
    */
    public SLPMessageHeader(int function, Locale lang) {
        this.function = function;
        language = lang;
        XID = nextXID();
        flags = 0x0;
        length = HEAD_LEN + language.getLanguage().length();
    }
    
    /**
        Create a new SLPMessageHeader with the given function.
        @param function The type of message we have.
    */
    public SLPMessageHeader(int function) {
        this.function = function;
        language = new Locale(SLPDefaults.DEF_CONFIG_LOCALE);
        XID = Math.abs(random.nextInt()%65534 + 1);
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
    
    /** Adds a flag to the message 
        @param f The flag to add.
    */
    public void setFlags(int f) {
        flags |= f;
    }
    
    /** Sets the flags variable to 0 */
    public void clearFlags() {
        flags = 0;
    }
    
    /** Sets the XID. Used when creating a reply message 
        @param id The xid to use.
    */
    public void setXID(int id) {
        XID = id;
    }
    /**
        Returns the next XID to use.
    */
    public int nextXID() {
        return Math.abs(random.nextInt()%65534 + 1);
    }
    
    /**
        Writes the SLPMessageHeader to an output stream.
        @param stream The SLPOutputStream to write to.
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
        }catch(IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }
    
    /**
        Reads the data for a SLPMessageHeader from an input stream.
        @param stream The stream to read from.
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
        }catch(IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }
    
    /**
        Writes the contents of the message to stdout.
        Useful when debugging :-)
    */
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

/***********************************/
/****** URL Entry Class ************/
/***********************************/

/**
This class represents an URL Entry.
 It can write an URL entry to an output stream, and read it back.
 
  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |   Reserved    |        Lifetime               |  URL Length   |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |Url len, contd.|             URL (variable length)             \
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |# of URL auths |      Authentication blocks (if any)           \
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 
 Note that authentication blocks are currently not supported in
 this implementation.
 
*/
class URLEntry {
    /** The total length of all constant-lenght fields in an URL entry. */
    private static final int CONST_LENGTH = 6;
    /** The lifetime of the url */
    private int lifetime;
    /** The length of the url */
    private int urlLength;
    /** The service url. */
    private ServiceURL url;
    /** A String representation of the service url */
    private String urlStr;
    /** The number of authentication blocks. */
    private int numAuths;
    /** A vector of authentication blocks for this url. */
    private Vector authBlocks;
    /** The total length of the url entry */
    private int length;
    
    /** Creates an URL entry with no url.
        Used when an entry is to be read from an input stream.
    */
    URLEntry() {
        lifetime = 0;
        urlLength = 0;
        url = null;
        urlStr = "";
        numAuths = 0;
        authBlocks = new Vector();
        length = CONST_LENGTH;
    }
    
    /** Creates an URL entry with the given url and authentication blocks.
        @param url The Service URL
        @param authBlocks The authentication blocks for the URL.
    */
    URLEntry(ServiceURL url, Vector authBlocks) {
        lifetime = url.getLifetime();
        if(lifetime == ServiceURL.LIFETIME_PERMANENT) lifetime = ServiceURL.LIFETIME_MAXIMUM;
        urlStr = url.toString();
        urlLength = urlStr.length();
        this.url = url;
        numAuths = authBlocks.size();
        this.authBlocks = authBlocks;
        length = CONST_LENGTH + urlLength;
        for(Iterator iter=authBlocks.iterator(); iter.hasNext(); ) {
            length += ((AuthBlock)iter.next()).getLength();
        }
    }
    
    /** Writes the URLEntry to an output stream.
        @param stream The stream to write to.
        @exception ServiceLocationException if writing fails.
    */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        try {
            stream.writeByte(0); // reserved
            stream.writeShort(lifetime); // lifetime
            stream.writeShort(urlLength); // length of URL
            stream.writeString(urlStr); // URL
            stream.writeByte(numAuths); // number of auth blocks.
            // write auth blocks
            for(Iterator iter = authBlocks.iterator(); iter.hasNext(); ) {
                ((AuthBlock)iter.next()).toOutputStream(stream);
            }
        }catch(IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }
    
    /** Reads an URLEntry from a stream.
        @param stream The stream to read from.
        @exception ServiceLocationException if reading fails.
    */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        try {
            stream.readByte(); // reserved
            lifetime = stream.readShort(); // lifetime
            urlLength = stream.readShort(); // length of URL
            urlStr = stream.readString(urlLength); // URL
            numAuths = stream.readByte(); // number of auth blocks.
            // read auth blocks...
            for(int i=0; i<numAuths; i++) {
                AuthBlock b = new AuthBlock();
                b.fromInputStream(stream);
                authBlocks.add(b);
            }
            url = new ServiceURL(urlStr, lifetime);
            if(numAuths != 0) {
                // Do not currently support authentication blocks
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED);
            }
        }catch(Exception e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }
    
    /** Returns the service url. */
    public ServiceURL getURL() {
        return url;
    }
    
    /** Returns the lifetime of the url. */
    public int getLifetime() {
        return lifetime;
    }
    
    /** Returns the number of authentication blocks. */
    public int getNumAuths() {
        return numAuths;
    }
    
    /** Returns the authentication block vector. */
    public Vector getAuthBlocks() {
        return authBlocks;
    }
    
    /** Returns the length of the URLEntry */
    public int getLength() {
        return length;
    }
}

/**********************************/
/****** Authentication Block ******/
/**********************************/

/**
Represents an authentication block.
 This class takes care of reading from and writing to a stream.
 
  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 | Block Structure Descriptor    |  Authentication block length  |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |                         Timestamp                             |
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |    SLP SPI string length      |         SLP SPI String        \
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 |               Structured authentication block                 \
 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 
*/
class AuthBlock {
    /** The total lenght of all constant-length fields. */
    private static final int CONST_LENGTH = 10;
    /** The id of the BSD_DSA thingy.fsafgdfvahiuer */
    private static final int BSD_DSA = 0x0002;
    /** Block structure descriptor */
    private int bsd;
    /** Length of AuthBlock */
    private int length;
    /** expiration timestamp */
    private int timestamp;
    /** lenght of SPI string */
    private int spiLength;
    /** SPI string */
    private String spi;
    /** Authentication block string */
    private String authBlock;
    
    /** Creates an empty AuthBlock.
        Used when reading an AuthBlock from a stream.
    */
    AuthBlock() {
        bsd = 0;
        length = CONST_LENGTH;
        timestamp = 0;
        spiLength = 0;
        spi = "";
        authBlock = "";
    }
    
    /** Creates an AuthBlock with the given values.
        @param bsd The BSD to use.
        @param ts The timestamp to use.
        @param spi The SPI string to use.
        @param authBlock The authentication block string.
    */
    AuthBlock(int bsd, int ts, String spi, String authBlock) {
        this.bsd = bsd;
        timestamp = ts;
        this.spi = spi;
        spiLength = spi.length();
        this.authBlock = authBlock;
        
        length = CONST_LENGTH + spiLength + authBlock.length();
    }
    
    /** Writes the AuthBlock to an output stream.
        @param stream The stream to write to.
        @exception ServiceLocationException if writing fails.
    */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        try {
            stream.writeShort(bsd); // BSD
            stream.writeShort(length); // length
            stream.writeInt(timestamp); // timestamp
            stream.writeShort(spiLength); // length of spi
            stream.writeString(spi); // spi
            stream.writeString(authBlock); // structured auth block
        }catch(IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }
    
    /** Reads an AuthBlock from an input stream.
        @param stream The stream to read from.
        @exception ServiceLocationException if reading fails.
    */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        try {
            bsd = stream.readShort(); // bsd
            length = stream.readShort(); // length
            timestamp = stream.readInt(); // timestamp
            spiLength = stream.readShort(); // length of spi
            spi = stream.readString(spiLength); // spi
            int remaining = length-CONST_LENGTH-spiLength;
            authBlock = stream.readString(remaining);
        }catch(IOException e) {
            throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR);
        }
    }
    
    /** Returns the BSD */
    public int getBSD() {
        return bsd;
    }
    
    /** Returns the timestamp */
    public int getTimestamp() {
        return timestamp;
    }
    
    /** Returns the SPI */
    public String getSPI() {
        return spi;
    }
    
    /** Returns the authentication block string */
    public String getAuthBlock() {
        return authBlock;
    }
    
    /** Returns the length of the AuthBlock */
    public int getLength() {
        return length;
    }
}

