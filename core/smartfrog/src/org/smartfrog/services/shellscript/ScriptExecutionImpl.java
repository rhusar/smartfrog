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

import java.util.*;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import java.lang.reflect.InvocationTargetException;


public class ScriptExecutionImpl  extends PrimImpl implements Prim, ScriptExecution, FilterListener {

// Inner class that implements futures ---

  public class ScriptResultsImpl implements ScriptResults {

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
        System.out.println("READY!!!!!!!!!");
        resultReady = true;
        notifyAll();
      }

      synchronized void setException(Throwable e) {
       exception = new InvocationTargetException(e);
       resultReady = true;
       notifyAll();
      }

      public String toString(){
        return result.toString();
      }
  }


// end of inner class ---------

  private String echoCommand ="echo";

  // cmd Data
  private Cmd cmd = new Cmd();

  private long ID = -1;
  private String name = null;

  private  RunProcess runProcess = null;

  private ScriptResults results = null;

  public ScriptExecutionImpl(long ID, String name, Cmd cmd) throws RemoteException {
    // RunProcessImpl
    runProcess = new RunProcessImpl (ID, name, cmd);
    this.ID = ID;
    this.name = name;

    if (cmd.getFilterOutListener()==null){
        String filters[]={"dir","done "+name+"_"+ID};
        cmd.setFilterOutListener(this,filters);
    }
    if (cmd.getFilterErrListener()==null){
        cmd.setFilterErrListener(this,null);
    }
    results = new ScriptResultsImpl();
    ((RunProcessImpl) runProcess).start();
    runProcess.waitForReady(200);;
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
  public ScriptResults execute(String command, ScriptLock lock) throws
      SmartFrogException {
    if (this.lock!=lock) throw new SmartFrogException( runProcess.toString() + " failed to execute '"+command.toString()+"': Wrong lock. ");
    ScriptResults res= this.results;
    // Run cmd in RunProcess setting filters for new listener
    //Get new resultSet
    runProcess.execCommand(echoCommand+" "+"done "+name+"_"+ID+ " ["+command+"]");
    res.waitForResults(0);
    res = this.results;
    runProcess.execCommand(command);
    //Finish resulSet
    runProcess.execCommand(echoCommand+" "+"done "+name+"_"+ID+ " ["+command+"]");
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
  public ScriptResults execute(List commands, long timeout) throws
      SmartFrogException {
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
  public ScriptResults execute(String command, long timeout) throws
      SmartFrogException {
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
    return null;
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
       this.sfLog().trace("FOUND LINE "+line+", "+filterIndex+", "+filterName);
    }
    if (filterIndex == 0) {
      //go for next command
      System.out.println("\n -- GO NEXT Command -- "+line);
    } else if (filterIndex==1){
      //Finished
      //What do we do if err continues producing output?, should we wait for ever?
      ((ScriptResultsImpl)results).stdOut.add("-finished-");
      ((ScriptResultsImpl)results).stdErr.add("-finished-");
       createNewScriptResults();
    } else {
      System.out.println("FOUND ???? LINE " + line + ", " + filterIndex + ", " + filterName);
    }
  }

  private ScriptResults createNewScriptResults() {
    ScriptResults finishedResults = this.results;
    this.results = new ScriptResultsImpl();
    ((ScriptResultsImpl)finishedResults).ready(new Integer(0));
    return finishedResults;
  }

}
