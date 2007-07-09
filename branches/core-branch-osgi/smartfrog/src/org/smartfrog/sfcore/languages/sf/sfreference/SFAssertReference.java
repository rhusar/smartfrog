package org.smartfrog.sfcore.languages.sf.sfreference;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.reference.*;

import java.util.Iterator;

/**
 * Representation of Assert Reference for the SF Language
 */
public class SFAssertReference extends SFApplyReference {
    protected SFComponentDescription copyComp;

    public SFAssertReference(SFComponentDescription comp) {
        super(comp);
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
    public Object resolve(RemoteReferenceResolver rr, int index)
            throws SmartFrogResolutionException
    {
        return doResolve(rr, true);

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
        return doResolve(rr, false);

    }

    private Object doResolve(Object rr, boolean remote) throws SmartFrogResolutionException {//take a new context...
        //     iterate over the attributes of comp- ignoring any beginning with sf;
        //     cache sfFunctionClass attribute;
        //     resolve all non-sf attributes, if they are links
        //     if any return s LAZY object, set self to lazy and return self, otherwise update copy
        //     and invoke function with copy of CD, return result

        copyComp = (SFComponentDescription) comp.copy();

        if (getData()) return this;

        initComp(rr);

        String assertionPhase = getAssertionPhase();

        Context forFunction = new ContextImpl();
        boolean hasLazyAttributes = createContext(forFunction, assertionPhase);

        if (hasLazyAttributes) return getLazyValue(assertionPhase);

        Object result = ApplyReference.createAndApplyFunction(rr, remote, comp, forFunction);

        AssertReference.checkAssert(this, result, rr, remote);

        return resultDependingOnPhase(assertionPhase);
    }

    private Object resultDependingOnPhase(String assertionPhase) {
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

    private String getAssertionPhase() throws SmartFrogResolutionException {
        String assertionPhase;
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
        return assertionPhase;
    }

    private Object getLazyValue(String assertionPhase) throws SmartFrogResolutionException {
        if (assertionPhase.equals("static")) {
            throw new SmartFrogResolutionException("Static assertion cannot evaluate due to LAZY attributes");
        } else if (assertionPhase.equals("staticLazy")) {
            return SFTempValue.get();
        } else { //(assertionPhase.equals("dynamic")) {
            setEager(false);
            comp = (SFComponentDescription) copyComp.copy();
            try {
                comp.sfRemoveAttribute("sfAssertionPhase");
            } catch (SmartFrogRuntimeException e) {
                //ignore
            }
            return this;
        }
    }

    /**
     * 
     * @param forFunction
     * @param assertionPhase
     * @return True iif a lazy parameter was passed to the function
     * @throws SmartFrogResolutionException
     */
    private boolean createContext(Context forFunction, String assertionPhase) throws SmartFrogResolutionException {
        boolean hasLazy = false;
        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();

            String nameS = name.toString();
            if (!nameS.startsWith(ApplyReference.SF_ATTRIBUTE_PREFIX)) {
                Object value;
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
        return hasLazy;
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
        res += comp.sfContext();
        res += "}";

        return res;
    }
}
