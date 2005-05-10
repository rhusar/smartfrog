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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.rmi.RemoteException;
import java.io.Serializable;

import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.logging.LogSF;
import org.smartfrog.sfcore.logging.LogFactory;

import org.smartfrog.sfcore.prim.Prim;

public class ScriptExecutionImpl  implements ScriptExecution, FilterListener {

// Inner class that implements futures ---

  public class ScriptResultsImpl implements ScriptResults, Serializable {

      protected boolean resultReady = false;

      protected ComponentDescription result = new ComponentDescriptionImpl(null,  new ContextImpl(), false);

      protected InvocationTargetException exception = null;

      List stdOut = null;
      List stdErr = null;

      public ScriptResultsImpl() {
        stdOut = Collections.synchronizedList(new ArrayList());
        stdErr = Collections.synchronizedList(new ArrayList());
        try {
          result.sfAddAttribute("stdOut", stdOut);
          result.sfAddAttribute("stdErr", stdErr);
        } catch (SmartFrogRuntimeException ex) {
          //@Todo add log
          ex.printStackTrace();
        }
      }

      public boolean resultsReady() {
          return resultReady;
      }

     public synchronized ComponentDescription waitForResults(long timeout) throws SmartFrogException {
        try {
          while (!resultReady) {
            wait(timeout);
          }
          if (exception != null)
            // Will throw , InterruptedException, InvocationTargetException
            throw exception;
          else
            return result;
        } catch (Exception ex) {
            // Will throw , InterruptedException, InvocationTargetException
            throw SmartFrogException.forward(exception);
        }
    }

    public synchronized void ready(Integer code) {
        try {
          result.sfAddAttribute("code", code);
        } catch (SmartFrogRuntimeException ex) {
          //@Todo add log
          ex.printStackTrace();
        }
        resultReady = true;
        notifyAll();
      }

      synchronized void setException(Throwable e) {
       exception = new InvocationTargetException(e);
       try {
         result.sfAddAttribute("code", new Integer(-1));
         result.sfAddAttribute("exception", exception);
       } catch (SmartFrogRuntimeException ex) {
         //@Todo add log
         ex.printStackTrace();
       }
       resultReady = true;
       notifyAll();
      }

      public String toString(){
        return result.toString();
      }
  }


// end of inner class ---------



  // cmd Data
  private Cmd cmd = new Cmd();

  private long ID = -1;
  private String name = null;

  private  RunProcess runProcess = null;

  private ScriptResults results = null;

  private static String TYPE_DONE ="done";
  private static String TYPE_NEXT_CMD ="next_cmd";

  /** Used to format times */
  protected static DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");

  /** Component Log. This log is used to from any component.
   *  Initialized to log into the core log: SF_CORE_LOG
   *  It can be replaced using sfSetLog()
   */
  private LogSF  sflog = LogFactory.sfGetProcessLog();

  public ScriptExecutionImpl(long ID, String name, Cmd cmd, Prim prim) throws RemoteException {
      // RunProcessImpl
      this.ID = ID;
      this.name = name;
      this.cmd = cmd;

      if (cmd.getFilterOutListener()==null) {
          String filters[] = {TYPE_DONE+" "+name+"_"+ID,
              TYPE_NEXT_CMD+" "+name+"_"+ID};
          cmd.setFilterOutListener(this, filters);
      }
      if (cmd.getFilterErrListener()==null) {
          cmd.setFilterErrListener(this, null);
      }
      runProcess = new RunProcessImpl(ID, name, cmd, prim);
      results = new ScriptResultsImpl();
      ((RunProcessImpl)runProcess).start();
      runProcess.waitForReady(200);
  }

  public ScriptExecutionImpl(long ID, String name, Cmd cmd) throws RemoteException {
    this(ID, name, cmd,null);
  }

  private void runProcess(long ID, String name, Cmd cmd) {

  }


  /**
   * Runs an echo commnad unless cmd.echoCommand is null.
   * @param text String Echoed string.
   * @return String, null if echoCommnand is null.
   */
  private String runEcho(String type, String text) {
    if (cmd.getEchoCommand()==null) return null;
    String echo = "MARK - "+type+" "+name+"_"+ID+ " ["+text+", "+dateFormatter.format(new Date())+"]";

    if (cmd.getExitErrorCommand()!=null) echo = echo + " Exit code#: "+cmd.getExitErrorCommand();

    runProcess.execCommand(cmd.getEchoCommand()+" "+echo);
    return echo;
  }

  /**
   *
   * @param text String Text to add to the marking message.
   * @param block boolean shold we block waiting for ScriptResult to be ready
   * @param timeout long
   * @return ScriptResults
   * @throws SmartFrogException
   */
  private ScriptResults closeResults(String text, boolean block, long timeout) throws SmartFrogException {
    //Clean resultSet - Terminate previous resultSet
    ScriptResults res= this.results;
    //Get new resultSet
    runEcho(TYPE_DONE,text);
    if (block) res.waitForResults(timeout);
    return res;
  }

