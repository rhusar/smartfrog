/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

For more information: www.smartfrog.org

*/
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
            Object name = parent.sfAttributeKeyFor(cd);
            parent.sfContext().put(name, new SFAssertReference(comp));
        }
    }

    // the component description which is to be transformed
    public void forComponent(SFComponentDescription componentDescription, String phase, Stack pathStack) {
        this.phaseName = phase;
        this.cd = componentDescription;
        this.path = pathStack;
    }
}
