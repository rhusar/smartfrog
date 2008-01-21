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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.logging.LogFactory;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.prim.Prim;

public class ScriptExecutionImpl  implements ScriptExecution, FilterListener {

// Inner class that implements futures ---

  public static class ScriptResultsImpl implements ScriptResults, Serializable {

      protected boolean resultReady = false;

      private Integer code = new Integer(-9999);

      protected InvocationTargetException exception = null;

      /** Verbose script results output */
      protected boolean verbose = false;

      private List<String> stdOut = null;
      private List<String> stdErr = null;
      private LogSF log;
      public static final String ERROR_RESULTS_NOT_READY = "Accessor should not be called before results are ready.";

      public ScriptResultsImpl(LogSF log) {
          this.log=log;
          stdOut = Collections.synchronizedList(new ArrayList<String>());
          stdErr = Collections.synchronizedList(new ArrayList<String>());
      }

      public List getStderr() throws SmartFrogException {
          checkResultsReady();
          return stdErr;
      }

      public List getStdout()  throws SmartFrogException {
          checkResultsReady();
          return stdOut;
      }

      public Integer getExitCode()  throws SmartFrogException {
          checkResultsReady();
          return code;
      }

      public synchronized InvocationTargetException getException()  throws SmartFrogException {
          checkResultsReady();
          return exception;
      }

      public String tailStderr(int num)  throws SmartFrogException {
          checkResultsReady();
          return tail(stdErr, num);
      }

      private void checkResultsReady() throws SmartFrogException {
          if (!resultReady) throw new SmartFrogException(ERROR_RESULTS_NOT_READY);
      }

      public String tailStdout(int num)  throws SmartFrogException {
          checkResultsReady();
          return tail(stdOut, num);
      }

      private String tail(List<String> list, int num) {
        StringBuilder res = new StringBuilder("");
        List<String> copy = new Vector<String>(list);
        int end = copy.size();
        int start = copy.size() - num;
        if (start < 0) start = 0;
        for (int i = start ; i < end ; i++) {
          res.append(copy.get(i));
          res.append("\n");
        }
        return res.toString();
      }


      public boolean resultsReady() {
          return resultReady;
      }

      public void verbose(){
        verbose = true;
      }

      /**
       * wait for the results to be ready for the timeout, and return them when they are
       *
       * @param timeout the maximum time to wait in milliseconds for the results: 0 don't wait, -1 wait forever
       *
       * @return a component description containing aspects of the result:
       * The result contains three attributes as follows:
       *   "code" the int result code of the final command in the vector - 0 if not supported in shell,
       *   "stdOut" a list of lines on stdout - empty if not supported in shell,
       *   "stdErr" a list of lines on stderr - empty if not supported in shell.
       *
       * @throws SmartFrogException if the results are not ready in time
     */
     public synchronized ComponentDescription waitForResults(long timeout) throws SmartFrogException {
       waitFor(timeout);
       return asComponentDescription();
    }

    /**
     * wait for the results to be ready for the timeout
     * @param timeout the maximum time to wait in milliseconds for the results: 0 don't wait, -1 wait forever
     *
     * @throws SmartFrogException if the results are not ready in time
     */

     public synchronized void waitFor(long timeout) throws SmartFrogException {
        try {
          if (resultReady) {
              return;
          }

          if (timeout != 0) {
            if (timeout == -1) {
              //Wait forever
              timeout = 0;
            }

            wait(timeout);

            if (exception != null)
              // Will throw , InterruptedException, InvocationTargetException
              throw exception;
          }
        }
        catch (Exception ex) {
          // Will throw , InterruptedException, InvocationTargetException
          throw SmartFrogException.forward(exception);
        }
        if (!resultReady) {
            throw new SmartFrogException("Time out reached before results ready.");
        }
    }


      public synchronized void ready(final Integer result) {
        code = result;
        resultReady = true;
        notifyAll();
      }

      synchronized void setException(Throwable e) {
       exception = new InvocationTargetException(e);
       code = new Integer(-1);
       resultReady = true;
       notifyAll();
      }

