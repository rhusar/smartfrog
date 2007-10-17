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

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.TerminatorThread;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.prim.TerminationRecord;
import org.smartfrog.sfcore.reference.Reference;

public class SFScriptImpl  extends PrimImpl implements Prim, SFScript, SFReadConfig {

    /**
     * Script run during deployment phase
     */
    private Object deployScript = null;
    /**
     * Script run during start phase
     */
    private Object startScript = null;
    /**
     * Script run during terminate phase
     */
    private Object terminateScript = null;
    /**
     * Host component should terminate when process terminates
     */
    private boolean autoTerminate = false;

    /**
     * Script out/err content shown
     */
    private boolean verbose = false;

    /** String name for line return. */
     private String lineSeparator = System.getProperty("line.separator");

    /**
     * Script Exec component
     */
    private SFScriptExecution shell = null;

  // For Prim
  public SFScriptImpl() throws RemoteException {
  }

  //Component and LiveCycle methods

  /**
   *  Reads SF description = initial configuration.
   * Override this to read/set properties before we read ours, but remember to call
   * the superclass afterwards
   */
  public void readConfig() throws SmartFrogException, RemoteException {
        this.shell = (SFScriptExecution) sfResolve (ATTR_SHELL,shell,true);
        this.deployScript = sfResolve(ATTR_DEPLOY_SCRIPT,false);
        this.startScript = sfResolve(ATTR_START_SCRIPT,false);
        this.terminateScript = sfResolve(ATTR_TERMINATE_SCRIPT,false);
        this.autoTerminate = sfResolve (ATR_AUTO_TERMINATE,autoTerminate,false);
        this.verbose = sfResolve (ATR_VERBOSE,autoTerminate,false);
  }


  /**
   *  This method retrieves the paramters from the .sf file. For the purposes
   *  of a demo default parameters could be hard coded.
   *
   * @exception SmartFrogException deployment failure
   * @exception  RemoteException In case of network/rmi error
   */
  public synchronized void sfDeploy() throws SmartFrogException, RemoteException {
      super.sfDeploy();
      readConfig();
      if (deployScript !=null) {
          ComponentDescription cd = run(deployScript);
          checkResult(deployScript, cd);
          if (sfLog().isDebugEnabled()){
             sfLog().debug(("run"+lineSeparator+"["+ deployScript+"]"+lineSeparator+" with result "+lineSeparator+"["+cd+"]"));
         }

      }
  }

  /**
  *  This sets a flag that will start the httpd process running.
  *
  * @exception  SmartFrogException starting failure
  * @exception  RemoteException In cas eof network/rmi error
  */
 public synchronized void sfStart() throws SmartFrogException,RemoteException {
     super.sfStart();
     if (startScript !=null) {
         ComponentDescription cd = run(startScript);
         if (sfLog().isDebugEnabled()){
             sfLog().debug(("run"+lineSeparator+"["+ startScript+"]"+lineSeparator+" with result "+lineSeparator+"["+cd+"]"));
         }
         checkResult(startScript, cd);
     }
     if (this.autoTerminate){
       TerminationRecord termR = new TerminationRecord(TerminationRecord.NORMAL, "Script '"+this.sfCompleteNameSafe() + "' done." , null);
       TerminatorThread terminator = new TerminatorThread(this,termR);
       terminator.start();
     }
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
          if (terminateScript !=null) {
              ComponentDescription cd = run(terminateScript);
              if (sfLog().isDebugEnabled()){
                  sfLog().debug(("run"+lineSeparator+"["+ terminateScript+"]"+lineSeparator+" with result"+lineSeparator+"["+cd+"]"));
              }
          }
     } catch (Exception ex) {
     }
     super.sfTerminateWith(tr);
 }

 //----

 private  ComponentDescription run (Object script) throws SmartFrogException, RemoteException {
    ComponentDescription cd = null;

    if (script == null) {return cd;}

    if (script instanceof String) {
      cd = run ((String)script);
    } else if (script instanceof Vector) {
      cd = run ((Vector)script);
    } else if (script instanceof ComponentDescription) {
      cd = run ((ComponentDescription)script);
    } else {
      String msg = "Wrong script class: "+script.toString() +" ["+script.getClass().getName()+"]";
      ComponentDescription cdWrongCmd = new ComponentDescriptionImpl(null,  new ContextImpl(), false);
      if (sfLog().isErrorEnabled()){ sfLog().error(msg); }
      cdWrongCmd.sfAddAttribute("Wrong script", script );
      cdWrongCmd.sfAddAttribute("Wrong script class", script.getClass().getName() );
      cdWrongCmd.sfAddAttribute("code", new Integer (-9999));
    }
    // checkResult(deployScript, cd);    //Only if we want to check every individual command.
    return cd;
 }

private void checkResult(Object script, ComponentDescription cd) throws  SmartFrogResolutionException, SmartFrogException {
    if (cd!=null) {
        Integer exitCode = new Integer(-9999);
        exitCode = (Integer)cd.sfResolve("code", exitCode, false);
        if (exitCode.intValue()!=0) {
            String msg = " Error running script [exit code ="+exitCode+"]:"+ script.toString();
            SmartFrogException sex = new SmartFrogException(msg, this);
            sex.add("script", script);
            sex.add("result", cd);
            if (sfLog().isErrorEnabled()) {
                sfLog().error(msg+"\n"+cd.toString());
            }
            throw sex;
        }
    }
}

 private ComponentDescription run (String script) throws SmartFrogException, RemoteException {
    ScriptResults result = shell.execute (script,-1,verbose);
    ComponentDescription cd = result.waitForResults(-1); //wait forever
    if (sfLog().isTraceEnabled()){ sfLog().trace("Executed (String script):\n "+result.toString()); }
    return cd;
 }

 private ComponentDescription run (Vector script) throws SmartFrogException, RemoteException {
   script = resolveCommands(script);
   ScriptResults result = shell.execute (script,-1,verbose);
   ComponentDescription cd =result.waitForResults(-1); //wait forever
   if (sfLog().isTraceEnabled()){ sfLog().trace("Executed (Vector script):\n "+result.toString()); }
   return cd;
 }

 private ComponentDescription run (ComponentDescription script) throws SmartFrogException, RemoteException {
   ComponentDescription cdAll = new ComponentDescriptionImpl(null,  new ContextImpl(), false);
   ComponentDescription cd = null;
   int count = 0;
   for (Iterator i = script.sfValues(); i.hasNext();) {
      Object item = i.next();
      if (item instanceof Reference){
          item = this.sfResolve ((Reference)item);
      }
      cd =run(item);
      count++;
      if (cd !=null) cdAll.sfAddAttribute(new Integer(count).toString(),cd);
   }
   Integer lastExitCode=new Integer (-9999);
   lastExitCode = (Integer) cd.sfResolve("code",lastExitCode,false);
   cdAll.sfAddAttribute("code",lastExitCode);
   return cdAll;
 }

 /**
  * Method to sfResolve any lazy reference in the list before it is executed
  */
 private Vector resolveCommands(Vector commands) throws SmartFrogException, RemoteException {
     Vector newCommands = new Vector();
     for (int i = 0; i<commands.size(); ++i) {
         Object command = commands.get(i);
         if (command instanceof Reference) {
             command = this.sfResolve((Reference)command);
         }
         newCommands.add(command);
     }
     return newCommands;
   }
}
