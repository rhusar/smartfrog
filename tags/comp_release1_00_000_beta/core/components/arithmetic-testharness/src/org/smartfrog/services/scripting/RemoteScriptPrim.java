package org.smartfrog.services.scripting;

import java.rmi.Remote;
/**
 * A remote interface to trigger evaluation of scripts.
 * @author Guillaume Mecheneau
 */

public interface RemoteScriptPrim extends Remote {
  /**
   * Evaluate the String as a beanshell script
   * @param script the script as a string.
   * @throw Exception if the execution of the script fails.
   */
  public Object eval(String script) throws Exception;
  /**
   * Bind an object to a name in the beanshell interpreter.
   * @param name the name you want the object to be called in the interpreter
   * @param obj the object you want to register in the interpreter.
   */
  public void setRemote(String name, Object obj) throws Exception;

}
