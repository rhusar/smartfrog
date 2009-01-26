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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.CompositeSource;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

/**
 * Does dynamic policy evaluation...
 */
public class DynamicPolicyEvaluation extends BaseFunction implements MessageKeys {
		
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
    	
    	boolean updateContextLocked = Constraint.isUpdateContextLocked();
    	if (!updateContextLocked) Constraint.lockUpdateContext();
    	
    	//Need to ascertain whether we do this or not
    	ComponentDescription comp = context.getOriginatingDescr();
    	context = comp.sfContext();
    	
    	////System.out.println("***Comp***"+comp);
    	
    	//Check the guards on the policy evaluation...
    	//(1) Explicit sfGuard
    	Object guard = null;
    	try { guard = comp.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.GUARD)));}
    	catch (SmartFrogResolutionException sfre){/*Intentionally do nothing*/}
    	if (guard!=null){
       	   if (!(guard instanceof Boolean)) throw new SmartFrogFunctionResolutionException("Guard in DynamicPolicyEvaluation: "+comp+" should be a Boolean");
       	   ////System.out.println("Guard parsed...");
       	   if (!((Boolean)guard).booleanValue()) return guard;
       	   ////System.out.println("Guard is true.");
    	}
    	
    	//(2) Tagged [sfGuard]s
    	try {
    		////System.out.println("TAGGED GUARDS...");
	    	Enumeration e = context.keys();
	    	while (e.hasMoreElements()){
	    		Object key = e.nextElement();
	    		////System.out.println("TAGGED GUARD?...");
	    		if (comp.sfContainsTag(key, ConstraintConstants.GUARD_TAG)){
	    			////System.out.println("TAGGED GUARD..."+key.toString());
		    		
	    			Object tagged_guard = comp.sfResolve(key.toString());
	    			////System.out.println(""+tagged_guard);
	    			if (!(tagged_guard instanceof Boolean)) throw new SmartFrogFunctionResolutionException("Tagged guard: "+key+" in DynamicPolicyEvaluation: "+comp+" should be a Boolean");
	    			////System.out.println("TAGGED GUARD..."+key+":"+((Boolean)tagged_guard).booleanValue());
	    	       	if (!((Boolean)tagged_guard).booleanValue()) return tagged_guard;
	    		}	    		
	    	}
    	} catch (SmartFrogException e){/*Shouldn't happen*/}
    
    	//OK, so we are allowed to evaluate the DPE...
    	try {
	    	Enumeration e = context.keys();
	    	while (e.hasMoreElements()) {
	    		Object key = e.nextElement();
	    		Object val = comp.sfResolve(key.toString());
	    		if (val!=null) context.put(key, val);
	    		
	    	}
    	} catch (SmartFrogException e){/*Shouldn't happen*/}
    	
    	//Let's finish with a few effects...
    	//(1) Explicit sfEffects
    	Object effects = null;
    	try { effects = comp.sfResolve(new Reference(ReferencePart.here(ConstraintConstants.EFFECTS)));}
    	catch (SmartFrogResolutionException sfre){/*Intentionally do nothing*/}
    	if (effects!=null){
       	   if (!(effects instanceof ComponentDescription)) throw new SmartFrogFunctionResolutionException("Effects in DynamicPolicyEvaluation: "+comp+" should be a ComponentDescription");
       	   //System.out.println("Effects parsed...");
       	   applyEffects((ComponentDescription)effects);
       	   
    	} //else //System.out.println("No effects?");

    	//(2) Tagged [sfEffects]s
    	//System.out.println("TAGGED EFFECTS...");
    	try {
	    	Enumeration e = context.keys();
	    	while (e.hasMoreElements()){
	    		Object key = e.nextElement();
	    		//System.out.println("TAGGED EFFECTS...?");
	    		if (comp.sfContainsTag(key, ConstraintConstants.EFFECTS)){
	    			//System.out.println("TAGGED EFFECTS...yes");
	    			Object tagged_effects = comp.sfResolve(key.toString());
	    		    if (!(tagged_effects instanceof ComponentDescription)) throw new SmartFrogFunctionResolutionException("Tagged effects in DynamicPolicyEvaluation: "+comp+" should be a ComponentDescription");
	    		    //System.out.println("TAGGED EFFECTS..."+tagged_effects);
	    		    applyEffects((ComponentDescription)tagged_effects);
	    		    
	    		}	    		
	    	}
    	} catch (SmartFrogException e){/*Shouldn't happen*/}
    	
    	if (!updateContextLocked) Constraint.applyUpdateContext();
    	
        return new Boolean(true);
    }
        
    public static void applyEffects(ComponentDescription effects) throws SmartFrogFunctionResolutionException {
    	
    	    java.util.Vector<CompositeSource> css = new java.util.Vector<CompositeSource>();
    	    java.util.Vector<Object> others = new java.util.Vector<Object>();
    	
    	    //System.out.println("IN applyEffects");
    	    
    	    Constraint.getCompositeSources(effects, css, others, false);
    	    
    	    //System.out.println("********************SIZES:"+css.size()+others.size());
    	    
    	    /*for (int i=0;i<css.size();i++){
    	    	//System.out.println("******CS"+i);
    	    	//System.out.println(css.get(i).toString());
    	    }/*
    	    for (int i=0;i<others.size();i++){
    	    	//System.out.println("******others"+i);
    	    	//System.out.println(others.get(i).toString());
    	    }*/
    	    
    	    Constraint.updateValues(css);
    	    	
    	    for (int i=0; i<others.size(); i++){
    	    	Object key = others.get(i);
    	    	Constraint.updateSimpleValue(effects, key);
    	    }
    	    
    	    //System.out.println("OUT:applyEffects...");
    	} 
   
}
