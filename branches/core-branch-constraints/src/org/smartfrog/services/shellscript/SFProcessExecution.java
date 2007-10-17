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
import org.smartfrog.sfcore.common.SmartFrogException;

/**
 * Interface that provides the API to the script component, allowing
 * other co-located components to submit script commands.
 *
 * The interface provides for the submission of lines of script and the
 * ability to lock a script component for unique use for a period. This ensures
 * that sequences of script commands will not be interleaved with other
 * script requets to the component.
 *
 * The operational model is asynchronous, in that the execute operation only queues
 * the execute request and does not wait until it is complete. An object implementing
 * the ScriptResult interface is returned, and this can be queried to find if the script has
 * completed and obtain the resultant output, both error and normal.
 *
 * Commands to be executed are passed in as a list the following format.
 * Each element is either a string, in which case it is treated as a command, or
 * a list in which case the command is the space-separated "toString" of its elements.
 *
 */
public interface SFProcessExecution extends SFExecution {

    /** This indicates if the component should start during deploy
     * phase. String name for attribute. Value {@value}. */
    public final static String ATR_AUTO_START = "autoStart";


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

   public InputStream getStdOutStream();

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
   public InputStream getStdErrStream();

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
    public OutputStream getStdInpStream();

    /**
     * Kill the process
     */
    public void kill();

    /**
     * Restarts the process
     */
    public void restart() throws SmartFrogException ;


    /**
     * Is the process running?
     * @return boolean
     */
    public boolean isRunning();


}
