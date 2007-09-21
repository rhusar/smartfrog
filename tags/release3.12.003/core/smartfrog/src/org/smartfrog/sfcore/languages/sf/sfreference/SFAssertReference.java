package org.smartfrog.sfcore.languages.sf.sfreference;

import org.smartfrog.sfcore.reference.*;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.parser.ReferencePhases;
import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;
import org.smartfrog.sfcore.security.SFClassLoader;

import java.util.Iterator;

/**
 * Representation of Assert Reference for the SF Language
 */
public class SFAssertReference extends SFReference implements ReferencePhases {
    protected SFComponentDescription comp;
    protected SFComponentDescription copyComp;

    public SFAssertReference(SFComponentDescription comp) {
        super();
        this.comp = comp;
    }

    /**
     * Get tje run-time version of the reference
     *
     * @return the reference
     * @throws org.smartfrog.sfcore.common.SmartFrogCompilationException
     */
    public Reference sfAsReference() throws SmartFrogCompilationException {
        AssertReference ar = new AssertReference(comp.sfAsComponentDescription());
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
        SFAssertReference ret = (SFAssertReference) clone();

        ret.comp = (SFComponentDescription) comp.copy();
        ret.setEager(eager);

        return ret;
    }

    /**
     * Makes a clone of the reference. The inside ref holder is cloned, but the
     * contained component is NOT.
     *
     * @return clone of reference
     */
    public Object clone() {
        SFAssertReference res = (SFAssertReference) super.clone();
        res.comp = comp;
        res.setEager(eager);

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
        if (!(reference instanceof SFAssertReference)) {
            return false;
        }

        return ((SFAssertReference) reference).comp == comp;

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
        String assertionPhase = "dynamic";
        boolean hasLazy = false;
        copyComp = (SFComponentDescription)comp.copy();

        if (getData()) return this;

        if (rr instanceof ComponentDescription)
            comp.setParent((ComponentDescription) rr);
        else if (rr instanceof Prim)
            comp.setPrimParent((Prim) rr);

        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogResolutionException("function class is not a string", e);
        }

