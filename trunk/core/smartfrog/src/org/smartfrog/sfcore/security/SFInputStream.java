/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * We need to wrap the input stream returned by a socket so that we can later
 * associate the thread that does the method invocation with the security
 * session. We do that in a non-portable, and ad-hoc way but according to SUN
 * experts in the RMI mailing list there is no better way to do it .... We
 * basically rely on the thread that unmarshalls the arguments (and therefore
 * calls read in this input stream) is the one that executes the method
 * invocation. Then, we propagate the security context in a thread local
 * updated in the read.
 *
 */
public class SFInputStream extends FilterInputStream {
    /**
     * A per thread variable that will attach a socket to the thread that is
     * executing the rmi call, so that it can be recovered while in the method
     * invocation.
     */
    private static ThreadLocal<SFSocket> currentSocket = new ThreadLocal<SFSocket>();

    /** A socket associated with this input stream. */
    private SFSocket sfs;



    /**
     * Constructs SFInputStream with input stream and sockat.
     *
     * @param in The original input stream associated with the socket.
     * @param sfs The socket that we want to associate with this input stream.
     */
    public SFInputStream(InputStream in, SFSocket sfs) {
        super(in);
        this.sfs = sfs;
    }

    /**
     * Accessor to the current socket.
     * @return the last socket used for IO, or null.
     */
    private static SFSocket getCurrentSocket() {
        return currentSocket.get();
    }

    /**
     * Used inside a method call invoked by the RMI Server to find out
     * authenticated information of our peer that called this function
     * remotely.
     *
     * @return Authenticated information about our peer.
     */
    public static String getPeerAuthenticatedSubjects() {
        SFSocket inSocket = SFInputStream.getCurrentSocket();
        return ((inSocket != null) ? inSocket.getPeerAuthenticatedSubjects()
                : null);
    }



    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is
     * detected, or an exception is thrown.
     *
     * <p>
     * This method simply performs <code>in.read()</code> and returns the
     * result.
     * </p>
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream is reached.
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see java.io.FilterInputStream#in
     */
    public int read() throws IOException {
        currentSocket.set(sfs);

        return super.read();
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream into
     * an array of bytes. This method blocks until some input is available.
     *
     * <p>
     * This method simply performs <code>in.read(b, off, len)</code> and
     * returns the result.
     * </p>
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the maximum number of bytes read.
     *
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of the
     *         stream has been reached.
     *
     * @throws IOException if an I/O error occurs.
     *
     * @see java.io.FilterInputStream#in
     */
    public int read(byte[] b, int off, int len) throws IOException {
        currentSocket.set(sfs);

        return super.read(b, off, len);
    }
}