  /**
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param command String
   * @param lock ScriptLock
   * @return ScriptResults
   * @todo Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, ScriptLock lock) throws SmartFrogException {
    if (this.lock!=lock) throw new SmartFrogException( runProcess.toString() + " failed to execute '"+command.toString()+"': Wrong lock. ");

    //Close results blocking
    closeResults(command, true, 0);
    ScriptResults res =  this.results;
    runProcess.execCommand(command);
    //Finish resulSet
    closeResults(command, false, 0);
    return res;
  }

  /**
   *
   * @param commands the list of commands
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   * @todo Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(List commands, long timeout) throws SmartFrogException {
    ScriptLock lock = this.lockShell(timeout);
    ScriptResults result = execute (commands,lock);
    this.releaseShell(lock);
    return result;
  }

  /**
   *
   * @param command the command
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   * @todo Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, long timeout) throws SmartFrogException {
    ScriptLock lock = this.lockShell(timeout);
    ScriptResults result = execute (command,lock);
    this.releaseShell(lock);
    return result;
  }

  /**
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param commands List
   * @param lock ScriptLock
   * @return ScriptResults
   * @todo Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(List commands, ScriptLock lock) throws
      SmartFrogException {
    if (this.lock!=lock) throw new SmartFrogException( runProcess.toString() + " failed to execute '"+commands.toString()+"': Wrong lock. ");
    // Loop through using extra echo to mark end of command and a lock to continue.

    //Close results blocking
    closeResults(commands.toString(), true, 0);
    ScriptResults res =  this.results;

    if (commands==null) {
      runEcho("exec_list_commands","NO Commands to run - NULL command list");
      closeResults(commands.toString(), false, 0);
      return res;
    }

    for (int i = 0; i < commands.size(); ++i) {
      //sfLog.trace("Comparing: "+ line +", "+filters[i]);
      runProcess.execCommand(commands.get(i).toString());
      //runEcho(TYPE_NEXT_CMD,commands.get(i).toString());
    }

    //Finish resulSet
    closeResults(commands.toString(), false, 0);
    return res;
  }

  /**
   *
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptLock
   * @todo Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public synchronized ScriptLock lockShell(long timeout) throws
      SmartFrogException {
    // throws InterruptedException
    try {
      if (timeout == 0) { // don't wait
        acquire_without_blocking();
      } else if (timeout == Long.MAX_VALUE) { // wait forever
        while (!acquire_without_blocking()) {
          this.wait(timeout);
        }
      } else { // wait  timeout
        if (!acquire_without_blocking()) {
          this.wait(timeout);
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
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @param lock the lock object receieved from the lockShell
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the l0ck
   * @todo Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public synchronized void releaseShell(ScriptLock lock) throws SmartFrogException {
    if (this.lock != lock ) throw new SmartFrogException("LockOwnershipException");
    this.lock = null;
    notify();
  }

  public void kill(){
    if (this.runProcess!=null) runProcess.kill();
  }

  //Filter listener interface implementation

  public void line (String line, String filterName){
      if (filterName.indexOf("out")!=-1){
        ((ScriptResultsImpl)results).stdOut.add(line);
      } else {
        ((ScriptResultsImpl)results).stdErr.add(line);
      }
      if (sfLog().isTraceEnabled()){
          this.sfLog().trace("LINE "+line+", "+filterName+", "+filterName.indexOf("out")+", "+ filterName.indexOf("err"));
      }
  }

  public synchronized void found( String line, int filterIndex, String filterName){
    if (sfLog().isTraceEnabled()) {
       sfLog().trace("FOUND LINE "+line+", "+filterIndex+", "+filterName);
    }
    if (filterIndex == 0) {
      //Finished
      if (line.indexOf(cmd.getEchoCommand()+" "+"MARK - "+TYPE_DONE+" "+name+"_"+ID)!=-1) return; // This is the echo command itself, ignore

      //What do we do if err continues producing output?, should we wait forever?
      ((ScriptResultsImpl)results).stdOut.add("-finished-");
      ((ScriptResultsImpl)results).stdErr.add("-finished-");
       Integer exitCode = new Integer(-999999);
       int index = line.indexOf("Exit code#:"); // 11 chars
       if (index!=-1){
        try {
          exitCode = new Integer(line.substring(index+ 12).trim());
        } catch (NumberFormatException ex) {
          if (sfLog().isWarnEnabled()) { sfLog().warn(ex);}
        }
       }
       //System.out.println("\n -- Finished -- "+line);
       createNewScriptResults(exitCode);
    } else if (filterIndex==1){
      //Next command will follow
       if (line.indexOf(cmd.getEchoCommand()+" "+"MARK - "+TYPE_NEXT_CMD+" "+name+"_"+ID)!=-1) return; // This is the echo command itself, ignore
      ((ScriptResultsImpl)results).stdOut.add("--- NEXT Command ---");
      ((ScriptResultsImpl)results).stdErr.add("--- NEXT Command ---");
       //System.out.println("\n -- GO NEXT Command -- "+line);
    } else {
      System.out.println("\nFOUND ???? LINE " + line + ", " + filterIndex + ", " + filterName);
    }
  }

  /**
   * Finishes present ScriptResult and creates a new one.
   * @return ScriptResults finished ScriptResult.
   */
  private ScriptResults createNewScriptResults(Integer exitCode) {
    ScriptResults finishedResults = this.results;
    this.results = new ScriptResultsImpl();
    ((ScriptResultsImpl)finishedResults).ready(exitCode);
    return finishedResults;
  }

  /**
   * This method should be used to log Core messages
   * @return Logger implementing LogSF and Log
   */
  public LogSF sfLog() {
     if (sflog!=null)
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
