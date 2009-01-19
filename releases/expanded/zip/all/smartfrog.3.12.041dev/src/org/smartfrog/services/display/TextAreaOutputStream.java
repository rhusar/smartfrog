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

package org.smartfrog.services.display;


import java.io.OutputStream;

import javax.swing.JTextArea;


/**
 *  Extends OutputStream to output to Text Area.
 *
 */
public class TextAreaOutputStream extends OutputStream {
    /** TextAreaOutputStram. */
    private static TextAreaOutputStream staticRef = null;

    /**
     *  TextArea output destination
     */
    private JTextArea jta;

    /**
     *Constructs TextAreaOutputStream with jtext area.
     *
     *@param  jta  jtext area
     */
    public TextAreaOutputStream(Object jta) {
        synchronized (jta) {
            this.jta = (JTextArea) jta;
        }
    }

    /**
     *Sets a single instance of jtext area accessible via static methods
     *
     *@param  jta  The new staticOutput value of jtext area
     */
    public static void setStaticOutput(Object jta) {
        staticRef = new TextAreaOutputStream(jta);
    }

    /**
     *Gets a single instance accessible jtext area
     *
     *@return    The staticOutput value
     */
    public static TextAreaOutputStream getStaticOutput() {
        return staticRef;
    }

    /**
     *Writes a byte subarray to the text area as a stream
     *
     *@param  buf  byte array to be written
     *@param  off  offset
     *@param  len  length
     */
    public void write(byte[] buf, int off, int len) {
        synchronized (jta) {
            jta.append(new String(buf, off, len));
        }
    }

    /**
     * Writes a byte array to the text area as a stream
     *
     *@param  b  byte array
     */
    public void write(byte[] b) {
        synchronized (jta) {
            jta.append(new String(b));
        }
    }

    /**
     *Writes an integer to the text area as a stream
     *
     *@param  b  integer
     */
    public void write(int b) {
        byte[] ba = new byte[1];
        ba[0] = (byte) b;

        synchronized (jta) {
            jta.append(new String(ba));
        }
    }
}
