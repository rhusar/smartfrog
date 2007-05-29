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
    String varSFProcessId = "processId";
    /** String for process name. */
    String varSFProcessName = "processName";
    /** String for shell command. */
    String varShellCommand = "shellCmd";

    /** String for commands. They could be Strings or Vectors of Strings. */
    String varCMDs = "cmd";

    /** String for exit command . They could be Strings or
     * Vectors of Strings. */
    String varExitCmd = "exitCmd";
    /**
     * String for exit command. Should I use exitCmd at the end of the script?
     */
    String varUseExitCmd = "useExitCmd";
    /** String for env properties. They could be Strings or
     * Vectors of Strings. */
    String varEnvProp = "envProperties";

    /**
     * Vector of strings for arguments.
     */
    String varShellArguments = "shellArguments";

    /** ProcessWorkingDirectory. */
    String varSFWorkDir = "workDir";
    /** LineReturn. */
    String varLineReturn = "lineReturn";
    /** Delay between commands. */
    String varDelayBetweenCmds = "delayBetweenCmds";
    /** Output message to. */
    String varOutputMsgTo = "outputMsgTo";

    /** Object that implements org.smartfrog.services.display.PrintMsgInt
     * /sfServices.sfDisplay uses it. */
    String varErrorMsgTo = "errorMsgTo";

    /** Object that implements org.smartfrog.services.display.PrintErrMsgInt
     * /sfServices.sfDisplay uses it. */
    String varOutputStreamTo = "OutputStreamTo";

    /** Object that implements
     * org.smartfrog.services.os.runCmd.OutputStreamInt. */
    String varErrorStreamTo = "errorStreamTo";

    /** Object that implements
     * org.smartfrog.services.os.runCmd.InfoStreamInt. */
    String varWaitSignalGoAhead = "waitSignalGoAhead";

   /** Should the batch be done step by step?. This indicates if the process
     * should detach when the spanned shell finishes and before terminating
     * the component*/
    String varShouldDetach = "shouldDetach";
    String varShouldTerminate = "shouldTerminate";

    /** Level log. */
    String varLogger = "logLevel";
    /** Print Stack. */
    String varPrintStack = "printStack";
    /**
     * status attribute; runtime
     */
    String varStatus = "status";

    /**
     * our exit code
     */
    String varExitValue = "exitValue";

    /**
     * terminate on failure
     */
    String varTerminateOnFailure = "terminateOnFailure";

    /**
     * Should we print the command on failure? The default is false, so that
     * nothing sensitive appears in logs.
     * {@value}
     */
    String varPrintCommandOnFailure = "printCommandOnFailure";

    /**
     * start the shell on sfDeploy() rather than sfStart()
     * {@value}
     */
    String varStartEarly = "startEarly";

    /**
     *  Executes the given command.
     *
     * @param  cmd  command to be exceuted
     *
     * @exception  RemoteException  In case of network/rmi error
     */
    public void execCmd(String cmd) throws RemoteException;

     /**
     *  Executes the batch of commands.
     *
     * @param  cmds  vector of commands to be executed
     *
     * @exception  RemoteException  In case of network/rmi error
     */
    public void execBatch(Vector cmds) throws RemoteException;

    //  public static String SCHEDULER="scheduler";
    //  public void shutdown();
}
