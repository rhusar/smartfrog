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

package org.smartfrog.services.comm.slp.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SLPOutputStream {
    private DataOutputStream stream;
    private ByteArrayOutputStream baos;

    public SLPOutputStream(ByteArrayOutputStream out) {
        baos = out;
        stream = new DataOutputStream(baos);
    }

    /**
     * Writes an int to the stream using 3 bytes. This is currently not properly implemented. It only uses 2 bytes for
     * the int, setting the first to 0. A short should, however, be more than enough to contain the length of the
     * message unless an extremely large MTU is used.
     */
    public void writeInt3B(int value) throws IOException {
        // write an int to the stream using 3 bytes.
        stream.writeByte(0);
        stream.writeShort(value);
    }

    /** Writes an int to the stream (4 bytes) */
    public void writeInt(int value) throws IOException {
        stream.writeInt(value);
    }

    /** Writes a short value to the stream (2 bytes) */
    public void writeShort(int value) throws IOException {
        stream.writeShort(value);
    }

    /** Writes a string to the stream */
    public void writeString(String s) throws IOException {
        stream.writeBytes(s);
    }

    /** Writes one byte to the stream */
    public void writeByte(int value) throws IOException {
        stream.writeByte(value);
    }

    /** Returns a byte arry holding the contents of the stream. */
    public byte[] getByteArray() {
        return baos.toByteArray();
    }

    /** Returns the number of bytes written to the stream */
    public int getSize() {
        return baos.size();
    }
}