      public String toString(){
        return asComponentDescription().toString();
      }

      private ComponentDescription asComponentDescription() {
        ComponentDescription cd = new ComponentDescriptionImpl(null, new ContextImpl(), false);
        try {
          cd.sfAddAttribute("resultReady", Boolean.valueOf(resultReady));
          if (code!=null) cd.sfAddAttribute("code", code);
          if (stdErr!=null) cd.sfAddAttribute("stdErr", stdErr);
          if (stdOut!=null) cd.sfAddAttribute("stdOut", stdOut);
          if (exception != null) cd.sfAddAttribute("exception", exception);
        }
        catch (SmartFrogRuntimeException ex) {
          if (log.isErrorEnabled()) log.error(ex);
        }
        return cd;
      }
  }


// end of inner class ---------



  // cmd Data
  private Cmd cmd = new Cmd();

  private String name = null;

  private  RunProcess runProcess = null;

  private ScriptResultsImpl results = null;

  private static String TYPE_DONE ="done";
  private static String TYPE_NEXT_CMD ="next_cmd";

  /** String name for line return. */
  private static String LR = System.getProperty("line.separator"); //"" + ((char) 10);

  /** Used to format times */
  protected static DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");

  /** Component Log. This log is used to from any component.
   *  Initialized to log into the core log: SF_CORE_LOG
   *  It can be replaced using sfSetLog()
   */
  private LogSF  sflog = LogFactory.sfGetProcessLog();

  public ScriptExecutionImpl(String name, Cmd cmd, Prim prim) throws RemoteException {
      // RunProcessImpl
      this.name = name;
      this.cmd = cmd;
      try {
          if (prim!=null){
              sflog = LogFactory.getLog(prim.sfResolve(SmartFrogCoreKeys.SF_APP_LOG_NAME, "", true));
          }
      } catch (Exception ex) {
          if (sflog.isErrorEnabled()){sflog.error("",ex);};
      }

      if (cmd.getFilterOutListener()==null) {
          String filters[] = {TYPE_DONE+" "+name, TYPE_NEXT_CMD+" "+name};
          cmd.setFilterOutListener(this, filters);
      }
      if (cmd.getFilterErrListener()==null) {
          cmd.setFilterErrListener(this, null);
      }
      runProcess = new RunProcessImpl(name, cmd, prim);
      results = new ScriptResultsImpl(sfLog());
      ((RunProcessImpl)runProcess).start();
      runProcess.waitForReady(200);
  }

  public ScriptExecutionImpl(long ID, String name, Cmd cmd) throws RemoteException {
    this(name, cmd,null);
  }


  /**
   * Runs an echo command unless cmd.echoCommand is null.
   * @param text String Echoed string.
   * @return String, null if echoCommnand is null.
   */
  private String runEcho(String type, String text) {
    if (cmd.getEchoCommand()==null) return null;

    String echoMark = "ScriptExecEcho - "+type+" "+name+ " ["+dateFormatter.format(new Date())+"]";

    if (cmd.getExitErrorCommand()!=null) {
        echoMark = echoMark + " Exit code#: "+cmd.getExitErrorCommand();
    }

    runProcess.execCommand(cmd.getEchoCommand()+" "+ echoMark);
    return echoMark;
  }

  /**
   *
   * @param text String Text to add to the marking message.
   * @param block boolean should we block waiting (timeout) for ScriptResult to be ready
   * @param timeout max number of miliseconds wait in case of block true: 0 is don't
   *   wait, -1 is wait forever
   * @return ScriptResults
   * @throws SmartFrogException
   */
  private ScriptResults closeResults(String text, boolean block, long timeout) throws SmartFrogException {
    //Clean resultSet - Terminate previous resultSet
    ScriptResults res= results;
    //Get new resultSet
    runEcho(TYPE_DONE,text);
    if (block) res.waitFor(timeout);
    return res;
  }

