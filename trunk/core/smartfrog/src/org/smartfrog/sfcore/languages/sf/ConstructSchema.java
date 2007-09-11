package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.componentdescription.ComponentDescription;

import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfreference.SFAssertReference;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException;

import java.util.Stack;
import java.util.Iterator;

/**
 * Construct the function assert reference object, and replace self with this
 */
public class ConstructSchema implements PhaseAction {
    // do the work
    String phaseName = null;
    SFComponentDescription cd = null;
    Stack path;

    final static String checkAllClass = "org.smartfrog.sfcore.languages.sf.functions.CheckAssertions";
    final static String checkSchema = "org.smartfrog.sfcore.languages.sf.functions.CheckSchemaElement";
    final static String functionClass = "sfFunctionClass";
    final static String functionPhase = "phase.function";
    final static String assertionPhase = "sfAssertionPhase";
    final static String schemaDescription = "sfSchemaDescription";


    public void doit() throws SmartFrogAssertionResolutionException {
        SFComponentDescription comp = new SFComponentDescriptionImpl(null, null, new ContextImpl(), false);


        for (Iterator i = cd.sfAttributes(); i.hasNext();) {
            Object key = i.next();
            Object value = null;
            SFApplyReference schemaElement;

            if (!((key.toString()).equals(functionPhase) ||
                    (key.toString()).equals(assertionPhase) ||
                    (key.toString()).equals(schemaDescription))){
                try {
                    value = cd.sfResolveHere(key);

                    if (value instanceof SFApplyReference) {
                         schemaElement = (SFApplyReference) value;
                    } else {
                        throw new SmartFrogAssertionResolutionException("Element " + key + " of schema " + cd.sfCompleteName()
                                + " must be a ComponentDescription, found: " + value + " of type "+value.getClass());
                    }

                    schemaElement.sfAddParameter("name", key);

                    comp.sfReplaceAttribute(key, schemaElement);
                } catch (SmartFrogRuntimeException e) {
                    throw new SmartFrogAssertionResolutionException("Error constructing schema assertion", e);
                }
            }
        }

        try {
            comp.sfAddAttribute(functionClass, checkAllClass);
        } catch (SmartFrogRuntimeException e) {
            throw new SmartFrogAssertionResolutionException("Error adding sfFunctionClass to assertion " + cd.sfCompleteName() + " - already present? " + comp, e);
        }

        try {
            Object assertionPhaseValue;
            try {
                assertionPhaseValue = cd.sfResolve(assertionPhase);
            } catch (SmartFrogResolutionException e) {
                assertionPhaseValue = "dynamic";
            }
            comp.sfAddAttribute(assertionPhase, assertionPhaseValue);
        } catch (SmartFrogRuntimeException e) {
            // should never happen!
            throw new SmartFrogAssertionResolutionException("Error adding assertion phase information to assertion " + cd.sfCompleteName() +  " - already present? " + comp, e);
        }

        ComponentDescription parent = cd.sfParent();
        if (parent != null) {
            Object name = parent.sfContext().keyFor(cd);
            parent.sfContext().put(name, new SFAssertReference(comp));
        }
    }

    // the component description which is to be transformed
    public void forComponent(SFComponentDescription componentDescription, String phaseName, Stack pathStack) {
        this.phaseName = phaseName;
        this.cd = componentDescription;
        this.path = pathStack;
    }
}
