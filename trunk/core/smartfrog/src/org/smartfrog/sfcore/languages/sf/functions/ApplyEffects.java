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
import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.common.SFNull;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
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
    
    	    //Array/path/pred?
    	    Reference array = null;
    	    String prefix = null;
    	    Reference pred = null;
    	    ComponentDescription pcontext = null;
    	    try { array = (Reference) effects.sfContext().get(ConstraintConstants.ARRAY); }
    	    catch(Exception e){/**/}
    	    //System.out.println("ARRAY: "+array);
    	    
    	    if (array!=null){
    	    	try { prefix = (String) effects.sfContext().get(ConstraintConstants.PREFIX); }
        	    catch(Exception e){/**/}
        	    //System.out.println("PREFIX: "+prefix);
        	    
        	    try { pred = (Reference) effects.sfContext().get(ConstraintConstants.PRED); }
        	    catch(Exception e){/**/}
        	    //System.out.println("PRED: "+pred);
        	    
        	    try { pcontext = (ComponentDescription) effects.sfContext().get(ConstraintConstants.CONTEXT); }
        	    catch(Exception e){/**/}
        	    //System.out.println("PCONTEXT: "+pcontext);
    	    }
    	    
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
    	    if (array==null){
	    	    if (path!=null){
	    	    	try { source=effects.sfResolve(path); }
	        	    catch(Exception e){
	        	    	throw new SmartFrogFunctionResolutionException("path in effects will not resolve: "+effects); }    
	    	    } else {        	
		        	source = effects;
		        	//System.out.println("Looking for..."+key+" to update with "+update);
		    		while (true){
		        		source = Constraint.resolveParent(source);
		        		
		        		if (source==null) throw new SmartFrogFunctionResolutionException("Failed to find attrib: "+key+" in effects: "+effects);
		        		if (Constraint.resolveKey(key,source)!=null) break;
		        	}
	    	    }
    	    
	    	    //System.out.println("SOURCE: "+source.getClass()+source.hashCode());
    	    }
    	    
    	    if (array==null && source==null) return;
 
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
    	    	
    	    	//Replace in array?
    	    	if (array!=null && prefix!=null){

    	    	//System.out.println("We are replacing in an array");
    	    		
    	    		try { source=effects.sfResolve(array); }
	        	    catch(Exception e){
	        	    	throw new SmartFrogFunctionResolutionException("path in effects will not resolve: "+effects); }    
    	    		
	        	    //System.out.println("000");
	        	    
	        	    Context src_context = null;
	        	    try {src_context = (source instanceof Prim?((Prim)source).sfContext():((ComponentDescription)source).sfContext());}
	        	    catch(Exception e){/**/}
	        	    
    	    		Enumeration en = src_context.keys();	
    	        	while (en.hasMoreElements()){
    	        		
    	        		//System.out.println("222");
    	        		
    	        		String akey = en.nextElement().toString();
    	            	
    	        		if (akey.startsWith(prefix)){
    	        			//System.out.println("In with the prefix..."+akey);
    	        			
    	        			Object member = src_context.get(akey);
    	        	    	//System.out.println("333");
    	            		
    	        	    	Prim p = (member instanceof Prim?(Prim)member:null); 
    	        	    	ComponentDescription c=(member instanceof ComponentDescription?(ComponentDescription)member:null); 
    	        	    	
    	        	    	
    	        			if (p!=null || c!=null){
    	        	    	        				
    	        				if (pcontext!=null){
    	    	    				//Add resolving context...
    	    	    				Enumeration keys = pcontext.sfContext().keys();
    	    	    				while (keys.hasMoreElements()){
    	    	    					Object ckey = keys.nextElement();
    	    	    					Object cval = pcontext.sfContext().get(ckey);
    	    	    					try {
    	    	    						if (p!=null) p.sfReplaceAttribute(ckey, cval);
    	    		    		    		else c.sfReplaceAttribute(ckey, cval);
    	    	    					} catch(Exception e){/*Shouldn't happen*/}
    	    	    				}
    	        				}
    	    	    				    	        					
	    	    				if (pred!=null){
	    	    					//System.out.println("Pred not null..."+pred);
	    	    					try {
	    	    					if (pred instanceof SFReference) pred=((SFReference) pred).sfAsReference();
	    	    					} catch (SmartFrogCompilationException sfce){ System.out.println(sfce); throw new SmartFrogFunctionResolutionException(sfce);}
	    	    					//System.out.println("Really pred not null...");
	    	    					
	    	    					Object eval_pred = null;
	    	    					try {
	    	    						eval_pred = (p!=null? p.sfResolve(pred) : c.sfResolve(pred));
	    	    					} catch (Exception e)
	    	    					{
	    	    						//System.out.println("FART::::"+e);
	    	    						/*Intentionally Leave*/
	    	    					}
	    	    					if (eval_pred!=null && eval_pred instanceof Boolean) {
	    	    						//System.out.println("Is pred false?..."+eval_pred);
	    	    						if (!((Boolean)eval_pred).booleanValue()) continue; //round while...
	    	    					}
	    	    					else if (eval_pred==null || !(eval_pred instanceof SFNull)) throw new SmartFrogFunctionResolutionException("In extracting values as per source, pred "+pred+" should yield Boolean from: "+source);
    	    	    			}
    	    					//System.out.println("We have a match...");
    	    					
    	    					try {
    	    						if (path!=null) member = (p!=null? p.sfResolve(path) : c.sfResolve(path));
    	    					} catch (Exception e)
    	    					{
    	    						System.out.println("FART::::"+e);
    	    						/*Intentionally Leave*/
    	    					}
    	    					
	    	    				replaceSingleValue(key, update, member);
	    	    				
	    	    				if (pcontext!=null){
    	    	    				//Add resolving context...
    	    	    				Enumeration keys = pcontext.sfContext().keys();
    	    	    				while (keys.hasMoreElements()){
    	    	    					Object ckey = keys.nextElement();
    	    	    					Object cval = pcontext.sfContext().get(ckey);
    	    	    					try {
        	        						if (p!=null) p.sfRemoveAttribute(ckey);
        	            		    		else c.sfRemoveAttribute(ckey);
        	        					} catch(Exception e){/*Shouldn't happen*/}
    	    	    				}
    	        				}
	    	    	    			
    	        			}
    	        		}
    	        	}
    	    	} else replaceSingleValue(key, update, source); 	    	
    	    }
    }
    
    static private void replaceSingleValue(Object key, Object update, Object source){    	
    	String fullname="";
    	try {
    	if (source instanceof Prim) {
    		PrimImpl p = (PrimImpl) source;
    		fullname = p.sfCompleteName().toString();
    		if (p.sfLog().isInfoEnabled())  p.sfLog().info("Replacing: "+key+" with: "+update+ " in: "+fullname);    
    	}
    	else {
    		fullname = ((ComponentDescription)source).sfCompleteName().toString();
    	}
    	}catch(Exception e){}
    	
    	//System.out.println("Replacing: "+key+" with: "+update+ " in: "+fullname);
    	
    	Constraint.replaceAttribute(key, update, source);
    }
}
