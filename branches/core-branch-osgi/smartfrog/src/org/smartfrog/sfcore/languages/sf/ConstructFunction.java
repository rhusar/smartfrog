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

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.smartfrog.sfcore.common.ContextImpl;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogRuntimeException;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescriptionImpl;
import org.smartfrog.sfcore.languages.sf.sfreference.SFApplyReference;
import org.smartfrog.sfcore.reference.Reference;

/**
 * Construct the function apply reference object, and replace self with this
 */
public class ConstructFunction implements PhaseAction {
    final static String functionClass = "sfFunctionClass";
    final static String functionLazy = "sfFunctionLazy";
    final static String functionPhase = "phase.function";

    // do the work
    String phaseName = null;
    SFComponentDescription cd = null;
    Stack path;

    public void doit() throws SmartFrogFunctionResolutionException {
        //SFComponentDescription comp = new SFComponentDescriptionImpl(null, (SFComponentDescription)(cd.sfParent()), new ContextImpl(), false);
        SFComponentDescription comp = new SFComponentDescriptionImpl(null, null, new ContextImpl(), false);

        try {
            comp.sfAddAttribute(functionClass, cd.sfResolve(functionClass));
        } catch (SmartFrogRuntimeException e) {
            throw new SmartFrogFunctionResolutionException("Unable to construct apply reference as sfFunctionClass is missing in phase: " + phaseName +
                    " for component: " + cd.sfCompleteName(), e);
        }

        for (Iterator i = cd.sfAttributes(); i.hasNext();) {
            Object key = i.next();
            Object value = null;
            Set tags = null;
            try {
                value = cd.sfResolveHere(key);
                tags = cd.sfGetTags(key);
            } catch (SmartFrogResolutionException e) {
                //shouldn't happen
            } catch (SmartFrogContextException e) {
                //shouldn't happen
            }
            if ((key.toString()).equals(functionPhase)) {
                //ignore
            } else {
                try {
                    comp.sfAddAttribute(key, value);
                    if (tags!=null) comp.sfSetTags(key, tags);
                } catch (SmartFrogRuntimeException e) {
                    //shouldn't happen
                }
            }
        }

        ComponentDescription parent = cd.sfParent();
        
        if (parent != null) {
            Object name = parent.sfContext().keyFor(cd);
            Reference newRef = new SFApplyReference(comp);
             try {
            // LAZY = false eager;
               newRef.setEager(!cd.sfResolve(functionLazy,false,false));
            } catch (SmartFrogRuntimeException e) {
                throw new SmartFrogFunctionResolutionException("Problem reading ("+functionLazy+"): " + phaseName + " for component: " + cd.sfCompleteName(), e);
            }
            parent.sfContext().put(name, newRef );
            
            comp.sfContext().remove(functionPhase);
        }
    }

    // the component description which is to be transformed
    public void forComponent(SFComponentDescription componentDescription, String phase, Stack pathStack) {
        this.phaseName = phase;
        this.cd = componentDescription;
        this.path = pathStack;        
    }
}
