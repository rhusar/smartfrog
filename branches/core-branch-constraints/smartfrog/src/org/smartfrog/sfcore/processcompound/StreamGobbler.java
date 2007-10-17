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

package org.smartfrog.sfcore.processcompound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * This class redirect InputStream to System.out.
 */
public class StreamGobbler extends Thread {
    InputStream is;
    boolean out = true;

    /**
     * Constructs StreamGobbler with the input stream and type of stream
     *
     * @param is Imput stream to gobble
     * @param typeS Type of the stream
     */
    public StreamGobbler(InputStream is, String typeS) {
        this.setName("StreamGobbler("+typeS+")");
        this.is = is;

        if (typeS.equals("err")) {
            this.out = false;
        }
    }

    /**
     * Reads an inputStream and shows the content in the System.out.
     * Overrides Thread.run.
     */
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;

            while ((line = br.readLine()) != null) {
                if (out) {
                    System.out.println(line);
                } else {
                    System.err.println(line);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
