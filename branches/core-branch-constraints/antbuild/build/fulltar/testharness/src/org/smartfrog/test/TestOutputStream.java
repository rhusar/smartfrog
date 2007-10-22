/** (C) Copyright 2004 Hewlett-Packard Development Company, LP

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


package org.smartfrog.test;

import java.io.PrintStream;
import java.io.OutputStream;

/**
 * This class buffers output to a stringbuffer.
 * @author steve loughran
 * Date: 18-Feb-2004
 * Time: 14:44:58
 */
public class TestOutputStream extends OutputStream {

    /**
     * a buffer for output
     */
    private StringBuffer buffer;

    /**
     * a test output stream bound to a buffer
     * @param buffer where to store text
     */
    public TestOutputStream(StringBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * a buffering test output stream
     */
    public TestOutputStream() {
        buffer=new StringBuffer();
    }

    /**
     * get the buffer
     * @return the buffer for this stream
     */
    public StringBuffer getBuffer() {
        return buffer;
    }

    /**
     * turn the buffer into a string
     * @return  a string representation of the object.
     */
    public String toString() {
        return buffer.toString();
    }

    /**
     * implement the most low level of all the write commands
     * @param ch char to write
     */
    public void write(int ch) {
        buffer.append((char) ch);
    }

    /**
     * create a printstream from this
     * @return the new stream
     */
    public PrintStream createPrintStream() {
        return new PrintStream(this);
    }

    /**
     * get the index of a search in the substring
     * @param substring  string to search for
     * @return position in the buffer, -1 for no hit
     */
    public int indexOf(String substring) {
        return buffer.indexOf(substring);
    }

    /**
     * test for containment- is the string
     * found in the current buffer?
     * 
     * @param substring string to search for
     * @return true if found
     */
    public boolean contains(String substring) {
        return buffer.indexOf(substring)>=0;
    }
}

