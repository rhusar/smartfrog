package org.smartfrog.regtest.arithmetic;

import java.rmi.*;
import org.smartfrog.services.scripting.*;
import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.common.*;

/**
 * A component to evaluate an arbitrary expression.
 * The variables are the actual inputs of the component.
 */
public class Evaluator extends NetElemImpl implements Remote {
  // the internal beanshell script
  private RemoteScriptPrim script;
  // the reference to look it up
  private static Reference scriptRef = new Reference(ReferencePart.here("script"));

  // the operation to evaluate
  String operationString;

  public Evaluator() throws java.rmi.RemoteException {}
/**
 * Return the value of the operation evaluation if possible.
 * Fails if some of the values (inputs) have not been initialized, or if the
 * result of the operation is not an integer. Passes on the input value in this case.
 */
  public int evaluate(String from, int value) {
    try {
      script.setRemote(from,new Integer(value));
      return ((Integer) script.eval(operationString)).intValue();
    } catch (Exception ex){
    }
    return value;
  }
  public void sfDeploy() throws SmartFrogException, RemoteException {
    super.sfDeploy();
    script = (RemoteScriptPrim)sfResolve(scriptRef);
    operationString = (String) sfResolve("operation");
  }
}
