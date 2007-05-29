package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfreference.SFAssertReference;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.util.Stack;
import java.util.Iterator;

/**
 * Construct the function assert reference object, and replace self with this
 */
public class ConstructAssertions implements PhaseAction {
    // do the work
    String phaseName = null;
    SFComponentDescription cd = null;
    Stack path;

    final static String checkAllClass = "org.smartfrog.sfcore.languages.sf.functions.CheckAssertions";
    final static String functionClass = "sfFunctionClass";
    final static String functionPhase = "phase.function";

    public void doit() throws SmartFrogAssertionResolutionException {
        SFComponentDescription comp = new SFComponentDescriptionImpl(null, null, new ContextImpl(), false);
        try {
            comp.sfAddAttribute(functionClass, checkAllClass);
        } catch (SmartFrogRuntimeException e) {
            // should never happen!
            throw new SmartFrogAssertionResolutionException("Error adding sfFunctionClass to assertion - already present?", e);
        }

        for (Iterator i = cd.sfAttributes(); i.hasNext();) {
            Object key = i.next();
            Object value = null;
            try {
                value = cd.sfResolveHere(key);
            } catch (SmartFrogResolutionException e) {
                //shouldn't happen
            }
            if ((key.toString()).equals(functionPhase)) {
                //ignore
            } else {
                try {
                    comp.sfAddAttribute(key, value);
                } catch (SmartFrogRuntimeException e) {
                    //shouldn't happen
                }
            }
        }

        ComponentDescription parent = cd.sfParent();
        if (parent != null) {
            Object name = parent.sfContext().keyFor(cd);
            parent.sfContext().put(name, new SFAssertReference(comp));
        }
    }

    // the component description which is to be transformed
    public void forComponent(SFComponentDescription cd, String phaseName, Stack path) {
        this.phaseName = phaseName;
        this.cd = cd;
        this.path = path;
    }
}
