package org.smartfrog.sfcore.reference;

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogCoreKeys;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.deployer.ClassLoadingEnvironment;
import org.smartfrog.sfcore.deployer.SFDeployer;
import org.smartfrog.sfcore.deployer.CoreClassesClassLoadingEnvironment;
import org.smartfrog.sfcore.prim.Prim;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;

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
    public static final String SF_ATTRIBUTE_PREFIX = "sf";

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
     * @param o to be compared
     * @return true if equal, false if not
     */
    public boolean equals(Object o) {
        if (!(o instanceof ApplyReference)) {
            return false;
        }

        return ((ApplyReference) o).comp == comp;

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

        return createAndApplyFunction(rr, remote, comp, createContext());
    }


    /**
     * Hack. This is a step towards eliminating duplicated code in SFAssertReference and SFApplyReference.
     * @param rr
     * @param remote
     * @param comp
     * @param forFunction
     * @return
     * @throws SmartFrogResolutionException
     */
    public static Object createAndApplyFunction(Object rr, boolean remote, final ComponentDescription comp, final Context forFunction)
            throws SmartFrogResolutionException
    {
        Function function = createFunction(comp);

        return evaluateFunction(remote, function, forFunction, rr);
    }

    public static Object evaluateFunction(boolean remote, Function function, Context forFunction, Object rr) throws SmartFrogResolutionException {
        try {
            if (remote)
                return function.doit(forFunction, null, (RemoteReferenceResolver) rr);
            else
                return function.doit(forFunction, null, (ReferenceResolver) rr);

        } catch (SmartFrogException e) {
            throw (SmartFrogResolutionException) SmartFrogResolutionException.forward
                    ("Failed to evaluate function: " + function + " with data " + forFunction, e);
        }
    }

    private static String getFunctionClass(ComponentDescription comp) throws SmartFrogResolutionException {
        String functionClass;
        try {
            functionClass = (String) comp.sfResolveHere(SmartFrogCoreKeys.SF_FUNCTION_CLASS);
        } catch (ClassCastException cce) {
            throw new SmartFrogResolutionException("function class is not a string", cce);
        }
        
        if (functionClass == null)
            throw new SmartFrogResolutionException("unknown function class");

        return functionClass;
    }

    private static Function createFunction(ComponentDescription description) throws SmartFrogResolutionException {
        String functionClass = getFunctionClass(description);
        try {
            ClassLoadingEnvironment env = resolveEnvironmentHere(description);            
            return (Function) env.loadClass(functionClass).newInstance();
        } catch (Exception e) {
            throw new SmartFrogResolutionException
                    ("Failed to create function class " + functionClass + " from description: " + description, e);
        }
    }

    private Context createContext()
            throws SmartFrogResolutionException
    {
        Context forFunction = new ContextImpl();
        for (Iterator v = comp.sfAttributes(); v.hasNext();) {
            Object name = v.next();
            String nameS = name.toString();
            if (!nameS.startsWith(SF_ATTRIBUTE_PREFIX)) {
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

    public static ClassLoadingEnvironment resolveEnvironmentHere(ComponentDescription cd) throws SmartFrogResolutionException {
        ClassLoadingEnvironment env = (ClassLoadingEnvironment) cd.sfResolveHere(SmartFrogCoreKeys.SF_CLASS_LOADING_ENVIRONMENT, false);
        if (env == null) env = new CoreClassesClassLoadingEnvironment();
        return env;
    }
}
