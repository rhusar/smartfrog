package org.smartfrog.sfcore.reference;

import java.io.Serializable;
import java.util.Iterator;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;
import org.smartfrog.sfcore.languages.sf.constraints.propositions.Proposition;
import org.smartfrog.sfcore.languages.sf.functions.Aggregator;
import org.smartfrog.sfcore.languages.sf.functions.BaseBinaryOperator;
import org.smartfrog.sfcore.languages.sf.functions.BaseOperator;
import org.smartfrog.sfcore.languages.sf.functions.BaseUnaryOperator;
import org.smartfrog.sfcore.languages.sf.functions.Constraint;
import org.smartfrog.sfcore.languages.sf.functions.DynamicPolicyEvaluation;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * The subclass of Reference that is a function application. The structure of the classes is
 * historical, in that function applications were added much later. A different structure would
 * have been prefereable - an abstract class indicating some object that resolves in a context,
 * with specializations that are link references and apply references. However unfortunately for
 * backward compatility reasons this is not possible. Consequently ApplyReference impements the entire
 * gamut of the reference behaviour, inlcuding having parts, which is not relevant to a function applicaiton.
 * It should override these methods and generate some form of run-time exception - this has not been implemented.
 * <p/>
 * The function application reference resolves by evaluating hte refeences it contains, then evaluating the funciton.
 * If
 */
public class ApplyReference extends Reference implements Copying, Cloneable, Serializable {
    protected ComponentDescription comp;

    public ApplyReference(ComponentDescription comp) {
        super();
        this.comp = comp;
    }

    /**
     * Get the component description that forms the basis of this apply reference
     * @return Component Description
     */
    public ComponentDescription getComponentDescription(){
    	return comp;
    }
    
    /**
     * Returns a copy of the reference, by cloning itself and the function part
     *
     * @return copy of reference
     * @see org.smartfrog.sfcore.common.Copying
     */
    public Object copy() {
        ApplyReference ret = (ApplyReference) clone();

        ret.comp = (ComponentDescription) comp.copy();

        return ret;
    }

    /**
     * Makes a clone of the reference. The inside ref holder is cloned, but the
     * contained component is NOT.
     *
     * @return clone of reference
     */
    public Object clone() {
        ApplyReference res = (ApplyReference) super.clone();
        res.comp = comp;
        return res;
    }

    /**
     * Checks if this and given reference are equal. Two references are
     * considered to be equal if the component they wrap are ==
     *
     * @param reference to be compared
     * @return true if equal, false if not
     */
    public boolean equals(Object reference) {
        if (!(reference instanceof ApplyReference)) {
            return false;
        }

        if (((ApplyReference) reference).comp != comp) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hashcode for this reference. Hash code for reference is made
     * out of the sum of the parts hashcodes
     *
     * @return integer hashcode
     */
    public int hashCode() {
        return comp.hashCode();
    }

    /**
     * Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws SmartFrogResolutionException if reference failed to resolve
     */
    public Object resolve(ReferenceResolver rr, int index)
            throws SmartFrogResolutionException {
    	return resolveWkr(rr, index);
    }
    	
    /**
     * Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws SmartFrogResolutionException if reference failed to resolve
     */
    public Object resolve(RemoteReferenceResolver rr, int index)
            throws SmartFrogResolutionException {
    	return resolveWkr(rr, index);
    }

    
    Object resolveWkr(Object rr, int index) throws SmartFrogResolutionException {
        //take a new context...
        //     iterate over the attributes of comp- ignoring any beginning with sf;
        //     cache sfFunctionClass attribute;
        //     resolve all non-sf attributes, if they are links
        //     update copy and invoke function with copy of CD, return result

    	Context forFunction = new ContextImpl();
        String functionClass = null;
        Object result=null;
        ComponentDescription tmp_comp = (ComponentDescription) comp.copy();
        
        if (getData()) return this;
		
    	if (rr instanceof ComponentDescription){
             tmp_comp.setParent((ComponentDescription) rr);
         } else if (rr instanceof Prim)
             tmp_comp.setPrimParent((Prim) rr);
    	
        try {
            functionClass = (String) tmp_comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogFunctionResolutionException("function class is not a string", e);
        }

        if (functionClass == null) {
            throw new SmartFrogFunctionResolutionException("unknown function class ");
        }
        
        Function function;
        try {
            function = (Function) SFClassLoader.forName(functionClass).newInstance();
        } catch (Exception e) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to create function class " + functionClass, e);
        }
        
        if (Proposition.g_EvaluatingPropositions && !(function instanceof BaseOperator || 
        		function instanceof BaseUnaryOperator || function instanceof BaseBinaryOperator)) return null;
        
        forFunction.setOriginatingDescr(tmp_comp);
        
        //In an Aggregator, we apply the function first, and then resolve the arguments
        //Normally, other way around...
        try {            
            if (function instanceof Aggregator) {
            	if (rr instanceof ReferenceResolver) result = function.doit(forFunction, null, (ReferenceResolver) rr);
            	else result = function.doit(forFunction, null, (RemoteReferenceResolver) rr);
            }          
        } catch (Exception e) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to evaluate function class " + functionClass + " with data " + forFunction, e);
        } 
               
        if (!(function instanceof DynamicPolicyEvaluation)){     
	        for (Iterator v = tmp_comp.sfAttributes(); v.hasNext();) {
	            Object name = v.next();
	            String nameS = name.toString();
	            
	            boolean skip = nameS.equals("sfFunctionClass");
	             
	            if (skip) continue;
	            
	            skip=Constraint.leaveResolve(tmp_comp, name);
	           
	            Object value = null;
	            
	            if (skip) value = tmp_comp.sfContext().get(name);
	            else {
	            	Reference ref = new Reference(ReferencePart.here(name));
	            	value=tmp_comp.sfResolve(ref);
	            }
	            
	            try {
	            	tmp_comp.sfContext().put(name, value);
	            	forFunction.sfAddAttribute(name, value);
	                forFunction.sfAddTags(name, tmp_comp.sfGetTags(name));     
	            } catch (SmartFrogContextException e) {
	                //shouldn't happen
	            } catch (SmartFrogRuntimeException e) {
	                //shouldn't happen
	            }    
	        }     
        }

        try {            
            if (!(function instanceof Aggregator)) {
            	Object arkey = (rr instanceof ComponentDescription ? ((ComponentDescription)rr).sfAttributeKeyFor(this) : ((Prim)rr).sfAttributeKeyFor(this));
            	
            	
            	if (rr instanceof ReferenceResolver) result = function.doit(forFunction, null, (ReferenceResolver) rr,  this, arkey);
            	else result = function.doit(forFunction, null, (RemoteReferenceResolver) rr, this, arkey);
            }
            
            if (function instanceof Constraint) CoreSolver.getInstance().stopSolving();
            
            
        } catch (Exception e) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to evaluate function class " + functionClass + " with data " + forFunction, e);
        }
        
        return result;   
    }
    
    	    

    /**
     * Returns string representation of the reference.
     * Overrides Object.toString.
     *
     * @return String representing the reference
     */
    public String toString() {
        String res = "";
        res += (eager ? "" : "LAZY ");
        res += (data ? "DATA " : "");

        res += "APPLY {";
        res += comp.sfContext().toString();
        res += "}";

        return res;
    }
}
