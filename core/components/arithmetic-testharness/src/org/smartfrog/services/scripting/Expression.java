package org.smartfrog.services.scripting;

import org.smartfrog.sfcore.languages.sf.functions.*;
import org.smartfrog.sfcore.languages.sf.*;
import org.smartfrog.sfcore.parser.*;

import bsh.Interpreter;
import bsh.EvalError;
import java.util.*;

/**
 * A function to evaluate an arbitrary arithmetic expression.
 * The result of the evaluation will be used as the function's value.
 *
 * @author Guillaume Mecheneau
 */
public class Expression extends BaseFunction implements PhaseAction {
  static String expString = "exp";
  /** The Beanshell interpreter */
  public Interpreter interpreter = new Interpreter();

  protected Object doFunction() {
    // bind all attributes in the beanshell interpreter
    for (Enumeration e = context.keys(); e.hasMoreElements();){
      String attName = (String) e.nextElement();
      try {
        Object value = context.get(attName);
        // if the value is a string & not the final expression , try to evaluate
        if ((expString.compareToIgnoreCase(attName) !=0 ) && (value instanceof String) ) {
          try {
            if (interpreter.eval((String) value)!=null)
              value =(interpreter.eval((String) value));
          } catch (EvalError ee) {
          // on failure do nothing : remains as is...
          }
        }
        interpreter.set(attName,value);
      } catch (EvalError ee){
        System.out.println( "Error setting " + attName + " in beanshell interpreter: "+ee);
      }
    }

    // return the evaluation of 'exp'
    try {
      return interpreter.eval((String)interpreter.get(expString));
    } catch (Exception e) {
      if (context.get(expString) == null)
        System.out.println( "Parsing Error : specify an expression in Expression function " );
      else {
        System.out.println( "Parsing Error in function Expression" );
        e.printStackTrace();
      }
    }
    return null;
  }
}
