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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import static org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants.*;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Vector;

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
     	ComponentDescription dest = comp;
    	
    	CoreSolver.getInstance().setShouldUndo(true);

        String path=null;
    	String prefix=null;

    	//Get the prefix...

        try {
            prefix = (String) comp.sfResolve(PREFIX);
        } catch (SmartFrogResolutionException ignore) {
            //can be null...
        }

        if (prefix==null){
            prefix=arkey.toString();
        }

    	int cidx=prefix.lastIndexOf(":");
    	if (cidx!=-1){
    		path=prefix.substring(0,cidx);
    		prefix=prefix.substring(cidx+1);
    	}
    	
    	if (path!=null){
            try {
                dest = (ComponentDescription) comp.sfResolve(Reference.fromString(path));
            } catch (SmartFrogResolutionException e) {
                //sfLog().debug(e);
                throw relay(this.getClass(), comp, CANNOTRESOLVE+" path: "+path);
            }
    	}

    	Object extent = null;
        try {
            extent = comp.sfResolve(EXTENT);
        } catch (SmartFrogResolutionException ignore) {
            //try size before throwing...
        }

        if (extent == null) {
            try {
                 extent = comp.sfResolve(SIZE);
            } catch (SmartFrogResolutionException e) {
                throw relay(this.getClass(), comp, CANNOTRESOLVE + EXTENT + ", " + SIZE);
            }
        }

        Object generator = orgContext.get(GENERATOR);
        if (generator == null) generator = orgContext.get(TEMPLATE);

    	if (extent!=null){

    		process_array_members(dest,prefix,extent,generator);
    	} /*
    	IN THE TIDY OF THIS FUNCTION, THIS ASPECT HAS BEEN DISABLED FOR TIME BEING...

    	else {
    		boolean first=true;
    		//Now do the tagged versions...
        	Enumeration key_enum = orgContext.keys();
        	Object key=null;
        	
	    	while (key_enum.hasMoreElements()){		    		
    			key = key_enum.nextElement();
	    		//System.out.println("key:"+key);
    			if (!orgContext.sfContainsTag(key, EXTENT_TAG)) continue; //around while...
	    		extent = orgContext.get(key);
	    		if (key_enum.hasMoreElements()) {
		    		key = key_enum.nextElement();
		    		//System.out.println("key:"+key);
		    		if (!orgContext.sfContainsTag(key, GENERATOR_TAG)) throw new SmartFrogFunctionResolutionException("In Array: "+comp+", generator must follow extent...");
		    		generator = orgContext.get(key);
		    		boolean md = process_array_members(dest,prefix,extent,generator);
		    		if (md && !first)  throw new SmartFrogFunctionResolutionException("In Array: "+comp+", multi-dimensional arrays can not define multiple extents...");
		    		first=false;
	    		} 
	    	}   
	    	if (first) throw new SmartFrogFunctionResolutionException("In Array: "+comp+", extent & generator must follow prefix...");

    	}*/
    	
    	//Set sfFunctionClass to "done"
    	orgContext.put(FunctionClassStatus, "done");
        orgContext.remove(GENERATOR);
        orgContext.remove(TEMPLATE); //this is the future...
        orgContext.remove(FunctionClassEvalEarly);
        orgContext.remove(FunctionClassReturnEarly);
    	
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
    		int ext_int = (Integer) extent + idx;
    		for (int i=idx; i<ext_int; i++) putArrayEntry(dest, prefix_s+i, generator, i);
    		idx=ext_int;
    	} else if (extent instanceof Vector){
    		Vector ext_vec = (Vector)extent;
    		
    		//Is it a multi-dimensional array?
    		if (ext_vec.get(0) instanceof String){
    			//no...
    			for (Object suff: ext_vec) {
        			if (!(suff instanceof String)) throw relay(this.getClass(), comp, VECTOREXTENTSTRING);
        			putArrayEntry(dest, prefix_s+suff, generator, suff);
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
    	} else throw relay(this.getClass(), comp, EXTENTTYPE);    	
    	return md;
    }
    
    /**
     * Gets first index of array, given extent vector
     * @param ref_vec extent vector
     * @return first index
     * @throws SmartFrogFunctionResolutionException
     */
    @SuppressWarnings("unchecked")
    private Vector get_first_el_idx(Vector ref_vec) throws SmartFrogFunctionResolutionException{
    	Vector el_idx = new Vector();
    	for (Object ref : ref_vec){
    		if (ref instanceof Integer){
    			el_idx.add(0);
    		} else if (ref instanceof Vector){
    			el_idx.add(((Vector)ref).get(0));
    		} else throw relay(this.getClass(), comp, BADLYFORMEDEXTENT);
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
    @SuppressWarnings("unchecked")
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
	    		   int ref_int = (Integer)ref;
	    		   int next_int = (Integer)el_idx.get(idx) + 1;
	    		   
	    		   if (ref_int>next_int) {
	    			   next=true; //found...
	    			   next_idx.add(next_int);
	    		   } else {
	    			   next_idx.add(0);
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
	    		   
	    		   
	    	   } else throw relay(this.getClass(), comp, BADLYFORMEDEXTENT);
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
    	
    	Context generator_context = generator_cd.sfContext();

    	if (el_idx instanceof Vector) {  //Multi-dimensional...
    	    Vector el_vec = (Vector) el_idx;
	    	for (int i=0; i<el_vec.size(); i++) {
	    		Object suff = el_vec.get(i);
	    		el+="_"+suff;
	    		generator_context.put(INDEX+i, suff);
	    	}
    	} else generator_context.put(INDEX, el_idx);
    	
    	generator_context.put(TAG, el);
    	
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
