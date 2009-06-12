
package org.smartfrog.sfcore.languages.sf.functions;

import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the unary operator not that negates its boolean parameter.
 * The parameter attribute is "data" and an exception will be thrown if it
 * doesn't exist or is not boolean
 */
public class Ref extends BaseFunction {
    boolean lazy;
    String reference = "";
    
    /**
     * Takes the inputs and creates a reference and resolves it if the lazy attribute is false
     * @return Either the reference, or the resolved reference if the lazy attribute is false
     * @throws SmartFrogFunctionResolutionException if any error occurs in evalatnig the reference or resolving it
     */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
	    Object val = null;
	    Reference r;
	    
	    try {
	    	reference = (String) context.get("reference");
	    } catch (ClassCastException e) {
	        throw new SmartFrogFunctionResolutionException(e);
	    }
	    
	    try {
	    	lazy = ((Boolean) context.get("lazy")).booleanValue();
	    } catch (Exception e) {
	    	lazy = false;
	    }
	    
	    Object returnNULLOnException = context.get("returnNULLOnException");
	    
	    try { 
	    	r = new Reference(reference, true);
	    } catch (Exception e) {
	        throw new SmartFrogFunctionResolutionException("Error constructing reference in Ref function" , e);
	    }	
	    
	    if (lazy) {
	    	r.setEager(false);
	    	val = r;
	    } else {
	    	r.setEager(true);
	    	try {
	    		if (rr != null) {
	    			val = rr.sfResolve(r);
	    		}
	    		else if (rrr != null) val = rrr.sfResolve(r);
	    	} catch (Exception e){
	    		if (returnNULLOnException!=null) val=SFNull.get();
	    		else throw new SmartFrogFunctionResolutionException("Error resolving reference in Ref function" , e);
	    	}
	    }
	    return val;
   }
}
