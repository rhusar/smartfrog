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
public class ForAllExists extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	ComponentDescription comp = context.getOriginatingDescr();
    	Context orgContext = comp.sfContext();
    	
    	boolean forall = ((String) orgContext.get("sfFunctionQual")).equals("forall");
    	
    	Object binOp = orgContext.get("sfAEOperator");
    	if (binOp==null || !(binOp instanceof SFApplyReference)) throw new SmartFrogFunctionResolutionException("sfAEOperator should be set/ be a Function in context: "+orgContext);
    	SFApplyReference binOp_ar = (SFApplyReference) binOp;
    	ComponentDescription binOp_cd = (ComponentDescription) binOp_ar.getComponentDescription();
    	    
    	Object left = orgContext.get("sfAELeftArg");
    	Object right = null;
    	if (left==null) {
    		right = orgContext.get("sfAERightArg");
    		if (right==null) throw new SmartFrogFunctionResolutionException("sfAELeftArg or sfAERightArg should be set in context: "+orgContext);
    	}
    		
       	//Process attributes, either constraint goals or other...
    	Enumeration attr_enum = orgContext.keys();
    	
    	if (right!=null) binOp_cd.sfContext().put("right", right);
    	else binOp_cd.sfContext().put("left", left);
    	
    	while (attr_enum.hasMoreElements()){
    		Object key = attr_enum.nextElement();
    		if (key.equals("sfFunctionClass") || key.equals("sfAEOperator") || key.equals("sfAELeftArg") || key.equals("sfAERightArg")) continue;
    		
    		Object val = orgContext.get(key);	
        	if (right!=null) binOp_cd.sfContext().put("left", val);
        	else binOp_cd.sfContext().put("right", val);
    		
    		Object res;
    		try {
    			res = binOp_ar.resolve(comp, 0);	
    		} catch (Exception e) { throw new SmartFrogFunctionResolutionException("Can not apply sfAEOperator in context: "+orgContext); }    		
    		if (!(res instanceof Boolean)) throw new SmartFrogFunctionResolutionException("Not a boolean sfAEOperator in context: "+orgContext); 
    		
    		if (forall && !((Boolean) res).booleanValue() || 
    		    !forall && ((Boolean) res).booleanValue()) return res;
    		
    	}
    
    	if (forall) return new Boolean(true);
    	else return new Boolean(false);
    }
}
