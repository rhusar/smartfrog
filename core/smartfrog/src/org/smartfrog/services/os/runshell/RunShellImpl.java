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

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.services.display.PrintErrMsgInt;
import org.smartfrog.services.display.PrintMsgInt;
import org.smartfrog.services.utils.generic.OutputStreamIntf;
import org.smartfrog.services.utils.generic.StreamGobbler;
import org.smartfrog.services.utils.generic.StreamIntf;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogLifecycleException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;

import org.smartfrog.sfcore.reference.Reference;


/**
 *  This class implements the Compound interface because it can "contain"
 *  Virtual Hosts components. The Apache interface is the Remoteable interface
 *  and the Runnable interface is used to monitor the httpd process. The httpd
 *  process is started in sfStart by setting the apacheState variable to true
 *  and ended in sfTerminate by setting the apacheState variable to false. The
 *  Internet Activator scripts are used to edit the httpd.conf file. These rely
 *  on certain environment variables being set, these variables are defined in
 *  the sf file and are passed to the common.executeScript() method. Adding them
 *  to the sf file avoids the need to hard code these paramters. The scripts are
 *  downloaded from a webserver and are then saved locally.
 *
 */
public class RunShellImpl extends PrimImpl implements Prim, RunShell, Runnable {
    //ToDo provide and EXIT command to terminate Shell!
    /** String name for Process name. */
    String processName = "defaultRunShell";

    //desired
    /** String name for process Id. */
    String processId = "";
    /** String name for sheel prefix. */
    String[] shellPrefix = { "" };
    /** String name for shell command. */
    String shellCommand = null;
    /** Set of shell command attributes. */
    Vector shellCommandAtt = new Vector();
    /** strign name for exit command. */
    String exitCmd = "exit 0";
    /** Flag indicating exit command. */
    boolean useExitCmd = true;
    /** String name for line return. */
    String lineReturn = "" + ((char) 10);

    //desired

    /**
     * The String for working directory.
     */
    protected String workDir = ".";

    //File workDir = null; //desired
    /** String name for env properties. */
    String[] envProp = null;

    //optional
    /** Set of commands. */
    Vector cmds = null;

    // Process Data
    /** Runtime. */
    Runtime runtime = Runtime.getRuntime();
    /** sub process. */
    Process subProcess = null;
    /** delay between commands. */
    int delayBetweenCmds = 0;
    /** Data output stream. */
    DataOutputStream dos = null;
    /** Thread object. */
    Thread thread = null;

    //Printers...
    /** Print message. */
    PrintMsgInt printMsgImp = null;
    /** Print error message. */
    PrintErrMsgInt printErrMsgImp = null;

    //Stream consumers
    /** Output stream consumer. */
    OutputStreamIntf outputStreamObj = null;
    /** Error stream consumer. */
    StreamIntf errorStreamObj = null;

    /** Should wait for GoAhead after executing a command? */
    boolean waitSignalGoAhead = false;

    // Auxiliar variables for termination
    /** String for Termination type. */
    String terminationType = "";
    /** TerminationRecord object. */
    TerminationRecord termR;
    /** Flag indicating detachment. */
    boolean shouldDetach = false;
    /** flag indicating termination. */
    boolean shouldTerminate = true;
    /** logger value. */
    int logger = 2;

    // 5- info log, 1 - Critical. Use -1 to avoid log
    /** Flag indicating print stack. */
    boolean printStack = false;

    /**
     *  Constructor.
     * @exception  RemoteException  In case of network/rmi error
     */
    public RunShellImpl() throws RemoteException {
    }

