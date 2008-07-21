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
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.DefaultParser;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.reference.Reference;

/** 
 * Defines the Constraint function.
 */
public class Aggregator extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction()  throws SmartFrogFunctionResolutionException {
    	ComponentDescription comp = context.getOriginatingDescr();
    	Context orgContext = comp.sfContext();
    	ComponentDescription source_cd;
    	Object source_obj;
    	Reference src_ref;
    	String path_s;

    	//Hybrid attribute?
    	Object arraypath = orgContext.get("sfAggregatorPath"); 
    	if (arraypath!=null){
    		//then it should be valid
    		if (!(arraypath instanceof String)) throw new SmartFrogFunctionResolutionException("AggregatorPath in Aggregator: "+comp+" should be a String");    	    	
    		String arraypath_s = (String) arraypath;
    		int src_idx = arraypath_s.indexOf(":");
    		if (src_idx<0) throw new SmartFrogFunctionResolutionException("AggregatorPath in Aggregator: "+comp+" is incorrectly formatted");    	
    		String src_s = arraypath_s.substring(0, src_idx);
    		try {
    		src_ref = Reference.fromString(src_s);
    		} catch (SmartFrogResolutionException e){ throw new SmartFrogFunctionResolutionException("Cannot construct reference for source array from AggregatorPath in Aggregator: "+comp);  }
    		path_s = arraypath_s.substring(src_idx);
    		
    		//System.out.println("Source:"+src_s+", Path:"+path_s);
    	} else {
    		//otherwise we look for split version...
        	//Source
        	Object arraysource = orgContext.get("sfAggregatorArraySource"); 
        	if (arraysource==null || !(arraysource instanceof Reference)) throw new SmartFrogFunctionResolutionException("ArraySource in Aggregator: "+comp+" should be a REFERENCE to a ComponentDescription");    	    	
        	src_ref = (Reference) arraysource;
    		
        	//Attr Path
        	Object attrpath = orgContext.get("sfAggregatorAttributePath"); 
        	if (attrpath==null || !(attrpath instanceof String)) throw new SmartFrogFunctionResolutionException("AttributePath in Aggregator: "+comp+" should be a String");    	    	
        	path_s = ":"+attrpath;
    	}   	
    	
    	try {
    		source_obj = comp.sfResolve(src_ref);
    	} catch (Exception e){ throw new SmartFrogFunctionResolutionException(e); }
    	if (source_obj==null || !(source_obj instanceof ComponentDescription)) throw new SmartFrogFunctionResolutionException("ArraySource in Aggregator: "+comp+" should resolve to a COMPONENT DESCRIPTION");    	    	
    	source_cd = (ComponentDescription) source_obj;
    	
    	
    	Vector arguments = new Vector();
    	extractArgumentsFromSource(comp, source_cd, path_s, arguments);
    	  	
    	CoreSolver.getInstance().setShouldUndo(true);
    	
    	//Attach arguments to contained function types...
    	Enumeration el_enum = orgContext.keys();
    	while (el_enum.hasMoreElements()){
    		String key = (String) el_enum.nextElement();
    		
    		if (key.indexOf("sf")==0) continue; //ignore sf attributes...
    		
    		Object val = orgContext.get(key);
    		if (val instanceof SFApplyReference){
    			ComponentDescription val_comp = ((SFApplyReference) val).getComponentDescription();
    			insertArguments(arguments, val_comp);
    		}
    	}

    	orgContext.put("sfFunctionClassStatus", "done");
		CoreSolver.getInstance().setShouldUndo(false);
    	
        return null;
    }
    
   static boolean extractArgumentsFromSource(ComponentDescription comp, ComponentDescription source_cd, String path_s, Vector arguments) throws SmartFrogFunctionResolutionException {  	
    	Object sourceClass = source_cd.sfContext().get("sfFunctionClass");
    	if (!sourceClass.equals("org.smartfrog.sfcore.languages.sf.functions.Array"))  throw new SmartFrogFunctionResolutionException("Source in Aggregator: "+comp+" must have orginated as an Array type"); 
    	
    	//Extent
    	Object extent = source_cd.sfContext().get("sfArrayExtent");
    	
    	//Prefix
    	String prefix_s = (String) source_cd.sfContext().get("sfArrayPrefix");
    	    	
    	boolean freevar=false;
    	
    	if (extent instanceof Integer){
    		int ext_int = ((Integer)extent).intValue();
    		for (int i=0; i<ext_int; i++){
    			String el = prefix_s+i;
    			Object arg = resolve(source_cd, el+path_s);
    			if (freevar==false && isFreeVar(arg)) freevar=true;
    			if (arg!=null) arguments.add(arg);
    		}
    	} else if (extent instanceof Vector){
    		Vector ext_vec = (Vector)extent;
    		for (int i=0; i<ext_vec.size(); i++){
    			Object suff = ext_vec.get(i);
    			if (!(suff instanceof String)) throw new SmartFrogFunctionResolutionException("Vector extent in Array: "+source_cd+" should be comprised of Strings");
    			String el=prefix_s+suff;
    			Object arg = resolve(source_cd, el+path_s);
    			if (freevar==false && isFreeVar(arg)) freevar=true;
    			if (arg!=null) arguments.add(arg);
    		}
    	}
    	
    	return freevar;
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
