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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.LinkResolutionState;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;

/**
 * Defines the Constraint function.
 */
public class Constraint extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	//If constraint resolution is not pertinent or possible return
    	if (!LinkResolutionState.getConstraintsPossible()) return null; 
    	    	
    	/**
    	 * Records the attributes used for consraint goal preds 
    	 */
    	Vector goal_attrs = new Vector();
    	
    	/**
    	 * Record the constraint goals to be processed
    	 */
    	Vector goal = new Vector();
    	/**
    	 * Records the attributes other than constraint goal preds
    	 */
    	Vector attrs = new Vector();
    	/**
    	 * Record the values of the attributes other than constraint goal preds
    	 */
    	Vector values = new Vector();
    	//Vector reg_logic = new Vector();	
    	
    	/**
    	 * The context pertaining to the Constraint
    	 */
    	Context orgContext = context.getOriginatingContext();
    	
    	//Process attributes, either constraint goals or other...
    	Enumeration attr_enum = orgContext.keys();
    	while (attr_enum.hasMoreElements()){
    		Object key = attr_enum.nextElement();
    		Object val = orgContext.get(key);
    		try {
    			if (orgContext.sfContainsTag(key, "sfConstraint")) goal_attrs.add(key);
    			else { 
	    			attrs.add(key.toString());  //making sure it is a string...
	    			values.add(val);
	    			//Set the attribute name originating this FreeVar
	    			if (val instanceof FreeVar) {
	    				FreeVar fv = (FreeVar) val;
	    				if (fv.getConsEvalKey()==null) fv.setConsEvalKey(key);
	    				//System.out.println("The Key! "+fv.getConsEvalKey());
	    			} 
	    		}
    		} catch (Exception e){}
    	}
    	
    	//If no goals, nor other attributes, nor free vars, nothing to do... 
    	if (goal_attrs.size()==0 || attrs.size()==0) return null;
    	
    	//Sort the goal in lex order
    	Collections.sort(goal_attrs);   	
    	
    	//Construct goal
    	Iterator goal_iter = goal_attrs.iterator();
    	while (goal_iter.hasNext()) {
    		goal.add(orgContext.get(goal_iter.next()));
    	}    	
    	
    	//Solve goal
    	try {
    	   CoreSolver.getInstance().solve(orgContext, attrs, values, null, goal);
    	} catch (Exception e){ 
    		e.printStackTrace();
    	    throw new SmartFrogFunctionResolutionException("Error in solving constraints in: "+context);
    	}   
        	
    	//Have we done backtracking, need to throw!
    	if (LinkResolutionState.getLRS().hasDoneBacktracking()){
 		   LinkResolutionState.getLRS().resetDoneBacktracking();
 		   throw new SmartFrogConstraintBacktrackError();
 	    }
    
    	//Mark constraint as done...
    	//Do not do this in backtracking, as the originating Constraint
    	//may not be done yet in backtracking...
    	LinkResolutionState.setConstraintsShouldUndo(true);
    	orgContext.put("sfFunctionClass", "done");
    	LinkResolutionState.setConstraintsShouldUndo(false);

        return null;
    }
    
    /**
     * Unchecked error. Used in popping call stack to get back to linkResolve()
     * @author anfarr
     *
     */
    public class SmartFrogConstraintBacktrackError extends Error{};
}
