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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.CompositeSource;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Does dynamic policy evaluation...
 */
public class DynamicPolicyEvaluation extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
     	Object retval=null;
    	synchronized(CoreSolver.getInstance()){
  		   retval=doFunctionWkr();
    	}
    	return retval;
    }
  	  
    protected Object doFunctionWkr() throws SmartFrogFunctionResolutionException {	
    	    	
    	//Need to ascertain whether we do this or not
    	ComponentDescription comp = context.getOriginatingDescr();
    	context = comp.sfContext();
    	
    	//Check the guards on the policy evaluation...
    	//(1) Explicit guard
    	Boolean guard = null;
    	try { guard = (Boolean) comp.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.GUARD)));}
    	catch (ClassCastException cce){throw new SmartFrogFunctionResolutionException("Guard in DynamicPolicyEvaluation: "+comp+" should be a Boolean");}
    	catch (Exception e){/*Intentionally do nothing*/}
    	if (guard!=null && !guard.booleanValue()) return guard;
    	   	
    	//Check the guards on the policy evaluation...
    	//(1) Explicit guard
    	ComponentDescription guards = null;
    	try { guards = (ComponentDescription) comp.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.GUARDS)));}
    	catch (ClassCastException cce){throw new SmartFrogFunctionResolutionException("Guards in DynamicPolicyEvaluation: "+comp+" should be a ComponentDescription");}
    	catch (Exception e){/*Intentionally do nothing*/}
    	if (guards!=null){
    	   Enumeration gnum = guards.sfContext().keys();
    	   while (gnum.hasMoreElements()){
    		    guard = null;
    	    	try { guard = (Boolean) comp.sfResolve(new Reference(ReferencePart.here(gnum.nextElement())));}
    	    	catch (ClassCastException cce){throw new SmartFrogFunctionResolutionException("Guard in DynamicPolicyEvaluation: "+guards+" should be a Boolean");}
    	    	catch (Exception e){/*Intentionally do nothing*/}
    	    	if (guard!=null && !guard.booleanValue()) return guard;
    	   }
    	}
    	    	    
    	//OK, so we are allowed to evaluate the DPE...
    	try {
	    	Enumeration e = context.keys();
	    	while (e.hasMoreElements()) {
	    		Object key = e.nextElement();
	    		Object val = comp.sfResolve(key.toString());
	    		if (val!=null) context.put(key, val);
	    		
	    	}
    	} catch (SmartFrogException e){/*Shouldn't happen*/}
    	
    	//Let's finish with a few effects...
    	
    	try { comp.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.EFFECTS)));}
    	catch (SmartFrogResolutionException sfre){/*Intentionally do nothing*/}
    	    	    	
        return new Boolean(true);
    }
           
}
