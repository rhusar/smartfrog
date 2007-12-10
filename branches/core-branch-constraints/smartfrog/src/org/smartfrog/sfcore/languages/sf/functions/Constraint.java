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
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;
import org.smartfrog.sfcore.reference.Reference;

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
			
			while (attr_enum.hasMoreElements()){
				Object attr = attr_enum.nextElement();
			
	    		try {
					if (orgContext.sfContainsTag(attr, "sfAggregatedConstraintSource")){
						
						Object val = orgContext.get(attr);
						if (!(val instanceof Vector)) throw new SmartFrogFunctionResolutionException("sfAggregatedConstraintSource-tagged attributes in AggregatedConstraint: "+comp+" must have Vector values.");
						Vector val_vec = (Vector) val;
						if (val_vec.size()!=3) throw new SmartFrogFunctionResolutionException("sfAggregatedConstraintSource-tagged attributes in AggregatedConstraint: "+comp+" must have Vector values with 2 members."); 
						Object arraysource = val_vec.get(0);
						
				    	if (arraysource==null || !(arraysource instanceof String)) throw new SmartFrogFunctionResolutionException("First argument of an sfAggregatedConstraintSource tagged attribute in: "+comp+" should be a STRING which resolves to a Component Description");    	    	
                        Object _arraysource = comp.sfResolve(Reference.fromString((String)arraysource));
				    	if (_arraysource==null || !(_arraysource instanceof ComponentDescription)) throw new SmartFrogFunctionResolutionException("First argument of an sfAggregatedConstraintSource tagged attribute in: "+comp+" should be a String which resolves to a COMPONENT DESCRIPTION");    	    	
				    	
				    	
                        ComponentDescription source_cd = (ComponentDescription) _arraysource;
                        
				    	Object sourceClass = source_cd.sfContext().get("sfFunctionClass");
				    	if (!sourceClass.equals("org.smartfrog.sfcore.languages.sf.functions.Array"))  throw new SmartFrogFunctionResolutionException("First argument of an sfAggregatedConstraintSource tagged attribute in: "+comp+" must have orginated as an Array type");    	    	
				    	
						Object attrpath = val_vec.get(1);
						if (attrpath==null || !(attrpath instanceof String)) throw new SmartFrogFunctionResolutionException("Second argument of an sfAggregatedConstraintSource tagged attribute in: "+comp+" should be a STRING");    	        	    	
				    	String path_s = ":"+attrpath;
						
						Object newattr = val_vec.get(2);
						if (newattr==null || !(newattr instanceof String)) throw new SmartFrogFunctionResolutionException("Third argument of an sfAggregatedConstraintSource tagged attribute in: "+comp+" should be a STRING");    	        	    	
				    	String newattr_s = (String)newattr;

				    	Vector ac_values = new Vector();
				    	
				    	boolean freevars = Aggregator.extractArgumentsFromSource(comp, source_cd, path_s, ac_values);	
				    	
				    	comp.sfContext().put(newattr_s, ac_values);
				    	if (freevars) comp.sfContext().sfAddTag(newattr_s, "sfAggregatedConstraintFreeVars");
					
					}
				} catch (Exception e){/*shouldn't happen*/}    		

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
    		} catch (Exception e){/**Shouldn't happen**/}
    	}
    	    	    	
    	//Sort the goal in lex order
    	Collections.sort(goal_attrs);   	
    	
    	//Construct goal
    	Iterator goal_iter = goal_attrs.iterator();
    	while (goal_iter.hasNext()) {
    		goal.add(orgContext.get(goal_iter.next()));
    	}    	
    	
    	//Add empty goal if no goal...
    	if (goal_attrs.size()==0) goal.add("true");
    	
    	//Solve goal
    	try {
    	   CoreSolver.getInstance().solve(comp, attrs, values, goal, autos, isuservars);
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
			
			while (attr_enum.hasMoreElements()){
				Object attr = attr_enum.nextElement();
				
	    		try {
	    			
	    			if (orgContext.sfContainsTag(attr, "sfAggregatedConstraintSource")){						
						Vector val = (Vector) orgContext.get(attr);
						Object arraysource = val.get(0);
						ComponentDescription source_cd = (ComponentDescription) comp.sfResolve(Reference.fromString((String)arraysource)); 
						String path_s = (String) val.get(1);
						Object newattr = val.get(2);
						
						//Manipulate path to get inter_path
						int idx = path_s.lastIndexOf(":");
						String interpath_s = "";
						if (idx!=-1){
							interpath_s = ":"+path_s.substring(0, idx);
							path_s = path_s.substring(idx+1);
						} 
						
						if (orgContext.sfContainsTag(newattr, "sfAggregatedConstraintFreeVars")){
						
							Vector ac_values = (Vector) orgContext.get(newattr);
						   						   	
					    	//Extent
					    	Object extent = source_cd.sfContext().get("sfArrayExtent");
					    	
					    	//Prefix
					    	String prefix_s = (String) source_cd.sfContext().get("sfArrayPrefix");
					    			    	
					    	if (extent instanceof Integer){
					    		int ext_int = ((Integer)extent).intValue();
					    		for (int i=0; i<ext_int; i++){
					    			String el = prefix_s+i;
					    			Object ac_val = ac_values.get(i);
					    			if (!(ac_val instanceof FreeVar)) replace(source_cd, el+interpath_s, path_s, ac_val);
					    		}
					    	} else if (extent instanceof Vector){
					    		Vector ext_vec = (Vector)extent;
					    		for (int i=0; i<ext_vec.size(); i++){
					    			String suff_s = (String) ext_vec.get(i);
					    			String el=prefix_s+suff_s;
					    			Object ac_val = ac_values.get(i);
					    			if (!(ac_val instanceof FreeVar)) replace(source_cd, el+interpath_s, path_s, ac_val);
					    		}
					    	}	
						}	
	    			}
				} catch (Exception e){}
				
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
    		refined_cd =  (ComponentDescription) source_cd.sfResolve(Reference.fromString(interPath));
    		refined_cd.sfContext().put(key, val);		
    	} catch (SmartFrogResolutionException e){ 
    		throw new SmartFrogFunctionResolutionException("Can't replace attributes as part of AggregatedConstraint, source:"+source_cd+" interPath:"+interPath+", key:"+key+", value:"+val); 
    	}
    }
    
}