    /**
     *  This method retrieves the paramters from the .sf file. For the purposes
     *  of a demo default paramteres could be hard coded.
     *
     * @exception SmartFrogException deployment failure
     * @exception  RemoteException In case of network/rmi error
     */
    public synchronized void sfDeploy() throws SmartFrogException,
    RemoteException {
        try {
            super.sfDeploy();
            readSFAttributes();

            //Create subProcess
            subProcess = runtime.exec(this.createCmd(this.shellPrefix,
                        this.shellCommand, this.shellCommandAtt), envProp,
                    new File(workDir));
            this.dos = new DataOutputStream(subProcess.getOutputStream());

            StreamGobbler outputGobbler;
            StreamGobbler errorGobbler;

            // any output?
            if (this.outputStreamObj != null) {
                outputGobbler = new StreamGobbler(subProcess.getInputStream(),
                        "[" + this.getNotifierId() + "] " + "OUT",
                        outputStreamObj.getOutputStream(), printMsgImp);
            } else {
                outputGobbler = new StreamGobbler(subProcess.getInputStream(),
                        "[" + this.getNotifierId() + "] " + "OUT", null,
                        printMsgImp);
            }

            // any error message?
            if (this.errorStreamObj != null) {
                errorGobbler = new StreamGobbler(subProcess.getErrorStream(),
                        "[" + this.getNotifierId() + "] " + "ERR",
                        errorStreamObj.getOutputStream(), printErrMsgImp);
            } else {
                errorGobbler = new StreamGobbler(subProcess.getErrorStream(),
                        "[" + this.getNotifierId() + "] " + "ERR", null,
                        printErrMsgImp);
            }

            errorGobbler.setPassType(false);
            outputGobbler.setPassType(false);

            // kick them off
            errorGobbler.start();
            outputGobbler.start();
            thread = new Thread(this);

            //Start listener
            thread.start();
            this.sfReplaceAttribute("status", "deployed");
        } catch (Throwable t) {
            throw SmartFrogLifecycleException.sfDeploy(t.getMessage(),t,this);
        }
    }

    /**
     *  Main processing method for the RunProcess object.
     */
    public void run() {
        try {
            int exitVal = subProcess.waitFor();

            // wait until process finishes
            if (exitVal == 0) {
                terminationType = "normal";
            } else {
                terminationType = "abnormal";
            }

            this.sfReplaceAttribute("status", "finished");
            log("Finished: " + terminationType + ", ExitVal: " + exitVal, 3);

            //Thread.sleep(3*1000);
        } catch (Exception ex) {
            terminationType = "abnormal";
            log(ex.getMessage(), 2);

            if (this.printStack) {
                ex.printStackTrace();
            }

            this.sfReplaceAttribute("status", "terminated");
        }

        termR = (new TerminationRecord(terminationType,
                "Shell finished: " + this.getNotifierId(), null));
        TerminatorThread terminator = new TerminatorThread(this,termR);
        if (shouldDetach)     terminator.detach();
        if (!shouldTerminate) terminator.dontTerminate();
        terminator.start();
    }

    /**
     *  Reads SF description = initial configuration.
     */
    private void readSFAttributes() throws SmartFrogException, RemoteException {

        logger = sfResolve(varLogger,logger,false);
        printStack = sfResolve(varPrintStack, printStack,false);

        //Optional attributes
        delayBetweenCmds = sfResolve(varDelayBetweenCmds,delayBetweenCmds,false);
        //4 methods for intf...

        waitSignalGoAhead = sfResolve(varWaitSignalGoAhead,waitSignalGoAhead,false);
        shouldTerminate = sfResolve(varShouldTerminate,shouldTerminate,false);
        shouldDetach = sfResolve(varShouldDetach,shouldDetach,false);
        lineReturn = sfResolve(varLineReturn, lineReturn, false);
        processId = sfResolve(varSFProcessId, processId, false);
        shellCommand = sfResolve(varShellCommand,shellCommand, false);
        workDir = sfResolve(varSFWorkDir, workDir, false);
        exitCmd = sfResolve(varExitCmd, exitCmd, false);
        useExitCmd =sfResolve(varUseExitCmd, useExitCmd, false);
        shellCommandAtt = this.readShellAttributes();
        cmds = this.readVarData(this.varCMDs);
        sfResolve(varEnvProp, envProp , false);

        // Mandatory attributes
        try{
            processName = sfResolve(varSFProcessName,processName,true);
        } catch (SmartFrogResolutionException e) {
            log("Failed to read mandatory attribute (readSFAttributes): "+e.toString(),1);
            throw e;
        }

        outputStreamObj = (OutputStreamIntf) sfResolve(varOutputStreamTo, outputStreamObj , false);
        errorStreamObj  = (StreamIntf) sfResolve(varErrorStreamTo, errorStreamObj , false);

        printMsgImp  =   (PrintMsgInt) sfResolve(varOutputMsgTo, printMsgImp , false);
        printErrMsgImp = (PrintErrMsgInt) sfResolve(varErrorMsgTo, printErrMsgImp , false);

    }



