package org.smartfrog.services.scripting;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.net.InetAddress;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.Reader;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.prim.*;
import org.smartfrog.sfcore.common.*;
//import org.smartfrog.sfcore.reference.SmartFrogResolutionException;
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
 * @author Guillaume Mecheneau
 */
public class ScriptPrimImpl extends PrimImpl implements Prim , RemoteScriptPrim {
  /** The Beanshell interpreter */
  public Interpreter interpreter = new Interpreter();

  /** The location of the code */
  private String sfScriptCodeBase ="";

  /** Standard RMI constructor */
  public ScriptPrimImpl() throws RemoteException {
    super();
  }

  /**
   * Bind an object to a name in the beanshell interpreter.
   * @param name the name you want the object to be called in the interpreter
   * @param obj the object you want to register in the interpreter.
   */
  public synchronized void setRemote(String name, Object obj) throws Exception {
    this.interpreter.set(name,obj);
  }

  /**
   * Evaluate the String as a beanshell script.
   * The string is handed off to the internal interpreter object
   * @param script the script as a string.
   * @throw Exception if the execution of the script fails.
   */
  public synchronized Object eval(String script) throws Exception {
    try {
      return interpreter.eval(script);
    } catch (Exception e) {
      interpreter.println( " Received : " + script);
      return null;
    }
  }
  /**
   * Interpret the string passed as a value in the ScriptPrimImpl description.
   * If it's a file, return a reader on this file. If it's the script itself, a reader on the string.
   * @param script the String describing either the script or its URL.
   * @return a Reader on the script.
   */
  public Reader getScript(String script){
    try {
      return new InputStreamReader(SFClassLoader.getResourceAsStream(script));
    } catch (Exception ex) {
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
  public void sfDeploy() throws SmartFrogException , RemoteException{
    super.sfDeploy();
    try {
        interpreter.set("prim", this);
    }catch (Exception e) {
        throw new SmartFrogException(e);
    }
    // maintain backward-compatibility with Strings true and false
    Object ind = this.sfResolve("attributesAsVariables");
    boolean attAsVar = false;
    if (ind != null) {
       attAsVar = (ind instanceof String) ? Boolean.valueOf((String)ind).booleanValue(): ((Boolean)ind).booleanValue();
    }
    if (attAsVar) {
    // go through all the attributes and bind them in the interpreter.
      for (Enumeration e = this.sfContext().keys(); e.hasMoreElements();){
        String attName = (String) e.nextElement();
        try {
            interpreter.set(attName,this.sfContext().get(attName));
        }catch (Exception ex) {
            throw new SmartFrogException(ex);
        }
      }
    }
    try {
      sfScriptCodeBase = (String)this.sfResolve("sfScriptCodeBase");
    } catch (SmartFrogResolutionException rex){}

    try {
      String portS = ((Integer) this.sfResolve("port")).toString();
      // call beanshell's interpreter own command to launch a server.
      interpreter.eval("server(+"+portS+");");
      interpreter.print("Go to: 'http://"+
              InetAddress.getLocalHost().getHostName()+":"+portS
              +"/remote/jconsole.html' to use remote console \n");
    } catch (SmartFrogResolutionException rex){
    } catch (Exception ex) {
        SmartFrogException.forward(ex);
    }

    try {
      String sfDeployCodeSource = sfScriptCodeBase+(String)this.sfResolve("sfDeployCode");
      interpreter.eval(getScript(sfDeployCodeSource));
      // exception handling could be lighter. For script debugging purpose, we'll keep it explicit & heavy
    } catch (SmartFrogResolutionException rex){
    } catch ( TargetError e ) {
            System.out.println(
                "The script, or the code called by the script, threw an exception during deploy phase: "
                + e.getTarget() );
    } catch ( EvalError e2 )    {
            System.out.println(
                "There was an error in evaluating the script:" + e2 );
    }

  }
/**
 * Start phase : execute the code described with the 'sfStartCode' attribute.
 * @throw Exception if the start phase fails.
 */
  public void sfStart() throws SmartFrogException, RemoteException {
    super.sfStart();
    try {
      String sfStartCodeSource = sfScriptCodeBase+(String) this.sfResolve("sfStartCode");
      interpreter.eval(getScript(sfStartCodeSource));
    } catch (SmartFrogResolutionException rex){
    } catch ( TargetError e ) {
            System.out.println(
                "The script, or the code called by the script, threw an exception during start phase: "
                + e.getTarget() );
    } catch ( EvalError e2 )    {
            System.out.println(
                "There was an error in evaluating the script:" + e2 );
    }
  }
/**
 * During termination phase execute the code described within
 * the 'sfTerminateWithCode' attribute. Any exception is caught and left untreated.
 * @param status the TerminationRecord for this phase.
 */
  public void sfTerminateWith(TerminationRecord status) {
    try {
      String sfTerminateWithCodeSource = sfScriptCodeBase+(String) this.sfResolve("sfTerminateWithCode");
      interpreter.set("status",status);
      interpreter.eval(getScript(sfTerminateWithCodeSource));
    } catch (SmartFrogResolutionException rex){
    } catch (Exception e){
      e.printStackTrace();
    }
    super.sfTerminateWith(status);
  }
}
