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
import java.util.List;

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
public interface ScriptExecution {

    /**
     * obtain a lock on the shell, will block until it is available
     *
     * @param timeout max number of miliseconds to obtain the lock:
     *    0 is don't wait,
     *    -1 is wait forever
     *
     * @return lock object that must be used in follow-up execute and
     * lock release operations
     *
     * @throws SmartFrogException if the lock is not obtained in the requisite
     * time
     */
    public ScriptLock lockShell(long timeout) throws SmartFrogException;

    /**
     * submit  a list of commands to the shell
     *
     * @param commands the list of commands
     * @param lock the lock object receieved from the lockShell
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is
     * not currently holding the l0ck
     */
    public ScriptResults execute(List commands, ScriptLock lock) throws SmartFrogException;

    /**
     * submit  a list of commands to the shell
     *
     * @param commands the list of commands
     * @param lock the lock object receieved from the lockShell
     * @param verbose determines if results output will be shown using out/err streams.
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is
     * not currently holding the l0ck
     */
    public ScriptResults execute(List commands, ScriptLock lock, boolean verbose) throws SmartFrogException;

    /**
     * submit  a command to the shell
     *
     * @param command the command to execute
     * @param lock the lock object receieved from the lockShell
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is
     * not currently holding the l0ck
     */
    public ScriptResults execute(String command, ScriptLock lock) throws SmartFrogException;

    /**
     * submit  a command to the shell
     *
     * @param command the command to execute
     * @param lock the lock object receieved from the lockShell
     * @param verbose determines if results output will be shown using out/err streams.
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is
     * not currently holding the l0ck
     */
    public ScriptResults execute(String command, ScriptLock lock, boolean verbose) throws SmartFrogException;


    /**
     * release the lock on the shell and resets verbose to false.
     *
     * @param lock the lock object receieved from the lockShell
     *
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is
     * not currently holding the l0ck
     */
    public void releaseShell(ScriptLock lock) throws SmartFrogException;

    /**
     * submit  a list of commands to the shell as a single atomic lock/execute/unlock
     *
     * @param commands the list of commands
     * @param timeout max number of miliseconds to obtain the lock:
     *    0 is don't wait,
     *    -1 is wait forever
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException  if the lock is not obtained in the requisite
     * time
     */
    public ScriptResults execute(List commands, long timeout) throws SmartFrogException;

    /**
     * submit  a list of commands to the shell as a single atomic lock/execute/unlock
     *
     * @param commands the list of commands
     * @param timeout max number of miliseconds to obtain the lock:
     *    0 is don't wait,
     *    -1 is wait forever
     * @param verbose determines if the shell output will be shown using out/err streams.
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException  if the lock is not obtained in the requisite
     * time
     */
    public ScriptResults execute(List commands, long timeout, boolean verbose) throws SmartFrogException;

    /**
     * submit  a command to the shell as a single atomic lock/execute/unlock
     *
     * @param command the command
     * @param timeout max number of miliseconds to obtain the lock:
     *    0 is don't wait,
     *    -1 is wait forever
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException  if the lock is not obtained in the requisite
     * time
     */
    public ScriptResults execute(String command, long timeout) throws SmartFrogException;

    /**
     * submit  a command to the shell as a single atomic lock/execute/unlock
     *
     * @param command the command
     * @param timeout max number of miliseconds to obtain the lock:
     *    0 is don't wait,
     *    -1 is wait forever
     * @param verbose determines if the shell output will be shown using out/err streams.
     *
     * @return the "future" ScriptResult implementation that allows the code to
     * obtain the results of executnig the script
     *
     * @throws SmartFrogException  if the lock is not obtained in the requisite
     * time
     */
    public ScriptResults execute(String command, long timeout, boolean verbose) throws SmartFrogException;

}