  /**
   * submit a command to the shell
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param command String
   * @param scriptLock ScriptLock
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, ScriptLock scriptLock) throws SmartFrogException {
    return execute (command,scriptLock,false);
  }

  /**
   * submit a command to the shell
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param command String
   * @param scriptLock ScriptLock
   * @param verbose script output
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, ScriptLock scriptLock, boolean verbose) throws SmartFrogException {
    if (this.lock!=scriptLock) {
        throw new SmartFrogException( runProcess.toString() + " failed to execute '"+command.toString()+"': Wrong lock. ");
    }
    //Close results blocking
    closeResults(command, true, -1);
    ScriptResults res =  results;
    if (verbose) res.verbose();
    runProcess.execCommand(command);
    //Finish resulSet
    closeResults(command, false, -1);
    return res;
  }

  /**
   * submit a list of commands to the shell
   * @param commands the list of commands
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   */
  public ScriptResults execute(List commands, long timeout) throws SmartFrogException {
      return execute (commands,timeout,false);
  }

  /**
   * submit  a list of commands to the shell
   * @param commands the list of commands
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @param verbose determines if results output will be shown using out/err streams.
   *
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   */
  public ScriptResults execute(List commands, long timeout, boolean verbose) throws SmartFrogException {
    ScriptLock lockedShell = lockShell(timeout);
    ScriptResults result = execute (commands,lockedShell,verbose);
    releaseShell(lockedShell);
    return result;
  }

  /**
   * submit a command to the shell
   * @param command the command
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @param verbose determines if the shell output will be shown using out/err streams.
   *
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   */
  public ScriptResults execute(String command, long timeout, boolean verbose) throws SmartFrogException {
    ScriptLock scriptLock = lockShell(timeout);
    ScriptResults result = execute (command,scriptLock,verbose);
    releaseShell(scriptLock);
    return result;
  }

  /**
   * submit a command to the shell
   * @param command the command
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   */
  public ScriptResults execute(String command, long timeout) throws SmartFrogException {
    return execute (command,timeout,false);
  }
  /**
   * submit  a list of commands to the shell
   *
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param commands List
   * @param scriptLock ScriptLock
   * @return ScriptResults
   */
  public ScriptResults execute(List commands, ScriptLock scriptLock) throws  SmartFrogException {
    return execute (commands,scriptLock,false);
  }

  /**
   * submit  a list of commands to the shell
   *
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param commands List
   * @param scriptLock ScriptLock
   * @param verbose script output
   * @return ScriptResults
   */
  public ScriptResults execute(List commands, ScriptLock scriptLock, boolean verbose) throws  SmartFrogException {
    if (this.lock!=scriptLock) {
        throw new SmartFrogException( runProcess.toString() + " failed to execute '"+commands.toString()+"': Wrong lock. ");
    }
    // Loop through using extra echo to mark end of command and a lock to continue.

    //Close results blocking
    closeResults(commands.toString(), true, -1);
    ScriptResults res =  results;
    if (verbose) res.verbose();
    if (commands==null) {
      runEcho("exec_list_commands","NO Commands to run - NULL command list");
      closeResults("", false, -1);
      return res;
    }
    for (int i = 0; i < commands.size(); ++i) {
      //sfLog.trace("Comparing: "+ line +", "+filters[i]);
      runProcess.execCommand(commands.get(i).toString());
      //runEcho(TYPE_NEXT_CMD,commands.get(i).toString());
    }

    //Finish resulSet
    closeResults(commands.toString(), false, -1);
    return res;
  }

  /**
   * obtain a lock on the shell, will block until it is available
   * @param timeout max number of milliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptLock
   */
  public synchronized ScriptLock lockShell(long timeout) throws
      SmartFrogException {
    // throws InterruptedException
      try {
          if (timeout == 0) { // don't wait
              acquire_without_blocking();
          } else if (timeout == -1) { // wait forever
              while (!acquire_without_blocking()) {
                  wait(Long.MAX_VALUE);
              }
          } else { // wait  timeout
              if (!acquire_without_blocking()) {
                  wait(timeout);
                  if (!acquire_without_blocking()) {
                      throw new SmartFrogException("Timeout waiting to lock Shell");
                  }
              }
          }
          return lock;
      } catch (InterruptedException iex) {
          throw SmartFrogException.forward(iex);
      }
  }

