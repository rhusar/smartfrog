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
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.DefaultParser;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.CompositeSource;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.reference.Reference;

/** 
 * Does aggregation, pasting aggregated output into contained apply references
 */
public class Aggregator extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {    	
	    java.util.Vector<CompositeSource> css = new java.util.Vector<CompositeSource>();
	    HashMap<Object, Object> others = new HashMap<Object, Object>();
	
	    //System.out.println("Getting sources...");
	    
	    Constraint.getAggregateSources(comp, css);
	    
	    //System.out.println("Got sources...");
	    
	    CoreSolver.getInstance().setShouldUndo(true);
    	
	    for (int i=0; i<css.size(); i++){
	    	CompositeSource cs = css.get(i);
	    	
	    	//System.out.println("****"+cs);
	    	
	    	Constraint.extractArgumentsFromSource(cs);	 
	    	    	
	    	//Attach arguments to contained function types...
	    	Enumeration el_enum = orgContext.keys();
	    	while (el_enum.hasMoreElements()){
	    		String key = (String) el_enum.nextElement();
	    		
	    		if (key.indexOf("sf")==0) continue; //ignore sf attributes...
	    		
	    		Object val = orgContext.get(key);
	    		if (val instanceof SFApplyReference){
	    			ComponentDescription val_comp = ((SFApplyReference) val).getComponentDescription();
	    			insertArguments(cs.arguments.getArgs(), val_comp);
	    		}
	    	}

	    }
	    	
    	orgContext.put(ConstraintConstants.FunctionClassStatus, ConstraintConstants.FCS_DONE);
		CoreSolver.getInstance().setShouldUndo(false);
    	
        return comp;
    }
    
    static boolean isFreeVar(Object arg){
    	if (arg instanceof FreeVar) return true;
    	else if (arg instanceof Vector){
    		Vector argv = (Vector)arg;
    		for (int i=0;i<argv.size();i++) 
    			if (isFreeVar(argv.get(i))) return true;
    	}
    	return false;
    }
    
    static Object resolve(ComponentDescription cd, String path) throws SmartFrogFunctionResolutionException{
    	try {
    		return cd.sfResolve(Reference.fromString(path));
    	} catch (Exception e){ 
    		throw new SmartFrogFunctionResolutionException(e); 
    	}
    }
    
    void insertArguments(Vector arguments, ComponentDescription comp){
    	for (int i=0; i<arguments.size(); i++) comp.sfContext().put("unique" + DefaultParser.nextId++, arguments.get(i));
    }
    
}
