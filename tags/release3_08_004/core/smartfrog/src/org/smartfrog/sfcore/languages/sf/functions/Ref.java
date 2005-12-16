
package org.smartfrog.sfcore.languages.sf.functions;

import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.languages.sf.functions.BaseFunction;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the unary operator not that negates its boolean parameter.
 * The parameter attribute is "data" and an exception will be thrown if it
 * doesn't exist or is not boolean
 */
public class Ref extends BaseFunction implements PhaseAction {
    boolean lazy;
    String reference = "";

    /**
     * Takes the inputs and creates a reference and resolves it if the lazy attribute is false
     * @return Either the reference, or the resolved reference if the lazy attribute is false
     * @throws SmartFrogCompileResolutionException if any error occurs in evalatnig the reference or resolving it
     */
    protected Object doFunction() throws SmartFrogCompileResolutionException {
	Object val;
	Reference r;
	try {
	    try {
		reference = (String) context.get("reference");
	    } catch (ClassCastException e) {
		reference = null;
	    }

	    try {
		lazy = ((Boolean) context.get("lazy")).booleanValue();
	    } catch (Exception e) {
		lazy = false;
	    }

	    r = new Reference(reference, true);
	    if (lazy) {
		r.setEager(false);
		val = r;
	    } else {
		r.setEager(true);
		val = component.sfResolve(r);
	    }
	} catch (Exception e) {
	    throw new SmartFrogCompileResolutionException("Error resolving Ref function" , e);
	}
	return val;
    }
}
