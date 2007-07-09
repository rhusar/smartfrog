package org.smartfrog.sfcore.reference;

import org.smartfrog.sfcore.common.Copying;
import org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.componentdescription.ComponentDescriptionImpl;
import org.smartfrog.sfcore.prim.PrimImpl;

import java.io.Serializable;

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
public class AssertReference extends ApplyReference implements Copying, Cloneable, Serializable {
    
    public AssertReference(ComponentDescription comp) {
        super(comp);
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
            throws SmartFrogResolutionException
    {
        Object result = super.resolve(rr, index);
        checkAssert(this, result, rr, false);
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
            throws SmartFrogResolutionException
    {
        Object result = super.resolve(rr, index);
        checkAssert(this, result, rr, true);
        return result;
    }

    public static void checkAssert(Object _this, Object result, Object rr, boolean remote) throws SmartFrogAssertionResolutionException {
        if (result instanceof Boolean) {
            if (!((Boolean) result).booleanValue())
                throw new SmartFrogAssertionResolutionException("Assertion failure (false) for "
                        + _this + sfCompleteNameSafe(rr, remote));
        } else {
            throw new SmartFrogAssertionResolutionException("Assertion failure (non boolean result) for "
                    + _this + sfCompleteNameSafe(rr, remote));
        }
    }

    private static String sfCompleteNameSafe(Object rr, boolean remote) {
        if (remote) {
            if (rr instanceof PrimImpl) return " in component " + ((PrimImpl) rr).sfCompleteNameSafe();
            else return " <complete name unknown>";
        } else {
            if (rr instanceof ComponentDescriptionImpl)
                return " in component " + ((ComponentDescriptionImpl) rr).sfCompleteNameSafe();
            else return " <complete name unknown>";
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
