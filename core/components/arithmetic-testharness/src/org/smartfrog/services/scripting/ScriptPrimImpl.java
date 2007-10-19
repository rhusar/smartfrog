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

package org.smartfrog.services.scripting;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.net.InetAddress;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Reader;
import java.io.InputStream;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import bsh.Interpreter;
import bsh.util.JConsole;
import bsh.EvalError;
import bsh.TargetError;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * ScriptPrimImpl is a SmartFrog component which allows the user to write
 * bits of java code in the description. BeansShell is used to interpret them.
 * It also implements the RemoteBSH which allows bsh scripts
 * to be remotely executed
 *
 */
public class ScriptPrimImpl
    extends PrimImpl
    implements Prim, RemoteScriptPrim {
  /** The Beanshell interpreter */
  public Interpreter interpreter = new Interpreter();

  /** The location of the code */
  private String sfScriptCodeBase = "";

  /** Standard RMI constructor
   * @throws RemoteException network problems
   * */
  public ScriptPrimImpl() throws RemoteException {
    super();
  }

  /**
   * Bind an object to a name in the beanshell interpreter.
   * @param name the name you want the object to be called in the interpreter
   * @param obj the object you want to register in the interpreter.
   * @throws RemoteException network problems
   * @throws SmartFrogException other problems   */
  public synchronized void setRemote(String name, Object obj) throws
      SmartFrogException, RemoteException {
    try {
      this.interpreter.set(name, obj);
    }
    catch (Throwable thr) {
      throw SmartFrogException.forward(thr);
    }
  }

  /**
   * Evaluate the String as a beanshell script.
   * The string is handed off to the internal interpreter object
   * @param script the script as a string.
   * @return  null the execution of the script fails.
   * @throws RemoteException network problems
   * @throws SmartFrogException other problems   */
  public synchronized Object eval(String script) throws SmartFrogException,
      RemoteException {
    try {
      return interpreter.eval(script);
    }
    catch (Exception e) {
      interpreter.println(" Received : " + script);
      return null;
    }
  }

  /**
   * Interpret the string passed as a value in the ScriptPrimImpl description.
   * If it's a file, return a reader on this file. If it's the script itself, a reader on the string.
   * @param script the String describing either the script or its URL.
   * @return a Reader on the script.
   */
  public Reader getScript(String script) {
    try {
        InputStream stream = SFClassLoader.getResourceAsStream(script);
        if(stream==null) {
            //assume its a script
            return new StringReader(script);
        }
        return new InputStreamReader(stream);
    }
    catch (Exception ex) {
        //assume it is a script
      return new StringReader(script);
    }
  }

  /**
   * Deploy the ScriptPrimImpl component.
   * It binds the 'prim' string to this instance for the beanshell interpreter.
   * The following attributes are looked up :
   * - If the 'attributesAsVariables' attribute is set to true / "true" in the description,
   * binds all attributes present during deploy phase to their keys for easier use in the interpreter.
   * - If the 'port' attribute is present, starts an htpp daemon on the component's host on the specified port.
   * - If the 'sfScriptCodeBase' attribute is present it will be used to find all the source scripts for all phases.
   * - If the 'sfDeployCode' is present the deploy script is evaluated.
   */
  public void sfDeploy() throws SmartFrogException, RemoteException {
    try {
      super.sfDeploy();

      interpreter.set("prim", this);

      boolean attAsVar = this.sfResolve("attributesAsVariables", false, false);

      if (attAsVar) {
// go through all the attributes and bind them in the interpreter.
        for (Enumeration e = this.sfContext().keys(); e.hasMoreElements(); ) {
          String attName = (String) e.nextElement();
          interpreter.set(attName, this.sfContext().get(attName));
        }
      }
      try {
        sfScriptCodeBase = (String)this.sfResolve("sfScriptCodeBase");
      }
      catch (SmartFrogResolutionException rex) {}

      try {
        String portS = ( (Integer)this.sfResolve("port")).toString();
        // call beanshell's interpreter own command to launch a server.
        interpreter.eval("server(+" + portS + ");");
        String messageHttp = "Go to: 'http://" +
                          InetAddress.getLocalHost().getHostName().toString() +
                          ":" + portS
                          +
                          "/remote/jconsole.html' to use remote console \n";
        interpreter.print(messageHttp);
      }
      catch (SmartFrogResolutionException rex) {}

      try {
        String sfDeployCodeSource = sfScriptCodeBase +
            (String)this.sfResolve("sfDeployCode");
        interpreter.eval(getScript(sfDeployCodeSource));
        // exception handling could be lighter. For script debugging purpose, we'll keep it explicit & heavy
      }
      catch (SmartFrogResolutionException rex) {
      }
      catch (TargetError e) {
        System.out.println(
            "The script, or the code called by the script, threw an exception during deploy phase: "
            + e.getTarget());
      }
      catch (EvalError e2) {
        System.out.println(
            "There was an error in evaluating the script:" + e2);
      }
    }
    catch (Exception e) {
      throw SmartFrogException.forward(e);
    }

  }

  /**
   * Start phase : execute the code described with the 'sfStartCode' attribute.
   * @throws Exception if the start phase fails.
   */
  public void sfStart() throws SmartFrogException, RemoteException {
    super.sfStart();
    try {
      String sfStartCodeSource = sfScriptCodeBase +
          (String)this.sfResolve("sfStartCode");
      interpreter.eval(getScript(sfStartCodeSource));
    }
    catch (SmartFrogResolutionException rex) {
    }
    catch (TargetError e) {
      System.out.println(
          "The script, or the code called by the script, threw an exception during start phase: "
          + e.getTarget());
    }
    catch (EvalError e2) {
      System.out.println(
          "There was an error in evaluating the script:" + e2);
    }
  }

  /**
   * During termination phase execute the code described within
   * the 'sfTerminateWithCode' attribute. Any exception is caught and left untreated.
   * @param status the TerminationRecord for this phase.
   */
  public void sfTerminateWith(TerminationRecord status) {
    try {
      String sfTerminateWithCodeSource = sfScriptCodeBase +
          (String)this.sfResolve("sfTerminateWithCode");
      interpreter.set("status", status);
      interpreter.eval(getScript(sfTerminateWithCodeSource));
    }
    catch (SmartFrogResolutionException rex) {
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    super.sfTerminateWith(status);
  }
}
