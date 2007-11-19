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
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.functions.Aggregator.AggregatorReverseSourcePath;
import org.smartfrog.sfcore.languages.sf.functions.Aggregator.AggregatorSourcePath;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;

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
    	Vector autos = new Vector();
    	//Vector reg_logic = new Vector();	
    	
    	/**
    	 * The context pertaining to the Constraint
    	 */
    	ComponentDescription comp = context.getOriginatingDescr();
    	Context orgContext = comp.sfContext();
    	
    	//UserVars?
    	boolean isuservars=false;
    	
    	
    	//First off, am I an aggregated constraint?
    	Object aggregated = orgContext.get("sfConstraintAggregated");
    	if (aggregated==null || !(aggregated instanceof Boolean)) throw new SmartFrogFunctionResolutionException("sfAggregatedConstraintSource in AggregatedConstraint: "+comp+" must be a String");
    	boolean aggregated_b = ((Boolean) aggregated).booleanValue();
    	
    	if (aggregated_b){ 
			LinkResolutionState.setConstraintsShouldUndo(true);
			Enumeration attr_enum = orgContext.keys();
			
			int acs_len = "sfAggregatedConstraintSource".length();
			
			while (attr_enum.hasMoreElements()){
				String key = (String) attr_enum.nextElement();
				
				if (key.indexOf("sfAggregatedConstraintSource")!=0) continue;
				
				String suff = key.substring(acs_len);
					
			   	Object val = comp.sfContext().get(key);
		    	if (!(val instanceof String)) throw new SmartFrogFunctionResolutionException("sfAggregatedConstraintSource in AggregatedConstraint: "+comp+" must be a String");
		    	String val_s = (String) val;
			 
		    	Vector ac_values = Aggregator.extractArgumentsFromSource(comp, suff, new AggregatorSourcePath(val_s));	
			//System.out.println(ac_values.toString());
		    	comp.sfContext().put("sfAggregatedConstraintVals"+suff, ac_values);
			}
			LinkResolutionState.setConstraintsShouldUndo(false);
    	}
    	
    	//Process attributes, either constraint goals or other...
    	Enumeration attr_enum = orgContext.keys();
    	while (attr_enum.hasMoreElements()){
    		Object key = attr_enum.nextElement();
    		Object val = orgContext.get(key);
    		try {
    			if (orgContext.sfContainsTag(key, "sfConstraint")) goal_attrs.add(key);
    			else { 
    				if (val instanceof String && !isLegal((String) val)) continue;

    				attrs.add(key.toString());  
	    			values.add(val);

	    			//Set the attribute name originating this FreeVar
	    			if (val instanceof FreeVar) {
	    				FreeVar fv = (FreeVar) val;
	    				if (fv.getConsEvalKey()==null) fv.setConsEvalKey(key);
	    				//System.out.println("The Key! "+fv.getConsEvalKey());
	    				
	    				//Make sure range is appropriated in free var
	    				fv.setRange(comp);
	    				
	    			} 
	    			if (orgContext.sfContainsTag(key, "sfConstraintAutoVar")) autos.add(key);
	    			else if (!isuservars && orgContext.sfContainsTag(key, "sfConstraintUserVar")) isuservars=true;
	    		}
    		} catch (Exception e){}
    	}
    	    	
    	//If no goals, nor other attributes, nor free vars, nothing to do... 
    	if (goal_attrs.size()==0 && autos.size()==0 && !isuservars) return null;
    	
    	//Sort the goal in lex order
    	Collections.sort(goal_attrs);   	
    	
    	//Construct goal
    	Iterator goal_iter = goal_attrs.iterator();
    	while (goal_iter.hasNext()) {
    		goal.add(orgContext.get(goal_iter.next()));
    	}    	
    	
    	//Solve goal
    	try {
    	   CoreSolver.getInstance().solve(comp, attrs, values, null, goal, autos, isuservars);
    	} catch (Exception e){ 
    		e.printStackTrace();
    	    throw new SmartFrogFunctionResolutionException("Error in solving constraints in: "+context);
    	}   
        	
    	//Have we done backtracking, need to throw!
    	Context backtracked = LinkResolutionState.getLRS().hasBacktrackedTo();
    	if (backtracked!=null) orgContext = backtracked;

    	//Mark (poss. backtracked) constraint as done...
    	LinkResolutionState.setConstraintsShouldUndo(true);
    	orgContext.put("sfFunctionClassStatus", "done");
    	LinkResolutionState.setConstraintsShouldUndo(false);
    	
    	//Finally, am I an aggregated constraint? If so, map values back...
    	if (((Boolean) orgContext.get("sfConstraintAggregated")).booleanValue()){ 
			LinkResolutionState.setConstraintsShouldUndo(true);

			attr_enum = orgContext.keys();
			int acs_len = "sfAggregatedConstraintVals".length();
			
			while (attr_enum.hasMoreElements()){
				String key = (String) attr_enum.nextElement();
				
				if (key.indexOf("sfAggregatedConstraintVals")!=0) continue;
				
				String suff = key.substring(acs_len);
				
				Object frees = orgContext.get("sfAggregatedConstraintFrees"+suff);
				if (frees==null) continue;
				
			   	Vector ac_values = (Vector) orgContext.get(key);
			   	Object source = orgContext.get("sfAggregatedConstraintSource"+suff);
			   	AggregatorReverseSourcePath asp = new AggregatorReverseSourcePath((String)source);
			
			   	ComponentDescription cd = orgContext.getOriginatingDescr();
			   	ComponentDescription source_cd;
			   	
			   	try {
		    		source_cd =  (ComponentDescription) cd.sfResolve(asp.getSource());
		    	} catch (SmartFrogResolutionException e){ 
		    		throw new SmartFrogFunctionResolutionException(e); 
		    	}
	    		
		    	//Extent
		    	Object extent = source_cd.sfContext().get("sfArrayExtent");
		    	
		    	//Prefix
		    	String prefix_s = (String) source_cd.sfContext().get("sfArrayPrefix");
		    			    	
		    	if (extent instanceof Integer){
		    		int ext_int = ((Integer)extent).intValue();
		    		for (int i=0; i<ext_int; i++){
		    			String el = prefix_s+i;
		    			replace(source_cd, el+asp.getInterPath(), asp.getKey(), ac_values.get(i));
		    		}
		    	} else if (extent instanceof Vector){
		    		Vector ext_vec = (Vector)extent;
		    		for (int i=0; i<ext_vec.size(); i++){
		    			String suff_s = (String) ext_vec.get(i);
		    			String el=prefix_s+suff_s;
		    			replace(source_cd, el+asp.getInterPath(), asp.getKey(), ac_values.get(i));
		    		}
		    	}	
	    	}
			
			LinkResolutionState.setConstraintsShouldUndo(false);
    	}
    	
    	if (backtracked!=null){
 		   LinkResolutionState.getLRS().resetDoneBacktracking();
 		   throw new SmartFrogConstraintBacktrackError();
 	    }
    
        return null;
    }
    
    /**
     * Unchecked error. Used in popping call stack to get back to linkResolve()
     * @author anfarr
     *
     */
    public class SmartFrogConstraintBacktrackError extends Error{};
    
    boolean isLegal(String val){
    	if (val.indexOf(0x21)>-1) return false;
    	if (val.indexOf(0x22)>-1) return false;
    	if (val.indexOf(0x23)>-1) return false;
    	if (val.indexOf(0x24)>-1) return false;
    	if (val.indexOf(0x25)>-1) return false;
    	if (val.indexOf(0x26)>-1) return false;
    	if (val.indexOf(0x27)>-1) return false;
    	if (val.indexOf(0x28)>-1) return false;
    	if (val.indexOf(0x29)>-1) return false;
    	if (val.indexOf(0x2A)>-1) return false;
    	if (val.indexOf(0x2B)>-1) return false;
    	if (val.indexOf(0x2C)>-1) return false;
    	if (val.indexOf(0x2D)>-1) return false;
    	if (val.indexOf(0x2E)>-1) return false;
    	if (val.indexOf(0x2F)>-1) return false;	
    	return true;
    }
    
    void replace(ComponentDescription source_cd, String interPath, String key, Object val) throws SmartFrogFunctionResolutionException{
    	ComponentDescription refined_cd;
    	try {
    		refined_cd =  (ComponentDescription) source_cd.sfResolve(interPath);
    		refined_cd.sfContext().put(key, val);		
    	} catch (SmartFrogResolutionException e){ 
    		throw new SmartFrogFunctionResolutionException("Can't replace attributes as part of AggregatedConstraint, source:"+source_cd+" interPath:"+interPath+", key:"+key+", value:"+val); 
    	}
    }
    
}
