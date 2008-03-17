/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.ssh;

import org.smartfrog.services.shellscript.ScriptExecution;
import org.smartfrog.services.shellscript.ScriptLock;
import org.smartfrog.services.shellscript.ScriptResults;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.rmi.RemoteException;
import java.util.List;

/**
 * A component to execute remote scripts.
 *
 * This is just a stub; nothing implemented yet.
 * Created 22-Oct-2007 13:29:37
 *
 */

public class SSHScriptExecution extends SSHExecImpl implements ScriptExecution {


    public SSHScriptExecution() throws RemoteException {
    }

    /**
     * obtain a lock on the shell, will block until it is available
     *
     * @param timeout max number of miliseconds to obtain the lock: 0 is don't wait, -1 is wait forever
     * @return lock object that must be used in follow-up execute and lock release operations
     * @throws SmartFrogException if the lock is not obtained in the requisite time
     */
    public ScriptLock lockShell(long timeout) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a list of commands to the shell
     *
     * @param commands the list of commands
     * @param lock     the lock object receieved from the lockShell
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is not currently holding the l0ck
     */
    public ScriptResults execute(List commands, ScriptLock lock) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a list of commands to the shell
     *
     * @param commands the list of commands
     * @param lock     the lock object receieved from the lockShell
     * @param verbose  determines if results output will be shown using out/err streams.
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is not currently holding the l0ck
     */
    public ScriptResults execute(List commands, ScriptLock lock, boolean verbose) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a command to the shell
     *
     * @param command the command to execute
     * @param lock    the lock object receieved from the lockShell
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is not currently holding the l0ck
     */
    public ScriptResults execute(String command, ScriptLock lock) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a command to the shell
     *
     * @param command the command to execute
     * @param lock    the lock object receieved from the lockShell
     * @param verbose determines if results output will be shown using out/err streams.
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is not currently holding the l0ck
     */
    public ScriptResults execute(String command, ScriptLock lock, boolean verbose) throws SmartFrogException {
        return null;
    }

    /**
     * release the lock on the shell and resets verbose to false.
     *
     * @param lock the lock object receieved from the lockShell
     * @throws SmartFrogException if the lock object is not valid, i.e. if it is not currently holding the l0ck
     */
    public void releaseShell(ScriptLock lock) throws SmartFrogException {

    }

    /**
     * submit  a list of commands to the shell as a single atomic lock/execute/unlock
     *
     * @param commands the list of commands
     * @param timeout  max number of miliseconds to obtain the lock: 0 is don't wait, -1 is wait forever
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock is not obtained in the requisite time
     */
    public ScriptResults execute(List commands, long timeout) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a list of commands to the shell as a single atomic lock/execute/unlock
     *
     * @param commands the list of commands
     * @param timeout  max number of miliseconds to obtain the lock: 0 is don't wait, -1 is wait forever
     * @param verbose  determines if the shell output will be shown using out/err streams.
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock is not obtained in the requisite time
     */
    public ScriptResults execute(List commands, long timeout, boolean verbose) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a command to the shell as a single atomic lock/execute/unlock
     *
     * @param command the command
     * @param timeout max number of miliseconds to obtain the lock: 0 is don't wait, -1 is wait forever
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock is not obtained in the requisite time
     */
    public ScriptResults execute(String command, long timeout) throws SmartFrogException {
        return null;
    }

    /**
     * submit  a command to the shell as a single atomic lock/execute/unlock
     *
     * @param command the command
     * @param timeout max number of miliseconds to obtain the lock: 0 is don't wait, -1 is wait forever
     * @param verbose determines if the shell output will be shown using out/err streams.
     * @return the "future" ScriptResult implementation that allows the code to obtain the results of executnig the
     *         script
     * @throws SmartFrogException if the lock is not obtained in the requisite time
     */
    public ScriptResults execute(String command, long timeout, boolean verbose) throws SmartFrogException {
        return null;
    }
}