        try {
            assertionPhase = (String) comp.sfResolveHere("sfAssertionPhase");
        } catch (ClassCastException e) {
            throw new SmartFrogResolutionException("assertion phase is not a string", e);
        } catch (SmartFrogResolutionException e) {
           assertionPhase = "dynamic";
        }
        if (!(assertionPhase.equals("dynamic") || assertionPhase.equals("static") || assertionPhase.equals("staticLazy"))) {
            throw new SmartFrogResolutionException("assertion phase is not a valid - must be static, staticLazy or dynamic");
        }

        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();

            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass") && !nameS.equals("sfAssertionPhase")) {
                Object value = null;
                try {
                     value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                     try {
                        comp.sfReplaceAttribute(name, value);
                        forFunction.sfAddAttribute(name, value);
                    } catch (SmartFrogContextException e) {
                        //shouldn't happen
                    } catch (SmartFrogRuntimeException e) {
                        //shouldn't happen
                    }
                } catch (SmartFrogLazyResolutionException e) {
                    if (assertionPhase.equals("static")) {
                        throw new SmartFrogResolutionException("Static assertion cannot evaluate due to LAZY attributes");
                    }
                    hasLazy = true;
                }


            }
        }

        if (functionClass == null) {
            throw new SmartFrogResolutionException("unknown function class ");
        }

        if (hasLazy) {
            if (assertionPhase.equals("static")) {
                throw new SmartFrogResolutionException("Static assertion cannot evaluate due to LAZY attributes");
            } else if (assertionPhase.equals("staticLazy")){
                return SFTempValue.get();
            } else { //(assertionPhase.equals("dynamic")) {
                setEager(false);
                comp = (SFComponentDescription)copyComp.copy();
                try {
                    comp.sfRemoveAttribute("sfAssertionPhase");
                } catch (SmartFrogRuntimeException e) {
                    //ignore
                }
                return this;
            }
        } else {
            try {
                Function function = (Function) SFClassLoader.forName(functionClass).newInstance();
                result = function.doit(forFunction, null, rr);
            } catch (Exception e) {
                System.out.println("obtained " + e);                
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward("failed to create or evaluate function class " + functionClass + " with data " + forFunction, e);
            }
        }

        if (result instanceof Boolean) {
            if (!((Boolean) result).booleanValue()) throw new SmartFrogAssertionResolutionException("Assertion failure (false) for "
                    + this +
                    ((rr instanceof ComponentDescriptionImpl) ?
                        " in component "
                        + ((ComponentDescriptionImpl)rr).sfCompleteNameSafe()
                            : ""));
        } else {
            throw new SmartFrogAssertionResolutionException("Assertion failure (non boolean result) for " +
                    this +
                    ((rr instanceof ComponentDescriptionImpl) ?
                        " in component "
                        + ((ComponentDescriptionImpl)rr).sfCompleteNameSafe()
                            : ""));
        }

        if (assertionPhase.equals("dynamic")) {
            setEager(false);
            comp = (SFComponentDescription) copyComp.copy();
            try {
                comp.sfRemoveAttribute("sfAssertionPhase");
            } catch (SmartFrogRuntimeException e) {
                //ignore
            }
            return this;
        } else { //static or staticLazy
            return SFTempValue.get();
        }
    }

    /**
     * Resolves this apply reference by applying the function - unless this is data..
     *
     * @param rr    ReferenceResolver to be used for resolving this reference
     * @param index index of first referencepart to start resolving at
     * @return value found on resolving this function
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if reference failed to resolve
     */
    public Object resolve(RemoteReferenceResolver rr, int index)
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
        String assertionPhase = "dynamic";
        boolean hasLazy = false;
        copyComp = (SFComponentDescription)comp.copy();

        if (getData()) return this;

        if (rr instanceof ComponentDescription)
            comp.setParent((ComponentDescription) rr);
        else if (rr instanceof Prim)
            comp.setPrimParent((Prim) rr);

        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogResolutionException("function class is not a string", e);
        }

        try {
            assertionPhase = (String) comp.sfResolveHere("sfAssertionPhase");
        } catch (ClassCastException e) {
            throw new SmartFrogResolutionException("assertion phase is not a string", e);
        } catch (SmartFrogResolutionException e) {
           assertionPhase = "dynamic";
        }
        if (!(assertionPhase.equals("dynamic") || assertionPhase.equals("static") || assertionPhase.equals("staticLazy"))) {
            throw new SmartFrogResolutionException("assertion phase is not a valid - must be static, staticLazy or dynamic");
        }

        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();

            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass") && !nameS.equals("sfAssertionPhase")) {
                Object value = null;
                try {
                     value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                     try {
                        comp.sfReplaceAttribute(name, value);
                        forFunction.sfAddAttribute(name, value);
                    } catch (SmartFrogContextException e) {
                        //shouldn't happen
                    } catch (SmartFrogRuntimeException e) {
                        //shouldn't happen
                    }
                } catch (SmartFrogLazyResolutionException e) {
                    if (assertionPhase.equals("static")) {
                        throw new SmartFrogResolutionException("Static assertion cannot evaluate due to LAZY attributes");
                    }
                    hasLazy = true;
                }


            }
        }

        if (functionClass == null) {
            throw new SmartFrogResolutionException("unknown function class ");
        }

        if (hasLazy) {
            if (assertionPhase.equals("static")) {
                throw new SmartFrogResolutionException("Static assertion cannot evaluate due to LAZY attributes");
            } else if (assertionPhase.equals("staticLazy")){
                return SFTempValue.get();
            } else { //(assertionPhase.equals("dynamic")) {
               setEager(false);
               comp = (SFComponentDescription)copyComp.copy();
               return this;
            }
        } else {
            try {
                Function function = (Function) SFClassLoader.forName(functionClass).newInstance();
                result = function.doit(forFunction, null, rr);
            } catch (Exception e) {
                System.out.println("obtained " + e);
                throw (SmartFrogResolutionException)SmartFrogResolutionException.forward("failed to create or evaluate function class " + functionClass + " with data " + forFunction, e);
            }
        }

        if (result instanceof Boolean) {
            if (!((Boolean) result).booleanValue()) throw new SmartFrogAssertionResolutionException("Assertion failure (false) for "
                    + this +
                    ((rr instanceof PrimImpl) ?
                        " in component "
                        + ((PrimImpl)rr).sfCompleteNameSafe()
                            : ""));
        } else {
            throw new SmartFrogAssertionResolutionException("Assertion failure (non boolean result) for " +
                    this +
                    ((rr instanceof PrimImpl) ?
                        " in component "
                        + ((PrimImpl)rr).sfCompleteNameSafe()
                            : ""));
        }

        if (assertionPhase.equals("dynamic")) {
            setEager(false);
            comp = (SFComponentDescription) copyComp.copy();
            try {
                comp.sfRemoveAttribute("sfAssertionPhase");
            } catch (SmartFrogRuntimeException e) {
                //ignore
            }
            return this;
        } else { //static or staticLazy
            return SFTempValue.get();
        }
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

        res += "ASSERT {";
        res += comp.sfContext().toString();
        res += "}";

        return res;
    }
}
