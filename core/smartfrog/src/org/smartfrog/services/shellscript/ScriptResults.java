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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;

/**
 * Interface to the "future" object returned from submiting an execute request
 * to A ScriptExecution implementing object.
 *
 * The resut contains three attributes as follows:
 *   "code" the int result code of the final command - 0 if not supported in shell,
 *   "stdout" a list of lines on stdout - empty if not supported in shell,
 *   "stdin" a list of lines on stderr - empty if not supported in shell.
 */
public interface ScriptResults {
    /**
     * check to see if the results have now been collates
     *
     * @return true if the results are ready, false otherwise
     */
    public boolean resultsReady();

    /**
     * wait for the results to be ready for the timeout, and return them when they are
     *
     * @param timeout the maximum time to wait for the results: 0 don't wait, -1 wait forever
     *
     * @return a component description containing aspects of the result:
     * The resut contains three attributes as follows:
     *   "code" the int result code of the final command in the vector - 0 if not supported in shell,
     *   "stdout" a list of lines on stdout - empty if not supported in shell,
     *   "stderr" a list of lines on stderr - empty if not supported in shell.
     *
     * @throws SmartFrogException if the results are not ready in time
     */
    public ComponentDescription waitForResults(long timeout) throws SmartFrogException;

    /**
      * Script result will be verbose using syste.out and system.err streams
    */
    public void verbose();

}
