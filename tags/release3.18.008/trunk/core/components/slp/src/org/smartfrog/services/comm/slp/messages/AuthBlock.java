/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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

 For more information: www.smartfrog.org

 */
package org.smartfrog.services.comm.slp.messages;

import org.smartfrog.services.comm.slp.ServiceLocationException;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;

import java.io.IOException;

/**********************************/
/****** Authentication Block ******/
/**********************************/

/**
 * Represents an authentication block. This class takes care of reading from and writing to a stream.
 *
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * | Block Structure Descriptor    |  Authentication block length  | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Timestamp                             | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |    SLP SPI string length      |         SLP SPI String        \ +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |               Structured authentication block                 \ +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
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

    /** Creates an empty AuthBlock. Used when reading an AuthBlock from a stream. */
    AuthBlock() {
        bsd = 0;
        length = CONST_LENGTH;
        timestamp = 0;
        spiLength = 0;
        spi = "";
        authBlock = "";
    }

    /**
     * Creates an AuthBlock with the given values.
     *
     * @param bsd       The BSD to use.
     * @param ts        The timestamp to use.
     * @param spi       The SPI string to use.
     * @param authBlock The authentication block string.
     */
    AuthBlock(int bsd, int ts, String spi, String authBlock) {
        this.bsd = bsd;
        timestamp = ts;
        this.spi = spi;
        spiLength = spi.length();
        this.authBlock = authBlock;

        length = CONST_LENGTH + spiLength + authBlock.length();
    }

    /**
     * Writes the AuthBlock to an output stream.
     *
     * @param stream The stream to write to.
     * @throws ServiceLocationException if writing fails.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        try {
            stream.writeShort(bsd); // BSD
            stream.writeShort(length); // length
            stream.writeInt(timestamp); // timestamp
            stream.writeShort(spiLength); // length of spi
            stream.writeString(spi); // spi
            stream.writeString(authBlock); // structured auth block
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * Reads an AuthBlock from an input stream.
     *
     * @param stream The stream to read from.
     * @throws ServiceLocationException if reading fails.
     */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        try {
            bsd = stream.readShort(); // bsd
            length = stream.readShort(); // length
            timestamp = stream.readInt(); // timestamp
            spiLength = stream.readShort(); // length of spi
            spi = stream.readString(spiLength); // spi
            int remaining = length - CONST_LENGTH - spiLength;
            authBlock = stream.readString(remaining);
        } catch (IOException e) {
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
