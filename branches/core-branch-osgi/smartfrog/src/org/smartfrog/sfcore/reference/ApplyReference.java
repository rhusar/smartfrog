package org.smartfrog.sfcore.reference;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.ParseTimeResourceFactory;
import org.smartfrog.sfcore.prim.Prim;

import java.io.Serializable;
import java.util.Iterator;

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
    // This will cause NotSerializableExceptions if someone tries to serialize us,
    // but the class should not be Serializable anyway
    protected ComponentDescription comp;

    public ApplyReference(ComponentDescription comp) {
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
     * @param ref to be compared
     * @return true if equal, false if not
     */
    public boolean equals(Object ref) {
        if (!(ref instanceof ApplyReference)) {
            return false;
        }

        if (((ApplyReference) ref).comp != comp) {
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
     * @throws org.smartfrog.sfcore.common.SmartFrogResolutionException
     *          if reference failed to resolve
     */
    public Object resolve(ReferenceResolver rr, int index)
            throws SmartFrogResolutionException
    {
        return doResolve(rr, false);
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

    private Object doResolve(Object rr, final boolean remote) throws SmartFrogResolutionException {//take a new context...
        //     iterate over the attributes of comp- ignoring any beginning with sf;
        //     cache sfFunctionClass attribute;
        //     resolve all non-sf attributes, if they are links
        //     if any return s LAZY object, set self to lazy and return self, otherwise update copy
        //     and invoke function with copy of CD, return result

        if (getData()) return this;

        initComp(rr);

        Object result = createAndApplyFunction(rr, remote);

        return result;
    }


    private Object createAndApplyFunction(Object rr, boolean remote) throws SmartFrogResolutionException {
        Object result;
        try {
            // First try to use the new syntax
            ComponentDescription metadata = (ComponentDescription) comp.sfResolveHere(SmartFrogCoreKeys.SF_METADATA);
            Context forFunction = createContext(SmartFrogCoreKeys.SF_METADATA);
            Function function = createFunction(metadata);

            try {
                if (remote) result = function.doit(forFunction, null, (RemoteReferenceResolver) rr);
                else result = function.doit(forFunction, null, (ReferenceResolver) rr);
            } catch (SmartFrogException e) {
                throw new SmartFrogResolutionException("Function invocation failed", e);
            }
        } catch (ClassCastException cce) {
            throw new SmartFrogResolutionException("The sfMeta attribute is not a DATA block", cce);


        } catch (SmartFrogResolutionException e) {

            // Using the old syntax
            String functionClass;
            try {
                functionClass = (String) comp.sfResolveHere(SmartFrogCoreKeys.SF_FUNCTION_CLASS);
            } catch (ClassCastException cce) {
                throw new SmartFrogResolutionException("function class is not a string", cce);
            }
            Context forFunction = createContext(SmartFrogCoreKeys.SF_FUNCTION_CLASS);
            result = createFunctionOldSyntax(functionClass);

        }
        return result;
    }

    private Function createFunctionOldSyntax(String functionClass) throws SmartFrogResolutionException {
        if (functionClass == null)
            throw new SmartFrogResolutionException("unknown function class");

        try {
            return (Function) Class.forName(functionClass).newInstance();
        } catch (Exception e) {
            throw new SmartFrogResolutionException("failed to create function class " + functionClass, e);
        }
    }

    private Function createFunction(ComponentDescription metadata) throws SmartFrogResolutionException {
        if (metadata == null)
            throw new SmartFrogResolutionException("sfMeta attribute is null");

        try {
            Reference factoryRef = (Reference) metadata.sfResolveHere(SmartFrogCoreKeys.SF_FACTORY);
            ParseTimeResourceFactory factory = (ParseTimeResourceFactory) metadata.sfResolve(factoryRef);
            return factory.getFunction(metadata);
        } catch (Exception e) {
            throw new SmartFrogResolutionException("Failed to create function. sfMeta block: " + metadata, e);
        }
    }

    private Context createContext(final String omittedAttribute)
            throws SmartFrogResolutionException
    {
        Context forFunction = new ContextImpl();
        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();
            String nameS = name.toString();
            if (!nameS.equals(omittedAttribute)) {
                Object value = comp.sfResolve(new Reference(ReferencePart.here(name)));
                try {
                    forFunction.sfAddAttribute(name, value);
                } catch (SmartFrogContextException e) {
                    //shouldn't happen
                    e.printStackTrace();
                }
            }
        }
        return forFunction;
    }


    private void initComp(Object rr) {
        if (rr instanceof ComponentDescription)
            comp.setParent((ComponentDescription) rr);
        else if (rr instanceof Prim)
            comp.setPrimParent((Prim) rr);
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
