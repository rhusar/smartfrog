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
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;

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

    	Object prefix = orgContext.get("sfArrayPrefix");
    	if (prefix==null || !(prefix instanceof String)) throw new SmartFrogFunctionResolutionException("Prefix in Array: "+comp+" should be a String");
    	String prefix_s = (String)prefix;
     	
    	Object generator = orgContext.get("sfArrayGenerator");
    	Object extent = orgContext.get("sfArrayExtent");
    	
    	int idx=0;
    	Vector vec_extent = new Vector();
    	Vector gen_exts = new Vector();
    	
    	if (extent instanceof Vector) {
    		Vector extent_v = (Vector) extent;
    		for (int i=0;i<extent_v.size();i++) vec_extent.add(extent_v.get(i)); 
    	}

    	LinkResolutionState.setConstraintsShouldUndo(true);
    	
    	//Do the standard generator and extent
    	idx = processGeneratorExtent(orgContext, idx, comp, prefix_s, generator, extent);

    	if (extent!=null) {
    		orgContext.remove("sfArrayGenerator");
    		orgContext.remove("sfArrayExtent");
    	}
    	
    	generator = extent = null;
    	
    	//Now do the tagged versions...
    	Enumeration attr_enum = orgContext.keys();
    	while (attr_enum.hasMoreElements()){
    		Object attr = attr_enum.nextElement();
    		if (generator!=null){
    			try {
    				if (orgContext.sfContainsTag(attr, "sfArrayExtent")){
    					gen_exts.add(attr);
    					extent = orgContext.get(attr);
    			    	if (extent instanceof Vector) {
    			    		Vector extent_v = (Vector) extent;
    			    		for (int i=0;i<extent_v.size();i++) vec_extent.add(extent_v.get(i)); 
    			    	}    					
    					idx = processGeneratorExtent(orgContext, idx, comp, prefix_s, generator, extent);
    					generator = extent = null;
    				}
    			} catch (Exception e){/*shouldn't happen*/}
    		} else {
    			try {
    				if (orgContext.sfContainsTag(attr, "sfArrayGenerator")){
    					gen_exts.add(attr);
    					generator = orgContext.get(attr);
    				}
    			} catch (Exception e){/*shouldn't happen*/}    			
    		}
    	}
    	
    	//Remove extents and gens
    	for (int i=0;i<gen_exts.size();i++) orgContext.remove(gen_exts.get(i));     	

    	//Write aggregated extent
    	if (idx>0) orgContext.put("sfArrayExtent", new Integer(idx));
    	else if (vec_extent.size()>0) orgContext.put("sfArrayExtent", vec_extent);
    	
    	//Set sfFunctionClass to "done"
    	orgContext.put("sfFunctionClassStatus", "done");
    	
    	//remove
    	
    	LinkResolutionState.setConstraintsShouldUndo(false);
    	
        return null;
    }
    
    int processGeneratorExtent(Context orgContext, int idx, ComponentDescription comp, String prefix_s, 
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
    	
    	ComponentDescription generator_cd;
    	Object generator_copy;
    	
    	if (generator!=null && generator instanceof ComponentDescription) {
    		generator_copy = generator_cd = (ComponentDescription) ((ComponentDescription) generator).copy();
    	} else if (generator!=null && generator instanceof SFApplyReference) {
    		generator_copy = ((SFApplyReference) generator).copy();
    		generator_cd = ((SFApplyReference)generator_copy).getComponentDescription();
    	} else throw new SmartFrogFunctionResolutionException("Generator in Array: "+orgComp+" should be a ComponentDescription or SFApplyReference");
    	
    	generator_cd.sfContext().put("sfArrayIndex", el_idx);
    	generator_cd.sfContext().put("sfArrayTag", el);
 	    orgComp.sfContext().put(el, generator_copy);   
    }
    
}
