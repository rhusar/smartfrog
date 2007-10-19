package org.smartfrog.sfcore.languages.sf.sfreference;

import java.util.Iterator;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.LinkResolutionState;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogLazyResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.FreeVar;
import org.smartfrog.sfcore.languages.sf.functions.Constraint;
import org.smartfrog.sfcore.languages.sf.functions.Constraint.SmartFrogConstraintBacktrackError;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.prim.Prim;
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
     * Get tje run-time version of the reference
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
     * @param ref to be compared
     * @return true if equal, false if not
     */
    public boolean equals(Object ref) {
        if (!(ref instanceof SFApplyReference)) {
            return false;
        }

        return ((SFApplyReference) ref).comp == comp;

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
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          if reference failed to resolve
     */
    public Object resolve(ReferenceResolver rr, int index)
            throws SmartFrogResolutionException {
        //take a new context...
        //     iterate over the attributes of comp- ignoring any beginning with sf;
        //     cache sfFunctionClass attribute;
        //     resolve all non-sf attributes, if they are links
        //     if any return s LAZY object, set self to lazy and return self, otherwise update copy
        //     and invoke function with copy of CD, return result

        Context forFunction = new ContextImpl();
        String functionClass = null;
        Object result;
        boolean isLazy = false;

        if (getData()) return this;

        if (!eager) throw new SmartFrogLazyResolutionException("function is lazy (sfFunctionLazy)");

        if (rr instanceof ComponentDescription)
            comp.setParent((ComponentDescription) rr);
        else if (rr instanceof Prim)
            comp.setPrimParent((Prim) rr);

        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogFunctionResolutionException("function class is not a string", e);
        }
        
        if (functionClass.equals("done")) return comp; //done already
        
        if (functionClass == null) {
            throw new SmartFrogFunctionResolutionException("unknown function class ");
        }
        
        Function function;
        try {
            function = (Function) SFClassLoader.forName(functionClass).newInstance();
        } catch (Exception e) {
                throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to create function class " + functionClass + " with data " + forFunction, e);
        }

        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();
            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass")){
                Object value = null;

                try {
                    value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                } catch (java.lang.StackOverflowError e) {
                    throw new SmartFrogFunctionResolutionException(e);
                } catch (SmartFrogLazyResolutionException e) {
                   isLazy = true;
                }

                if (value==null) continue;
                	
                try {
                	LinkResolutionState.setConstraintsShouldUndo(true);
                    comp.sfReplaceAttribute(name, value);
                    LinkResolutionState.setConstraintsShouldUndo(false);
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
        
        try {
            
            //We set the originating context in the copy context as we want changes to persist immediately if it is a Constraint
            //This is a minimal impact solution, but should be reworked...
            if (function instanceof Constraint) forFunction.setOriginatingContext(comp.sfContext());
            
            result = function.doit(forFunction, null, rr);
            
            //If Constraint, the result is the component description...
            if (function instanceof Constraint) result = comp; 
            
        } catch (SmartFrogConstraintBacktrackError sfcbe){
        	throw sfcbe;
        } catch (Exception e) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to create or evaluate function class " + functionClass + " with data " + forFunction, e);
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
    // Why is it not a stub?
    public Object resolve(RemoteReferenceResolver rr, int index)
            throws SmartFrogResolutionException {
        //take a new context...
        //     iterate over the attributes of comp- ignoring any beginning with sf;
        //     cache sfFunctionClass attribute;
        //     resolve all non-sf attributes, if they are links
        //     otherwise update copy
        //     and invoke function with copy of CD, return result
        Context forFunction = new ContextImpl();
        String functionClass = null;
        Object result;

        if (getData()) return this;

        if (rr instanceof ComponentDescription)
            comp.setParent((ComponentDescription) rr);
        else if (rr instanceof Prim)
            comp.setPrimParent((Prim) rr);

        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogFunctionResolutionException("function class is not a string", e);
        }
        
        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();

            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass")) {
                Object value = null;

                try {
                    value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                } catch (java.lang.StackOverflowError e) {
                    throw new SmartFrogFunctionResolutionException(e);
                }

                if (value != null) {
                    try {
                        comp.sfReplaceAttribute(name, value);
                        forFunction.sfAddAttribute(name, value);
                    } catch (SmartFrogContextException e) {
                        //shouldn't happen
                    } catch (SmartFrogRuntimeException e) {
                        //shouldn't happen
                    }
                }
            }
        }

        if (functionClass == null) {
            throw new SmartFrogFunctionResolutionException("unknown function class ");
        }

        try {
            Function function = (Function) SFClassLoader.forName(functionClass).newInstance();
            result = function.doit(forFunction, null, rr);
        } catch (Exception e) {
            System.out.println("obtained " + e);
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward("failed to create or evaluate function class " + functionClass + " with data " + forFunction, e);
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

    /**
     * Adds a parameter to the Context contained in the reference.
     *
     * @param name String representing the name of the parameter
     * @param data the Object which is the data associated with the parameter
     */
    public void sfAddParameter(String name, Object data) throws SmartFrogRuntimeException {
        comp.sfReplaceAttribute(name, data);
    }
}
