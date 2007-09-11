package org.smartfrog.sfcore.reference;

import org.smartfrog.sfcore.common.*;

import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.Serializable;
import java.util.Iterator;

/**
 * The subclass of Reference that is a function application. The structure of the classes is
 * historical, in that function applications were added much later. A different structure would
 * have been prefereable - an abstract class indicating some object that resolves in a context,
 * with specializations that are link references and apply references. However unfortunately for
 * backward compatility reasons this is not possible. Consequently Assert Reference impements the entire
 * gamut of the reference behaviour, inlcuding having parts, which is not relevant to a function applicaiton.
 * It should override these methods and generate some form of run-time exception - this has not been implemented.
 * <p/>
 * The function application reference resolves by evaluating hte refeences it contains, then evaluating the funciton.
 * If
 */
public class AssertReference extends Reference implements Copying, Cloneable, Serializable {
    protected ComponentDescription comp;

    public AssertReference(ComponentDescription comp) {
        super();
        this.comp = comp;
    }


    /**
     * Returns a copy of the reference, by cloning itself and the function part
     *
     * @return copy of reference
     * @see org.smartfrog.sfcore.common.Copying
     */
    public Object copy() {
        AssertReference ret = (AssertReference) clone();

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
        AssertReference res = (AssertReference) super.clone();
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
        if (!(reference instanceof AssertReference)) {
            return false;
        }

        if (((AssertReference) reference).comp != comp) {
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
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException if reference failed to resolve
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

        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();
            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass")) {
                Object value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                try {
                    forFunction.sfAddAttribute(name, value);
                } catch (SmartFrogContextException e) {
                    //shouldn't happen
                }
            }
        }
        if (functionClass == null) {
            throw new SmartFrogResolutionException("unknown function class ");
        }
        try {
            Function function = (Function) (SFClassLoader.forName((String) functionClass)
                    .newInstance());
            result = function.doit(forFunction, null, rr);
        } catch (Exception e) {
            throw new SmartFrogResolutionException("failed to create function class " + functionClass, e);
        }
        if (result instanceof Boolean) {
            if (!((Boolean) result).booleanValue()) throw new SmartFrogAssertionResolutionException("Assertion failure (false) for " +
                    this +
                    ((rr instanceof ComponentDescriptionImpl) ?
                        " in component "
                        + ((ComponentDescriptionImpl)rr).sfCompleteNameSafe()
                            : ""));
        } else {
            throw new SmartFrogAssertionResolutionException("Assertion failure (non boolean result) for "
                    + this +
                    ((rr instanceof ComponentDescriptionImpl) ?
                        " in component "
                        + ((ComponentDescriptionImpl)rr).sfCompleteNameSafe()
                            : ""));
        }
        return result;
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

        if (getData()) return this;

        if (rr instanceof ComponentDescription)
            comp.setParent((ComponentDescription) rr);
        else if (rr instanceof Prim)
            comp.setPrimParent((Prim) rr);

        try {
            functionClass = (String) comp.sfResolveHere("sfFunctionClass");
        } catch (ClassCastException e) {
            throw new SmartFrogAssertionResolutionException("function class is not a string", e);
        }

        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();
            String nameS = name.toString();
            if (!nameS.equals("sfFunctionClass")) {
                Object value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                try {
                    forFunction.sfAddAttribute(name, value);
                } catch (SmartFrogContextException e) {
                    //shouldn't happen
                }
            }
        }
        if (functionClass == null) {
            throw new SmartFrogAssertionResolutionException("unknown function class ");
        }
        try {
            Function function = (Function) (SFClassLoader.forName((String) functionClass)
                    .newInstance());
            result = function.doit(forFunction, null, rr);
        } catch (Exception e) {
            throw new SmartFrogAssertionResolutionException("failed to create or evaluate function class \"" + functionClass + "\" with data " + forFunction, e);
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

        res += "ASSERT {";
        res += comp.sfContext().toString();
        res += "}";

        return res;
    }
}
