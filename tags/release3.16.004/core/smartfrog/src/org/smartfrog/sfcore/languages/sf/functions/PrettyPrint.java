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
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.ComponentResolution;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.CompositeSource;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Pretty prints array attribute aggregations
 */
public class PrettyPrint extends BaseFunction implements MessageKeys {
		
	
	private int idx=0;
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	
    	java.util.Vector<CompositeSource> css = new java.util.Vector<CompositeSource>();
	    Constraint.getCompositeSources(comp, css, null, true);
	    
	    CoreSolver.getInstance().setShouldUndo(true);
    	
	    for (int i=0; i<css.size(); i++){
	    	CompositeSource cs = css.get(i);
	    	Constraint.extractArgumentsFromSource(cs);	 
	    	orgContext.put(cs.key, cs.arguments.toFullVector());
	    }
	    	
    	orgContext.put("sfFunctionClassStatus", "done");
		CoreSolver.getInstance().setShouldUndo(false);
    	return comp;
    }
         
}
