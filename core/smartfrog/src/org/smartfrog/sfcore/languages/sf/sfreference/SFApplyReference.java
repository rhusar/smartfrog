package org.smartfrog.sfcore.languages.sf.sfreference;

import java.util.Iterator;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLazyResolutionException;
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
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.reference.ApplyReference;
import org.smartfrog.sfcore.reference.Function;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.ReferenceResolver;
import org.smartfrog.sfcore.reference.RemoteReferenceResolver;
import org.smartfrog.sfcore.security.SFClassLoader;

/**
 * Representation of ApplyReference for the SF Language
 */
public class SFApplyReference extends SFReference implements ReferencePhases {
    protected SFComponentDescription comp;
    
    public SFApplyReference(SFComponentDescription comp) {
        super();
        this.comp = comp;
    }
    
    /**
     * Get the component description that forms the basis of this apply reference
     * @return Component Description
     */
    public SFComponentDescription getComponentDescription(){
    	return comp;
    }
    
    /**
     * Get the run-time version of the reference
     *
     * @return the reference
     * @throws SmartFrogCompilationException
     */
    public Reference sfAsReference() throws SmartFrogCompilationException {
        ApplyReference ar = new ApplyReference(comp.sfAsComponentDescription());
        ar.setEager(getEager());
        ar.setData(getData());
        return ar;
    }

    /**
     * Returns a copy of the reference, by cloning itself and the function part
     *
     * @return copy of reference
     * @see org.smartfrog.sfcore.common.Copying
     */
    public Object copy() {
        SFApplyReference ret = (SFApplyReference) clone();

        ret.comp = (SFComponentDescription) comp.copy();

        return ret;
    }

    /**
     * Makes a clone of the reference. The inside ref holder is cloned, but the
     * contained component is NOT.
     *
     * @return clone of reference
     */
    public Object clone() {
        SFApplyReference res = (SFApplyReference) super.clone();
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
        if (!(reference instanceof SFApplyReference)) {
            return false;
        }

        return ((SFApplyReference) reference).comp == comp;

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

    //public static boolean g_ignoreApplyReference = false;
    
    /**Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          if reference failed to resolve
     */
    public Object resolve(ReferenceResolver rr, int index) throws SmartFrogResolutionException {
    	
    	//System.out.println("sfApplyReference");
    	
    	if (!eager) throw new SmartFrogLazyResolutionException("function is lazy (sfFunctionLazy)");
    	if (getData()) return this; 	
    	
    	//Part of original link resolution?  
    	boolean orig = CoreSolver.getInstance().getOriginalDescription()==null;
        //We may assume it is actually an SFComponentDescription...
        if (orig) CoreSolver.getInstance().setDescriptionMarkers((SFComponentDescription)rr);
        
    	ComponentDescription rrcd = (ComponentDescription) rr;
        comp.setParent(rrcd);
           	
        //Am I an array generator?, if so quit...
        try { if (comp.sfContext().get("sfIsArrayGenerator")!=null) return this; } catch (Exception e){/*Do nothing!*/}
    	
		String functionClassStatus = (String) comp.sfContext().get("sfFunctionClassStatus");
        if (functionClassStatus!=null && functionClassStatus.equals("done")) return comp; //done already
       
        String functionClass = null;
        
        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogFunctionResolutionException("function class is not a string", e);
        }

        if (functionClass == null) {
            throw new SmartFrogFunctionResolutionException("unknown function class ");
        }
        
        //System.out.println(functionClass);
        
        Function function;
        try {
            function = (Function) SFClassLoader.forName(functionClass).newInstance();
        } catch (Exception e) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to create function class " + functionClass, e);
        }   
        
        if (Proposition.g_EvaluatingPropositions && !(function instanceof BaseOperator || 
        		function instanceof BaseUnaryOperator || function instanceof BaseBinaryOperator)) return null;
        
  	
    	Object result=null;
    	result= resolveWkr(rrcd, index, function);
    	if (orig) CoreSolver.getInstance().tidyConstraintBasedDescription((SFComponentDescription)rr);
    	return result;
    }
    
    
    /**
     * Does the work of resolve 
     *
     * @param rrcd    ComponentDescription to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          if reference failed to resolve
     */
    public Object resolveWkr(ComponentDescription rrcd, int index, Function function)
            throws SmartFrogResolutionException {
        //take a new context...
        //     iterate over the attributes of comp- ignoring any beginning with sf;
        //     cache sfFunctionClass attribute;
        //     resolve all non-sf attributes, if they are links
        //     if any returns LAZY object, set self to lazy and return self, otherwise update copy
        //     and invoke function with copy of CD, return result

    	Context forFunction = new ContextImpl();
        Object result=null;
        boolean isLazy = false;
                
        //In an Aggregator, we apply the function first, and then resolve the arguments
        //Normally, other way around...
        try {            
            if (function instanceof Aggregator) {
            	forFunction.setOriginatingDescr(comp);
            	result = function.doit(forFunction, null, rrcd);
            }          
        } catch (Exception e) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to evaluate function class " + function + " with data " + forFunction, e);
        } 
        
        comp.linkResolve();  // link resolve up front...
        
        if (function instanceof Aggregator) return result;
        
        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();
            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass")){
            	Object value = comp.sfContext().get(name);
            	
            	boolean skip=Constraint.leaveResolve(comp, name);
                if (!skip) 
	            	try {
	            		value=comp.sfResolve(new Reference(ReferencePart.here(name)));
	            	} catch (java.lang.StackOverflowError e) {
	                    throw new SmartFrogFunctionResolutionException(e);
	                } catch (SmartFrogLazyResolutionException e) {
	                   isLazy = true;
	                } 

	            if (value==null) continue;
	            	
	            try {
	                forFunction.sfAddAttribute(name, value);
	                forFunction.sfAddTags(name, comp.sfGetTags(name));     
	            } catch (SmartFrogContextException e) {
	                //shouldn't happen
	            } catch (SmartFrogRuntimeException e) {
	                //shouldn't happen
	            }
                
            }
        }
        
        if (isLazy) throw new SmartFrogLazyResolutionException("function has lazy parameter");

        //System.out.println("SFAR:Going in..."+function.getClass().toString());
        
       	forFunction.setOriginatingDescr(comp);
    	try {
    		result = function.doit(forFunction, null, rrcd, this, rrcd.sfAttributeKeyFor(this));
    	} catch (SmartFrogException e){
    		throw new SmartFrogResolutionException(e);
    	}
    	
        return result;   
    }    
    
    /**
     * Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws SmartFrogResolutionException if reference failed to resolve
     */
    // This is never called (needed for completeness). At runtime ApplyReference is called.
    public Object resolve(RemoteReferenceResolver rr, int index)
            throws SmartFrogResolutionException {    	
    	return null;
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

    /**
     * Adds a parameter to the Context contained in the reference.
     *
     * @param name String representing the name of the parameter
     * @param dataValue the Object which is the data associated with the parameter
     */
    public void sfAddParameter(String name, Object dataValue) throws SmartFrogRuntimeException {
        comp.sfReplaceAttribute(name, dataValue);
    }
}