    /**
     *  This sets a flag that will start the httpd process running.
     *
     * @exception  SmartFrogException starting failure
     * @exception  RemoteException In cas eof network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException,
    RemoteException {
        super.sfStart();
        this.sfReplaceAttribute("status", "running");
        this.execBatch(this.cmds);
    }

    /**
     *  This shuts down Apache by requesting that the ApacheState variable be
     *  set to false.
     *
     * @param  tr  TerminationRecord object
     */
    public synchronized void sfTerminateWith(TerminationRecord tr) {
        try {
            // This should kill the thread and this closes dumpOut...
            dos.close();
        } catch (IOException e) {
            log(e.getMessage(), 3);

            if (this.printStack) {
                e.printStackTrace();
            }
        }

        try {
            if (subProcess != null) {
                this.subProcess.destroy();
                this.subProcess = null;
            }
        } catch (Exception ex) {
            log(ex.getMessage(), 3);

            if (this.printStack) {
                ex.printStackTrace();
            }
        }

        super.sfTerminateWith(tr);
    }

    /**
     *  Executes the given command.
     *
     * @param  cmd  command to be exceuted
     */
    public synchronized void execCmd(String cmd) {
        if (cmd==null) return;
        cmd = cmd + lineReturn;

        if (delayBetweenCmds > 0) {
            try {
                Thread.sleep(delayBetweenCmds * 1000);
            } catch (InterruptedException iex) {
                log(iex.getMessage(), 2);
            }
        } else {
            log("No delay between two consecutive cmds.", 5);
        }

        if (dos != null) {
            try {
                log("Executing: " + cmd, 2);
                dos.writeBytes(cmd);
                dos.flush();
            } catch (IOException ex) {
                log(ex.getMessage(), 1);

                if (this.printStack) {
                    ex.printStackTrace();
                }
            }
        } else {
            log("Error: Stream closed. Shell probably terminated.", 1);
        }
    }

    /**
     *  Exceutes the batch of commands.
     *
     * @param  cmds  vector of commands to be executed
     */
    public synchronized void execBatch(Vector cmds) {

        if ((cmds==null)||(cmds.isEmpty())) return;

        log("Executing Batch: " + cmds, 1);
        Object element = null;

        for (Enumeration e = cmds.elements(); e.hasMoreElements();) {
            element = e.nextElement();

            if (element instanceof String) {
                this.execCmd((String) element);
            }
        }

        if (this.useExitCmd) {
            this.execCmd(this.exitCmd);
        }

        log("Executing Batch: DONE." + cmds, 3);
    }

    // Aux Methods!

