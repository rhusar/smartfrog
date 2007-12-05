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
import org.smartfrog.services.comm.slp.ServiceURL;
import org.smartfrog.services.comm.slp.util.SLPInputStream;
import org.smartfrog.services.comm.slp.util.SLPOutputStream;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class represents an URL Entry. It can write an URL entry to an output stream, and read it back.
 *
 * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   Reserved    |        Lifetime               |  URL Length   | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Url len, contd.|             URL (variable length)             \ +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |# of URL auths |      Authentication blocks (if any)           \ +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 *
 * Note that authentication blocks are currently not supported in this implementation.
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

    /** Creates an URL entry with no url. Used when an entry is to be read from an input stream. */
    URLEntry() {
        lifetime = 0;
        urlLength = 0;
        url = null;
        urlStr = "";
        numAuths = 0;
        authBlocks = new Vector();
        length = CONST_LENGTH;
    }

    /**
     * Creates an URL entry with the given url and authentication blocks.
     *
     * @param url        The Service URL
     * @param authBlocks The authentication blocks for the URL.
     */
    URLEntry(ServiceURL url, Vector authBlocks) {
        lifetime = url.getLifetime();
        if (lifetime == ServiceURL.LIFETIME_PERMANENT) lifetime = ServiceURL.LIFETIME_MAXIMUM;
        urlStr = url.toString();
        urlLength = urlStr.length();
        this.url = url;
        numAuths = authBlocks.size();
        this.authBlocks = authBlocks;
        length = CONST_LENGTH + urlLength;
        for (Iterator iter = authBlocks.iterator(); iter.hasNext();) {
            length += ((AuthBlock) iter.next()).getLength();
        }
    }

    /**
     * Writes the URLEntry to an output stream.
     *
     * @param stream The stream to write to.
     * @throws ServiceLocationException if writing fails.
     */
    public void toOutputStream(SLPOutputStream stream) throws ServiceLocationException {
        try {
            stream.writeByte(0); // reserved
            stream.writeShort(lifetime); // lifetime
            stream.writeShort(urlLength); // length of URL
            stream.writeString(urlStr); // URL
            stream.writeByte(numAuths); // number of auth blocks.
            // write auth blocks
            for (Iterator iter = authBlocks.iterator(); iter.hasNext();) {
                ((AuthBlock) iter.next()).toOutputStream(stream);
            }
        } catch (IOException e) {
            throw new ServiceLocationException(ServiceLocationException.INTERNAL_SYSTEM_ERROR);
        }
    }

    /**
     * Reads an URLEntry from a stream.
     *
     * @param stream The stream to read from.
     * @throws ServiceLocationException if reading fails.
     */
    public void fromInputStream(SLPInputStream stream) throws ServiceLocationException {
        try {
            stream.readByte(); // reserved
            lifetime = stream.readShort(); // lifetime
            urlLength = stream.readShort(); // length of URL
            urlStr = stream.readString(urlLength); // URL
            numAuths = stream.readByte(); // number of auth blocks.
            // read auth blocks...
            for (int i = 0; i < numAuths; i++) {
                AuthBlock b = new AuthBlock();
                b.fromInputStream(stream);
                authBlocks.add(b);
            }
            url = new ServiceURL(urlStr, lifetime);
            if (numAuths != 0) {
                // Do not currently support authentication blocks
                throw new ServiceLocationException(ServiceLocationException.AUTHENTICATION_FAILED);
            }
        } catch (Exception e) {
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
