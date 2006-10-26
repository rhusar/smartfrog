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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.TerminationRecord;

public class SFScriptExecutionImpl  extends PrimImpl implements Prim, SFScriptExecution, SFReadConfig {

  private String name = null;

  /**
   * Exec data
   */
  private Cmd cmd = new Cmd();

  /**
   * This component should terminate when exec terminates
   */
  private boolean shouldTerminate = true;

  /**
   * This component should detach when exec terminates
   */
  private boolean shouldDetatch = false;

  /**
   * Script Exec component
   */
  private  ScriptExecution scriptExec = null;

  // For Prim
  public SFScriptExecutionImpl() throws RemoteException {

  }

  //Component and LiveCycle methods

  /**
   *  Reads SF description = initial configuration.
   * Override this to read/set properties before we read ours, but remember to call
   * the superclass afterwards
   */
  public void readConfig () throws SmartFrogException, RemoteException {

        this.name = sfResolve(ATR_NAME, name , false);
        if (name == null) {
            name = this.sfCompleteNameSafe().toString();
        }

        this.cmd = new Cmd(sfResolve(ATR_EXEC,new ComponentDescriptionImpl(null,null,false),true));

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
      readConfig();
      scriptExec = new ScriptExecutionImpl (name, cmd,this);
      sfLog().info("Init done");
  }
  /**
  *  This sets a flag that will start the httpd process running.
  *
  * @exception  SmartFrogException starting failure
  * @exception  RemoteException In cas eof network/rmi error
  */
 public synchronized void sfStart() throws SmartFrogException,RemoteException {
     super.sfStart();
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
         if (scriptExec != null) {
             ((ScriptExecutionImpl)scriptExec).kill();
             scriptExec = null;
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
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, ScriptLock lock) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.execute(command,lock);
  }


  /**
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param command String
   * @param lock ScriptLock
   * @param verbose determines if results output will be shown using out/err streams.
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, ScriptLock lock, boolean verbose) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.execute(command,lock,verbose);
  }

  /**
    * submit  a list of commands to the shell
    * @param commands the list of commands
    * @param timeout max number of miliseconds to obtain the lock: 0 is don't
    *   wait, -1 is wait forever
   * @param verbose determines if the shell output will be shown using out/err streams.
    * @throws SmartFrogException if the lock is not obtained in the requisite
    *   time
    * @return ScriptResults
    // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
    *   method
    */
   public ScriptResults execute(List commands, long timeout, boolean verbose) throws SmartFrogException{
     if (this.scriptExec==null) return null;
     return scriptExec.execute(commands,timeout,verbose);
   }


  /**
   * submit  a list of commands to the shell
   * @param commands the list of commands
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(List commands, long timeout) throws SmartFrogException{
    if (this.scriptExec==null) return null;
    return scriptExec.execute(commands,timeout);
  }

  /**
   * submit a command to the shell
   * @param command the command
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, long timeout) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.execute(command,timeout);
  }

  /**
   * submit a command to the shell
   * @param command the command
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @param verbose determines if the shell output will be shown using out/err streams.
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(String command, long timeout, boolean verbose) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.execute(command,timeout,verbose);
  }

  /**
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param commands List
   * @param lock ScriptLock
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(List commands, ScriptLock lock) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.execute(commands,lock);
  }

  /**
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the lock
   * @param commands List
   * @param lock ScriptLock
   * @param verbose determines if results output will be shown using out/err streams.
   * @return ScriptResults
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public ScriptResults execute(List commands, ScriptLock lock, boolean verbose) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.execute(commands,lock, verbose);
  }

  /**
   *
   * @param timeout max number of miliseconds to obtain the lock: 0 is don't
   *   wait, -1 is wait forever
   * @throws SmartFrogException if the lock is not obtained in the requisite
   *   time
   * @return ScriptLock
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public synchronized ScriptLock lockShell(long timeout) throws SmartFrogException {
    if (this.scriptExec==null) return null;
    return scriptExec.lockShell(timeout);
  }

  /**
   * @throws SmartFrogException if the lock object is not valid, i.e.
   *
   * @param lock the lock object receieved from the lockShell
   * @throws SmartFrogException if the lock object is not valid, i.e. if it is
   *   not currently holding the l0ck
   // TODO:  Implement this org.smartfrog.services.shellscript.ScriptExecution
   *   method
   */
  public synchronized void releaseShell(ScriptLock lock) throws SmartFrogException {
    if (this.scriptExec == null) return;
    scriptExec.releaseShell(lock);
  }

//----------------------------------------------
//  private void test1() throws SmartFrogException {
//    ScriptLock lock = scriptExec.lockShell(1000);
//    ScriptResults resultCD = scriptExec.execute("cd ",lock);
//    scriptExec.execute("cd \\",lock);
//    ScriptResults resultCD2 = scriptExec.execute("cd ",lock);
//    ScriptResults resultCD3 = scriptExec.execute("cdd ",lock);
//    try {
//      scriptExec.execute("dir /s", new ScriptLockImpl(this));
//    } catch (SmartFrogException ex) {
//      sfLog().error("", ex);
//    }
//    ScriptResults resultDir = scriptExec.execute("dir",lock);
//    ScriptResults resultExit = scriptExec.execute("cd",lock);
//    resultCD.waitForResults(0);
//    System.out.println("Test1- Result (CD): "+resultCD.toString());
//    resultDir.waitForResults(0);
//    System.out.println("Test1- Result (Dir): "+resultDir.toString());
//    resultExit.waitForResults(0);
//    System.out.println("Test1- Result (CDEnd): "+resultExit.toString());
//    resultCD3.waitForResults(0);
//    System.out.println("Test1- Result (CD3) Failed: "+resultCD3.toString());
//    resultCD2.waitForResults(0);
//    System.out.println("Test1- Result (CD2): "+resultCD2.toString());
//
//    scriptExec.releaseShell(lock);
//  }
//
//  private void test2() throws SmartFrogException {
//    ScriptLock lock = scriptExec.lockShell(1000);
//    List commands = new ArrayList();
//    commands.add("cd ");
//    commands.add("dir");
//    commands.add("echo Julio1");
//    commands.add("echo Julio2");
//    commands.add("echo Julio3");
//    commands.add("echo JulioEnd4");
//    commands.add("exit");
//
//    ScriptResults result = scriptExec.execute(commands,lock);
//    result.waitForResults(0);
//    System.out.println("Test2- Result: "+result.toString());
//    try {
//      System.out.println("\n Getting new lock (5 sec timeout)");
//      ScriptLock lock2 = scriptExec.lockShell(5000);
//    } catch (SmartFrogException ex) {
//      System.out.println("\n Timeout "+ex.toString());
//    }
//    System.out.println("\n Releasing lock");
//    scriptExec.releaseShell(lock);
//
//  }

}
