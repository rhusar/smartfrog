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
import org.smartfrog.sfcore.prim.TerminationRecord;

public class ScriptExecutionImpl  extends PrimImpl implements Prim, ScriptExecution {

  // cmd Data
  private Cmd cmd = new Cmd();

  private long ID = -1;
  private String name = null;


  private  RunProcess runProcess = null;

  public ScriptExecutionImpl(long ID, String name, Cmd cmd) throws RemoteException {
    // RunProcessImpl
    runProcess = new RunProcessImpl (ID, name, cmd);
    runProcess.run();
  }

  // For Prim
  public ScriptExecutionImpl() throws RemoteException {

  }

  //Component and LiveCycle methods

  /**
   *  Reads SF description = initial configuration.
   * Override this to read/set properties before we read ours, but remember to call
   * the superclass afterwards
   */
  protected void readSFAttributes() throws SmartFrogException, RemoteException {
        java.io.File f =new java.io.File(".");
        cmd.setCmdArray(sfResolve(ATR_CMD,cmd.getCmdArray(),true));
        cmd.setEnvp(sfResolve(ATR_ENVP,cmd.getEnvp(),false));
        cmd.setFile(sfResolve(ATR_DIR,f,false));
        cmd.setLineSeparator(sfResolve(ATR_LINE_SEPARATOR,cmd.getLineSeparator(),false));

        ID = sfResolve(ATR_ID, ID , true);
        name = sfResolve(ATR_NAME, name , false);

  }

  /**
   *  This method retrieves the paramters from the .sf file. For the purposes
   *  of a demo default paramteres could be hard coded.
   *
   * @exception SmartFrogException deployment failure
   * @exception  RemoteException In case of network/rmi error
   */
  public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      readSFAttributes();
      runProcess = new RunProcessImpl (ID, name, cmd);
      ((RunProcessImpl) runProcess).start();

      if (!runProcess.ready()){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
          //throw new SmartFrogException("Process '"+ID+" "+name+" "+cmd+"' not ready",this);
      }
      //@TODO add a waitfor state processing, just in case program fails.
  }
  /**
  *  This sets a flag that will start the httpd process running.
  *
  * @exception  SmartFrogException starting failure
  * @exception  RemoteException In cas eof network/rmi error
  */
 public synchronized void sfStart() throws SmartFrogException,RemoteException {
     super.sfStart();
     runProcess.execCommand("dir");
     runProcess.execCommand("exit");
     //@Todo: if defined we could run an initial set of commands here
     // execute(commands,timeout);
 }

 /**
  *  This shuts down Apache by requesting that the ApacheState variable be
  *  set to false.
  *
  * @param  tr  TerminationRecord object
  */
 public synchronized void sfTerminateWith(TerminationRecord tr) {
     try {
         if (sfLog().isDebugEnabled()){
             sfLog().debug("Terminating.",null,tr);
          }
         if (runProcess != null) {
             runProcess.kill();
             runProcess = null;
         }
     } catch (Exception ex) {
     }
     super.sfTerminateWith(tr);
 }

  //-----------------



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
    if (this.lock!=lock) return null;
    // Run cmd in RunProcess setting filters for new listener
    runProcess.execCommand(command);
    return null;
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
    if (this.lock!=lock) return null;
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
    if (this.lock != lock )
      throw new SmartFrogException("LockOwnershipException");
    this.lock = null;
    notify();
  }
}
