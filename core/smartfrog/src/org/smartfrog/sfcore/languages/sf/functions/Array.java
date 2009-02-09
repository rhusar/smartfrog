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
import java.util.Vector;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.ComponentResolution;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the Constraint function.
 */
public class Array extends BaseFunction implements MessageKeys {	
	
	private int idx=0;
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	Object result=null;
    	try {result=doFunctionWkr();} catch (SmartFrogContextException e){/*Shouldn't happen*/}
    	return result;
    }
    
    /**
     * Internal worker method for doFunction
     * @return result of applying function
     * @throws SmartFrogFunctionResolutionException
     * @throws SmartFrogContextException
     */
    private Object doFunctionWkr()  throws SmartFrogFunctionResolutionException, SmartFrogContextException {
     	Object dest = comp; 
    	
    	CoreSolver.getInstance().setShouldUndo(true);
    	
    	//Now do the tagged versions...
    	Enumeration key_enum = orgContext.keys();
    	
    	Object key = null; 
    	Object path=null;
    	Object prefix=null;
    	String prefix_s=null;
    	
    	//Get the prefix...
    	prefix = orgContext.get(ConstraintConstants.PREFIX);
    	if (prefix==null){
    		while (key_enum.hasMoreElements()){
	    		key = key_enum.nextElement();
	    		if (orgContext.sfContainsTag(key, ConstraintConstants.PREFIX_TAG)){
		    		prefix = orgContext.get(key);
		    		break;
	    		}
	    	}
    	}
    	
    	if (prefix==null) throw new SmartFrogFunctionResolutionException("Array: "+comp+" has no prefix");
    	if (prefix instanceof String) prefix_s= (String) prefix;
		else throw new SmartFrogFunctionResolutionException("In Array: "+comp+", prefix must be a String...");     	
    	
    	/* PATHs are not currently offered for Arrays for simplicity.  Additional measures for link resolution would need to be taken otherwise which I feel complicate matters
    	 path = orgContext.get(ConstraintConstants.PATH);
    	 
    	
    	boolean spare_key=false;
    	if (path==null){
	    	//Is there a path?
	    	if (key_enum.hasMoreElements()) {
	    		key = key_enum.nextElement();
	    		if (orgContext.sfContainsTag(key, ConstraintConstants.PATH_TAG)) path = orgContext.get(key);
	    		else spare_key=true;
	    	}    	
    	}
    	
    	//Resolve path...
    	if (path!=null && path instanceof Reference) {
    		try{dest = comp.sfResolve(((Reference)path).copyandRemoveLazy());}catch(SmartFrogResolutionException sfre){/*Shouldn't happen}
    	}*/
    	
    	Object extent = orgContext.get(ConstraintConstants.EXTENT);
    	Object generator = orgContext.get(ConstraintConstants.GENERATOR);
        	
    	if (extent!=null){
    		process_array_members(dest,prefix_s,extent,generator);
    	} else {    	
    		boolean first=true;
	    	while (key_enum.hasMoreElements()){		    		
    			key = key_enum.nextElement();
	    		//System.out.println("key:"+key);
    			if (!orgContext.sfContainsTag(key, ConstraintConstants.EXTENT_TAG)) continue; //around while...
	    		extent = orgContext.get(key);
	    		if (key_enum.hasMoreElements()) {
		    		key = key_enum.nextElement();
		    		//System.out.println("key:"+key);
		    		if (!orgContext.sfContainsTag(key, ConstraintConstants.GENERATOR_TAG)) throw new SmartFrogFunctionResolutionException("In Array: "+comp+", generator must follow extent...");     			
		    		generator = orgContext.get(key);
		    		boolean md = process_array_members(dest,prefix_s,extent,generator);
		    		if (md && !first)  throw new SmartFrogFunctionResolutionException("In Array: "+comp+", multi-dimensional arrays can not define multiple extents...");
		    		first=false;
	    		} 
	    	}   
	    	if (first) throw new SmartFrogFunctionResolutionException("In Array: "+comp+", extent & generator must follow prefix...");
    	}
    	
    	//Set sfFunctionClass to "done"
    	orgContext.put("sfFunctionClassStatus", "done");
    	
    	CoreSolver.getInstance().setShouldUndo(false);
        return comp;
    }
    
    /**
     * Adds individual array members
     * @param dest  destination for array members 
     * @param prefix_s  prefix of array members
     * @param extent  size of array
     * @param generator  template for array members
     * @return whether it is a multi-dimensional array
     * @throws SmartFrogFunctionResolutionException
     */
    private boolean process_array_members(Object dest, String prefix_s, Object extent, Object generator) throws SmartFrogFunctionResolutionException {	    
    	boolean md=false;
    	if (extent instanceof Integer){	
    		int ext_int = ((Integer)extent).intValue() + idx;
    		for (int i=idx; i<ext_int; i++) putArrayEntry(dest, prefix_s+i, generator, new Integer(i));
    		idx=ext_int;
    	} else if (extent instanceof Vector){
    		Vector ext_vec = (Vector)extent;
    		
    		//Is it a multi-dimensional array?
    		if (ext_vec.get(0) instanceof String){
    			//no...
    			for (int i=0; i<ext_vec.size(); i++) {
        			Object suff = ext_vec.get(i);
        			if (!(suff instanceof String)) throw new SmartFrogFunctionResolutionException("Vector extent in Array: "+comp+" should be comprised of Strings");
        			putArrayEntry(dest, prefix_s+suff, generator, (String) suff);
        		}
    		} else {
    			md=true;
    		   //yes...
    			//Compose initial index...
 			   Vector el_idx = null;
 			   while ((el_idx=next_el_idx(el_idx,ext_vec))!=null){
    			   putArrayEntry(dest, prefix_s, generator, el_idx);
    		   }
    		}
    	} else throw new SmartFrogFunctionResolutionException("Extent in Array: "+dest+" should be an Integer or a Vector");    	
    	return md;
    }
    
    /**
     * Gets first index of array, given extent vector
     * @param ref_vec extent vector
     * @return first index
     * @throws SmartFrogFunctionResolutionException
     */
    private Vector get_first_el_idx(Vector ref_vec) throws SmartFrogFunctionResolutionException{
    	Vector el_idx = new Vector();
    	for (int i=0; i<ref_vec.size(); i++){
    		Object ref = ref_vec.get(i);
    		if (ref instanceof Integer){
    			el_idx.add(new Integer(0));
    		} else if (ref instanceof Vector){
    			el_idx.add(((Vector)ref).get(0));
    		} else throw new SmartFrogFunctionResolutionException("In Array: "+comp+" badly formed multi-dimensional extent");
    	}
    	return el_idx;
    }
    
    /**
     * Gets next index of extent vector
     * @param el_idx  previous index
     * @param ref_vec extent vector
     * @return next index
     * @throws SmartFrogFunctionResolutionException
     */
    
    private Vector next_el_idx(Vector el_idx, Vector ref_vec) throws SmartFrogFunctionResolutionException {
    	if (el_idx==null) return get_first_el_idx(ref_vec);
    	
    	Vector next_idx = new Vector();
    	boolean next=false;
    	
    	for (idx=0;idx<ref_vec.size();idx++){
    	   Object ref =  ref_vec.get(idx);
    	   Object el = el_idx.get(idx);
    	   if (next){ //already found a next element, so suffix stays the same...
    		   next_idx.add(el);
    	   } else {
	    	   if (ref instanceof Integer){
	    		   int ref_int = ((Integer)ref).intValue();
	    		   int next_int = ((Integer)el_idx.get(idx)).intValue() + 1;
	    		   
	    		   if (ref_int>next_int) {
	    			   next=true; //found...
	    			   next_idx.add(new Integer(next_int));
	    		   } else {
	    			   next_idx.add(new Integer(0));
	    		   }
	    		   
	    		   
	    	   } else if (ref instanceof Vector){
	    		   Vector ref_v = (Vector) ref;
	    		   int ref_size = ref_v.size();
	    		   Object prev = el_idx.get(idx);
	    		   int next_entry = ref_v.indexOf(prev)+1;
	    		   
	    		   
	    		   if (next_entry!=ref_size){
	    			   next=true; //found...
	    			   next_idx.add(ref_v.get(next_entry));
	    		   } else {
	    			   next_idx.add(ref_v.get(0));
	    		   }
	    		   
	    		   
	    	   } else throw new SmartFrogFunctionResolutionException("In Array: "+comp+" badly formed multi-dimensional extent");
    	   }
    	}
    	if (next) return next_idx;
    	else return null;
    }
    
    /**
     * Puts a member entry into array   
     * @param dest  destination of member put
     * @param el  prefix of name of member attribute to put
     * @param generator  member template to put 
     * @param el_idx index of member in array 
     */
    private void putArrayEntry(Object dest, String el, Object generator, Object el_idx) {
     	ComponentDescription generator_cd=null;
    	Object generator_copy=null;
    	
    	
    	if (generator instanceof ComponentDescription) {
    		generator_copy = generator_cd = (ComponentDescription) ((ComponentDescription) generator).copy();
    	} else if (generator instanceof SFApplyReference) {
    		generator_copy = ((SFApplyReference) generator).copy();
    		generator_cd = ((SFApplyReference)generator_copy).getComponentDescription();
    	}    	
    	
    	
    	if (el_idx instanceof Vector) {  //Multi-dimensional...
    	    Vector el_vec = (Vector) el_idx;
	    	for (int i=0; i<el_vec.size(); i++) {
	    		Object suff = el_vec.get(i);
	    		el+="_"+suff;
	    		generator_cd.sfContext().put(ConstraintConstants.INDEX+i, suff);
	    	}
    	} else generator_cd.sfContext().put(ConstraintConstants.INDEX, el_idx);
    	
    	generator_cd.sfContext().put(ConstraintConstants.TAG, el);
    	generator_cd.sfContext().remove("sfIsArrayGenerator");
    	
    	if (dest instanceof ComponentDescription){
    		generator_cd.setParent((ComponentDescription) dest);
    		try{((ComponentDescription) dest).sfAddAttribute(el, generator_copy);} catch (Exception e){/*Shouldn't happen*/}    		
    	} /*COMMENTED OUT FOR NOW AS FOR TIME BEING ARRAYS ARE STRICTLY PARSE TIME CREATURES
    	   else /*Assume Prim* {    
    		generator_cd.setPrimParent((Prim) dest);
    		try {((Prim)dest).sfAddAttribute(el, generator_copy);} catch (Exception e){/*Shouldn't happen*}
    	} */      	
    }
         
}
