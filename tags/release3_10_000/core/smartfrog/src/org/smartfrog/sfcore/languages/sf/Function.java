package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.reference.ApplyReference;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;

import java.util.Stack;
import java.util.Iterator;

/**
 * Construct the function apply reference object, and replace self with this
 */
public class Function implements PhaseAction {
    // do the work
    String phaseName = null;
    SFComponentDescription cd = null;
    Stack path;

    public void doit() throws SmartFrogCompileResolutionException {
        SFComponentDescription comp = new SFComponentDescriptionImpl(null, null, new ContextImpl(), false);
        SFApplyReference apply = new SFApplyReference(comp);

        try {
            comp.sfAddAttribute("sfFunctionClass", cd.sfResolve("sfFunctionClass"));
        } catch (SmartFrogRuntimeException e) {
            throw new SmartFrogCompileResolutionException("Unable to construct apply reference as sfFunctionClass is missing in phase: " + phaseName +
                    " for component: " + cd.sfCompleteName(), e);
        }

        for (Iterator i = cd.sfAttributes(); i.hasNext();) {
            Object key = i.next();
            Object value = null;
            try {
                value = cd.sfResolveHere(key);
            } catch (SmartFrogResolutionException e) {
                //shouldn't happen
            }
            if ((key.toString()).startsWith("sf") || (key.toString()).startsWith("phase.")) {
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
            parent.sfContext().put(name, apply);
        }
    }

    // the component description which is to be transformed
    public void forComponent(SFComponentDescription cd, String phaseName, Stack path) {
        this.phaseName = phaseName;
        this.cd = cd;
        this.path = path;
    }
}
