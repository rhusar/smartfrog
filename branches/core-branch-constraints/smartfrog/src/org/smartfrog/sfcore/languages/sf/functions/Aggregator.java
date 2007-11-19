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
import org.smartfrog.sfcore.languages.sf.DefaultParser;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.LinkResolutionState;
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

    	//Path
    	Object path = orgContext.get("sfAggregatorPath"); 
    	if (path==null || !(path instanceof String)) throw new SmartFrogFunctionResolutionException("Path in Aggregator: "+comp+" should be a String");    	    	
    	AggregatorSourcePath asp = new AggregatorSourcePath((String)path);
    	
    	Vector arguments = extractArgumentsFromSource(comp, null, asp);
    	  	
    	LinkResolutionState.setConstraintsShouldUndo(true);
    	
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
		LinkResolutionState.setConstraintsShouldUndo(false);
    	
        return null;
    }
    
    public static class AggregatorSourcePath {
    	private Reference source;
    	private String path;
    	AggregatorSourcePath(String sourcePath){
        	int endFirst = sourcePath.indexOf(':');
        	if (endFirst>-1){
        		try {
        		    source = Reference.fromString(sourcePath.substring(0, endFirst));
        		} catch (Exception e){}
        		path = ":"+sourcePath.substring(endFirst+1);
        	} 
    	}
    	public Reference getSource(){return source;}
    	public String getPath(){return path;}
    }
    
    public static class AggregatorReverseSourcePath {
    	private Reference source;
    	private String interPath="";
    	private String key="";
    	AggregatorReverseSourcePath(String sourcePath){
        	int idx = sourcePath.indexOf(':');
        	if (idx>-1){
        		try {
        		    source = Reference.fromString(sourcePath.substring(0, idx));
        		} catch (Exception e){}    
        		sourcePath = sourcePath.substring(idx+1);
        		
        		idx = sourcePath.lastIndexOf(':');
        		if (idx>-1){
        			interPath = sourcePath.substring(0, idx);
        			key = sourcePath.substring(idx+1);
        		} else key = sourcePath;
        	} 
    	}
     	public Reference getSource(){return source;}
    	public String getInterPath(){return interPath;}
    	public String getKey() { return key; }
    }

    
    static Vector extractArgumentsFromSource(ComponentDescription comp, String idx_s, AggregatorSourcePath asp) throws SmartFrogFunctionResolutionException {
    	
    	if (asp.getPath()==null) throw new SmartFrogFunctionResolutionException("No path specified from source "+asp.getSource()+" in Aggregator: "+comp);    	
    	
    	Object source=null;
    	//And resolve source...
    	try {
    		source = comp.sfResolve(asp.getSource());
    	} catch (Exception e) {  throw new SmartFrogFunctionResolutionException(e); }
    	if (source==null || !(source instanceof ComponentDescription)) throw new SmartFrogFunctionResolutionException("Source Reference in Aggregator: "+comp+" should RESOLVE TO a ComponentDescription");    	
    	ComponentDescription source_cd = (ComponentDescription)source;
    	
    	Object sourceClass = source_cd.sfContext().get("sfFunctionClass");
    	if (!sourceClass.equals("org.smartfrog.sfcore.languages.sf.functions.Array"))  throw new SmartFrogFunctionResolutionException("Source in Aggregator: "+comp+" must be an Array"); 
    	
    	//Extent
    	Object extent = source_cd.sfContext().get("sfArrayExtent");
    	
    	//Prefix
    	String prefix_s = (String) source_cd.sfContext().get("sfArrayPrefix");
    	
    	//Gather arguments for the processing functions...
    	Vector arguments = new Vector();
    	
    	boolean freevar=false;
    	
    	if (extent instanceof Integer){
    		int ext_int = ((Integer)extent).intValue();
    		for (int i=0; i<ext_int; i++){
    			String el = prefix_s+i;
    			Object arg = resolve(source_cd, el+asp.getPath());
    			if (freevar==false && isFreeVar(arg)) freevar=true;
    			if (arg!=null) arguments.add(arg);
    		}
    	} else if (extent instanceof Vector){
    		Vector ext_vec = (Vector)extent;
    		for (int i=0; i<ext_vec.size(); i++){
    			Object suff = ext_vec.get(i);
    			if (!(suff instanceof String)) throw new SmartFrogFunctionResolutionException("Vector extent in Array: "+comp+" should be comprised of Strings");
    			String el=prefix_s+suff;
    			Object arg = resolve(source_cd, el+asp.getPath());
    			if (freevar==false && isFreeVar(arg)) freevar=true;
    			if (arg!=null) arguments.add(arg);
    		}
    	}
    	
    	if (idx_s!=null && freevar) comp.sfContext().put("sfAggregatedConstraintFrees"+idx_s, "true");
    	return arguments;
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