    /**
     *  Read all Replace Var Attributes in Vectors
     *
     *@param  typeAttrib  all var attributes
     *@return a vector containing all Replace Var Attributes or null.
     */
    private Vector readVarData(String typeAttrib) {
        if (typeAttrib==null) return null;

        log(" runShell.readVarData()", 4);

        Object key = null;
        Object value = null;
        Vector data = new Vector();

        for (Enumeration e = sfContext().keys(); e.hasMoreElements();) {
            key = e.nextElement();

            if (key instanceof String) {
                try {
                    if (((String) key).startsWith(typeAttrib)) {
                        value = (sfResolve((String) key));

                        if (value instanceof Vector) {
                            Object element = null;

                            for (Enumeration enu = ((Vector) value).elements();
                                    enu.hasMoreElements();) {
                                element = enu.nextElement();

                                if (element instanceof String) {
                                    data.add((String) element);
                                    log("runShell.readVarData().Adding(Vect): " +
                                        element, 5);
                                }
                            }
                        } else if (value instanceof String) {
                            data.add(value);
                            log("runShell.readVarData().Adding(Str):  " +
                                value, 5);
                        }
                    }
                } catch (Exception ex) {
                    log(ex.getMessage(), 2);

                    if (this.printStack) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if (data.isEmpty()) {
            log("runShell.readVarData(): Not data.", 5);
            data = null;
        }

        log("runShell.readVarData().data: " + data, 4);
        return data;
    }

    /**
     *  Read all shell Attributes in Vectors. If and attribute name ends with
     *  'b' then it is added to the previous one!
     *
     *@return    a vector conatining all shell attributes
     */
    private Vector readShellAttributes() {
        Vector shellCommandAtt = new Vector();
        Object key = null;
        String auxString = "";

        //System.out.println("reading Cmd Attributes...");
        for (Enumeration e = sfContext().keys(); e.hasMoreElements();) {
            key = e.nextElement();

            if (key instanceof String) {
                try {
                    if (((String) key).startsWith(varShellCommand + "Att")) {
                        if (((String) key).endsWith("b")) {
                            // To concatenate two parameters
                            // for parameters like ATTa=ATTb
                            //shellCommandAtt = shellCommandAtt + "" +
                //sfResolve((String)key);
                //former code when shellCommandAtt was a String
                            auxString = (String) shellCommandAtt.lastElement() +
                                (sfResolve((String) key)).toString();
                            shellCommandAtt.remove(shellCommandAtt.size() - 1);
                            shellCommandAtt.add(auxString);
                        } else {
                            shellCommandAtt.add((sfResolve((String) key)).
                        toString());
                        }
                    }
                } catch (Exception ex) {
                    log(ex.getMessage(), 2);

                    if (this.printStack) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return shellCommandAtt;
    }

    /**
     *  Creates a command.
     *
     *@param  cmdGeneral  command
     *@param  cmdStr      command string
     *@param  attributes  attributes
     *@return             textaul representation of command
     */
    private String[] createCmd(String[] cmdGeneral, String cmdStr,
        Vector attributes) {
        String[] cmd = null;

        try {
            if (attributes == null) {
                attributes = new Vector();
                attributes.add("");
            }

            int additionalParam = 3;

            int i = 0;
            cmd = new String[attributes.size() + additionalParam];

            //Cleaning empty args in the array
            for (int a = 0; a < cmdGeneral.length; a++) {
                if ((cmdGeneral[a] != null) && (!(cmdGeneral[a].equals(""))) &&
                        (!(cmdGeneral[a].equals(" ")))) {
                    cmd[i++] = cmdGeneral[a];
                }
            }

            if ((cmdStr != null) && (!(cmdStr.equals(""))) &&
                    (!(cmdStr.equals(" ")))) {
                cmd[i++] = cmdStr;
            }

            if (!attributes.isEmpty()) {
                attributes.trimToSize();

                Iterator iter = attributes.iterator();

                while (iter.hasNext()) {
                    String temp = (String) iter.next();

                    if ((temp != null) && (!(temp.equals(""))) &&
                            (!(temp.equals(" ")))) {
                        cmd[i++] = temp;
                    }
                }

                //end envProp
            }

            //Cleaning end empty args
            String[] result = new String[i];

            for (int j = 0; j < i; j++) {
                result[j] = cmd[j];
            }

            cmd = result;
            log("CreatedCmd:" + arrayToString(cmd), 5);
        } catch (Exception e) {
            log("SFRunCommand.createCmd:Error creating Cmd.(" + e.getMessage() +
                ")", 5);

            if (this.printStack) {
                e.printStackTrace();
            }
        }

        return cmd;
    }

    /**
     *  Converts array to string
     *
     *@param  array  array
     *@return        array converted to string
     */
    private String arrayToString(String[] array) {
        int i = 0;
        StringBuffer stringB = new StringBuffer();

        while (i < array.length) {
            stringB.append(array[i++]);
            stringB.append(" ");
        }

        return stringB.toString();
    }

    /**
     *  Gets the identity of the notifier process.
     *
     * @return process identity
     */
    public String getNotifierId() {
        return (processName + processId);
    }

    /**
     *  Log - Writes messages in the standart output
     *
     *@param  severity  severity of message
     *@param  message   message
     */
    private void log(String message, int severity) {
        try {
            //if (logger != false ) {
            if (logger >= severity) {
                //System.out.println("  LOG: Process "+ notifierId()+"  msg:" +
        //  message + ", serverity: "+ severity);
                System.out.println("[" + this.getNotifierId() + "] " + "LOG" +
                    " > " + message + ", SFRunShell, " + severity);
            }
        } catch (Exception e) {
            if (printStack != false) {
                e.printStackTrace();
            }
        }
    }

}
