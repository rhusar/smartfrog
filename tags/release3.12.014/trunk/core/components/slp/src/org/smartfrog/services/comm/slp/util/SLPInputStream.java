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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SLPInputStream {
    private DataInputStream stream;

    public SLPInputStream(/*ByteArray*/InputStream bais) {
        stream = new DataInputStream(bais);
    }

    /** Reads a byte from the stream */
    public int readByte() throws IOException {
        return stream.readUnsignedByte();
    }

    /** Reads a short (2 bytes) from the stream */
    public int readShort() throws IOException {
        //return stream.readShort();
        return stream.readUnsignedShort();
    }

    /** Reads an int (4 bytes) from the stream */
    public int readInt() throws IOException {
        return stream.readInt();
    }

    /**
     * Reads a string from the stream.
     *
     * @param length The number of bytes to read into the string.
     */
    public String readString(int length) throws IOException {
        String s = "";
        for (int i = 0; i < length; i++) {
            s = s + (char) stream.readByte();
        }
        return s;
    }

    /**
     * Skips a number of bytes.
     *
     * @param n the number of bytes to skip.
     * @return the actual number of bytes to skip...
     */
    public long skip(int n) throws IOException {
        return stream.skip(n);
    }

}
