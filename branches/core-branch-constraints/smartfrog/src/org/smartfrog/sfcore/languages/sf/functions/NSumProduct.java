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

package org.smartfrog.sfcore.languages.sf.functions;

import java.util.Enumeration;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;

/**
 * Defines the Constraint function.
 */
public class NSumProduct extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    		
       	//Process attributes, either constraint goals or other...
    	Enumeration attr_enum = context.keys();

    	Object runningTot= null;
    	
    	boolean sum = ((String) context.get("sfFunctionQual")).equals("sum");
    	
    	while (attr_enum.hasMoreElements()){
    		Object key = attr_enum.nextElement();
    		if (key.equals("sfFunctionClass") || key.equals("sfFunctionQual")) continue;
    		
    		Object val = context.get(key);	

    		if (runningTot==null) runningTot=val;
    		else {
    			runningTot = (sum ? new Sum().doOperator(runningTot, val) : new Product().doOperator(runningTot, val));
    		}
    	}
    
    	return runningTot;
    }
}
