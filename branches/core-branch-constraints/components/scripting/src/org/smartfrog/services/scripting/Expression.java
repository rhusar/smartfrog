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
 */
public class Expression extends BaseFunction {
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