  // Lock.
   private ScriptLock lock = null;

   /**
    * get lock if it cans.
    * @return boolean
    */
   private synchronized boolean acquire_without_blocking() {
     if (lock == null) { //lock = new ScriptLock();
       lock = new ScriptLockImpl(this);
       return true;
     }
     return false;
   }

  /**
   *
   * release the lock on the shell and resets verbose to false.
   *
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @param scriptLock the lock object received from the lockShell
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   */
  public synchronized void releaseShell(ScriptLock scriptLock) throws SmartFrogException {
    if (this.lock != scriptLock ) throw new SmartFrogException("LockOwnershipException");
    this.lock = null;
    notify();
  }

  public void kill(){
    if (this.runProcess!=null) runProcess.kill();
  }

  //Filter listener interface implementation

    public void line(String line, String filterName) {
        if (filterName.indexOf("out") != -1) {
            results.stdOut.add(line);
            if (results.verbose) {
                sfLog().out(line);
            }
        } else {
            results.stdErr.add(line);
            if (results.verbose) {
                sfLog().err(line);
            }
        }
        if (sfLog().isTraceEnabled()) {
            sfLog().trace("LINE " + line + ", " + filterName + ", "
                    + filterName.indexOf("out") + ", "
                    + filterName.indexOf("err"));
        }
    }

    public synchronized void found(String line, int filterIndex, String filterName) {
        if (sfLog().isTraceEnabled()) {
            sfLog().trace("FOUND LINE " + line + ", " + filterIndex + ", " + filterName);
        }
        if (filterIndex == 0) {
            //Finished
            if (line.indexOf(cmd.getEchoCommand() + " " + "ScriptExecEcho - " + TYPE_DONE + " " + name) != -1) {
                return; // This is the echo command itself, ignore
            }

            //What do we do if err continues producing output?, should we wait forever?
            Integer exitCode = new Integer(-999999);
            int index = line.indexOf("Exit code#:"); // 11 chars
            if (index != -1) {
                try {
                    exitCode = new Integer(line.substring(index + 12).trim());
                    if (sfLog().isDebugEnabled()) {
                       sfLog().debug("FOUND exit code: " + exitCode );
                    }
                } catch (NumberFormatException ex) {
                    if (sfLog().isWarnEnabled()) {
                        sfLog().warn(ex);
                    }
                }
            }
//            //remove the ScriptExecEcho output from stdout...
//            //New attribute passPositives controls this now directly on the filter.            
//            int last = results.stdOut.lastIndexOf(line);
//            results.stdOut.remove(last);

            createNewScriptResults(exitCode);
        } else if (filterIndex == 1) {
            //Next command will follow
            if (line.indexOf(cmd.getEchoCommand() + " " + "ScriptExecEcho - " + TYPE_NEXT_CMD + " " + name) != -1)
                return; // This is the echo command itself, ignore

        } else {
            if (sfLog().isWarnEnabled())
                sfLog().warn("\nFOUND ???? LINE " + line + ", " + filterIndex + ", " + filterName);
        }
    }

    /**
     * Finishes present ScriptResult and creates a new one.
     * @param exitCode the exit code
     * @return ScriptResults finished ScriptResult.
     */
    private synchronized ScriptResults createNewScriptResults(Integer exitCode) {
        ScriptResultsImpl finishedResults = results;
        results = new ScriptResultsImpl(sfLog());
        finishedResults.ready(exitCode);
        return finishedResults;
    }

    /**
     * This method should be used to log Core messages
     *
     * @return Logger implementing LogSF and Log
     */
    public LogSF sfLog() {
        if (sflog != null)
            return sflog;
        else {
            try {
                return LogFactory.getLog(name);
            } catch (Exception ex) {
                return LogFactory.sfGetProcessLog();
            }
        }
    }

}
