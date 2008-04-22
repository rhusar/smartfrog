/* (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.logging;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

/**
 * This creates an output stream from a log, an output stream that is buffered on a line-by-line basis.
 * At the end of each line (or on a flush), the output is written using the default encoding.
 * <p/>
 * Created 01-Feb-2008 13:24:40
 *
 */

public class OutputStreamLog extends OutputStream {

    private int level;
    private LogSF log;
    private ByteArrayOutputStream buffer;

    /**
     * Log to a given log at the given level
     * @param log the log to log to
     * @param level the level to log at
     */
    public OutputStreamLog(LogSF log, int level) {
        this.level = level;
        this.log = log;
        resetBuffer();
    }

    /**
     * Reset the buffer to a new empty ByteArrayOutputStream
     */
    private void resetBuffer() {
        buffer=new ByteArrayOutputStream();
    }

    /**
     * Print then reset the buffer
     */
    private void printBuffer() {
        String contents=buffer.toString();
        if(contents.length()>0) {
            resetBuffer();
            LogUtils.log(log,level,contents);
        }
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be written out.
     */
    public void flush() {
        printBuffer();
    }

    /**
     * Closes this output stream and releases any system resources associated with this stream. The general contract of
     * <code>close</code> is that it closes the output stream. A closed stream cannot perform output operations and
     * cannot be reopened. <p> The <code>close</code> method of <code>OutputStream</code> does nothing.
     *
     */
    public void close() {
        printBuffer();
    }

    /**
     * Writes the specified byte to this output stream.
     * @param b the <code>byte</code>.
     */
    public void write(int b)  {
        buffer.write(b);
        if (b=='\n') {
            printBuffer();
        }
    }
}
