/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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

package org.smartfrog.sfcore.languages.sf.predicates;

import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.Context;

import java.util.Stack;

/**
 * Defines the basic schema implementation.
 */
public class TBD implements PhaseAction {
    /** Flag indicating to keep predicate or not.
      */
     public static boolean keepPredicates = false;

     /** The component description. */
     protected ComponentDescription component;

     /** The context of the component. */
     protected Context context;
     protected String phaseName;
     protected Stack stack;

    /**
     * Applies predicates - by definition this is an error...!
     * @throws SmartFrogCompileResolutionException Executing this is an error...
     */
    public void doit() throws SmartFrogCompileResolutionException {
       ComponentDescription comeFrom = (ComponentDescription)stack.peek();
       Reference ref = comeFrom.sfCompleteName();
       Object name = comeFrom.sfAttributeKeyFor(component);
       throw new SmartFrogCompileResolutionException (
                                 "attribute is still TBD (to be defined) in attribute " + name , null, ref, "predicate", null
                                 );
    }

    /**
     * Sets the component.
     * @param cd component description
     */
    public void forComponent(ComponentDescription cd, String phasename, Stack p) {
        stack = p;
        component = cd;
        phaseName = phasename;
        context = cd.sfContext();
    }
}
