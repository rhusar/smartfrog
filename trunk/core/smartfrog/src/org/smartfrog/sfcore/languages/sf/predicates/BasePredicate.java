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

import org.smartfrog.sfcore.common.Context;
import org.smartfrog.sfcore.common.MessageUtil;
import org.smartfrog.sfcore.common.MessageKeys;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.PhaseAction;
import org.smartfrog.sfcore.languages.sf.SmartFrogCompileResolutionException;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Stack;

/**
 * Defines the base predicate for all the schemas.
 */
public abstract class BasePredicate implements PhaseAction {

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
     * The method to implement the functionality of any schema.
     *
     * @throws SmartFrogCompileResolutionException failed to implement the
     * schema
     */
    protected abstract void doPredicate() throws SmartFrogCompileResolutionException;

    /**
     * Default implementation of doit method.
     */
    public void doit() throws SmartFrogCompileResolutionException {
        ComponentDescription comeFrom = (ComponentDescription)stack.peek();
        Reference ref = comeFrom.sfCompleteName();

        // check that the predicate is being applied to a component which is the parent, and not merely linked
        if (stack.peek() != component.sfParent())
            throw new SmartFrogCompileResolutionException( MessageUtil.formatMessage(MessageKeys.CANNOT_LINK_TO_PREDICATE,
                                                          ref,
                                                          comeFrom.sfAttributeKeyFor(this)));


        doPredicate();
        ComponentDescription parent = (ComponentDescription) component.sfParent();
        Context parentContext = parent.sfContext();
        Object key = parentContext.keyFor(component);
        if (!keepPredicates) parentContext.remove(key);
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
