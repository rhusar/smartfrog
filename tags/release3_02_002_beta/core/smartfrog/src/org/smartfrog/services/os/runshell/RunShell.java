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

package org.smartfrog.services.os.runshell;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;


/**
 *  Defines the interface for RunShellScripts component. It defines the
 *  variables and utlity methods for executing the commands.
 */
public interface RunShell extends Remote {
    /** String for process id. */
    final String varSFProcessId = "processId";
    /** String for process name. */
    final String varSFProcessName = "processName";
    /** String for shell command. */
    final String varShellCommand = "shellCmd";

    /** String for commands. They could be Strings or Vectors of Strings. */        final String varCMDs = "cmd";

    /** String for exit command . They could be Strings or
     * Vectors of Strings. */
    final String varExitCmd = "exitCmd";
    /**
     * String for exit command. Should I use exitCmd at the end of the script?
     */
    final String varUseExitCmd = "useExitCmd";
    /** String for env properties. They could be Strings or
     * Vectors of Strings. */
    final String varEnvProp = "envProperties";

    /** ProcessWorkingDirectory. */
    final String varSFWorkDir = "workDir";
    /** LineReturn. */
    final String varLineReturn = "lineReturn";
    /** Delay between commands. */
    final String varDelayBetweenCmds = "delayBetweenCmds";
    /** Output message to. */
    final String varOutputMsgTo = "outputMsgTo";

    /** Object that implements org.smartfrog.services.display.PrintMsgInt
     * /sfServices.sfDisplay uses it. */
    final String varErrorMsgTo = "errorMsgTo";

    /** Object that implements org.smartfrog.services.display.PrintErrMsgInt
     * /sfServices.sfDisplay uses it. */
    final String varOutputStreamTo = "OutputStreamTo";

    /** Object that implements
     * org.smartfrog.services.os.runCmd.OutputStreamInt. */
    final String varErrorStreamTo = "errorStreamTo";

    /** Object that implements
     * org.smartfrog.services.os.runCmd.InfoStreamInt. */
    final String varWaitSignalGoAhead = "waitSignalGoAhead";

   /** Should the batch be done step by step?. This indicates if the process
     * should detach when the spanned shell finishes and before terminating
     * the component*/
    final String varShouldDetach = "shouldDetach";
    final String varShouldTerminate = "shouldTerminate";

    /** Level log. */
    final String varLogger = "logLevel";
    /** Print Stack. */
    final String varPrintStack = "printStack";


     /**
     *  Executes the given command.
     *
     * @param  cmd  command to be exceuted
     *
     * @exception  RemoteException  In case of network/rmi error
     */
    public void execCmd(String cmd) throws RemoteException;

     /**
     *  Exceutes the batch of commands.
     *
     * @param  cmds  vector of commands to be executed
     *
     * @exception  RemoteException  In case of network/rmi error
     */
    public void execBatch(Vector cmds) throws RemoteException;

    //  public static final String SCHEDULER="scheduler";
    //  public void shutdown();
}
