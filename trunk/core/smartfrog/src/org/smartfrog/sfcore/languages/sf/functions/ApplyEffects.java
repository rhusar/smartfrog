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

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;

import org.smartfrog.SFSystem;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfreference.SFReference;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;

import static org.smartfrog.sfcore.languages.sf.constraints.ConstraintConstants.*;
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
    	//First try me...
        if (SFSystem.sfLog().isDebugEnabled())
            SFSystem.sfLog().debug(Thread.currentThread().getStackTrace()[1]);
    	applyEffects(comp);

        //Then any nested effects...
        Enumeration e = context.keys();
        while (e.hasMoreElements()){
            Object key = e.nextElement();
            Object effects = context.get(key);
            if (!(effects instanceof ComponentDescription)) continue;
            applyEffects((ComponentDescription)effects);
        }
        if (SFSystem.sfLog().isDebugEnabled())
            SFSystem.sfLog().debug(Thread.currentThread().getStackTrace()[1] + ":LEAVING");

        return true;
    }
        
    public static void applyEffects(ComponentDescription effects) throws SmartFrogFunctionResolutionException {
        if (SFSystem.sfLog().isDebugEnabled())
            SFSystem.sfLog().debug(Thread.currentThread().getStackTrace()[1]);

        Reference array = null;
        try {
            array = (Reference) effects.sfContext().get(ARRAY);
        } catch (ClassCastException ignore) {
        }
        SFSystem.sfLog().debug("ARRAY: "+array);

            String prefix = null;
            Reference pred = null;
            ComponentDescription pcontext = null;
    	    if (array!=null){
                try {
                    prefix = (String) effects.sfContext().get(PREFIX);
                } catch (ClassCastException ignore) {
                }
                SFSystem.sfLog().debug("PREFIX: "+prefix);

                try {
                    pred = (Reference) effects.sfContext().get(PRED);
                } catch (ClassCastException ignore) {
                }
                SFSystem.sfLog().debug("PRED: "+pred);

                try {
                    pcontext = (ComponentDescription) effects.sfContext().get(CONTEXT);
                } catch (ClassCastException ignore) {
                }
                SFSystem.sfLog().debug("PCONTEXT: "+pcontext);
    	    }
    	    
    	    Object key = effects.sfContext().get(KEY);

    	    if (key==SFNull.get()) key=null;
    	    if (key==null) return; //nothing here...
    	    if (key instanceof Reference) {
                try {
                    key=effects.sfResolve((Reference)key);
                } catch (SmartFrogResolutionException e) {
                    SFSystem.sfLog().debug(e);
                    throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                }
            }
    	    SFSystem.sfLog().debug("KEY: "+key);
    	    
    	    Object update = effects.sfContext().get(UPDATE);
    	    //if (update==SFNull.get()) update=null;
    	    if (update!=null && update instanceof Reference) {
                try {
                    update=effects.sfResolve((Reference)update);
                } catch (SmartFrogResolutionException e) {
                    SFSystem.sfLog().debug(e);
                    throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                }
            }
    	    SFSystem.sfLog().debug("UPDATE: "+update);
    	    
    	    ComponentDescription deploy = null;
            Object toTerminate = null;
    	    if (update==null){
                try {
                    deploy = (ComponentDescription) effects.sfResolve(new Reference(ReferencePart.here(DEPLOY)));
                } catch (Exception ignore) {
                }

	    	    if (deploy==null){
                    try {
                        toTerminate = effects.sfResolve(new Reference(ReferencePart.here(TERMINATE)));
                    } catch (Exception ignore) {
                    }
	    	    }
    	    }
    	    SFSystem.sfLog().debug("DEPLOY: "+deploy);
    	    if (deploy==null && update==null && toTerminate==null) return;
    	    
    	    Reference path = null;
    	    try {
                path = (Reference) effects.sfContext().get(PATH);
            } catch(ClassCastException cce){ /*Take as null*/
            }
    	    SFSystem.sfLog().debug("PATH: "+path);
    	   
    	    Object source = null; 
    	    if (array==null){
	    	    if (path!=null){
                    try {
                        source=effects.sfResolve(path);
                    } catch (SmartFrogResolutionException e) {
                        SFSystem.sfLog().debug(e);
                        throw new SmartFrogFunctionResolutionException(PATHNOTRESOLVEINEFFECTS + effects, e);
                    }
	    	    } else {        	
		        	source = effects;
		        	SFSystem.sfLog().debug("Looking for..."+key+" to update with "+update);
		    		while (true){
		        		source = Constraint.resolveParent(source);
		        		
		        		if (source==null) throw new SmartFrogFunctionResolutionException(FAILEDTOFINDATTRIBEFFECTS+key+":"+effects);
		        		if (Constraint.resolveKey(key,source)!=null) break;
		        	}
	    	    }
    	    
	    	    SFSystem.sfLog().debug("SOURCE: "+source.getClass()+source.hashCode());
    	    }
    	    
    	    if (array==null && source==null) return;
 
    	    if (deploy!=null) {
    	    	
    	    	SFSystem.sfLog().debug("DEPLOYING..."+key);
    	    	
    	    	
    	    	if (source instanceof DeployingAgent) {
    	    		ComponentDescription deploy_cd = (ComponentDescription) deploy.copy();	   	    		
                    try {
                        ((DeployingAgent) source).addToDeploy(key.toString(), deploy_cd);
                    } catch (Exception e) {
                        SFSystem.sfLog().debug(e);
                        throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                    }
    	    	}
    	  
    	    	
    	    } else if (toTerminate!=null){
    	    	
    	    	if (source instanceof DeployingAgent) {
    	    		try {
    	    			((DeployingAgent) source).addToTerminate(key.toString());
    	    		} catch (Exception e) {
                        SFSystem.sfLog().debug(e);
                        throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                    }
    	
    	    	}
    	    	
    	    } else {
    	    	
    	    	//Replace in array?
    	    	if (array!=null && prefix!=null){

    	    	SFSystem.sfLog().debug("We are replacing in an array");

                try {
                    source=effects.sfResolve(array);
                } catch (SmartFrogResolutionException e) {
                    SFSystem.sfLog().debug(e);
                    throw new SmartFrogFunctionResolutionException(PATHNOTRESOLVEINEFFECTS + effects, e);
                }

                Context src_context = null;
                try {
                    src_context = (source instanceof Prim?((Prim)source).sfContext():((ComponentDescription)source).sfContext());
                } catch (RemoteException e) {
                    SFSystem.sfLog().debug(e);
                    throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                }

                Enumeration en = src_context.keys();
                while (en.hasMoreElements()){

                    String akey = en.nextElement().toString();
                    if (akey.startsWith(prefix)){
                        SFSystem.sfLog().debug("In with the prefix..."+akey);

                        Object member = src_context.get(akey);
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
                                    } catch(Exception e){
                                        SFSystem.sfLog().debug(e);
                                        throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                                    }
                                }
                            }

                            if (pred!=null){
                                SFSystem.sfLog().debug("Pred not null..."+pred);
                                try {
                                if (pred instanceof SFReference) pred=((SFReference) pred).sfAsReference();
                                } catch (SmartFrogCompilationException sfce){ throw new SmartFrogFunctionResolutionException(sfce);}
                                SFSystem.sfLog().debug("Really pred not null...");

                                Object eval_pred = null;
                                try {
                                    eval_pred = (p!=null? p.sfResolve(pred) : c.sfResolve(pred));
                                } catch (Exception e)
                                {
                                    SFSystem.sfLog().debug(e);
                                    throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                                }
                                if (eval_pred!=null && eval_pred instanceof Boolean) {
                                    SFSystem.sfLog().debug("Is pred false?..."+eval_pred);
                                    if (!(Boolean) eval_pred) continue; //round while...
                                }
                                else if (eval_pred==null || !(eval_pred instanceof SFNull)) {
                                    throw new SmartFrogFunctionResolutionException( PREDSHOULDYIELDBOOLEANFROMSOURCE+pred+":"+source );
                                }
                            }
                            SFSystem.sfLog().debug("We have a match...");

                            try {
                                if (path!=null) member = (p!=null? p.sfResolve(path) : c.sfResolve(path));
                            } catch (Exception e) {
                                SFSystem.sfLog().debug(e);
                                throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                            }

                            replaceSingleValue(key, update, member);

                            if (pcontext!=null){
                                //Add resolving context...
                                Enumeration keys = pcontext.sfContext().keys();
                                while (keys.hasMoreElements()){
                                    Object ckey = keys.nextElement();
                                    try {
                                        if (p!=null) p.sfRemoveAttribute(ckey);
                                        else c.sfRemoveAttribute(ckey);
                                    } catch (Exception e) {
                                        SFSystem.sfLog().debug(e);
                                        throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
                                    }
                                }
                            }

                        }
                    }
                }
            } else replaceSingleValue(key, update, source);
        }
    }
    
    static private void replaceSingleValue(Object key, Object update, Object source) throws SmartFrogFunctionResolutionException {
    	String fullname="";
    	try {
            if (source instanceof Prim) {
                Prim p = (Prim) source;
                fullname = p.sfCompleteName().toString();
                if (SFSystem.sfLog().isInfoEnabled())  SFSystem.sfLog().info("Replacing: "+key+" with: "+update+ " in: "+fullname);
            }
            else {
                fullname = ((ComponentDescription)source).sfCompleteName().toString();
            }
    	} catch (Exception e) {
            SFSystem.sfLog().debug(e);
            throw (SmartFrogFunctionResolutionException) SmartFrogFunctionResolutionException.forward(e);
        }
    	
    	SFSystem.sfLog().debug("Replacing: "+key+" with: "+update+ " in: "+fullname);
    	Constraint.replaceAttribute(key, update, source);
    }
    
    public interface DeployingAgent extends Remote {
    	public void addToDeploy(String name, ComponentDescription cd) throws SmartFrogException, RemoteException;
    	public void addToTerminate(String name) throws RemoteException;
    	public void waitOnQueuesCleared() throws IOException;
    }
    
}
