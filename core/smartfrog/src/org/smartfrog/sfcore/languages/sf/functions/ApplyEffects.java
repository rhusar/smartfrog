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

import org.smartfrog.services.dependencies.statemodel.state.SynchedComposite;
import org.smartfrog.services.orchcomponent.model.OrchComponentModel;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Does dynamic policy evaluation...
 */
public class ApplyEffects extends BaseFunction implements MessageKeys {
		
    /**
     * The method to implement the functionality of the if-then-else function.
     *
     * @return an Object representing the answer
     * @throws SmartFrogFunctionResolutionException if any of the parameters are not there or of the wrong type
     *  */
    protected Object doFunction() throws SmartFrogFunctionResolutionException {
     	Object retval=null;
    	synchronized(CoreSolver.getInstance()){
    	    	retval=doFunctionWkr();
    	}
    	return retval;
    }
  	  
    protected Object doFunctionWkr() throws SmartFrogFunctionResolutionException {	
    	//First try me...
    	
    	applyEffects(comp);
    	
    	try {
	    	Enumeration e = context.keys();
	    	while (e.hasMoreElements()){
	    		Object key = e.nextElement();
    			Object tagged_effects = comp.sfResolve(key.toString());
    		    if (!(tagged_effects instanceof ComponentDescription)) continue;
    		    applyEffects((ComponentDescription)tagged_effects);	    		        		
	    	}
    	} catch (SmartFrogException e){/*Shouldn't happen*/}
    	
        return new Boolean(true);
    }
        
    public static void applyEffects(ComponentDescription effects) throws SmartFrogFunctionResolutionException {
    	    //System.out.println("IN applyEffects");
    
    	    Object key = effects.sfContext().get(ConstraintConstants.KEY);
    	    if (key==SFNull.get()) key=null;
    	    if (key!=null && key instanceof Reference) try { key=effects.sfResolve((Reference)key);}catch(Exception e){}
    	    //System.out.println("KEY: "+key);
    	    
    	    Object update = effects.sfContext().get(ConstraintConstants.UPDATE);
    	    if (update==SFNull.get()) update=null;
    	    if (update!=null && update instanceof Reference) try { update=effects.sfResolve((Reference)update);}catch(Exception e){}
    	    //System.out.println("UPDATE: "+update);
    	    
    	    
    	    ComponentDescription deploy = null;
    	    if (update==null){
	    	    try { deploy= (ComponentDescription) effects.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.DEPLOY)));}catch(Exception e){}
    	    }
    	    //System.out.println("DEPLOY: "+deploy);
    	    if (deploy==null && update==null) return;
    	    
    	    Reference path = null;
    	    try { path = (Reference) effects.sfContext().get(ConstraintConstants.PATH); }
    	    catch(ClassCastException cce){ /*Take as null*/ }
    	    //System.out.println("PATH: "+path);
    	   
    	    Object source = null; 
    	    if (path!=null){
    	    	try { source=effects.sfResolve(path); }
        	    catch(Exception e){
        	    	throw new SmartFrogFunctionResolutionException("path in effects will not resolve: "+effects); }    
    	    } else {        	
	        	source = effects;
	    		while (true){
	        		source = Constraint.resolveParent(source);
	        		
	        		if (source==null) throw new SmartFrogFunctionResolutionException("Failed to find attrib: "+key+" in effects: "+effects);
	        		if (Constraint.resolveKey(key,source)!=null) break;
	        	}
    	    }
    	    
    	    //System.out.println("SOURCE: "+source.getClass()+source.hashCode());
    	    if (source==null) return;
    	        
    	    if (deploy!=null) {
    	    	if (source instanceof Compound) {
    	    		Compound source_nd = (Compound) source;
    	    		ComponentDescription deploy_cd = (ComponentDescription) deploy.copy();
    	    		try {source_nd.sfCreateNewChild(key, deploy_cd, null);}
    	    		catch(Exception e){/*Elaborate*/}
    	    		
    	    		OrchComponentModel model = null;
    	    		try {
    	    			model = (OrchComponentModel) effects.sfResolve(new Reference(ReferencePart.attrib("orchModel")));
    	    		} catch (Exception e){ /*Intentionally leave*/ }
    	    		if (model!=null){
    	    			Prim added = null;
    	    			try{
    	    				added = (Prim) source_nd.sfResolve(key.toString());
    	    			} catch (Exception e){/**/}
    	    			if (added!=null && added instanceof SynchedComposite) model.addToRun(added); 
    	    		}
    	    	}
    	    }
    	    else {
    	    	String fullname="";
    	    	try {
    	    	if (source instanceof Prim) fullname = ((Prim)source).sfCompleteName().toString();
    	    	else fullname = ((ComponentDescription)source).sfCompleteName().toString();
    	    	}catch(Exception e){}
    	    	
    	    	//System.out.println("Replacing: "+key+" with: "+update+ " in: "+fullname);
    	    	
    	    	Constraint.replaceAttribute(key, update, source);
    	    	
    	    }
    	} 
}
