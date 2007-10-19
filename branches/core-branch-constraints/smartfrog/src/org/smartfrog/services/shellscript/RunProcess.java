/** (C) Copyright 1998-2005 Hewlett-Packard Development Company, LP

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


package org.smartfrog.services.shellscript;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public interface RunProcess {

    public int getProcessState();
    public boolean ready();
    public void run();
    public void execCommand(String command);
    public void kill();
    public void waitForReady(long time);

    /**
     * Gets the input stream of the subprocess.
     * The stream obtains data piped from the standard output stream
     * of the process (<code>Process</code>) object.
     * <p>
     * Implementation note: It is a good idea for the input stream to
     * be buffered.
     *
     * @return  the input stream connected to the normal output of the
     *          subprocess.
     */

    public InputStream getInputStream();

    /**
     * Gets the error stream of the subprocess.
     * The stream obtains data piped from the error output stream of the
     * process (<code>Process</code>) object.
     * <p>
     * Implementation note: It is a good idea for the input stream to be
     * buffered.
     *
     * @return  the input stream connected to the error stream of the
     *          subprocess.
     */
    public InputStream getErrorStream();

    /**
     * Gets the output stream of the subprocess.
     * Output to the stream is piped into the standard input stream of
     * the process (<code>Process</code>) object.
     * <p>
     * Implementation note: It is a good idea for the output stream to
     * be buffered.
     *
     * @return  the output stream connected to the normal input of the
     *          subprocess.
     */
    public OutputStream getOutputStream();

}
