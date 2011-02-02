/** (C) Copyright 2007 Hewlett-Packard Development Company, LP

 Disclaimer of Warranty

 The Software is provided "AS IS," without a warranty of any kind. ALL
 EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
 EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
 not undergone complete testing and may contain errors and defects. It
 may not function properly and is subject to change or withdrawal at
 any time. The user must assume the entire risk of using the
 Software. No support or maintenance is provided with the Software by
 Hewlett-Packard. Do not install the Software if you are not accustomed
 to using experimental software.

 Limitation of Liability

 TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
 OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
 THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
 ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
 SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
 BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
 HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
 ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

 */
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
      int result = ((Integer) script.eval(operationString)).intValue();
      System.out.println("--------------------------------------"
                        +"\n EVALUATOR : "
                        +"\n     - Operation: "+ operationString
                        +"\n     - from: "+ value
                        +"\n     - Result: " + result
                        +"\n------------------------------------");
      sfReplaceAttribute("result",new Integer(result));
      return result;
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
