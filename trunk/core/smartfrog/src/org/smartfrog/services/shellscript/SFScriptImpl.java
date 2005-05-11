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

import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import java.rmi.RemoteException;
import org.smartfrog.sfcore.prim.TerminationRecord;

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
    private boolean autoTerminate = true;

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
      if (deployScript !=null) {
          run(deployScript);
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
         run(startScript);
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
              run(terminateScript);
          }
     } catch (Exception ex) {
     }
     super.sfTerminateWith(tr);
 }

 //----

 private void run (Object script) throws SmartFrogException, RemoteException {

    if (script == null) {return;}

    if (script instanceof String) {
      run ((String)script);
    } else if (script instanceof Vector) {
      run ((Vector)script);
    } else if (script instanceof ComponentDescription) {
      run ((ComponentDescription)script);
    } else {
      if (sfLog().isErrorEnabled()){ sfLog().error("Wrong command: "+script.toString() +"["+script.getClass().getName()+"]"); }
    }
 }

 private void run (String script) throws SmartFrogException, RemoteException {
    ScriptResults result = shell.execute (script,0);
    result.waitForResults(0);
    if (sfLog().isInfoEnabled()){ sfLog().info("Executed: "+result.toString()); }
 }

 private void run (Vector script) throws SmartFrogException, RemoteException {
   ScriptResults result = shell.execute (script,0);
   result.waitForResults(0);
   if (sfLog().isInfoEnabled()){ sfLog().info("Executed: "+result.toString()); }
 }

 private void run (ComponentDescription script) throws SmartFrogException, RemoteException {
   for (Iterator i = script.sfValues(); i.hasNext();) {
     run(i.next());
   }
 }

}
