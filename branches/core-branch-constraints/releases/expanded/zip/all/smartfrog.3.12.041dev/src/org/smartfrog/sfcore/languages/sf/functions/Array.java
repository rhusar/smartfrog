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
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Defines the Constraint function.
 */
public class Array extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	ComponentDescription comp = context.getOriginatingDescr();
    	Context orgContext = comp.sfContext();

    	int idx=0;
    	Vector vec_extent = new Vector();
    	
    	CoreSolver.getInstance().setShouldUndo(true);
    	
    	Object prefix = orgContext.get("sfArrayPrefix");
    	if (prefix==null || !(prefix instanceof String)) throw new SmartFrogFunctionResolutionException("Prefix in Array: "+comp+" should be a String");
    	String prefix_s = (String)prefix;
     	
    	
    	Object generator = orgContext.get("sfArrayGenerator");
    	Object extent = orgContext.get("sfArrayExtent");
    	
    	if (generator!=null || extent!=null){
    	
	    	if (extent instanceof Vector) {
	    		Vector extent_v = (Vector) extent;
	    		for (int i=0;i<extent_v.size();i++) vec_extent.add(extent_v.get(i)); 
	    	}
		    	
	    	//Do the standard generator and extent
	    	idx = processExtentGenerator(idx, comp, prefix_s, generator, extent);
			
	    	generator = extent = null;
    	
    	}
    	
    	//Now do the tagged versions...
    	Enumeration attr_enum = orgContext.keys();
    	while (attr_enum.hasMoreElements()){
    		Object attr = attr_enum.nextElement();
    		try {
				if (orgContext.sfContainsTag(attr, "sfArrayExtentGenerator")){
					Object val = orgContext.get(attr);
					if (!(val instanceof Vector)) throw new SmartFrogFunctionResolutionException("sfArrayExtentGenerator-tagged attributes in Array: "+comp+" must have Vector values.");
					Vector val_vec = (Vector) val;
					if (val_vec.size()!=2) throw new SmartFrogFunctionResolutionException("sfArrayExtentGenerator-tagged attributes in Array: "+comp+" must have Vector values with 2 members."); 
					extent = val_vec.get(0);
					generator = val_vec.get(1);		
					if (!(generator instanceof String)) throw new SmartFrogFunctionResolutionException("sfArrayExtentGenerator-tagged attributes in Array: "+comp+" generator: "+ generator+" must be a String"); 
		    		Reference gen_ref = Reference.fromString((String)generator);
		        	generator = comp.sfResolve(gen_ref);
					
					if (extent instanceof Vector) {
			    		Vector extent_v = (Vector) extent;
			    		for (int i=0;i<extent_v.size();i++) vec_extent.add(extent_v.get(i)); 
			    	}    					
					idx = processExtentGenerator(idx, comp, prefix_s, generator, extent);
					generator = extent = null;
				}
			} catch (Exception e){/*shouldn't happen*/}    		
    	}
    	
    	//Write aggregated extent
    	if (idx>0) orgContext.put("sfArrayExtent", new Integer(idx));
    	else if (vec_extent.size()>0) orgContext.put("sfArrayExtent", vec_extent);
    	
    	//Set sfFunctionClass to "done"
    	orgContext.put("sfFunctionClassStatus", "done");
    	
    	CoreSolver.getInstance().setShouldUndo(false);
    	
        return null;
    }
        
    int processExtentGenerator(int idx, ComponentDescription comp, String prefix_s, 
    		Object generator, Object extent) throws SmartFrogFunctionResolutionException {
    	if (extent!=null){
    		if (extent instanceof Integer){
        		int ext_int = ((Integer)extent).intValue() + idx;
        		for (int i=idx; i<ext_int; i++) putArrayEntry(comp, prefix_s+i, generator, new Integer(i));
        		idx=ext_int;
        	} else if (extent instanceof Vector){
        		Vector ext_vec = (Vector)extent;
        		for (int i=0; i<ext_vec.size(); i++) {
        			Object suff = ext_vec.get(i);
        			if (!(suff instanceof String)) throw new SmartFrogFunctionResolutionException("Vector extent in Array: "+comp+" should be comprised of Strings");
        			putArrayEntry(comp, prefix_s+suff, generator, (String) suff);
        		}
        	} else throw new SmartFrogFunctionResolutionException("Extent in Array: "+comp+" should be an Integer or a Vector");    	
    	}
    	return idx;
    }
    
    void putArrayEntry(ComponentDescription orgComp, String el, Object generator, Object el_idx) throws SmartFrogFunctionResolutionException{
    	
    	ComponentDescription generator_cd=null;
    	Object generator_copy=null;
    	
    	if (generator instanceof ComponentDescription) {
    		generator_copy = generator_cd = (ComponentDescription) ((ComponentDescription) generator).copy();
    	} else if (generator instanceof SFApplyReference) {
    		generator_copy = ((SFApplyReference) generator).copy();
    		generator_cd = ((SFApplyReference)generator_copy).getComponentDescription();
    	} 
    	
    	generator_cd.sfContext().put("sfArrayIndex", el_idx);
    	generator_cd.sfContext().put("sfArrayTag", el);
    	generator_cd.sfContext().remove("sfIsGenerator");
 	    orgComp.sfContext().put(el, generator_copy);   
    }
    
}
