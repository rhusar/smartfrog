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

package org.smartfrog.services.utils.generic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.smartfrog.services.display.PrintErrMsgInt;
import org.smartfrog.services.display.PrintMsgInt;

/**
 * Stream Gobbler used to redirect content from System streams.
 * Active thread that will read the content from a stream and will
 * send it to another stream and/or external method.
 *
 * @see PrintMsgInt
 * @see PrintErrMsgInt
 */
public class StreamGobbler extends Thread {
    /** Input stream. */
    InputStream is;
    /** Staring for type. */
    String type;
    /** Output stream. */
    OutputStream os;
    /** Flag indicating print to output stream */
    boolean printToOutputStream = false;
    /** Flag indicating print to output message */
    boolean printToOutputMsg = false;
    /** Flag indicating print to error message*/
    boolean printToErrMsg = false;
    /** Flag indicating print to std output */
    boolean printToStdOutput = true;
    /** Flag indicating passtype */
    boolean passType = false;
    /** PrintErrMsgInt object. */
    PrintErrMsgInt printerError = null;
    /** PrintMsgInt object. */
    PrintMsgInt printerOutput = null;

    /**
     *  Constructs a StreamGobbler object.
     *
     * @param is    an InputStream object
     * @param type  pass type
     */
    public StreamGobbler(InputStream is, String type) {
        this(is, type, null);
    }

    /**
     *  Constructs a StreamGobbler object.
     *
     * @param is    an InputStream object
     * @param type  pass type
     * @param redirect an OutputStream object
     */
    public StreamGobbler(InputStream is, String type, OutputStream redirect) {
        this.is = is;
        this.type = type;
        this.os = redirect;

        if (os != null) {
            this.printToOutputStream = true;
        }
    }

    /**
     *  Constructs a StreamGobbler object.
     *
     * @param is    an InputStream object
     * @param type  pass type
     * @param redirect an OutputStream object
     * @param printer an object implementing printMsgInt or printErrMsgInt
     */
    public StreamGobbler(InputStream is, String type, OutputStream redirect,
        Object printer) {
        this.is = is;
        this.type = type;
        this.os = redirect;

        if (os != null) {
            this.printToOutputStream = true;
        }

        if (printer != null) {
            if (printer instanceof PrintMsgInt) {
                this.printerOutput = (PrintMsgInt) printer;
                this.printToOutputMsg = true;
            } else if (printer instanceof PrintErrMsgInt) {
                this.printerError = (PrintErrMsgInt) printer;
                this.printToErrMsg = true;
            }
        }
    }

    /**
     * Main processing method.
     */
    public void run() {
        if (type == null) {
            type = "GOBBLER";
        }

        try {
            PrintWriter pw = null;

            if (os != null) {
                pw = new PrintWriter(os);
            }

            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            boolean next = true;

            while (next) {
                //              try {
                if ((line = br.readLine()) != null) {
                } else {
                    next = false;
                }

                //To std Output
                if (printToStdOutput) {
                    System.out.println(type + " > " + line);
                }

                if (passType) {
                    line = type + " > " + line;
                }

                // To External OutputStream
                if (this.printToOutputStream) {
                    pw.println(line);
                    pw.flush();
                }

                // To External Obj implementing printMsgInt or printErrMsgInt
                try {
                    // Print to one or the other
                    if (this.printToOutputMsg) {
                        this.printerOutput.printMsg(line);
                    } else if (this.printToErrMsg) {
                        this.printerError.printErrMsg(line);
                    }
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }

            // while
            // Flush Stream before finishing!
            if (this.printToOutputStream) {
                pw.flush();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Identifies this object as print to std output.
     *
     * @param bool  true to make this object print to std output
     */
    public void setPrintToStdOutput(boolean bool) {
        this.printToStdOutput = bool;
    }

    //Should add type to printMsgs and Streams ...
    /**
     * Identifies this object as pass type.
     *
     * @param bool  true to make this object pass type
     */
    public void setPassType(boolean bool) {
        this.passType = bool;
    }
}
