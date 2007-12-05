/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.projects.alpine.transport.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * created 24-Oct-2006 16:53:16
 */

public class CachingInputStream extends InputStream {




    private InputStream stream;
    private String encoding;
    private ByteArrayOutputStream buffer;

    public CachingInputStream(InputStream stream,String encoding) {
        this.stream = stream;
        this.encoding=encoding;
        buffer=new ByteArrayOutputStream(1024);
    }

    /**
     * Reads the next byte of data from the input stream.
     * @return the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception IOException  if an I/O error occurs.
     *
     * */
    public int read() throws IOException {
        int ch = stream.read();
        if(ch>=0) {
            buffer.write(ch);
        }
        return ch;
    }


    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     * <p/>
     * <p> The <code>close</code> method of <code>InputStream</code> does
     * nothing.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        stream.close();
    }


    /**
     * @return a string representation of the object.
     */
    public String toString() {
        try {
            return buffer.toString(encoding);
        } catch (UnsupportedEncodingException e) {
            //fallback
            return buffer.toString();
        }
    }
}
